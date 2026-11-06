---
feature: legal-holidays-2027
status: in-progress
updated: 2026-11-06
branch: feature/legal-holidays-2027
---

# 法定假日模块支持 2027 年

## Report

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

## [S3] Out of Scope

- **按年分组 API（`yearGroups` / `HolidayYearGroup`）**：上一轮遗留的半截改动已按要求整体撤掉，本次不引入任何新 API。
- iOS `project.yml` scheme / `xcshareddata` xcscheme 配置：与假日无关，不动。
- Android / iOS / 小程序 UI 与 widget 布局：消费方读 `periods()` 即自动生效，无需改。
- 2028 年数据、调休上班日建模、按年折叠列表 UI。

## Tasks

- [ ] T1: `LegalHolidays` 新增 `Y2027` 七段并接入 `periods()` — acceptance: `periods()` 返回 14 段，2027 各段起止日期与 S2.1 表逐格一致，2026 数据零改动 (covers: S2.1, S2.2)
- [ ] T2: `HolidayCalculatorTest` 适配 14 段并补 2027 专项断言 — acceptance: `:shared:jvmTest` 全绿，含跨年倒数、春节 9 天、端午/中秋单日 (covers: S2.3)
- [ ] T3: 重建 weapp bundle 并补 smoke 断言 — acceptance: `node scripts/weapp-smoke.js` 通过，两条假日断言命中 2027；bundle 内可见 2027 区间 (covers: S2.4)

## Journey log

- 上一轮把 `yearGroups` 插进 `HolidayCalculator.kt` 时吃掉了 `listRows` 的 `sortedWith(...)` 收尾和 `HolidayListRow` 的类声明，文件直接编译不过。教训：往 `object` 内插入新 fun 时要整块检查花括号配平；半截 API 与其带崩整个文件，不如整段撤掉重来。
