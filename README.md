# NumberOurDays · Android（原生 Kotlin）

参考 `/Users/mac/Documents/code/NumberOurDays` 的设计与产品逻辑，使用 **Jetpack Compose + Material 3（Kotlin）** 实现的到数日 App。

## 功能

- 首页：主数字轮播卡 + 纪念日列表（星标置顶、拖拽排序）
- 详情：天 / 月 / 年切换、已经过时长循环、星标、编辑
- 编辑：标题、日期、重复（天/周/月/年）、提醒开关、备注、删除
- 设置：本地条数、剪贴板导出/导入 JSON、合并/覆盖、清空
- 本地持久化：`SharedPreferences` JSON（`nod:events:v1`）
- 首次空库播种 demo（周末 / 新年）

## 设计 Token

| Token | Hex |
|---|---|
| 纸底 n-0 | `#FAF7F2` |
| 浅底 n-50 | `#F3EEE6` |
| 主色 accent | `#D4553A` |
| 正文 n-900 | `#1C1916` |

## 构建

需要本机 JDK 17 + Android SDK（已装 platform 35）。

```bash
cd NumberOurDays-Android
# 若本机 ~/.gradle/gradle.properties 配置了不可用的 SOCKS，可用独立 GRADLE_USER_HOME：
export GRADLE_USER_HOME=/tmp/gradle-home-nod
export ANDROID_HOME=$HOME/Library/Android/sdk
$HOME/.gradle/wrapper/dists/gradle-8.11.1-all/*/gradle-8.11.1/bin/gradle assembleDebug
```

产物：

`app/build/outputs/apk/debug/app-debug.apk`

根目录亦有拷贝：`NumberOurDays-debug.apk`

## 包名

`com.numberourdays.app`

## 页面（Compose + Navigation）

- `MainActivity` — `NavHost` 宿主
- `HomeScreen` — 首页（轮播 + 列表 + FAB）
- `DetailScreen` — 详情
- `EditScreen` — 添加/编辑
- `SettingsScreen` — 备份设置
