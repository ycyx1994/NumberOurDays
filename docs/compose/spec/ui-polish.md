---
feature: ui-polish
status: delivered
updated: 2026-09-21
branch: feature/ui-polish
commits: a1f3d2c01a5ec0331775120035eef045b5e929f9..930318c65fdf17929d2e2ed0f21beb0781d9e280
---

# UI Polish — 审计全量修订

## Report

**What was built** — 按设计审计全量落地 UI 修订：设置页改为文件导出/导入为主、剪贴板为次、粘贴 JSON 默认折叠；首页收敛 accent（数字+FAB，星标/圆点中性色）、主卡 meta 去重、分隔线 N100、FAB 形态写死；详情 TopBar 显示事件名、主卡 12dp、「已经过」单位改为 chip；编辑日期行增加日历 affordance；Theme 统一圆角 token、共享 `NodDisplayNumber`、锁定纸色浅色主题。

**Verification** —
- `testDebugUnitTest` — PASS — 11 tests, 0 failures (BackupTransferTest 6 + DragReorderTest 5)
- `assembleDebug` — PASS
- `adb` 安装无线 nezha — PASS — MainActivity 可启动
- Independent review (general-2) — PASS — 7/7 AC met, 0 critical

**Journey log** —
- 审计全量范围含品牌偏离项（设置 IA、accent 预算、形状系统、浅色锁定），信息架构优先于像素微调。
- Theme 去掉 `darkTheme` 参数后仅 `MainActivity` 调用，API 收缩无破坏。
- 设置页规格中「同屏最多 1 个 accent」与「各区文件操作各自主按钮」略有张力；实现按后者，同屏可见「导出到文件」「从文件导入」两个 primary。
- 真机反馈覆盖审计决策：星标与列表天数数字改回主题色 Accent；日期选择弹窗标题去掉重复 ISO 日期。
- T7 真机四屏目视核对待用户最终确认后合并。

## [S1] Problem

Android 端由 Taro/小程序迁移而来，信息架构可用，但偏离品牌 DESIGN.md：设置页多主按钮与开发者式 JSON 粘贴主路径、首页 accent 超预算、详情/编辑 affordance 弱、圆角与分隔线不统一、主数字缺 display 质感、深色主题半吊子、FAB 形态随 ROM 漂移。

## [S2] Design

Taste 沿用参考 `DESIGN.md`（纸底 `n-0`、accent `#D4553A`、数字优先、线性列表、按钮仅 primary/ghost）。

### 2.1 设置页 IA

| 区块 | 行为 |
|---|---|
| 数据 | 只读元信息文本，非输入框 |
| 备份 | **导出到文件** primary；**复制到剪贴板** ghost |
| 导入 | **从文件导入** primary；粘贴 JSON 默认折叠（advanced） |
| 危险操作 | 清空描边红 + 确认 |

各区文件主路径为 primary；剪贴板与粘贴为次级。

### 2.2 首页 accent 与列表

- **用户反馈修订（2026-09-21）**：列表星标高亮使用主题色 `Accent`（未选中仍为 `N300`）；详情页星标同理。列表右侧「还有 X 天」等文案中的**天数数字**亦用 `Accent`（单位与前缀保持中性）。
- **用户反馈修订**：首页轮播大卡内容水平/垂直居中；卡外左右边距 16→12dp，卡高 220→200，内边距略收。
- 主卡 meta：日期 · 重复/类型，去掉与大数字重复的「还有 N 天」。
- 列表右侧 `item.label` 保留；分隔线 `N100`；FAB `NodFabShape`。

### 2.3 详情页

- TopBar：事件标题（单行 ellipsis），否则「详情」。
- 主卡 `NodCardShape`（12dp）；卡内内容居中、上下间距收紧。
- **用户反馈**：天/月/年 chip **移出主卡**，居中放在卡片下方。
- **用户反馈**：「已经过」单位改为 **紧凑点击轮换**（Meta 行尾部小标签，点按 天→月→年），不再另起一排 chip。
- Meta 行垂直 padding 略收（14→12dp）。

### 2.4 编辑页

- 日期行 trailing 日历图标 + `choose_date` a11y，整行可点。
- **用户反馈修订**：日期选择弹窗标题不再重复当前 ISO 日期（M3 DatePicker 已展示选中日）；标题仅为「选择日期」。

### 2.5–2.7 形状 / Display / 浅色

- 控件 8dp、卡片 12dp、FAB 16dp token 化。
- `NodDisplayNumber` 共享主数字样式。
- `NumberOurDaysTheme` 始终浅色纸底。

## [S3] Out of Scope

- 提醒通知、云同步、正式 dark tokens、字体文件分发、架构改造、自动化 UI 测试。

## Tasks

- [x] T1: 设置页 IA — acceptance: 只读条数、文件主路径、粘贴折叠 (covers: S2.1)
- [x] T2: Theme 形状/显示数字/锁定浅色 — acceptance: 始终浅色；token 清晰 (covers: S2.5, S2.6, S2.7)
- [x] T3: 首页 accent/meta/分隔线/FAB — acceptance: accent 预算与 N100 分隔 (covers: S2.2)
- [x] T4: 详情标题/12dp/已经过 chip — acceptance: 无 Refresh 切单位 (covers: S2.3)
- [x] T5: 编辑日期 calendar affordance — acceptance: 图标 + 打开选择器 (covers: S2.4)
- [x] T6: 构建与既有单测 — acceptance: assembleDebug + testDebugUnitTest (covers: S2.9; depends: T1–T5)
- [ ] T7: 安装测试机并人工核对四屏 — acceptance: 用户目视确认四屏与 S2 一致 (covers: S2; depends: T6)
  - 注：安装与启动已完成；四屏人工目视核对待用户确认。
