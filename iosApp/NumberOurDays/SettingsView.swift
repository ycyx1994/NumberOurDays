import SwiftUI
import Shared

struct SettingsView: View {
    @ObservedObject var store: EventStore
    @Environment(\.dismiss) private var dismiss
    @State private var importText = ""
    @State private var message: String?

    var body: some View {
        NavigationStack {
            Form {
                Section("本地数据") {
                    LabeledContent("纪念日条数", value: "\(store.events.count)")
                }
                Section("备份") {
                    Button("导出到剪贴板") {
                        UIPasteboard.general.string = store.exportJSON()
                        message = "已复制 JSON"
                    }
                    Button("从剪贴板导入（合并）") {
                        if let text = UIPasteboard.general.string {
                            store.importJSON(text, replace: false)
                            message = store.message ?? "导入完成"
                        }
                    }
                    Button("从剪贴板导入（覆盖）", role: .destructive) {
                        if let text = UIPasteboard.general.string {
                            store.importJSON(text, replace: true)
                            message = store.message ?? "已覆盖导入"
                        }
                    }
                }
                Section("调试") {
                    Button("一分钟后提醒（本地通知）") {
                        Task {
                            await ReminderScheduler.shared.requestAuthorization()
                            ReminderScheduler.shared.scheduleDebugReminder(after: 60)
                            message = "已排定，约 60 秒后通知"
                        }
                    }
                }
                Section("危险操作") {
                    Button("清空全部", role: .destructive) {
                        store.clearAll()
                        message = "已清空"
                    }
                }
                if let message {
                    Section {
                        Text(message)
                            .foregroundStyle(NodTheme.n500)
                    }
                }
            }
            .navigationTitle("设置")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("完成") { dismiss() }
                }
            }
        }
    }
}
