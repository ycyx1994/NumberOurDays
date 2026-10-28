import Foundation
#if canImport(WidgetKit)
import WidgetKit
#endif

/// Shared storage for main app + WidgetKit extension.
/// Primary: UserDefaults suite (reliable for free provisioning).
/// Fallback: App Group container file.
enum AppGroup {
    static let id = "group.com.numberourdays.app"
    private static let jsonKey = "nod.events.json.v1"

    static var defaults: UserDefaults? {
        UserDefaults(suiteName: id)
    }

    static var containerURL: URL? {
        FileManager.default.containerURL(forSecurityApplicationGroupIdentifier: id)
    }

    static var eventsFileURL: URL? {
        containerURL?.appendingPathComponent("nod-events.json")
    }

    static func loadEventsJSON() -> String? {
        if let text = defaults?.string(forKey: jsonKey), !text.isEmpty {
            return text
        }
        if let url = eventsFileURL,
           let data = try? Data(contentsOf: url),
           !data.isEmpty {
            let text = String(decoding: data, as: UTF8.self)
            // Backfill suite so next read is faster/more reliable
            defaults?.set(text, forKey: jsonKey)
            return text
        }
        return nil
    }

    static func saveEventsJSON(_ text: String) {
        defaults?.set(text, forKey: jsonKey)
        defaults?.synchronize()
        if let url = eventsFileURL {
            let dir = url.deletingLastPathComponent()
            try? FileManager.default.createDirectory(at: dir, withIntermediateDirectories: true)
            try? text.data(using: .utf8)?.write(to: url, options: .atomic)
        }
    }

    static func hasEventsJSON() -> Bool {
        if let text = defaults?.string(forKey: jsonKey), !text.isEmpty { return true }
        if let url = eventsFileURL,
           let data = try? Data(contentsOf: url),
           !data.isEmpty {
            return true
        }
        return false
    }

    static func reloadWidgets() {
        #if canImport(WidgetKit)
        WidgetCenter.shared.reloadAllTimelines()
        #endif
    }
}
