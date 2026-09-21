---
feature: ui-polish
status: in-progress
updated: 2026-09-21
branch: feature/ui-polish
commits: a1f3d2c01a5ec0331775120035eef045b5e929f9..<TBD>
---

# UI Polish — 审计全量修订

## Report

## [S1] Problem

Android 端由 Taro/小程序迁移而来，信息架构可用，但偏离品牌 DESIGN.md：设置页多主按钮与开发者式 JSON 粘贴主路径、首页 accent 超预算、详情/编辑 affordance 弱、圆角与分隔线不统一、主数字缺 display 质感、深色主题半吊子、FAB 形态随 ROM 漂移。

## [S2] Design

Taste 沿用参考 `DESIGN.md`（纸底 `n-0`、accent `#D4553A`、数字优先、线性列表、每屏 accent≤2、按钮仅 primary/ghost）。本轮按审计全量落地。

### 2.1 设置页 IA

| 区块 | 行为 |
|---|---|
| 数据 | 只读元信息：`N 条纪念日` 普通文本行，**不再用输入框** |
| 备份（主） | **导出到文件**（SAF CreateDocument）为 accent 主按钮；**复制到剪贴板** 为 ghost |
| 备份说明 | 保留本地存储风险说明，12sp `n-500` |
| 导入（主） | **从文件导入**（OpenDocument）为 accent 主按钮；打开后仍走合并/覆盖/取消确认 |
| 导入（高级） | **粘贴 JSON** 默认折叠；展开后才有 textarea +「合并导入 / 覆盖本地」（覆盖保持描边红） |
| 危险操作 | **清空本地数据** ghost/描边红 + 二次确认 |
| 页脚 | 保留 |

Acceptance：设置屏同时最多 **1 个** accent 实心按钮（从文件导入与导出到文件分属备份/导入两区时，各区最多 1 个主按钮，同屏可见时优先仅「导出到文件」「从文件导入」为 primary，剪贴板与粘贴均为次级）。

### 2.2 首页 accent 与列表

- **Accent 预算**：同屏可见 accent 仅：主数字 + FAB。星标图标：未选中 `N300`，已选中改为 `N700`（或填充星但用 `N700`），**不用 Accent**。
- **Pager 圆点**：选中 `N700`/`N500`，未选中 `N300`；尺寸略增至 6–7dp。
- **主卡 meta**：去掉与大数字重复的「还有 N 天」；保留 `日期 · 重复/类型`（类型可用「倒数/已过/今天/每周」等短标签）。
- **列表右侧数值**：`还有 N 天` 保持；字重 Bold、颜色 `N900`；星标行把手常驻占位（已实现），把手图标 `N300`。
- **分隔线**：`HorizontalDivider` 颜色 `N100`（非 N50）。
- **FAB**：应用内固定 `FloatingActionButton`（或等价）containerColor=`Accent`，shape 统一 `RoundedCornerShape(16.dp)` 或圆形，**不依赖 ROM 默认**。

### 2.3 详情页

- **TopBar 标题**：有事件时用 `d.title`（ellipsis），无事件仍用「详情」+ 返回。
- **主卡圆角**：16dp → **12dp**（与 `NodShapes.large` / 首页主卡一致）。
- **天/月/年**：主数字下方保持一组 chip。
- **「已经过」单位**：移除 Refresh 图标切换；在 Meta 行「已经过」下方或行内增加与上方同风格的 **天/月/年** chip（或第二组 FilterChip），状态存 `elapsedUnit`。
- **主数字**：`displayLarge` 语义：字重 Bold，可加轻微 `letterSpacing`；见 2.6。
- 备注空态：无备注时不渲染备注行或弱化空文案（与现逻辑一致即可，不强制新文案）。

### 2.4 编辑页

