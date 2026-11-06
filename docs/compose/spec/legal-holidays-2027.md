---
feature: legal-holidays-2027
status: delivered
updated: 2026-11-06
branch: feature/legal-holidays-2027
commits: 63a64f5..c491659
---

# 法定假日模块支持 2027 年

## Report

**What was built** — `LegalHolidays` 增补 2027 年国务院七段放假区间（国办发明电〔2026〕10号），`periods()` 改为 `Y2026 + Y2027` 共 14 段；Android 首页/假日列表/桌面 widget、iOS 首页/假日页/WidgetKit、微信小程序全部经由 `periods()` 取数，自动获得 2027 倒数目标，表尾（2027-10-07）之后仍返回 null 隐藏入口。

数据跨年后假日名不再唯一，假期列表改为**按年分组 + 可折叠**（见 S2.5）：`HolidayCalculator.yearGroups()` 产出 `HolidayYearGroup`，已过完的年份默认收起；三端列表的行 key 一律带放假首日，修掉了 iOS `ForEach` 因重名撞 identity 导致中段漏画、下段重复的缺陷（小程序 `wx:key="name"` 同款问题一并修）。Android 箭头改用 Material 图标（文本字符 `▾` 太小），iOS 年份改 `Text(verbatim:)` 避免 `2,027` 千分位。

测试适配 14 段并新增跨年倒数、春节 9 天、端午/中秋单日、2027 覆盖、以及 4 组 `yearGroups` 断言；同名假日的 `first {}` 断言加年份过滤。weapp bundle 重建同步同一张表，构建脚本在 minify 后把裸 `require('@js-joda/core')` 改写为 `require('./@js-joda/core.js')`（此前 Node smoke 直接 `MODULE_NOT_FOUND`），并跳过已压缩的 `@js-joda/*`。

**Verification** —
- `:shared:jvmTest` — PASS — 53 tests, 0 failures（HolidayCalculatorTest 19 例）
- `:app:testDebugUnitTest` / `:app:assembleDebug` — PASS
- `node scripts/weapp-smoke.js` — PASS — 含 2027 倒数 / 14 行 / 分组顺序三条断言
- iOS `xcodebuild` 真机构建 — PASS，已装机验证
- Android 真机 `adb install` — Success
- Independent review (general-1) — PASS — 0 critical，2 non-critical

## [S1] Problem

内置法定假日表 `LegalHolidays` 只有 `Y2026` 七段放假区间。国办已于 2026-11-05 发布《关于2027年部分节假日安排的通知》（国办发明电〔2026〕10号），但 App 尚未收录：2026 国庆之后 `nextOrOngoing` 因表尾无数据返回 null，首页假日小卡/轮播页与桌面假日 widget 隐藏，假日列表只剩 2026 的 7 行，2027 全年没有倒数目标。

所有平台（Android home/widget/假日列表、iOS home/假日页/WidgetKit、微信小程序）都经由 `LegalHolidays.periods()` 取数，因此本次只改数据表本身与测试/产物，不改任何 UI。

## [S2] Design

### S2.1 2027 放假区间（唯一事实来源）

来源：国务院办公厅《关于2027年部分节假日安排的通知》，国办发明电〔2026〕10号，2026-11-05，中国政府网
<https://www.gov.cn/zhengce/content/202611/content_7085101.htm>。

| 假日 | 放假区间（含首尾） | 天数 | 调休上班日 |
|---|---|---|---|
| 元旦 | 2027-01-01 ~ 2027-01-03 | 3 | — |
| 春节 | 2027-02-05 ~ 2027-02-13 | 9 | 01-31、02-14 |
| 清明节 | 2027-04-03 ~ 2027-04-05 | 3 | — |
| 劳动节 | 2027-05-01 ~ 2027-05-05 | 5 | 05-08 |
| 端午节 | 2027-06-09 ~ 2027-06-09 | 1 | — |
| 中秋节 | 2027-09-15 ~ 2027-09-15 | 1 | — |
| 国庆节 | 2027-10-01 ~ 2027-10-07 | 7 | 09-26、10-09 |

与 2026 同口径：**只建模放假区间，调休上班日不是假期、不入库**。端午、中秋各放 1 天不调休，是 2027 的新特点（2026 两节各 3 天）。

### S2.2 `LegalHolidays.periods()` 契约

| 契约 | 行为 |
|---|---|
| 组织 | 逐年私有列表 `Y2026` / `Y2027`，`periods() = Y2026 + Y2027`，返回 **14** 段 |
| 顺序 | 年内按起始日升序，跨年连续（2026 元旦 … 2027 国庆） |
| 表尾行为 | 不变：今天晚于最后一段（2027-10-07）且无后续年数据时，`nextOrOngoing` 返回 null，UI 隐藏假日入口 |
| 年内行为 | 不变：`nextOrOngoing` / `listRows` 算法零改动，仅数据源变长 |
| 2026 数据 | 原样保留，一字不改 |

### S2.3 测试契约（`:shared:jvmTest`）

