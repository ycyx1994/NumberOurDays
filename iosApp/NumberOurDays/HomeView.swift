import SwiftUI
import Shared

struct HomeView: View {
    @ObservedObject var store: EventStore
    @Binding var deepLinkEventId: String?
    @Binding var deepLinkHolidays: Bool
    @State private var selectedId: String?
    @State private var showSettings = false
    @State private var showAdd = false
    @State private var showHolidays = false
    /// iPhone Duo 外屏为紧凑宽度，内屏为常规宽度；主卡高度据此调整。
    @Environment(\.horizontalSizeClass) private var sizeClass

    private var displays: [DayEventDisplay] { store.sortedDisplays() }

    private var holidayCountdown: HolidayCountdown? {
        HolidayCalculator.shared.nextOrOngoing(
            periods: LegalHolidays.shared.periods(),
            today: DateCalculator.shared.today(),
        )
    }

    var body: some View {
        NavigationSplitView {
            sidebar
        } detail: {
            detail
        }
        .tint(NodTheme.accent)
        .sheet(isPresented: $showSettings) {
            SettingsView(store: store)
        }
        .sheet(isPresented: $showAdd) {
            EditView(store: store, eventId: nil)
        }
        .sheet(isPresented: $showHolidays) {
            HolidayListView()
        }
        .onChange(of: deepLinkEventId) { newValue in
            guard let id = newValue, store.event(id: id) != nil else { return }
            selectedId = id
            deepLinkEventId = nil
        }
        .onChange(of: deepLinkHolidays) { open in
            guard open else { return }
            showHolidays = true
            deepLinkHolidays = false
        }
        .onChange(of: showHolidays) { open in
            if open { deepLinkHolidays = false }
        }
        .onChange(of: store.events.count) { _ in
            if let id = selectedId, store.event(id: id) == nil {
                selectedId = nil
            }
        }
    }

    // MARK: - Sidebar (list column; the only column on compact width)

    private var sidebar: some View {
        List(selection: $selectedId) {
            if !displays.isEmpty {
                Section {
                    heroCarousel
                        .listRowInsets(EdgeInsets(top: 12, leading: 16, bottom: 0, trailing: 16))
                        .listRowBackground(Color.clear)
                        .listRowSeparator(.hidden)
                }
            }

            Section {
                holidayEntry
                    .listRowInsets(EdgeInsets(top: 12, leading: 16, bottom: 4, trailing: 16))
                    .listRowBackground(Color.clear)
                    .listRowSeparator(.hidden)
            }

            Section {
                ForEach(displays, id: \.id) { item in
                    EventRow(display: item) {
                        store.toggleStar(id: item.id)
                    }
                    .tag(item.id)
                    .listRowInsets(EdgeInsets(top: 0, leading: 16, bottom: 0, trailing: 16))
                    .listRowBackground(Color.clear)
                    .listRowSeparatorTint(NodTheme.n100)
                }
            } header: {
                Text("全部纪念日")
                    .font(.system(size: 13))
                    .foregroundStyle(NodTheme.n500)
                    .textCase(nil)
                    .padding(.top, 8)
            }
        }
        .listStyle(.plain)
        .scrollContentBackground(.hidden)
        .background(NodTheme.n0)
        .navigationTitle("数日子")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button {
                    showSettings = true
                } label: {
                    Label("设置", systemImage: "gearshape")
                }
            }
            // 紧凑宽度（直板机 / Duo 外屏）：+ 挂侧栏工具栏——直板机进顶部导航栏，
            // Duo 外屏由系统排入右缘竖栏。常规宽度（Duo 内屏）双栏同屏，+ 由详情栏
            // 承接（见 detail），此处不渲染以免重复。
            if sizeClass != .regular {
                AddEventToolbarItem {
                    showAdd = true
                }
            }
        }
    }

    private var heroCarousel: some View {
        VStack(spacing: 12) {
            TabView {
                ForEach(displays, id: \.id) { item in
                    EventHeroCard(display: item)
                        .onTapGesture {
                            selectedId = item.id
                        }
                }
            }
            .tabViewStyle(.page(indexDisplayMode: .never))
            .frame(height: heroHeight)
            .background(NodTheme.n50)
            .clipShape(RoundedRectangle(cornerRadius: 16))

            HStack(spacing: 6) {
                ForEach(Array(displays.indices.prefix(6)), id: \.self) { i in
                    Capsule()
                        .fill(i == 0 ? NodTheme.n700 : NodTheme.n300)
                        .frame(width: i == 0 ? 18 : 6, height: 6)
                }
            }
            .frame(maxWidth: .infinity)
        }
    }

    /// iPhone Duo 外屏为紧凑宽度（220），内屏为常规宽度，加高主卡让大屏不空旷。
    private var heroHeight: CGFloat { sizeClass == .regular ? 280 : 220 }

    private var holidayEntry: some View {
        Button {
            showHolidays = true
        } label: {
            HolidayEntryCard(countdown: holidayCountdown)
        }
        .buttonStyle(.plain)
    }

    // MARK: - Detail column (regular width: side-by-side; compact width: pushed)

    @ViewBuilder
    private var detail: some View {
        if let id = selectedId, store.event(id: id) != nil {
            DetailView(store: store, eventId: id, onAdd: { showAdd = true }) {
                selectedId = nil
            }
            .id(id)
        } else {
            placeholder
        }
    }

    private var placeholder: some View {
        VStack(spacing: 10) {
            Image(systemName: "calendar")
                .font(.system(size: 34, weight: .light))
                .foregroundStyle(NodTheme.n300)
            Text("选择一个纪念日")
                .font(.system(size: 15, weight: .semibold))
                .foregroundStyle(NodTheme.n700)
            Text("在左侧列表里点一条查看详情")
                .font(.system(size: 12))
                .foregroundStyle(NodTheme.n500)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(NodTheme.n0)
        .toolbar {
            AddEventToolbarItem {
                showAdd = true
            }
        }
    }
}