- **日期行**：与其它 OutlinedTextField 视觉对齐的可点击行；**右侧 trailing 日历图标**（`Icons.Outlined.CalendarMonth` 或等价），contentDescription「选择日期」；整行可点打开 DatePicker。
- 重复/提醒/保存/删除交互保持；保存仍为本屏唯一 accent 主按钮。

### 2.5 圆角与形状系统

| Token | 值 | 用途 |
|---|---|---|
| Field/Control | 8dp | 输入框、按钮、chip 底 |
| Card | 12dp | 首页主卡、详情主卡、拖拽抬升卡 |
| FAB | 16dp 或 circle | 写死，见 2.2 |

禁止同功能组件混用 8/12/16。

### 2.6 主数字 Display

- `Typography.displayLarge`（及首页/详情主数字处显式使用）：`fontWeight = FontWeight.Bold`，`fontSize = 64.sp`，`letterSpacing` 略收（如 `-1.sp` 或 0，以更「数块」为准，避免过散）。
- **v1 不引入新字体文件**（无授权/体积问题）；在 Theme 中集中定义 display 数字样式，两处主数字共用同一 TextStyle，避免各写各的 64.sp。
- 若后续引入 condensed 字体，只改 Theme 一处。

### 2.7 浅色纸感锁定

- `NumberOurDaysTheme`：**忽略系统深色**，v1 始终 LightColors（纸底 N0）。
- DarkColors 可保留在 Theme.kt 注释/备用，但默认不启用。
- App 不跟随 `isSystemInDarkTheme()`。

### 2.8 错误与边界

- 设置折叠状态：`rememberSaveable`，进程死亡后保持展开/折叠。
- 详情事件不存在：TopBar 仍「详情」，内容空态不变。
- 单位 chip 切换：不影响列表排序或持久化，仅 UI 展示状态。
- 星标改 N700 后：星标排序逻辑不变。

### 2.9 测试边界

- 纯逻辑若被抽取（例如设置区「可见 primary 按钮数」无逻辑）可不测。
- 回归：现有 `BackupTransferTest` / `DragReorderTest` 必须仍通过。
- UI 观感以 assembleDebug + 截图/真机目视为主；不强制 Compose UI 测试。

## [S3] Out of Scope

- 提醒通知能力（独立 P0）
- 云同步、主题商店、多套 dark token 正式设计
- 打包字体文件 / 下载 DIN
- 重构整个导航或引入 ViewModel 架构改造
- 无障碍全面审计（仅保证现有 contentDescription 不倒退）
- 真机自动化 UI 测试框架

## Tasks

- [ ] T1: 设置页 IA — 只读条数、文件导出/导入为各自主按钮、剪贴板次级、粘贴 JSON 折叠 — acceptance: 源码中设置区无「输入框只读条数」；折叠可展开粘贴路径；SAF 路径仍可用 (covers: S2.1)
- [ ] T2: Theme 形状/显示数字/锁定浅色 — acceptance: `NumberOurDaysTheme` 默认浅色；displayLarge 统一；圆角 token 注释或常量清晰 (covers: S2.5, S2.6, S2.7)
- [ ] T3: 首页 — 星标/圆点降 accent、主卡 meta 去重、分隔线 N100、FAB 形态写死 — acceptance: 同屏 accent 主要为数字+FAB；divider 用 N100 (covers: S2.2)
- [ ] T4: 详情 — TopBar 事件名、主卡 12dp、已经过单位改 chip — acceptance: 标题随事件变化；无 Refresh 图标切单位 (covers: S2.3)
- [ ] T5: 编辑 — 日期行 calendar affordance — acceptance: 日期行有日历图标且点击打开选择器 (covers: S2.4)
- [ ] T6: 构建与既有单测 — acceptance: `assembleDebug` + `testDebugUnitTest` 通过 (covers: S2.9; depends: T1–T5)
- [ ] T7: 安装测试机并人工核对四屏 — acceptance: 安装成功；设置/首页/详情/编辑关键项与 S2 一致 (covers: S2; depends: T6)
