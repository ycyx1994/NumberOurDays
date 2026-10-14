---
feature: kmp-shared-logic
status: delivered
updated: 2026-10-14
branch: feature/kmp-shared
commits: 654e31c..7e08194
---

# KMP 共享逻辑层（P0+P1）

## Report

**What was built** — 新增 KMP 模块 `:shared`（androidTarget / jvm / iosArm64 / iosSimulatorArm64 / iosX64，framework `Shared`），将倒数日核心纯逻辑从 Android `app` 抽出到 `commonMain`：`DateCalculator` / `HolidayCalculator` / `LegalHolidays` / `Models` / `ReminderTiming` / `DayEventCodec` / `EventRepository` 接口 / `DemoEvents` / `BackupNaming`。日期统一为 `kotlinx.datetime`，备份 JSON 走 `kotlinx.serialization` 且 schema v1 字段冻结不变。Android 侧 `RoomEventRepository` 实现接口，保留 Room、prefs 迁移、demo 播种、提醒调度与 Widget 刷新副作用；UI 改为依赖接口并直接调用 `DayEventCodec`。纯逻辑测试迁入 `commonTest`（kotlin.test），iOS 接入约定写入文档，本迭代不包含 SwiftUI 工程。

**Verification** —
- `:shared:jvmTest` — PASS（50 tests, 0 failures；含 merge 时间戳/自然键回归）
- `:app:testDebugUnitTest` — PASS（2 tests, 0 failures，Room mappers）
- `:app:assembleDebug` — PASS
- `:app:compileDebugKotlin` / `:shared:compileDebugKotlinAndroid` — PASS

**Journey log** —
1. `java.time` API（`daysUntil` / `isoDayNumber` / `monthValue`）与 kotlinx-datetime 不一致，改为 `toEpochDays()` 跨度与 `monthNumber`。
2. kotlinx-datetime/coroutines 必须在 shared 里用 `api` 暴露，否则 app 编译期看不到 `LocalDate`。
3. 初版 `DayEvent` 时间戳默认值写成 `0`，违反 S2 契约且改变缺失字段的 migrate 语义；review 抓出后改回 `Clock.System.now()` 并补测。
4. `DayEventCodec.merge` 原有自然键索引过期问题（同 id 改标题后错并）随迁移带入 shared；一并修复并加回归测试。
5. 壳层默认工作目录是主仓库 `main`，在 worktree 上干活时命令必须显式 `workdir` / `git -C`。

## [S1] Problem

NumberOurDays 的领域逻辑（日期计算、节假日、重复规则、备份 JSON、提醒时间点）目前住在 Android `app` 模块里，与 Room / WorkManager / Compose 搅在一起。计划在 iOS 用 SwiftUI 重做 UI，但业务规则必须与 Android 同源，否则倒数天数、重复展开、备份互导会漂移。本交付把可共享逻辑抽到 KMP `shared` 模块，并把存储收成接口，为 iOS 接入铺路；本迭代不写 iOS/SwiftUI 工程。

## [S2] Design

### 模块边界

- 新增 Gradle 模块 `:shared`（`kotlin("multiplatform")` + `com.android.library`）。
- 目标：`androidTarget`、`jvm`（本地测试）、`iosArm64` / `iosSimulatorArm64` / `iosX64`（framework `baseName = "Shared"`，本迭代不产出 iOS App）。
- `:app` 依赖 `:shared`，UI 与系统能力（Compose、Widget、WorkManager、通知、SAF）仍留在 Android。

### 包名与导出

- 迁入代码保持包名 `com.numberourdays.app.*`，Android 调用方 import 不改。
- iOS 侧后续通过 `Shared` framework 消费同名 API；Swift 类型名以 KMP 导出为准。

### 迁入 `commonMain`（纯逻辑 / 无 Android API）

| 来源 | 目标 | 说明 |
|---|---|---|
| `data/Models.kt` | 同包 | `DayEvent` / `DayEventDisplay` / `RepeatUnit` / `EventKind` / `Duration*` |
| `data/LegalHolidays.kt` | 同包 | 2026 法定放假区间 |
| `data/DayEventCodec`（原 `BackupTransfer.kt` 内） | `data/DayEventCodec.kt` | JSON 编解码 + merge |
| `data/EventRepository` 接口 | `data/EventRepository.kt` | 仅数据 API，无 Context |
| `data/DemoEvents.kt` | 新增 | 首次播种 demo（周末 / 新年） |
| `data/BackupNaming.kt` | 新增 | 备份文件名纯函数 |
| `domain/DateCalculator.kt` | 同包 | 日期/展示/排序/表单校验 |
| `domain/HolidayCalculator.kt` | 同包 | 法定假期倒数与列表 |
| `notify/ReminderTiming.kt` | 同包 | 下次提醒时刻（纯计算） |

### 留在 Android（`app`）

- Room：`NodDatabase` / `DayEventDao` / `DayEventEntity` / mappers
- `RoomEventRepository`（原 `EventRepository` 实现）：Room + prefs 迁移 + demo 播种 + `ReminderScheduler` + Widget 刷新
- `BackupTransfer` 的 ContentResolver 读写
- `NotificationHelper` / `ReminderScheduler` / `ReminderWorker`
- Widget receivers、全部 Compose UI

