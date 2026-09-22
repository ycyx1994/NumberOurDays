---
feature: notification-deeplink
status: delivered
updated: 2026-09-22
branch: feature/notification-deeplink
commits: 7fa9ed7..2f0458e
---

# Notification Deep Link to Detail

## Report

**What was built** — 提醒通知 Intent 携带 `EXTRA_EVENT_ID`，点击后进入对应 `detail/{id}`；`singleTop` + `onNewIntent` 处理已在前台时的通知；返回栈可回首页。

**Verification** — `testDebugUnitTest` PASS · `assembleRelease` PASS · adb install PASS

**Journey log** — 通知深链用 activity extra + 导航 route 即可，不必上 NavDeepLink XML；前台二次通知通过 `onNewIntent` 重设 content 保证跳转。

## [S1] Problem

点提醒通知只打开首页，用户还要再在列表里找事件。

## [S2] Design

| 契约 | 行为 |
|---|---|
| Pending Intent | 携带 `EXTRA_EVENT_ID` |
| 启动 | `MainActivity` 读取 extra；已有任务则 `onNewIntent` 更新并导航 |
| 导航 | 有 id → `detail/{id}`（栈底可保留 home 以便返回）；无 id → home |
| 事件已删 | 详情页既有「事件不存在」空态 |
| launchMode | `singleTask` / `singleTop`，避免每次通知叠 Activity |
| 兼容 | 主页/设置入口不变；无 extra 时行为与现网一致 |

## [S3] Out of Scope

- 通知内操作按钮（稍后提醒/完成）
- 分享 PendingIntent、多通知合组

## Tasks

- [x] T1: NotificationHelper extra + MainActivity/导航接线 — acceptance: 通知启动可到对应 detail；返回可到 home (covers: S2)
- [x] T2: 构建 + 既有单测 — acceptance: testDebugUnitTest + assembleRelease 通过 (covers: S2; depends: T1)