/// + 号工具栏项：统一控件。系统按屏幕形态呈现——直板机进顶部导航栏，
/// Duo 外屏/内屏进右缘竖栏（标准工具栏项会被系统自动排到侧边）。
struct AddEventToolbarItem: ToolbarContent {
    let action: () -> Void

    var body: some ToolbarContent {
        if #available(iOS 27.0, *) {
            ToolbarItem(placement: .topBarTrailing) {
                Button(action: action) {
                    Label("添加纪念日", systemImage: "plus")
                }
            }
            .visibilityPriority(.high)
        } else {
            ToolbarItem(placement: .topBarTrailing) {
                Button(action: action) {
                    Label("添加纪念日", systemImage: "plus")
                }
            }
        }
    }
}

private struct EventHeroCard: View {
    let display: DayEventDisplay

    private var heroNumber: String {
        let target = DateCalculator.shared.detailTargetDate(event: display)
        let bd = DateCalculator.shared.convertDuration(from: DateCalculator.shared.today(), to: target)
        let shown = DateCalculator.shared.formatDuration(b: bd, unit: HomeUnitPrefs.unit)
        return String(shown.value)
    }

    private var heroSuffix: String {
        let target = DateCalculator.shared.detailTargetDate(event: display)
        let bd = DateCalculator.shared.convertDuration(from: DateCalculator.shared.today(), to: target)
        let shown = DateCalculator.shared.formatDuration(b: bd, unit: HomeUnitPrefs.unit)
        return [shown.unitLabel, shown.rest].filter { !$0.isEmpty }.joined(separator: " ")
    }

    var body: some View {
        VStack(spacing: 8) {
            Text(headline)
                .font(.system(size: 11))
                .foregroundStyle(NodTheme.n500)
            Text(display.title)
                .font(.system(size: 17, weight: .bold))
                .foregroundStyle(NodTheme.n900)
                .lineLimit(1)
            Text(heroNumber)
                .font(.system(size: 56, weight: .bold, design: .rounded))
                .foregroundStyle(NodTheme.accent)
            Text("\(heroSuffix) · \(display.date)")
                .font(.system(size: 12))
                .foregroundStyle(NodTheme.n500)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(16)
    }

    private var headline: String {
        if let repeatLabel = display.repeatLabel, display.kind == EventKind.repeat {
            return "下一个 · \(repeatLabel)"
        }
        switch display.kind {
        case EventKind.countdown: return "下一个 · 倒数"
        case EventKind.elapsed: return "下一个 · 已过"
        case EventKind.today: return "下一个 · 今天"
        case EventKind.repeat: return "下一个 · 重复"
        default: return "下一个"
        }
    }
}

private struct EventRow: View {
    let display: DayEventDisplay
    let onToggle: () -> Void

    var body: some View {
        HStack(spacing: 8) {
            Button(action: onToggle) {
                Image(systemName: display.starred ? "star.fill" : "star")
                    .foregroundStyle(display.starred ? NodTheme.accent : NodTheme.n300)
            }
            .buttonStyle(.plain)
            .frame(width: 40, height: 40)

            VStack(alignment: .leading, spacing: 4) {
                Text(display.title)
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundStyle(NodTheme.n900)
                    .lineLimit(1)
                Text(subtitle)
                    .font(.system(size: 12))
                    .foregroundStyle(NodTheme.n500)
                    .lineLimit(1)
            }
            Spacer(minLength: 8)
            Text(display.label)
                .font(.system(size: 13, weight: .medium))
                .foregroundStyle(NodTheme.accent)
        }
        .padding(.vertical, 12)
        .contentShape(Rectangle())
    }

    private var subtitle: String {
        if let repeatLabel = display.repeatLabel {
            return "\(display.date) · \(repeatLabel)"
        }
        let kindText = display.kind == EventKind.elapsed ? "已过" : "倒数"
        return "\(display.date) · \(kindText)"
    }
}
