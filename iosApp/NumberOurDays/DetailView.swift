import SwiftUI
import Shared

struct DetailView: View {
    @ObservedObject var store: EventStore
    let eventId: String
    @State private var unit: DurationUnit = .day
    @State private var showEdit = false
    @State private var showDeleteConfirm = false
    @Environment(\.dismiss) private var dismiss

    private var event: DayEvent? { store.event(id: eventId) }

    var body: some View {
        ScrollView {
            if let event {
                let d = store.display(for: event)
                VStack(alignment: .leading, spacing: 16) {
                    hero(d)

                    HStack(spacing: 8) {
                        unitChip("天", .day)
                        unitChip("月", .month)
                        unitChip("年", .year)
                    }
                    .frame(maxWidth: .infinity)

                    VStack(spacing: 0) {
                        metaRow("重复", d.repeatLabel ?? "不重复")
                        if let next = d.nextDate {
                            metaRow("下一次", next)
                        }
                        if let elapsed = elapsedParts(d), elapsed.value != 0 || !elapsed.rest.isEmpty {
                            metaRow("已过", "\(elapsed.value) \(elapsed.unitLabel)\(elapsed.rest.isEmpty ? "" : " \(elapsed.rest)")")
                        }
                        metaRow("状态", d.label)
                        metaRow(
                            "提醒",
                            d.remindEnabled
                                ? "已开 · \(d.remindHour):\(String(format: "%02d", d.remindMinute))"
                                : "未开启"
                        )
                        if let note = d.note, !note.isEmpty {
                            metaRow("备注", note)
                        }
                    }
                    .background(NodTheme.n50)
                    .clipShape(RoundedRectangle(cornerRadius: 12))

                    Button(role: .destructive) {
                        showDeleteConfirm = true
                    } label: {
                        Text("删除")
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 12)
                            .background(NodTheme.n50)
                            .foregroundStyle(NodTheme.accent)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                    }
                    .confirmationDialog("删除后无法恢复，确定删除吗？", isPresented: $showDeleteConfirm, titleVisibility: .visible) {
                        Button("删除", role: .destructive) {
                            store.remove(id: eventId)
                            dismiss()
                        }
                        Button("取消", role: .cancel) {}
                    }
                }
                .padding(16)
            } else {
                Text("事件不存在")
                    .foregroundStyle(NodTheme.n500)
                    .padding(24)
            }
        }
        .background(NodTheme.n0)
        .navigationTitle(event?.title ?? "详情")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button("编辑") { showEdit = true }
            }
        }
        .sheet(isPresented: $showEdit) {
            EditView(store: store, eventId: eventId)
        }
    }

    private func hero(_ d: DayEventDisplay) -> some View {
        let duration = durationParts(d)
        return VStack(spacing: 8) {
            Text("目标")
                .font(.system(size: 11))
                .foregroundStyle(NodTheme.n500)
            Text(d.title)
                .font(.system(size: 17, weight: .bold))
                .foregroundStyle(NodTheme.n900)
            // Big number + remaining "X 月 X 天" like Android DayCountHero
            HStack(alignment: .firstTextBaseline, spacing: 6) {
                Text("\(duration.value)")
                    .font(.system(size: 52, weight: .bold, design: .rounded))
                    .foregroundStyle(NodTheme.accent)
                if !duration.suffix.isEmpty {
                    Text(duration.suffix)
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundStyle(NodTheme.n700)
                }
            }
            Text(d.date)
                .font(.system(size: 12))
                .foregroundStyle(NodTheme.n500)
        }
        .frame(maxWidth: .infinity)
        .padding(20)
        .background(NodTheme.n50)
        .clipShape(RoundedRectangle(cornerRadius: 16))
    }

    private func durationParts(_ d: DayEventDisplay) -> (value: Int, suffix: String) {
        let target = DateCalculator.shared.detailTargetDate(event: d)
        let breakdown = DateCalculator.shared.convertDuration(
            from: DateCalculator.shared.today(),
            to: target
        )
        let shown = DateCalculator.shared.formatDuration(b: breakdown, unit: unit)
        var parts: [String] = [shown.unitLabel, shown.rest].filter { !$0.isEmpty }
        return (Int(shown.value), parts.joined(separator: " "))
    }

    private func elapsedParts(_ d: DayEventDisplay) -> (value: Int32, unitLabel: String, rest: String)? {
        let anchor = DateCalculator.shared.parse(text: d.date) ?? DateCalculator.shared.today()
        let today = DateCalculator.shared.today()
        let bd = DateCalculator.shared.elapsedSince(anchor: anchor, today: today)
        let shown = DateCalculator.shared.formatDuration(b: bd, unit: unit)
        return (shown.value, shown.unitLabel, shown.rest)
    }

    private func unitChip(_ title: String, _ value: DurationUnit) -> some View {
        Button {
            unit = value
        } label: {
            Text(title)
                .font(.system(size: 13, weight: .semibold))
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .background(unit == value ? NodTheme.accent : NodTheme.n50)
                .foregroundStyle(unit == value ? NodTheme.n0 : NodTheme.n700)
                .clipShape(Capsule())
        }
    }

    private func metaRow(_ label: String, _ value: String) -> some View {
        HStack {
            Text(label)
                .foregroundStyle(NodTheme.n500)
                .font(.system(size: 13))
            Spacer()
            Text(value)
                .foregroundStyle(NodTheme.n900)
                .font(.system(size: 14))
                .multilineTextAlignment(.trailing)
        }
        .padding(.horizontal, 14)
        .padding(.vertical, 12)
    }
}
