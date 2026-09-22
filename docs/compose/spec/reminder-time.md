---
feature: reminder-time
status: delivered
updated: 2026-09-22
branch: feature/reminder-time
commits: cc010c1..12d1b73
---

# Reminder Time Presets + Worktree Cleanup

## Report

**What was built** — 提醒改为预设时刻 Chip 09:00 / 12:00 / 20:00（仅提醒开关打开时显示），`DayEvent.remindHour/remindMinute` 落库；Room v1→v2 迁移加列；调度与备份 JSON 兼容旧数据（缺省 9:00）。已合并 worktree/分支已清理。

**Verification** —
- `testDebugUnitTest` — PASS — 30+ tests, 0 failures
- `assembleDebug` / `assembleRelease` — PASS
- Independent review (general-4) — PASS — 0 critical
- Device install — pending（验证时 adb 设备离线）

**Journey log** —
- 预设 Chip 比 TimePicker 更轻，模型仍存 hour/minute 便于以后扩展。
- Room 手写 Migration(1,2) + addMigrations，列名与 Entity 必须人工对齐。
- 备份 schemaVersion 仍为 1，字段可选 + 默认值即可向后兼容。
- 已合并 worktree 清理应在 feature 合并后立刻做，避免仓库膨胀。

## [S1] Problem

1. 提醒时刻写死 **当天 09:00**，用户无法改成中午/晚上。
2. 仓库堆积多个已合并 worktree/分支，影响维护。

## [S2] Design

### 2.1 预设提醒时刻

| 契约 | 行为 |
|---|---|
| 可选时刻 | Chip 三档：**09:00 / 12:00 / 20:00** |
| 模型 | `DayEvent.remindHour: Int = 9`，`remindMinute: Int = 0`（仅存 hour/minute；Chip 映射 (9,0)(12,0)(20,0)） |
| 默认 | 09:00（与旧行为一致；迁移旧数据缺字段时默认 9:00） |
| 编辑页 | 「提醒」开关下增加时刻 Chip 行；关掉提醒时可隐藏 Chip 或只读展示（推荐仅 remindOn 时显示） |
| 详情 | Meta「提醒」文案：`已开启 · 09:00` |
| 调度 | `ReminderTiming.nextReminderMillis(..., hourOfDay = remindHour, minuteOfHour = remindMinute)` |
| 编解码 | `DayEventCodec.toJson/migrate` 读写 `remindHour`/`remindMinute`；备份兼容旧 JSON |
| 验证 | 单测：hour/minute 对齐 fire 时刻；旧 JSON 无字段 → 9:00 |

### 2.2 工程清理

- 删除已合并 worktree：`saf-backup` / `ui-polish` / `date-picker` / `reminders-font` / `release-date-fix` / `room-store`（及本地分支若已合并）
- 保留 `feature/reminder-time` worktree 至本轮交付合并后一并清理
- 不强制 `git worktree remove` 未合并分支

## [S3] Out of Scope

- 自由 TimePicker 任意时刻、按事件重复时刻不同
- 提醒音效/铃声、免打扰
- 云同步

## Tasks

- [x] T1: 模型 + 编解码 + ReminderTiming 支持 minute — acceptance: 旧备份可解析；新字段往返；单测通过 (covers: S2.1)
- [x] T2: 编辑页时刻 Chip + 详情文案 — acceptance: 仅提醒开时显示 Chip；保存后详情显示 HH:mm (covers: S2.1; depends: T1)
- [x] T3: 调度接线 — acceptance: schedule 使用事件 remindHour/Minute；12:00/20:00 用例正确 (covers: S2.1; depends: T1)
- [x] T4: 测试 + debug/release 构建 — acceptance: testDebugUnitTest + assembleRelease 通过 (covers: S2.1; depends: T1–T3)
- [x] T5: 清理已合并 worktree/分支 — acceptance: 除本轮 worktree 外无已合并残留 (covers: S2.2)
