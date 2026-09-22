---
feature: release-date-fix
status: in-progress
updated: 2026-09-22
branch: feature/release-date-fix
commits: 751e3c9..<TBD>
---

# Release Hardening + Month-End Date Fix

## Report

## [S1] Problem

1. **无法出 Release 包**：无签名配置、未开 minify，只有 debug APK，不适合长期自用分发。
2. **月末日期漂移**：`DateCalculator.resolveNextDate` 用 `plusMonths` 迭代，1/31 每月重复会变成 2/28 → 3/28 → 4/28…，展示「下次」与提醒调度都会错。

## [S2] Design

### 2.1 月末 / 闰日稳定步进

- 新增纯函数（可放 `DateCalculator` 内部或同文件）：
  - `plusMonthsFromAnchor(anchor, monthsToAdd)`：目标年月 = `YearMonth.from(anchor).plusMonths(monthsToAdd)`，日 = `min(anchor.dayOfMonth, 目标月天数)`。
  - `plusYearsFromAnchor(anchor, yearsToAdd)`：目标年 = anchor.year + years，日 = `min(anchor.dayOfMonth, 目标年该月天数)`（2/29 → 平年 2/28，闰年仍 2/29）。
- `resolveNextDate`：DAY/WEEK 可继续 `anchor.plusDays/Weeks(n * step)`；**MONTH/YEAR 必须从 anchor 按 n*step 一次算出**，禁止 `current.plusMonths` 迭代。
- 保持 `NONE` 返回锚点；`ref` 语义不变（返回 ≥ ref 的下一次，当天返回当天）。
- **测试**（JVM）：
  - 1/31 + 每 1 月：→ 2/28（或 2/29），**下一次是 3/31**（不是 3/28）
  - 1/31 + 每 1 年 → 1/31
  - 2/29 + 每 1 年：平年 → 2/28；闰年 → 2/29
  - 既有重复/非重复用例不回归（可抽 `ResolveNextDateTest`）

### 2.2 Release 签名与打包

| 项 | 约定 |
|---|---|
| Keystore | 本地生成 `app/nod-release.keystore`（RSA 2048、有效期 ≥ 10000 天），alias `nod` |
| 凭证 | `keystore.properties`（项目根，**gitignore**）：`storeFile` / `storePassword` / `keyAlias` / `keyPassword`；另提供 `keystore.properties.example` 无真实密码 |
| Gradle | `app/build.gradle.kts` 读取 properties 配 `signingConfigs.release`；缺失时 `assembleRelease` 失败并提示如何创建 |
| buildType release | `isMinifyEnabled = true`，`isShrinkResources = true`，`proguard-android-optimize.txt` + 项目 `proguard-rules.pro`（保 Compose/JSON/org.json、Parcelable 等标准规则） |
| 版本 | `versionCode = 2`，`versionName = "1.1.0"` |
| 产物 | `app/build/outputs/apk/release/app-release.apk`（已签名、可 `adb install`） |
| 安全 | keystore / properties / 密码不进 git；README 一段说明如何本地配置 |

不上传 Play App Signing、不配 CI 发布。

## [S3] Out of Scope

- 数据层 Room/DataStore、提醒自定义时刻
- Play 上架资料、AAB 优化轨道
- 密钥轮换 / 上传密钥管理流程文档以外的内容

## Tasks

- [ ] T1: `resolveNextDate` 稳定月/年步进 + 回归测试 — acceptance: 1/31 月序列为 …2/28, 3/31；2/29 年规则正确；既有单测仍绿 (covers: S2.1)
- [ ] T2: 生成 keystore + properties 模板 + gitignore — acceptance: keystore 在 app/ 且被忽略；example 无真实密码 (covers: S2.2)
- [ ] T3: release signingConfig + minify + proguard + 版本号 — acceptance: `assembleRelease` 产出已签名 APK (covers: S2.2; depends: T2)
- [ ] T4: 全量单测 + debug/release 构建 — acceptance: `testDebugUnitTest` 通过；debug/release 均 assemble 成功 (covers: S2; depends: T1–T3)
- [ ] T5: （可选）release 包安装冒烟 — acceptance: release APK 可安装启动 (covers: S2.2; depends: T3)
