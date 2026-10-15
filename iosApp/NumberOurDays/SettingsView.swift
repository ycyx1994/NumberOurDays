import SwiftUI
import Shared
import UniformTypeIdentifiers

struct SettingsView: View {
    @ObservedObject var store: EventStore
    @Environment(\.dismiss) private var dismiss
    @State private var message: String?
    @State private var exportJSON: String?
    @State private var showExporter = false
    @State private var showImporter = false
    @State private var pendingImport: String?
    @State private var pendingImportNote: String?
    @State private var showImportConfirm = false

    var body: some View {
        NavigationStack {
            Form {
                Section("本地数据") {
                    LabeledContent("纪念日条数", value: "\(store.events.count)")
                }
                Section("首页大卡单位") {
                    Picker("单位", selection: Binding(
                        get: { HomeUnitPrefs.unit },
                        set: { HomeUnitPrefs.unit = $0 }
                    )) {
                        Text("天").tag(DurationUnit.day)
                        Text("月").tag(DurationUnit.month)
                        Text("年").tag(DurationUnit.year)
                    }
                    .pickerStyle(.segmented)
                    Text("首页轮播数字按此单位显示")
                        .font(.footnote)
                        .foregroundStyle(NodTheme.n500)
                }
                Section("备份 · 文件") {
                    Button("导出到文件…") {
                        exportJSON = store.exportJSON()
                        showExporter = true
                    }
                    Button("从文件导入…") {
                        showImporter = true
                    }
                    if let exportJSON {
                        ShareLink(item: exportJSON, preview: SharePreview("NumberOurDays 备份")) {
                            Label("分享 JSON", systemImage: "square.and.arrow.up")
                        }
                    }
                }
                Section("备份 · 剪贴板") {
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
            .fileExporter(
                isPresented: $showExporter,
                document: exportJSON.map { JSONDocument(text: $0) },
                contentType: .json,
                defaultFilename: defaultBackupName()
            ) { result in
                switch result {
                case .success:
                    message = "已导出备份文件"
                case .failure(let err):
                    message = "导出失败：\(err.localizedDescription)"
                }
            }
            .fileImporter(
                isPresented: $showImporter,
                allowedContentTypes: [.json, .plainText],
                allowsMultipleSelection: false
            ) { result in
                guard case .success(let urls) = result, let url = urls.first else {
                    if case .failure(let err) = result {
                        message = "打开失败：\(err.localizedDescription)"
                    }
                    return
                }
                let access = url.startAccessingSecurityScopedResource()
                defer { if access { url.stopAccessingSecurityScopedResource() } }
                guard let text = try? String(contentsOf: url, encoding: .utf8) else {
                    message = "无法读取文件"
                    return
                }
                let pair = DayEventCodec.shared.parseBackup(text: text)
                let events = (pair.first as? [DayEvent]) ?? []
                guard !events.isEmpty else {
                    message = (pair.second as? String) ?? "文件里没有有效纪念日"
                    return
                }
                pendingImport = text
                pendingImportNote = pair.second as? String
                showImportConfirm = true
            }
            .alert("导入备份", isPresented: $showImportConfirm, actions: {
                Button("合并") {
                    if let text = pendingImport {
                        store.importJSON(text, replace: false)
                        message = store.message ?? "已合并导入"
                    }
                    pendingImport = nil
                }
                Button("覆盖", role: .destructive) {
                    if let text = pendingImport {
                        store.importJSON(text, replace: true)
                        message = store.message ?? "已覆盖导入"
                    }
                    pendingImport = nil
                }
                Button("取消", role: .cancel) {
                    pendingImport = nil
                }
            }, message: {
                let n = pendingImportNote.map { "，\($0)" } ?? ""
                Text("将写入本地纪念日\(n)。合并保留本机独有，覆盖以文件为准。")
            })
        }
    }

    private func defaultBackupName() -> String {
        BackupNaming.shared.defaultBackupFileName(date: DateCalculator.shared.today())
    }
}

/// Minimal FileDocument for JSON export.
struct JSONDocument: FileDocument {
    static var readableContentTypes: [UTType] { [.json, .plainText] }
    var text: String

    init(text: String) {
        self.text = text
    }

    init(configuration: ReadConfiguration) throws {
        if let data = configuration.file.regularFileContents {
            text = String(decoding: data, as: UTF8.self)
        } else {
            text = ""
        }
    }

    func fileWrapper(configuration: WriteConfiguration) throws -> FileWrapper {
        FileWrapper(regularFileWithContents: Data(text.utf8))
    }
}
