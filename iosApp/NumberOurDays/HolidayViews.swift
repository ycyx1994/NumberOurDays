import SwiftUI
import Shared

/// Legal-holiday entry + list. Domain math stays in Shared.
struct HolidayEntryCard: View {
    let countdown: HolidayCountdown?

    var body: some View {
        if let countdown {
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("法定假日")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundStyle(NodTheme.accent)
                    Text(countdown.period.name)
                        .font(.system(size: 15, weight: .bold))
                        .foregroundStyle(NodTheme.n900)
                }
                Spacer()
                Text(
                    countdown.status == HolidayStatus.ongoing
                        ? "假期中"
                        : "还有 \(countdown.daysUntilStart) 天"
                )
                .font(.system(size: 22, weight: .bold, design: .rounded))
                .foregroundStyle(NodTheme.accent)
            }
            .padding(12)
            .background(NodTheme.n50)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(NodTheme.n100, lineWidth: 1)
            )
        } else {
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("法定假日")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundStyle(NodTheme.n300)
                    Text("今年没有节假日咯😭")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundStyle(NodTheme.n500)
                }
                Spacer()
                Text("点我查看放假安排")
                    .font(.system(size: 12))
                    .foregroundStyle(NodTheme.n300)
            }
            .padding(12)
            .background(NodTheme.n50)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(NodTheme.n100, lineWidth: 1)
            )
        }
    }
}

struct HolidayListView: View {
    @Environment(\.dismiss) private var dismiss

    private let groups: [HolidayYearGroup]
    /// 已过完的年份默认收起
    @State private var collapsedYears: Set<Int>

    init() {
        let groups = HolidayCalculator.shared.yearGroups(
            periods: LegalHolidays.shared.periods(),
            today: DateCalculator.shared.today(),
        )
        self.groups = groups
        _collapsedYears = State(initialValue: Set(groups.filter { $0.allPast }.map { Int($0.year) }))
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                LazyVStack(spacing: 8) {
                    ForEach(groups, id: \.year) { group in
                        let year = Int(group.year)
                        let expanded = !collapsedYears.contains(year)

                        YearGroupHeader(year: year, expanded: expanded) {
                            if expanded {
                                collapsedYears.insert(year)
                            } else {
                                collapsedYears.remove(year)
                            }
                        }
                        if expanded {
                            ForEach(group.rows, id: \.identity) { row in
                                HolidayRowView(row: row)
                            }
                        }
                    }
                }
                .padding(16)
            }
            .background(NodTheme.n0)
            .navigationTitle("法定假日")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                if #available(iOS 27.0, *) {
                    ToolbarItem(placement: .topBarPinnedTrailing) {
                        Button {
                            dismiss()
                        } label: {
                            Label("完成", systemImage: "checkmark")
                        }
                    }
                } else {
                    ToolbarItem(placement: .confirmationAction) {
                        Button {
                            dismiss()
                        } label: {
                            Label("完成", systemImage: "checkmark")
                        }
                    }
                }
            }
        }
    }
}

/// 假日名会跨年重复（2026/2027 各有一套元旦…国庆），identity 必须带上放假首日才唯一。
private extension HolidayListRow {
    var identity: String { "\(period.name)@\(period.start)" }
}

private struct YearGroupHeader: View {
    let year: Int
    let expanded: Bool
    let onToggle: () -> Void

    var body: some View {
        Button(action: onToggle) {
            HStack {
                Text(verbatim: "\(year) 年")
                    .font(.system(size: 13, weight: .semibold))
                    .foregroundStyle(NodTheme.n500)
                Spacer()
                Image(systemName: expanded ? "chevron.down" : "chevron.right")
                    .font(.system(size: 11, weight: .semibold))
                    .foregroundStyle(NodTheme.n300)
            }
            .padding(.horizontal, 4)
            .padding(.vertical, 8)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

private struct HolidayRowView: View {
    let row: HolidayListRow

    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text(row.period.name)
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundStyle(NodTheme.n900)
                Text(verbatim: "\(row.period.start) ~ \(row.period.end) · 放假 \(row.vacationDays) 天")
                    .font(.system(size: 12))
                    .foregroundStyle(NodTheme.n500)
            }
            Spacer()
            Text(statusText)
                .font(.system(size: 13, weight: .medium))
                .foregroundStyle(row.past ? NodTheme.n300 : NodTheme.accent)
        }
        .padding(12)
        .background(row.past ? NodTheme.n50.opacity(0.7) : NodTheme.n50)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }

    private var statusText: String {
        if row.ongoing { return "假期中" }
        if row.past { return "已过 \(row.daysPastEnd) 天" }
        return "还有 \(row.daysUntilStart) 天"
    }
}
