# NumberOurDays（数日子）

跨端倒数日 / 纪念日 App：**Android（Compose）+ iOS（SwiftUI）**，业务逻辑共享在 Kotlin Multiplatform `:shared`。

参考 `/Users/mac/Documents/code/NumberOurDays` 的设计与产品逻辑。

## 功能

- 首页：主数字轮播卡 + 纪念日列表（星标置顶，组内按倒数日期）
- 详情：天 / 月 / 年切换（含「X 月 X 天」）、已过时长联动、编辑、删除
- 编辑：标题、日期选择器、重复（天/周/月/年）、提醒、备注
- 设置：本地条数、JSON 文件 / 剪贴板备份（合并 / 覆盖）、清空
- 法定假日倒数与列表（无假期时置灰入口）
- 本地提醒：Android 精确闹钟；iOS 本地通知
- 桌面小组件（Android）

## 工程结构

| 目录 | 说明 |
|---|---|
| `shared/` | KMP 共享领域：日期计算、节假日、备份 JSON、提醒时机 |
| `app/` | Android UI（Compose + Material 3） |
| `iosApp/` | iOS UI（SwiftUI），见 `iosApp/README.md` |

## 设计 Token

| Token | Hex |
|---|---|
| 纸底 n-0 | `#FAF7F2` |
| 浅底 n-50 | `#F3EEE6` |
| 主色 accent | `#D4553A` |
| 正文 n-900 | `#1C1916` |

## 构建

### Android

需要本机 JDK 17 + Android SDK（platform 35）。

```bash
# 若 ~/.gradle 配置了不可用 SOCKS，可用独立 GRADLE_USER_HOME：
export GRADLE_USER_HOME=/tmp/gradle-home-nod
export ANDROID_HOME=$HOME/Library/Android/sdk
./gradlew :app:assembleDebug
# 产物：app/build/outputs/apk/debug/app-debug.apk
```

### iOS

需要 Xcode + xcodegen。详见 [iosApp/README.md](iosApp/README.md)。

```bash
./iosApp/scripts/build-shared.sh Debug
cd iosApp && xcodegen generate
open NumberOurDays.xcodeproj
```

## 共享逻辑

倒数 / 重复展开、法定假期、备份 schema（`schemaVersion = 1`）、下次提醒时刻等均在 `:shared`，Android 与 iOS 不得各自重写日期算法。
