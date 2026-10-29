import SwiftUI
import Shared

struct EditView: View {
    @ObservedObject var store: EventStore
    let eventId: String?

    @Environment(\.dismiss) private var dismiss
    @State private var title = ""
    @State private var date = Date()
    @State private var note = ""
    @State private var repeatOn = false
    @State private var repeatUnit: RepeatUnit = .day
    @State private var repeatInterval = 1
    @State private var remindOn = false
    @State private var remindTime = Calendar.current.date(from: DateComponents(hour: 8, minute: 0)) ?? Date()
    @State private var errorText: String?

    var body: some View {
        NavigationStack {
            Form {
                Section("标题") {
                    TextField("最多 30 字", text: $title)
                }
                Section("日期") {
                    DatePicker(
                        "目标日期",
                        selection: $date,
                        displayedComponents: .date
                    )
                    .datePickerStyle(.compact)
                    .environment(\.locale, Locale(identifier: "zh_CN"))
                }
                Section {
                    Toggle("开启重复", isOn: $repeatOn)
                    if repeatOn {
                        Stepper("每 \(repeatInterval) 次", value: $repeatInterval, in: 1...99)
                        Picker("单位", selection: $repeatUnit) {
                            Text("天").tag(RepeatUnit.day)
                            Text("周").tag(RepeatUnit.week)
                            Text("月").tag(RepeatUnit.month)
                            Text("年").tag(RepeatUnit.year)
                        }
                        .pickerStyle(.segmented)
                    }
                } header: {
                    Text("重复")
                } footer: {
                    if repeatOn {
                        Text("从目标日期起，每 \(repeatInterval) \(unitName)一次")
                            .foregroundStyle(NodTheme.n500)
                    }
                }
                Section {
                    Toggle("开启提醒", isOn: $remindOn)
                    if remindOn {
                        HStack(spacing: 8) {
                            presetButton(title: "08:00", hour: 8, minute: 0)
                            presetButton(title: "12:00", hour: 12, minute: 0)
                            presetButton(title: "20:00", hour: 20, minute: 0)
                        }
                        DatePicker(
                            "自定义时刻",
                            selection: $remindTime,
                            displayedComponents: .hourAndMinute
                        )
                        .datePickerStyle(.wheel)
                        .environment(\.locale, Locale(identifier: "zh_CN"))
                    }
                } header: {
                    Text("提醒")
                }
                Section("备注") {
                    TextField("可选", text: $note)
                }
                if let errorText {
                    Section {
                        Text(errorText)
                            .foregroundStyle(NodTheme.accent)
                    }
                }
            }
            .navigationTitle(eventId == nil ? "添加纪念日" : "编辑")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button {
                        dismiss()
                    } label: {
                        Label("取消", systemImage: "xmark")
                    }
                }
                if #available(iOS 27.0, *) {
                    ToolbarItem(placement: .topBarPinnedTrailing) {
                        Button {
                            save()
                        } label: {
                            Label("保存", systemImage: "checkmark")
                        }
                    }
                } else {
                    ToolbarItem(placement: .confirmationAction) {
                        Button {
                            save()
                        } label: {
                            Label("保存", systemImage: "checkmark")
                        }
                    }
                }
            }
            .onAppear(perform: loadExisting)
            .onChange(of: repeatOn) { on in
                if on && repeatUnit == .none {
                    repeatUnit = .day
                }
            }
        }
    }

    private var unitName: String {
        switch repeatUnit {
        case .day: return "天"
        case .week: return "周"
        case .month: return "月"
        case .year: return "年"
        default: return "天"
        }
    }

    private var dateText: String {
        DateCalculator.shared.format(date: toSharedDate(date))
    }

    private func toSharedDate(_ d: Date) -> Kotlinx_datetimeLocalDate {
        let c = Calendar.current.dateComponents([.year, .month, .day], from: d)
        return Kotlinx_datetimeLocalDate(
            year: Int32(c.year ?? 2026),
            monthNumber: Int32(c.month ?? 1),
            dayOfMonth: Int32(c.day ?? 1)
        )
    }

    private func fromSharedDate(_ s: Kotlinx_datetimeLocalDate) -> Date {
        var c = DateComponents()
        c.year = Int(s.year)
        c.month = Int(s.monthNumber)
        c.day = Int(s.dayOfMonth)
        return Calendar.current.date(from: c) ?? Date()
    }

    private func presetButton(title: String, hour: Int, minute: Int) -> some View {
        let cal = Calendar.current
        let target = cal.date(from: DateComponents(hour: hour, minute: minute)) ?? remindTime
        let selected = {
            let a = cal.dateComponents([.hour, .minute], from: remindTime)
            return a.hour == hour && a.minute == minute
        }()
        return Button {
            remindTime = target
        } label: {
            Text(title)
                .font(.system(size: 13, weight: .semibold))
                .padding(.horizontal, 12)
                .padding(.vertical, 8)
                .background(selected ? NodTheme.accent : NodTheme.n50)
                .foregroundStyle(selected ? NodTheme.n0 : NodTheme.n700)
                .clipShape(Capsule())
        }
        .buttonStyle(.plain)
    }

    private func loadExisting() {
        if let eventId, let e = store.event(id: eventId) {
            title = e.title
            if let parsed = DateCalculator.shared.parse(text: e.date) {
                date = fromSharedDate(parsed)
            }
            note = e.note ?? ""
            repeatOn = e.repeatUnit != RepeatUnit.none
            repeatUnit = e.repeatUnit == RepeatUnit.none ? .day : e.repeatUnit
            repeatInterval = Int(e.repeatInterval)
            remindOn = e.remindEnabled
            var c = DateComponents()
            c.hour = Int(e.remindHour)
            c.minute = Int(e.remindMinute)
            remindTime = Calendar.current.date(from: c) ?? remindTime
        } else {
            date = Date()
            repeatUnit = .day
        }
    }

    private func save() {
        let err = DateCalculator.shared.validateForm(
            title: title,
            date: dateText,
            unit: repeatOn ? repeatUnit : RepeatUnit.none,
            interval: Int32(repeatInterval)
        )
        if let err {
            errorText = err
            return
        }
        let cal = Calendar.current
        let tc = cal.dateComponents([.hour, .minute], from: remindTime)
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        let existing = eventId.flatMap { store.event(id: $0) }
        let event = DayEvent(
            id: existing?.id ?? store.createId(),
            title: title.trimmingCharacters(in: .whitespaces),
            date: dateText,
            note: note.trimmingCharacters(in: .whitespaces).isEmpty
                ? nil
                : note.trimmingCharacters(in: .whitespaces),
            starred: existing?.starred ?? false,
            starRank: existing?.starRank,
            repeatUnit: repeatOn ? repeatUnit : RepeatUnit.none,
            repeatInterval: Int32(repeatInterval),
            remindEnabled: remindOn,
            remindHour: Int32(tc.hour ?? 8),
            remindMinute: Int32(tc.minute ?? 0),
            createdAt: existing?.createdAt ?? now,
            updatedAt: now
        )
        store.upsert(event)
        dismiss()
    }
}
