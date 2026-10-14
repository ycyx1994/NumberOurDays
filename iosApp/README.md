# NumberOurDays · iOS（SwiftUI + KMP Shared）

业务规则来自 `:shared`（Kotlin Multiplatform）。UI 为原生 SwiftUI。

## 构建

```bash
# 1) 产出 Shared.framework（iOS Simulator arm64）
./iosApp/scripts/build-shared.sh Debug

# 2) 生成 Xcode 工程（需要 xcodegen）
cd iosApp && xcodegen generate

# 3) 编译（本机 Apple Silicon 模拟器）
xcodebuild -project NumberOurDays.xcodeproj \
  -scheme NumberOurDays \
  -destination 'id=<simulator-udid>' \
  -configuration Debug \
  ONLY_ACTIVE_ARCH=YES \
  CODE_SIGNING_ALLOWED=NO \
  build
```

也可用 Xcode 打开 `iosApp/NumberOurDays.xcodeproj` 直接运行。

## 结构

| 文件 | 职责 |
|---|---|
| `EventStore.swift` | 本地 JSON 持久化（`DayEventCodec`），事件 CRUD |
| `HomeView.swift` | 首页轮播 + 列表 + 添加 |
| `DetailView.swift` | 详情（天/月/年、已过、提醒） |
| `EditView.swift` | 添加/编辑 |
| `SettingsView.swift` | 剪贴板备份导出/导入/清空 |
| `Theme.swift` | 纸色设计 token |

## 与 Android 对齐

- 日期计算 / 节假日 / 备份 schema 全部走 `Shared`，勿在 Swift 重写算法。
- 备份 JSON 与 Android `DayEventCodec` 往返兼容。
