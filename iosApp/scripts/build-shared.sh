#!/usr/bin/env bash
# Build the KMP Shared.framework for iOS Simulator (arm64) and stage it for Xcode.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
OUT="$ROOT/shared/build/xcode-frameworks"
CONFIG="${1:-Debug}"

export GRADLE_USER_HOME="${GRADLE_USER_HOME:-/tmp/gradle-home-nod}"
export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
GRADLE="${GRADLELE:-$HOME/.gradle/wrapper/dists/gradle-8.11.1-all/6gcpoccneql1b0krsle0llw37/gradle-8.11.1/bin/gradle}"

if [[ "$CONFIG" == "Release" ]]; then
  TASK=":shared:linkReleaseFrameworkIosSimulatorArm64"
else
  TASK=":shared:linkDebugFrameworkIosSimulatorArm64"
fi

cd "$ROOT"
"$GRADLE" "$TASK"

SRC="$ROOT/shared/build/bin/iosSimulatorArm64/${CONFIG}Framework/Shared.framework"
mkdir -p "$OUT"
rm -rf "$OUT/Shared.framework"
cp -R "$SRC" "$OUT/Shared.framework"
echo "Staged $OUT/Shared.framework"
