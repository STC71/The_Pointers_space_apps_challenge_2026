/* APOD transport and one atomic, private, bounded IndexedDB cache record. No DOM injection. */
(() => {
    const maxCacheChars = 4300000;
    const requests = new Map();
    let nextId = 1;

    function cacheOperation(value, done) {
        let finished = false;
        let db = null;
        let transaction = null;
        const finish = result => {
            if (finished) return;
            finished = true;
            clearTimeout(timer);
            if (db) db.close();
            done(result);
        };
        const timer = setTimeout(() => {
            if (transaction) { try { transaction.abort(); } catch (_) {} }
            finish(value === undefined ? null : false);
        }, 3000);
        try {
            if (value !== undefined && (typeof value !== "string" || value.length > maxCacheChars)) {
                finish(false);
                return;
            }
            const open = indexedDB.open("wco-apod", 1);
            open.onupgradeneeded = () => open.result.createObjectStore("background");
            open.onerror = open.onblocked = () => finish(value === undefined ? null : false);
            open.onsuccess = () => {
                db = open.result;
                if (finished) { db.close(); return; }
                db.onversionchange = () => db.close();
                try {
                    transaction = db.transaction("background", value === undefined ? "readonly" : "readwrite");
                    const store = transaction.objectStore("background");
                    const request = value === undefined ? store.get("last-valid") : store.put(value, "last-valid");
                    let result = value === undefined ? null : true;
                    if (value === undefined) request.onsuccess = () => {
                        const raw = request.result;
                        result = typeof raw === "string" && raw.length <= maxCacheChars ? raw : null;
                    };
                    transaction.oncomplete = () => finish(result);
                    transaction.onerror = transaction.onabort = () => finish(value === undefined ? null : false);
                } catch (_) { finish(value === undefined ? null : false); }
            };
        } catch (_) { finish(value === undefined ? null : false); }
    }
    window.wcoApodReadCache = done => cacheOperation(undefined, done);
    window.wcoApodWriteCache = (value, done) => cacheOperation(value, done);

    window.wcoApodAbort = id => {
        const controller = requests.get(id);
        if (controller) controller.abort();
    };
    window.wcoApodFetch = (raw, maxBytes, done) => {
        const id = nextId++;
        const controller = new AbortController();
        requests.set(id, controller);
        const timer = setTimeout(() => controller.abort(), 20000);
        (async () => {
            let reader;
            try {
                const url = new URL(raw);
                if (url.protocol !== "https:" || url.username || url.password ||
                    !Number.isInteger(maxBytes) || maxBytes < 1 || maxBytes > 3145728) throw Error("Invalid APOD request");
                const response = await fetch(url.href, {
                    signal: controller.signal, mode: "cors", credentials: "omit",
                    redirect: "error", cache: "no-store", headers: { Accept: "application/json,image/jpeg,image/png" }
                });
                const type = (response.headers.get("content-type") || "").split(";")[0].toLowerCase();
                const length = Number(response.headers.get("content-length"));
                if (response.status !== 200 || (type !== "application/json" && !type.startsWith("image/")) ||
                    length > maxBytes || !response.body) throw Error("Invalid APOD response");
                reader = response.body.getReader();
                const chunks = [];
                let total = 0;
                for (;;) {
                    const { value, done: ended } = await reader.read();
                    if (ended) break;
                    total += value.length;
                    if (total > maxBytes) throw Error("APOD transfer limit");
                    chunks.push(value);
                }
                // Only bounded primitive data crosses the Kotlin/Wasm bridge.
                const bytes = new Uint8Array(total);
                let offset = 0;
                for (const chunk of chunks) { bytes.set(chunk, offset); offset += chunk.length; }
                let binary = "";
                for (let i = 0; i < bytes.length; i += 8192) {
                    binary += String.fromCharCode(...bytes.subarray(i, i + 8192));
                }
                done(btoa(binary));
            } catch (_) {
                controller.abort();
                done(null);
            } finally {
                if (reader) { try { await reader.cancel(); } catch (_) {} }
                clearTimeout(timer);
                requests.delete(id);
            }
        })();
        return id;
    };
})();
