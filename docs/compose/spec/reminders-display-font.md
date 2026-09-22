---
feature: reminders-display-font
status: delivered
updated: 2026-09-21
branch: feature/reminders-display-font
commits: 0268e7e..98c5bd9
---

# Reminders + Display Font

## Report

**What was built** — 目标日（或下次重复日）本地 09:00 的 WorkManager 唯一提醒：通知渠道 + 运行时权限、开关/删除取消、重复到点续期；主数字改为打包的 Barlow Condensed Bold（OFL）。保存提醒时若未授权通知会 Toast 提示。

**Verification** —
- `testDebugUnitTest` — PASS — 16 tests, 0 failures (Backup 6 + Drag 5 + ReminderTiming 5)
- `assembleDebug` — PASS
- adb install to nezha — PASS
- Independent review (general-3) — PASS — 0 critical；已补 Font Bold 权重、权限 Toast、通知文案

**Journey log** —
- 09:00 用 WorkManager 而非精确 Alarm，避开 SCHEDULE_EXACT_ALARM 限制。
- OFL 许可证不能放 `res/font`（资源合并要求扩展名），改到 `docs/licenses/`。
- Compose `Font(@FontRes)` 默认 Normal，Bold 字体需显式 `weight = FontWeight.Bold` 避免合成加粗。
- `afterFire` 在 schedule 里靠 `notBefore=now` 推进到下一周期；Worker 发完再 schedule。
- 月末 `plusMonths` 夹断为既有 DateCalculator 行为，会连带影响月/年提醒日。


## [S1] Problem

1. **提醒是空壳**：编辑页「提醒」只落库 `remindEnabled`，文案写明「通知后续版本」；用户开了也不会收到任何通知。
2. **主数字缺品牌字体**：首页/详情大数字仍是系统 sans + 字重/字距微调，缺少 condensed/numeric 气质。

## [S2] Design

### 2.1 提醒通知（当天 09:00 一条）

| 契约 | 行为 |
|---|---|
| 触发时刻 | 事件目标日（无重复）或**下次重复日**（有重复）的 **本地时区 09:00** |
| 条数 | 每次目标日 **1 条**；无「前一天」等第二条 |
| 通知内容 | 标题：事件 title；正文：`今天 · <label 或 还有 0 天 / 已经过 N 天>` 简短文案，如「今天是「周末」」/「今天：周末」 |
| 点击 | 打开 `MainActivity`（深链 detail 可选 v1 只进主页） |
| 权限 | API 33+ 运行时 `POST_NOTIFICATIONS`；拒绝则开关仍可存，但保存时 Toast 提示去开通知 |
| 渠道 | id `nod_reminders`，名称「纪念日提醒」，IMPORTANCE_DEFAULT |
| 调度 | **WorkManager** `OneTimeWorkRequest`，`initialDelay` = now→目标 09:00；unique work name `remind_<eventId>` |
| 为何不用精确 Alarm | 免 `SCHEDULE_EXACT_ALARM` 商店/系统限制；09:00 纪念日提醒可接受近准时（WM 窗口/打盹延迟） |
| 开关 ON 并保存 | `schedule` 当前 `resolveNextDate`/锚点日 09:00 |
| 开关 OFF 或删除事件 | `cancel` unique work |
| 重复事件到点后 | Worker 发完通知后，若 `repeatUnit != NONE` 再 `schedule` 下一周期；`NONE` 不再排 |
| 启动/进应用 | 不强制全量重排（WM 已持久化）；`App.onCreate` 创建通知渠道 |
| 时区 | 按设备本地日历日 09:00；系统时区变更 v1 不做监听（OUT OF SCOPE） |
| 校验 | JVM 测 `nextReminderMillis(anchor, unit, interval, now)` 纯函数（09:00 对齐、过去则下一天/下周期） |

Manifest 新增：`POST_NOTIFICATIONS`。不新增存储等权限。

UI：`strings` 去掉「通知能力将在后续版本提供」；提醒 hint 改为「将在目标日 09:00 提醒一次」。详情「已开启（通知后续版本）」→「已开启」。

### 2.2 主数字 Barlow Condensed

| 契约 | 行为 |
|---|---|
| 字体 | **Barlow Condensed**（OFL），打包 `res/font/barlow_condensed_bold.ttf`（及 Medium 可选） |
| 应用点 | `Theme.NodDisplayNumber` / 首页轮播大数字 / 详情主数字 |
| 样式 | `FontFamily` 指向资源字体，`fontWeight` 与字形一致（Bold），保留/微调 letter-spacing |
| 正文 | 仍用系统/中文栈，不替换 |
| 获取 | 字体文件入库 `app/src/main/res/font/`；附 OFL 说明放 `app/src/main/res/font/OFL.txt` 或 README 一行 |
| 失败回退 | 若下载失败则保持现状 SansSerif，并在 Report 标明 — **必须先确保文件在再宣称完成** |

## [S3] Out of Scope

- 通知渠道设置页、免打扰、自定义提醒时刻
- 时区变更监听、钉屏 AlarmClock UI
- 前一天/多档提醒
- 通知历史、撤销、富媒体
- 字体动态下载、可变字重完整家族（只要 Bold 主数字）
- 点击通知进详情深链（v1 进主页即可）

## Tasks

- [x] T1: 接入 Barlow Condensed Bold 到 `NodDisplayNumber` — acceptance: 首页/详情主数字使用 res/font 字体；OFL 说明存在 (covers: S2.2)
- [x] T2: 通知渠道 + 运行时权限请求 — acceptance: API33+ 启动请求通知权限；渠道创建；文案更新 (covers: S2.1)
- [x] T3: `ReminderScheduler` + `ReminderWorker` + 09:00 纯函数与单测 — acceptance: `nextReminderMillis` 单测通过；schedule/cancel 可被仓库调用 (covers: S2.1)
- [x] T4: 仓库/UI 接线（保存开关、删除、重复续期） — acceptance: 开提醒保存后有 unique work；关/删后 cancel；Worker 发通知后重复事件续期 (covers: S2.1; depends: T3)
- [x] T5: `assembleDebug` + `testDebugUnitTest` — acceptance: 全绿 (covers: S2; depends: T1–T4)
- [x] T6: 安装测试机并人工点检提醒与字体 — acceptance: 大数字为 Barlow Condensed；开提醒可触发（可用改系统时间/短 initialDelay 调试或说明验证方式） (covers: S2; depends: T5)
