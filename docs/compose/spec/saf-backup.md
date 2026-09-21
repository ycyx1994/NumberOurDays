---
feature: saf-backup
status: delivered
updated: 2026-09-21
branch: feature/saf-backup
commits: 7bc820b08ab18ee3a6aeedc7b48875fb6ac8df7f..391c4a0a31c3424f489328e60932407736970ef4
---

# SAF Backup — 真实文件导出/导入

## Report

**What was built** — 设置页备份路径从「假的导出文件」升级为真正的 SAF 文件读写：「导出 JSON 文件」通过 `CreateDocument` 写入 `serialize(events)` 的 UTF-8 JSON（默认名 `numberourdays-backup-YYYYMMDD.json`）；新增「从文件导入」通过 `OpenDocument` 读取后走 `parseBackup`，合法备份弹出合并/覆盖/取消，仅确认后写库。剪贴板导出与粘贴导入保留。纯逻辑抽到 `DayEventCodec` + `BackupTransfer`，便于 JVM 单测。

**Verification** —
- `testDebugUnitTest` — PASS — `BackupTransferTest` 6 tests, 0 failures/errors
- `assembleDebug` — PASS — `app/build/outputs/apk/debug/app-debug.apk`
- `adb -s c2def901 install -r` — PASS — 已安装并启动 `com.numberourdays.app/.uiMainActivity`
- Independent review (general-1) — PASS — 6/6 AC met, 0 critical findings

**Journey log** —
- 本目录原本无 git；先 `git init` + baseline `7bc820b`，再在 `.worktrees/saf-backup` / `feature/saf-backup` 开发，符合 Compose Next 工作区模型。
- 本机 Gradle 不能用 Android Studio JBR（OpenJDK 25）；AGP 需 JDK 17：`JAVA_HOME=/opt/homebrew/opt/openjdk@17` + `GRADLE_USER_HOME=/tmp/gradle-home-nod`。
- JVM 单测碰 `org.json` 时 android.jar 是 stub，必须 `testImplementation("org.json:json:…")`。
- SAF 写文件应用 `openOutputStream(uri, "wt")` 截断，避免部分 provider 上 `"w"` 追加。
- Review 低优先级未修：合并成功文案用 `next.size`（合并后总数）而非 incoming 条数；真机 SAF 全流程人工冒烟（T7）尚未完整走过。

## [S1] Problem

设置页「导出 JSON 文件」只把备份再复制一次到剪贴板，用户必须手动粘贴另存；导入则依赖粘贴大段 JSON。真机上备份/迁移成本高，且文案与行为不一致，属于 P0 半成品。

## [S2] Design

### 行为契约

1. **导出到文件**
   - 入口：设置页主操作「导出 JSON 文件」（替换当前“再次剪贴板”行为）。
   - 使用 SAF `ActivityResultContracts.CreateDocument("application/json")`。
   - 默认文件名：`numberourdays-backup-YYYYMMDD.json`（设备本地日期）。
   - 写入内容：`EventRepository.serialize(events)` 的完整 UTF-8 JSON（`schemaVersion=1`，字段与现有剪贴板备份一致）。
   - 空库：不启动选择器，Toast「暂无数据可导出」。
   - 用户取消选择器：无错误，不提示成功。
   - 写入失败（Uri 不可写等）：展示可读错误，如「导出失败」，不得假装成功。
   - 成功：Toast/文案提示已导出条数，例如「已导出 N 条到文件」。

2. **从文件导入**
   - 入口：设置页新增主操作「从文件导入」。
   - 使用 `ActivityResultContracts.OpenDocument`，MIME：`application/json` 与 `text/plain`（及 `*/*` 兜底以便系统选择器可见 `.json`）。
   - 读取为 UTF-8 文本后走现有 `EventRepository.parseBackup`；非法 JSON / 无有效事件时提示原因，不改本地数据。
   - 有效时弹出确认对话框，展示将写入的条数（及跳过条数），三选项：
     - **合并**：`merge(local, incoming)` 后 `saveAll`（与现有「合并导入」语义一致，按 `updatedAt` 较新者胜）。
     - **覆盖**：`saveAll(incoming)`（与现有「覆盖本地」语义一致）。
     - **取消**：不写库。
   - 不直接改剪贴板导入输入框；文件导入与粘贴导入并行保留。