- 既有断言基数随数据量调整：`listRows` 行数 7 → 14，`afterAllBundledPeriods` 的"表尾"日期从 2026-10-08 后移到 2027-10-08。
- 新增 2027 专项断言：
  - 春节 2027-02-05 ~ 2027-02-13（9 天连休，与 2026 春节同为 9 天但区间不同，断言须带年份过滤）；
  - 端午节、中秋节 2027 为单日（`start == end`）；
  - `periods()` 覆盖 2027 全年七段，首段起 2027-01-01、末段止 2027-10-07；
  - 跨年倒数：今天 2026-10-08（2026 国庆后首个工作日）→ 目标 2027 元旦。
- 断言不得复述生产端排序/计算逻辑，只锚定官方日期与可见行为。

### S2.4 微信小程序产物同步

小程序经 `miniprogram/packageApp/libs/shared/*.js`（Kotlin/JS CommonJS 产物）取假日数据，改 `LegalHolidays` 后必须重建 bundle，否则小程序仍是 2026-only。

| 契约 | 行为 |
|---|---|
| 重建 | `scripts/build-weapp-shared.sh` 产出新的 `NumberOurDays-shared.js` 等 |
| require 修正 | Kotlin/JS 新产出带裸 npm specifier `require('@js-joda/core')`；minify 后统一改写为 `require('./@js-joda/core.js')`，否则 Node smoke 与微信都解析不到 |
| vendored shim | `@js-joda/*` 为已压缩的第三方产物，跳过 terser，避免二次压缩破坏 |
| smoke | `scripts/weapp-smoke.js` 增加两条断言：`holidayCountdown('2026-10-08')` 命中 2027 元旦（start `2027-01-01`）；`holidayList('2026-10-08')` 返回 14 行 |

### S2.5 年份分组列表（三端）

数据表跨年后假日名不再唯一（2026/2027 各有一套元旦…国庆），列表按年分组并解决 identity。

| 契约 | 行为 |
|---|---|
| 分组 | `HolidayCalculator.yearGroups()`：按放假首日所在年分组，一年一组 → `HolidayYearGroup(year, rows, allPast)` |
| 组内顺序 | 保持 `listRows` 顺序（进行中 → 未到 → 已过） |
| 组间顺序 | 仍有未结束段的年份在上（最近放假首日优先）；全部已过的年份沉底（最近的年份在前） |
| 默认折叠 | `allPast` 的年份默认收起，点年份标题切换展开/收起 |
| 行 identity | key 必须带放假首日：iOS `name@start`、Android `name + start`、小程序 `wx:key="start"`。只用 name 会撞 identity——iOS 的 `LazyVStack` 会漏画中段、下段重复 |
| 箭头 | 展开/收起指示：Android 用 Material `KeyboardArrowDown` / `KeyboardArrowRight`（20.dp）；iOS 用 SF Symbol `chevron.down` / `chevron.right` |
| 整数渲染 | 年份等整数进 `Text` 须用 `Text(verbatim:)`：SwiftUI 字符串字面量插值走 `LocalizedStringKey`，会给 2027 加千分位显示成 2,027 |

## [S3] Out of Scope

- iOS `project.yml` scheme / `xcshareddata` xcscheme 配置：与假日无关，不动。
- 2028 年数据、调休上班日建模。

## Tasks

- [x] T1: `LegalHolidays` 新增 `Y2027` 七段并接入 `periods()` — acceptance: `periods()` 返回 14 段，2027 各段起止日期与 S2.1 表逐格一致，2026 数据零改动 (covers: S2.1, S2.2)
- [x] T2: `HolidayCalculatorTest` 适配 14 段并补 2027 专项断言 — acceptance: `:shared:jvmTest` 全绿，含跨年倒数、春节 9 天、端午/中秋单日 (covers: S2.3)
- [x] T3: 重建 weapp bundle 并补 smoke 断言 — acceptance: `node scripts/weapp-smoke.js` 通过，两条假日断言命中 2027；bundle 内可见 2027 区间 (covers: S2.4)
- [x] T4: 三端年份分组列表 + 可折叠 — acceptance: `yearGroups` 排序/`allPast` 有单测；iOS/Android/小程序均可展开收起且已过年份默认收起；行 key 带放假首日 (covers: S2.5)

## Journey log

- 上一轮把 `yearGroups` 插进 `HolidayCalculator.kt` 时吃掉了 `listRows` 的 `sortedWith(...)` 收尾和 `HolidayListRow` 的类声明，文件直接编译不过。教训：往 `object` 内插入新 fun 时要整块检查花括号配平；半截 API 与其带崩整个文件，不如整段撤掉重来。
- 假日表跨年后 `first { it.name == "春节" }` 这类断言会静默绑到错误年份（`listRows_past_showsDaysPastEnd` 尤其危险：`first` 会命中 2027 端午而非已过的 2026 端午，断言直接红）。带年份的假日断言一律加 `start.year` 过滤。
- Kotlin/JS 新产出会带裸 `require('@js-joda/core')`，Node 与微信都解析不到（实测 `MODULE_NOT_FOUND`）；必须在 terser 之后改写为 `require('./@js-joda/core.js')`——放在 minify 之前会被 terser 重新引号化而失配。另：`listRows` 的 past 段按 `start` 倒序而非 `end` 倒序，注释写的是「最近结束的在最前」；因放假区间互不重叠，两序恒等，行为无差，但注释措辞与比较器不一致，将来若引入重叠区间需回头改。