### 关键契约

**日期类型**：`commonMain` 使用 `kotlinx.datetime.LocalDate` / `Clock` / `TimeZone`（不再用 `java.time`）。`DayEvent.date` 仍是 `YYYY-MM-DD` 字符串。展示文案（「还有 N 天」等）保持中文、字段语义不变。

**JSON schema（冻结）**：备份文件字段与现网一致：

```json
{
  "app": "NumberOurDays",
  "schemaVersion": 1,
  "exportedAt": 0,
  "events": [ { "id", "title", "date", "note", "starred", "starRank",
                "repeatUnit", "repeatInterval", "remindEnabled",
                "remindHour", "remindMinute", "createdAt", "updatedAt" } ]
}
```

`DayEventCodec` 使用 `kotlinx.serialization`（替换 `org.json`），对外函数：

- `toJson(e: DayEvent): String`
- `migrate(...)` / `parseEventsArray(raw: String): List<DayEvent>`
- `serialize(events: List<DayEvent>): String`
- `parseBackup(text: String): Pair<List<DayEvent>, String?>`
- `merge(local: List<DayEvent>, incoming: List<DayEvent>): List<DayEvent>`

**EventRepository 接口**（`commonMain`）：

```kotlin
interface EventRepository {
    val events: StateFlow<List<DayEvent>>
    fun loadEvents(): List<DayEvent>
    fun getEvent(id: String): DayEvent?
    fun upsert(event: DayEvent)
    fun remove(id: String)
    fun clearAll()
    fun toggleStar(id: String)
    fun reorderStars(orderedIds: List<String>)
    fun saveAll(events: List<DayEvent>)
    fun createId(): String
    fun ensureDemoIfEmpty()
}
```

Android `RoomEventRepository(context)` 实现该接口并保留副作用（提醒调度、Widget 刷新、prefs 迁移标记）。UI 构造处依赖接口类型；备份编解码由 `DayEventCodec` 承担。

**ID / 时间戳**：`createId()` 与默认 `createdAt`/`updatedAt` 使用 `kotlin.random` + `Clock.System.now()`，不再依赖 `UUID` / `System.currentTimeMillis()`。JSON 缺省时间戳时同样回落为 wall-clock now。

**提醒计算**：`ReminderTiming.nextReminderMillis(...)` 接受 epoch millis 与 `kotlinx.datetime.TimeZone`（默认 `TimeZone.currentSystemDefault()`）；纯函数不变式：同输入同输出。

### 测试

`shared/src/commonTest`（`kotlin.test`）：

- `DateCalculator`：`ResolveNextDateTest`、`DurationBreakdownTest`、`DragReorderTest`
- `HolidayCalculatorTest`
- `ReminderTimingTest`
- `DayEventCodecTest`（serialize/parse/merge/时间戳缺省）

`app` 侧保留 `DayEventMappersTest`（Room 映射）。

### iOS 接入约定（本迭代只写约定）

1. Xcode 工程以 `embedAndSignAppleFrameworkForXcode` 或 SPM 引入 `Shared`。
2. 领域 API：`DateCalculator` / `HolidayCalculator` / `DayEventCodec` / `ReminderTiming` 同步纯函数，Swift 直接调用。
3. 数据：Swift 自建 `EventRepository` 实现（SQLite / 文件），或后续补 `iosMain` 实现；UI 不得复制日期算法。
4. 小组件 / 通知：逻辑算日期走 `Shared`，调度与 WidgetKit 留在 Swift。

## [S3] Out of Scope

- iOS/SwiftUI 工程、WidgetKit、UNUserNotificationCenter
- Room → SQLDelight / 多平台存储落地（接口已预留）
- Compose Multiplatform UI
- 节假日网络下发、schema v2
- 更改产品功能或文案语义
- 构建脚本 Version Catalog / plugins DSL 大改造

## Tasks

- [x] T1: 建立 `:shared` KMP 模块（android/jvm/ios targets + serialization + datetime + coroutines），settings/include 就位 — acceptance: `./gradlew :shared:compileKotlinJvm` 成功 (covers: S2)
- [x] T2: 迁移 Models / LegalHolidays / DateCalculator / HolidayCalculator / ReminderTiming 到 commonMain，日期改为 kotlinx-datetime — acceptance: shared 内无 `java.time` import；源文件位于 shared/src/commonMain (covers: S2; depends: T1)
- [x] T3: 抽出 `DayEventCodec`（kotlinx.serialization，schema 不变）与 `EventRepository` 接口 — acceptance: codec 往返 serialize/parse 保持字段；接口无 Android 依赖 (covers: S2; depends: T2)
- [x] T4: 迁移纯逻辑测试到 commonTest 并通过 — acceptance: `./gradlew :shared:jvmTest` 全绿 (covers: S2; depends: T2, T3)
- [x] T5: Android 接入：`RoomEventRepository` 实现接口，UI/提醒/Widget 走 shared API，删除 app 内重复纯逻辑 — acceptance: `./gradlew :app:assembleDebug :app:testDebugUnitTest` 通过 (covers: S2; depends: T3, T4)
