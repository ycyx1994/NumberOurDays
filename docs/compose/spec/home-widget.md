---
feature: home-widget
status: designed
updated: 2026-09-22
branch: feature/home-widget
commits: 8d39f93..<TBD>
---

# Home Screen Widget (HyperOS-first)

## Report

## [S1] Problem

桌面无法一眼看到最近的纪念日天数，必须打开 App。目标机为澎湃 OS（Xiaomi）。

## [S2] Design

| 契约 | 行为 |
|---|---|
| 形态 | **2×2 / 4×2** 纸质感 RemoteViews widget（澎湃 OS 常见桌面格子） |
| HyperOS | 标准 `AppWidgetProvider`；`previewLayout`；`minResize`/`targetCellWidth` 等；点击进 App；不依赖小米私有推送 SDK |
| 数据 | `DateCalculator.pickNearest`（与首页主卡一致） |
| 点击 | 打开 `MainActivity`（带 `EXTRA_EVENT_ID` 进详情） |
| 更新 | `onUpdate` + 事件变更后 `ACTION_APPWIDGET_UPDATE` 广播 |
| 空库 | 「还没有纪念日」 |
| 样式 | 背景 `n-50`、数字 accent、圆角 12 |
| 尺寸 | 4×2 信息更全；2×2 紧凑大数字 |

## [S3] Out of Scope

- 多尺寸自由缩放列表、Glance/Compose 动效小组件
- 桌面主题/图标包装、widget 配置页
- 多事件横向滑动小组件

## Tasks

- [ ] T1: Widget 布局 + Receiver + Manifest — acceptance: 桌面可添加小组件并显示最近一条 (covers: S2)
- [ ] T2: 事件变更刷新 + 点击进详情 — acceptance: 改数据后 widget 更新；点击进对应详情 (covers: S2; depends: T1)
- [ ] T3: 构建单测 + 装机 — acceptance: 测试与 assembleRelease 通过；USB 机可添加 widget (covers: S2; depends: T1–T2)
