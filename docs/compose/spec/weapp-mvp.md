---
feature: weapp-mvp
status: delivered
updated: 2026-10-14
branch: feature/weapp-mvp
commits: e822612..4f71a0c
---

# 原生微信小程序 MVP

## Report

**What was built** — 主仓新增原生微信小程序 `miniprogram/`（AppID 同旧 Taro：`wxea507de2dccbf14a`），四页：首页 / 详情 / 编辑 / 设置。领域经 `WeappDomain`（Kotlin/JS `@JsExport`）走同一套 `DateCalculator` / `DayEventCodec`；存储为 `wx.setStorage` + 备份 schema v1。假日与推送未做。

**Verification** — `:shared:jvmTest` PASS；`scripts/weapp-smoke.js` PASS（含 tab 标题与 targetDuration）；`:app:assembleDebug` PASS（未伤 Android）。

**Journey log** — 1) ProductionLibrary 产物几乎为空，改用 developmentLibrary +  vendored `@js-joda`。2) 手写 JSON 在 tab/反斜杠标题下会丢列表，改为 kotlinx.serialization DTO。3) 详情已过不能用 `/30` 启发式，必须走 `convertDuration`/`formatDuration`。4) hero 大数字要绑 `target.value` 而非 `display.days`。


## [S1] Problem

产品已有 Android（Compose）与 iOS（SwiftUI），领域逻辑在 KMP `:shared` 并已可编译为 JS。缺少微信小程序端；旧 Taro 仓算法与现产品分叉，不宜继续双轨。需要在主仓新建**原生微信小程序**，规则走 KMP/JS，避免第三套日期/备份实现。

## [S2] Design

### 范围（MVP）

| 做 | 不做 |
|---|---|
| 首页列表（星标置顶 + 组内倒数日期） | 法定假日页 / 假日卡片 |
| 详情（天/月/年 + 已过联动） | 本地通知 / 订阅消息 |
| 添加 / 编辑（标题、日期、重复、备注） | 云同步、账号 |
| 本地存储 + JSON 备份导入导出 | 分类、封面、多主题 |

### 架构

```
miniprogram/          原生小程序（WXML/WXSS/JS）
  libs/shared/        由 :shared Kotlin/JS 构建拷入
  domain/adapter.js   对 shared 导出的薄封装（稳定 API 面）
  pages/index|detail|edit|settings
  storage/events.js   wx.setStorage + DayEventCodec JSON
shared/               唯一业务源（commonMain → android/ios/js）
```

- **不**引入 Taro / uni-app；**不**改 Android/iOS 行为。
- 备份 schema **v1** 与 `DayEventCodec` 一致（`schemaVersion: 1`）。
- 列表排序规则与 `DateCalculator.sortEvents` 相同：星标优先，组内 rank + 日期。

### 数据

- 键：`nod:events`（`DayEvent[]` 明文 JSON 字符串或对象数组，与 codec 往返兼容）
- `id` 生成：与 `createEventId` 同形（时间戳 + 随机）
- 空库不强制 demo（可选播种周末/新年，与 `DemoEvents` 一致则优先调 JS）

### UI 要点

- 纸色 token 对齐：`#FAF7F2` / `#D4553A` / `#1C1916`
- 首页：可选简易 hero 轮播或首条大数字 + 全部列表；必须有添加入口
- 详情：单位切换「天/月/年」，已过随同一单位；底部删除（确认）
- 编辑：日期 picker、重复（默认天）、备注

### KMP JS 契约

`adapter.js` 只暴露稳定面，避免小程序直接依赖 Kotlin 导出细节：

- `sortDisplays(events, today)` / `computeDisplay(event, today)`
- `serialize(events)` / `parseBackup(text)` / `merge(local, incoming)`
- `validateForm(...)` / `formatDate` / `parseDate`
- `nextReminderMillis` **本 MVP 不接提醒 UI**（API 可导出备用）

构建：`scripts/build-weapp-shared.sh` → `shared:compileKotlinJs` 产物拷贝至 `miniprogram/libs/shared/`。

### 测试边界

- 逻辑以 `:shared` 的 `jsNodeTest` / `jvmTest` 为准，不在小程序里重测算法。
- 小程序侧可有简单 `domain/adapter` 自检脚本（Node require 构建产物算倒数）。

## [S3] Out of Scope

- 旧 Taro 仓合并、RN、H5
- 订阅消息 / 推送
- 法定假日 UI
- 上线审核、AppID 绑定（可留 `project.config.json` 占位）

## Tasks

- [x] T1: `miniprogram/` 工程骨架 + `project.config.json` + 构建脚本拷贝 JS — acceptance: 目录可被微信开发者工具识别；脚本产出 libs（covers: S2）
- [x] T2: `domain/adapter.js` 接通 KMP JS（sort/display/codec/validate） — acceptance: Node 冒烟脚本对样例事件算对倒数与 JSON 往返（covers: S2; depends: T1）
- [x] T3: 首页 + 详情四页交互与存储 — acceptance: 本地增删改查、排序与详情单位切换行为与契约一致（covers: S2; depends: T2）
- [x] T4: 编辑/设置（备份导入导出） — acceptance: 导出 JSON 可被 parseBackup 解析；导入 merge/替换更新列表（covers: S2; depends: T3）
