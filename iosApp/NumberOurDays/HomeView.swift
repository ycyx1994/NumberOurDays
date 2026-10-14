import SwiftUI
import Shared

struct HomeView: View {
    @ObservedObject var store: EventStore
    @State private var path = NavigationPath()
    @State private var showSettings = false
    @State private var showAdd = false
    @State private var showHolidays = false

    private var displays: [DayEventDisplay] { store.sortedDisplays() }

    private var holidayCountdown: HolidayCountdown? {
        HolidayCalculator.shared.nextOrOngoing(
            periods: LegalHolidays.shared.periods(),
            today: DateCalculator.shared.today(),
        )
    }

    private var holidayEntry: some View {
        Button {
            showHolidays = true
        } label: {
            HolidayEntryCard(countdown: holidayCountdown)
        }
        .buttonStyle(.plain)
    }

    var body: some View {
        NavigationStack(path: $path) {
            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    if !displays.isEmpty {
                        TabView {
                            ForEach(displays, id: \.id) { item in
                                EventHeroCard(display: item)
                                    .onTapGesture {
                                        path.append(item.id)
                                    }
                            }
                        }
                        .tabViewStyle(.page(indexDisplayMode: .never))
                        .frame(height: 220)
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

                    holidayEntry
                        .padding(.top, 8)

                    Text("全部纪念日")
                        .font(.system(size: 13))
                        .foregroundStyle(NodTheme.n500)
                        .padding(.top, 8)

                    VStack(spacing: 0) {
                        ForEach(displays, id: \.id) { item in
                            EventRow(display: item) {
                                path.append(item.id)
                            } onToggle: {
                                store.toggleStar(id: item.id)
                            }
                            if item.id != displays.last?.id {
                                Divider().background(NodTheme.n100)
                            }
                        }
                    }
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 96)
            }
            .background(NodTheme.n0)
            .navigationDestination(for: String.self) { id in
                DetailView(store: store, eventId: id)
            }
            .navigationTitle("数日子")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        showSettings = true
                    } label: {
                        Image(systemName: "gearshape")
                    }
                }
            }
            .sheet(isPresented: $showSettings) {
                SettingsView(store: store)
            }
            .sheet(isPresented: $showAdd) {
                EditView(store: store, eventId: nil)
            }
            .overlay(alignment: .bottomTrailing) {
                Button {
                    showAdd = true
                } label: {
                    Image(systemName: "plus")
                        .font(.system(size: 20, weight: .semibold))
                        .foregroundStyle(NodTheme.n0)
                        .frame(width: 56, height: 56)
                        .background(NodTheme.accent, in: Circle())
                }
                .padding(24)
            }
        }
    }
}

private struct EventHeroCard: View {
    let display: DayEventDisplay

    var body: some View {
        VStack(spacing: 8) {
            Text(headline)
                .font(.system(size: 11))
                .foregroundStyle(NodTheme.n500)
            Text(display.title)
                .font(.system(size: 17, weight: .bold))
                .foregroundStyle(NodTheme.n900)
                .lineLimit(1)
            Text("\(display.days)")
                .font(.system(size: 56, weight: .bold, design: .rounded))
                .foregroundStyle(NodTheme.accent)
            Text("天 · \(display.date)")
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
    let onOpen: () -> Void
    let onToggle: () -> Void

    var body: some View {
        HStack(spacing: 8) {
            Button(action: onToggle) {
                Image(systemName: display.starred ? "star.fill" : "star")
                    .foregroundStyle(display.starred ? NodTheme.accent : NodTheme.n300)
            }
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
        .onTapGesture(perform: onOpen)
    }

    private var subtitle: String {
        if let repeatLabel = display.repeatLabel {
            return "\(display.date) · \(repeatLabel)"
        }
        let kindText = display.kind == EventKind.elapsed ? "已过" : "倒数"
        return "\(display.date) · \(kindText)"
    }
}
