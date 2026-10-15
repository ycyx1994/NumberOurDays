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

# WeChat resolves require('@js-joda/core') as libs/shared/@js-joda/core.js
mkdir -p "$OUT/@js-joda"
if [ -f "$OUT/node_modules/@js-joda/core/dist/js-joda.js" ]; then
  cp -f "$OUT/node_modules/@js-joda/core/dist/js-joda.js" "$OUT/@js-joda/core.js"
fi
if [ ! -f "$OUT/format-util.js" ]; then
  cat > "$OUT/format-util.js" <<'EOS'
function format(fmt) {
  var args = Array.prototype.slice.call(arguments, 1)
  var i = 0
  return String(fmt).replace(/%s|%d|%j/g, function (m) {
    var v = args[i++]
    if (m === '%j') { try { return JSON.stringify(v) } catch (e) { return String(v) } }
    return String(v)
  })
}
module.exports = format
module.exports.format = format
EOS
fi
echo "WeChat shims ready in $OUT/@js-joda and format-util.js"

# Minify for WeChat 2MB source limit
if command -v npx >/dev/null 2>&1; then
  find "$OUT" -name '*.js' ! -name 'format-util.js' -print0 | while IFS= read -r -d '' f; do
    npx --yes terser "$f" -c -m -o "$f" >/dev/null 2>&1 || true
  done
fi
