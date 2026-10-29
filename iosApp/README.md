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
| `HomeView.swift` | `NavigationSplitView` 双栏：列表 + 详情（紧凑宽度折叠为单栏） |
| `DetailView.swift` | 详情（天/月/年、已过、提醒） |
| `EditView.swift` | 添加/编辑 |
| `SettingsView.swift` | 剪贴板备份导出/导入/清空 |
| `Theme.swift` | 纸色设计 token |

## iPhone Duo 适配

折叠屏 iPhone：外屏 5.4"（紧凑宽度）、内屏 7.6"（常规宽度），带铰链多种姿态。
官方指南：<https://developer.apple.com/cn/design/human-interface-guidelines/designing-for-iphone-duo>、
[为 iPhone Duo 准备你的 App](https://developer.apple.com/cn/documentation/technologyoverviews/preparing-your-app-for-iphone-duo)。

已落地（按官方要求）：

| 要求 | 实现 |
|---|---|
| 尺寸可调、内屏常规宽度双栏 | 首页改 `NavigationSplitView`：外屏/紧凑宽度单栏推详情，内屏/常规宽度列表+详情并排；选中态驱动详情列 |
| 栏移至侧边（垂直呈现）需图标+标题 | 所有工具栏项改 `Label("标题", systemImage:)`（设置/编辑/取消/保存/完成） |
| 「完成」等主要操作固定尾部 | iOS 27+ 用 `.topBarPinnedTrailing`，低版本回退 `.confirmationAction` |
| 避免固定宽度/固定 iPhone 尺寸 | 主卡高度按 `horizontalSizeClass` 取 220/280，其余为自适应容器 |
| 各姿态填满屏幕 | `Info.plist` 放开横屏（iPhone 横竖屏 + iPad 全向） |
| 深链 | 注册 `numberourdays://` scheme（此前 `onOpenURL` 无 scheme 注册，属死代码） |
| + 号（统一控件） | 唯一控件 `AddEventToolbarItem`（标准 `ToolbarItem` + `Label`）。系统按屏幕形态呈现：直板机进顶部导航栏，Duo 外屏/内屏进右缘竖栏。挂载：紧凑宽度挂侧栏工具栏，常规宽度（双栏同屏）挂详情栏工具栏，避免重复 |

用标准系统容器（`NavigationSplitView`、`List`、`Form`、sheet），系统会自动绕开折叠区/前置相机保留区。

**待真机验证**（本机模拟器无 iPhone Duo / 无法注入点击）：

- 外屏点行 → 推详情；展开内屏 → 双栏并排；折叠过程中布局是否突变
- 展开态 + 号在右缘竖栏的排布（与「编辑」上下顺序、溢出表现）
- 部分折叠时主卡/行是否被折痕遮挡，控件是否可达
- 侧边垂直工具栏形态（外屏与内屏横屏）
- 横竖屏切换、灵动岛/相机保留区避让

**待 Xcode 27.1+ 再做**（本机 Xcode 27.0 SDK 尚无这些 API）：

- `ArrangementView` / `UIArrangementViewController`：主/辅视图随姿态分屏或叠放
- `ReservedRegion` / `GeometryProxy.reservedRegions`：自定义主卡精确避开折叠区
- `toolbarVerticalEdge` / `visibilityPriority` / `ToolbarOverflowMenu`：垂直栏精细化

## 与 Android 对齐

- 日期计算 / 节假日 / 备份 schema 全部走 `Shared`，勿在 Swift 重写算法。
- 备份 JSON 与 Android `DayEventCodec` 往返兼容。
