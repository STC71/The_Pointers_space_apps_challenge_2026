/* Browser media stays here so cancellation and rejected play() promises are explicit. */
(() => {
    const active = new Set();
    let removeLifecycle = () => {};

    function clearTimers(entry) {
        clearTimeout(entry.timer);
        entry.timer = null;
    }

    function detachMedia(entry) {
        const audio = entry.audio;
        entry.audio = null;
        if (!audio) return;
        audio.onended = null;
        audio.onerror = null;
        audio.onplaying = null;
        audio.pause();
        audio.removeAttribute("src");
        audio.load();
    }

    function stop(entry) {
        entry.stopped = true;
        entry.attempt++;
        clearTimers(entry);
        detachMedia(entry);
        active.delete(entry);
    }

    function finish(entry) {
        if (entry.stopped) return;
        const complete = entry.complete;
        stop(entry);
        complete();
    }

    function failed(entry) {
        if (entry.stopped) return;
        clearTimers(entry);
        if (!entry.usingFallback) {
            entry.usingFallback = true;
            entry.attempt++;
            detachMedia(entry);
            begin(entry);
        } else finish(entry);
    }

    function attemptPlay(entry) {
        if (entry.stopped || entry.pending || !entry.audio || document.hidden) return;
        const audio = entry.audio;
        const attempt = ++entry.attempt;
        entry.pending = true;
        entry.blocked = false;
        // Includes loading. A blocked autoplay request waits for a gesture instead.
        clearTimers(entry);
        entry.timer = setTimeout(() => failed(entry), 15000);
        try {
            const promise = audio.play();
            Promise.resolve(promise).then(() => {
                if (entry.stopped || entry.attempt !== attempt || entry.audio !== audio) return;
                entry.pending = false;
                clearTimers(entry);
                if (!entry.loop) {
                    const duration = Number.isFinite(audio.duration) ? audio.duration : 180;
                    entry.timer = setTimeout(() => finish(entry), Math.min(duration + 5, 185) * 1000);
                }
            }).catch(error => {
                if (entry.stopped || entry.attempt !== attempt || entry.audio !== audio) return;
                entry.pending = false;
                clearTimers(entry);
                if (error && error.name === "NotAllowedError") {
                    // Do not replay old alarms in a burst on the next interaction.
                    if (entry.channel === "effect") finish(entry);
                    else entry.blocked = true;
                } else failed(entry);
            });
        } catch (_) {
            entry.pending = false;
            failed(entry);
        }
    }

    function begin(entry) {
        if (entry.stopped) return;
        entry.pending = false;
        const audio = new Audio();
        entry.audio = audio;
        audio.preload = "auto";
        audio.loop = entry.loop;
        audio.onended = () => { if (entry.audio === audio) finish(entry); };
        audio.onerror = () => { if (entry.audio === audio) failed(entry); };
        audio.src = entry.usingFallback ? entry.fallback : entry.url;
        attemptPlay(entry);
    }

    window.wcoStopAudio = channel => {
        for (const entry of [...active]) {
            if (channel === "all" || entry.channel === channel) stop(entry);
        }
    };

    window.wcoPlayAudio = (url, fallback, loop, channel, complete) => {
        if (channel === "music" && [...active].some(entry => entry.channel === channel)) return;
        if (channel !== "effect") window.wcoStopAudio(channel);
        const effects = [...active].filter(entry => entry.channel === "effect");
        if (channel === "effect" && effects.length >= 8) stop(effects[0]);
        const entry = {
            url, fallback, loop, channel, complete, usingFallback: !url,
            stopped: false, blocked: false, pending: false, attempt: 0, audio: null, timer: null
        };
        active.add(entry);
        begin(entry);
    };

    // Retry only still-wanted playback inside a user gesture; mute / pause remove it.
    const unlock = () => {
        for (const entry of [...active]) if (entry.blocked) attemptPlay(entry);
    };

    window.wcoInstallAudioLifecycle = (pause, resume) => {
        removeLifecycle();
        const visibility = () => document.hidden ? pause() : resume();
        document.addEventListener("pointerdown", unlock, true);
        document.addEventListener("keydown", unlock, true);
        document.addEventListener("visibilitychange", visibility);
        window.addEventListener("pagehide", pause);
        window.addEventListener("pageshow", visibility);
        removeLifecycle = () => {
            document.removeEventListener("pointerdown", unlock, true);
            document.removeEventListener("keydown", unlock, true);
            document.removeEventListener("visibilitychange", visibility);
            window.removeEventListener("pagehide", pause);
            window.removeEventListener("pageshow", visibility);
        };
        if (document.hidden) pause();
    };

    window.wcoReleaseAudio = () => {
        window.wcoStopAudio("all");
        removeLifecycle();
        removeLifecycle = () => {};
    };

    window.wcoOpenExternalUrl = raw => {
        try {
            const url = new URL(raw);
            if (url.protocol !== "https:" && url.protocol !== "http:") return;
            window.open(url.href, "_blank", "noopener,noreferrer");
        } catch (_) { /* Ignore malformed references without interrupting the mission. */ }
    };
})();
