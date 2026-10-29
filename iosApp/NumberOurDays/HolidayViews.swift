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

    private var rows: [HolidayListRow] {
        HolidayCalculator.shared.listRows(
            periods: LegalHolidays.shared.periods(),
            today: DateCalculator.shared.today(),
        )
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                LazyVStack(spacing: 8) {
                    ForEach(rows, id: \.period.name) { row in
                        HolidayRowView(row: row)
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

private struct HolidayRowView: View {
    let row: HolidayListRow

    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text(row.period.name)
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundStyle(NodTheme.n900)
                Text("\(row.period.start) ~ \(row.period.end) · 放假 \(row.vacationDays) 天")
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
