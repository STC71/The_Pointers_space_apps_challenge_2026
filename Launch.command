#!/bin/bash
set -e
cd "$(dirname "$0")"

if [ "$(uname -s)" = "Darwin" ]; then
    # Finder no carga la configuración de la terminal ni el PATH de Homebrew.
    for jdk in /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home /usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home; do
        if [ -x "$jdk/bin/java" ]; then
            export JAVA_HOME="$jdk"
            break
        fi
    done
    if [ -z "${JAVA_HOME:-}" ]; then
        JAVA_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null) || {
            echo "Instala Java 17 con: brew install openjdk@17"
            read -r -p "Pulsa Intro para cerrar."
            exit 1
        }
        export JAVA_HOME
    fi
    export PATH="$JAVA_HOME/bin:/opt/homebrew/bin:/usr/local/bin:$PATH"
else
    # Linux utiliza JAVA_HOME, si está definido, o Java instalado en el PATH.
    if [ -n "${JAVA_HOME:-}" ]; then
        if [ ! -x "$JAVA_HOME/bin/java" ]; then
            echo "JAVA_HOME no apunta a un JDK válido: $JAVA_HOME"
            exit 1
        fi
        export PATH="$JAVA_HOME/bin:$PATH"
    elif ! command -v java >/dev/null 2>&1; then
        echo "Instala un JDK 17 o superior o configura JAVA_HOME."
        exit 1
    fi
fi

./gradlew :composeApp:run "$@"
