#!/usr/bin/env bash
# Build Kotlin/JS shared and stage CommonJS bundles for WeChat mini program.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
OUT="$ROOT/miniprogram/libs/shared"
export GRADLE_USER_HOME="${GRADLE_USER_HOME:-/tmp/gradle-home-nod}"
export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
GRADLE="${GRADLE:-$HOME/.gradle/wrapper/dists/gradle-8.11.1-all/6gcpoccneql1b0krsle0llw37/gradle-8.11.1/bin/gradle}"

cd "$ROOT"
"$GRADLE" :shared:jsNodeDevelopmentLibraryDistribution

mkdir -p "$OUT"
rm -f "$OUT"/*.js
SRC="$ROOT/shared/build/dist/js/developmentLibrary/NumberOurDays-shared.js"
if [ ! -f "$SRC" ]; then
  SRC="$(find "$ROOT/shared/build" -type f -name 'NumberOurDays-shared.js' | head -1)"
fi
if [ -z "$SRC" ]; then
  echo "NumberOurDays-shared.js not found" >&2
  exit 1
fi
cp -f "$SRC" "$OUT/NumberOurDays-shared.js"
DIR="$(dirname "$SRC")"
find "$DIR" -maxdepth 1 -type f -name '*.js' -exec cp -f {} "$OUT/" \;
echo "Staged into $OUT:"
ls "$OUT"