3. **保留的既有行为**
   - 「导出到剪贴板」不变。
   - 粘贴导入 +「合并导入 / 覆盖本地」不变（合并/覆盖仍可作用于粘贴内容）。
   - 「清空本地数据」不变。
   - 备份 JSON schema 不升级；导入仍接受 `parseBackup` 已支持的形态。

### 实现契约

- I/O 与 JSON 解析逻辑收敛到可测纯函数/小类（`data/BackupTransfer.kt` 内 `BackupTransfer` + `DayEventCodec`）。
- UI 仅负责：注册 launcher、拼默认名、读入后触发确认框、调用 repo 写库、展示 message/Toast。
- 权限：SAF 不需要存储权限；Manifest 不新增权限。
- 字符串进入 `strings.xml`，禁止在新增用户可见文案中散落硬编码（既有硬编码不在本次范围强行全清）。

### 错误与边界

| 场景 | 行为 |
|---|---|
| 导出时本地 0 条 | 不打开选择器，Toast 既有 `nothing_export` |
| CreateDocument 返回 null（取消） | 静默返回 |
| ContentResolver 打开输出流失败 | message：导出失败 |
| OpenDocument 返回 null（取消） | 静默返回 |
| 读入空文本 / 非法 JSON | message 用 `parseBackup` 的错误文案，不写库 |
| 读入有效但 0 条可用 | message「备份中没有有效事件」，不写库 |
| 合并/覆盖成功 | message 报告条数（及跳过说明） |
| 大文件 | 流式读入字符串即可；v1 不做分片 |

### 测试边界

- JVM 单测（不依赖 Android 框架）：
  - `defaultBackupFileName` 形如 `numberourdays-backup-YYYYMMDD.json`。
  - `serialize` → `parseBackup` 往返：事件字段保持；非法日期/空标题被跳过。
  - `merge`：同 id 时 `updatedAt` 较大者胜；本地独有保留；incoming 新 id 追加。
- UI/SAF 真机完整路径不在单测内；验证阶段用 assembleDebug + adb 安装冒烟。

## [S3] Out of Scope

- 系统分享面板 / 导出到其他 App
- 云备份、自动定时备份
- 通知/提醒能力（下一 P0）
- 重写全部既有硬编码文案
- Room/DataStore 迁移
- schemaVersion 2 或加密备份

## Tasks

- [x] T1: 添加 JVM 单测依赖与 `BackupTransfer`/仓库往返单测 — acceptance: `testDebugUnitTest` 通过，覆盖文件名、serialize→parseBackup 往返、merge 语义 (covers: S2)
- [x] T2: 实现 `data/BackupTransfer.kt`（默认文件名 + 基于 Uri 的写/读辅助） — acceptance: 可从 ContentResolver 写 UTF-8 与读 UTF-8；单测覆盖文件名纯函数 (covers: S2; depends: T1)
- [x] T3: 设置页导出改走 SAF CreateDocument — acceptance: 点「导出 JSON 文件」打开系统创建文档；成功写入与 serialize 一致的 JSON；空库/取消/失败行为符合 S2 (covers: S2; depends: T2)
- [x] T4: 设置页新增「从文件导入」（OpenDocument + 合并/覆盖/取消） — acceptance: 选择合法备份后仅在用户确认合并/覆盖时改库；取消/非法文件不改库 (covers: S2; depends: T2)
- [x] T5: 更新 `strings.xml` 与设置页说明文案 — acceptance: 新增入口与成功/失败文案均可从资源读出，无新增硬编码用户可见串 (covers: S2)
- [x] T6: 在 worktree 构建与单测 — acceptance: `assembleDebug` 与 `testDebugUnitTest` 成功 (covers: S2; depends: T1, T3, T4, T5)
- [ ] T7: （可选）adb 安装到测试机并人工冒烟导出/导入 — acceptance: 安装成功；真机可完成一次文件导出与再导入（合并） (covers: S2; depends: T6)
  - 注：安装与启动已完成；SAF 选择器端到端人工点击尚未记录。
