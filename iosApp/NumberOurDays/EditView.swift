import SwiftUI
import Shared

struct EditView: View {
    @ObservedObject var store: EventStore
    let eventId: String?

    @Environment(\.dismiss) private var dismiss
    @State private var title = ""
    @State private var dateText = ""
    @State private var note = ""
    @State private var repeatOn = false
    @State private var repeatUnit: RepeatUnit = .none
    @State private var repeatInterval = 1
    @State private var remindOn = false
    @State private var remindHour = 8
    @State private var remindMinute = 0
    @State private var errorText: String?

    var body: some View {
        NavigationStack {
            Form {
                Section("标题") {
                    TextField("最多 30 字", text: $title)
                }
                Section("日期") {
                    TextField("YYYY-MM-DD", text: $dateText)
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled()
                }
                Section("重复") {
                    Toggle("开启重复", isOn: $repeatOn)
                    if repeatOn {
                        Picker("单位", selection: $repeatUnit) {
                            Text("天").tag(RepeatUnit.day)
                            Text("周").tag(RepeatUnit.week)
                            Text("月").tag(RepeatUnit.month)
                            Text("年").tag(RepeatUnit.year)
                        }
                        Stepper("间隔 \(repeatInterval)", value: $repeatInterval, in: 1...99)
                    }
                }
                Section("提醒") {
                    Toggle("开启提醒", isOn: $remindOn)
                    if remindOn {
                        Stepper("小时 \(remindHour)", value: $remindHour, in: 0...23)
                        Stepper("分钟 \(remindMinute)", value: $remindMinute, in: 0...59)
                    }
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
                    Button("取消") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("保存") { save() }
                }
            }
            .onAppear(perform: loadExisting)
        }
    }

    private func loadExisting() {
        guard let eventId, let e = store.event(id: eventId) else {
            dateText = DateCalculator.shared.format(date: DateCalculator.shared.today())
            return
        }
        title = e.title
        dateText = e.date
        note = e.note ?? ""
        repeatOn = e.repeatUnit != RepeatUnit.none
        repeatUnit = e.repeatUnit
        repeatInterval = Int(e.repeatInterval)
        remindOn = e.remindEnabled
        remindHour = Int(e.remindHour)
        remindMinute = Int(e.remindMinute)
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
            remindHour: Int32(remindHour),
            remindMinute: Int32(remindMinute),
            createdAt: existing?.createdAt ?? now,
            updatedAt: now
        )
        store.upsert(event)
        dismiss()
    }
}
