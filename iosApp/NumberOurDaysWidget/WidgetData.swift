import Foundation
import Shared
import WidgetKit

/// Snapshot model for Widget timeline (no ObservableObject needed).
struct DayWidgetEntry: TimelineEntry {
    let date: Date
    let nearest: NearestDayModel?
    let holiday: HolidayWidgetModel?
}

struct NearestDayModel: Identifiable {
    let id: String
    let tag: String
    let title: String
    let days: Int
    let meta: String
}

struct HolidayWidgetModel: Identifiable {
    let id: String
    let title: String
    let days: Int
    let meta: String
    let ongoing: Bool
}

enum WidgetDataProvider {
    static func nearestDay() -> NearestDayModel? {
        let events = loadEvents()
        guard !events.isEmpty else { return nil }
        let displays = Array(
            DateCalculator.shared.sortEvents(
                list: events.map {
                    DateCalculator.shared.computeDisplay(event: $0, ref: DateCalculator.shared.today())
                }
            )
        )
        guard let display = DateCalculator.shared.pickNearest(list: displays) else { return nil }
        return NearestDayModel(
            id: display.id,
            tag: tagLabel(display),
            title: display.title,
            days: Int(display.days),
            meta: display.date
        )
    }

    static func holiday() -> HolidayWidgetModel? {
        guard let countdown = HolidayCalculator.shared.nextOrOngoing(
            periods: LegalHolidays.shared.periods(),
            today: DateCalculator.shared.today()
        ) else { return nil }
        let ongoing = countdown.status == HolidayStatus.ongoing
        let start = DateCalculator.shared.format(date: countdown.period.start)
        let end = DateCalculator.shared.format(date: countdown.period.end)
        return HolidayWidgetModel(
            id: countdown.period.name,
            title: countdown.period.name,
            days: ongoing ? 0 : Int(countdown.daysUntilStart),
            meta: ongoing ? "假期中 · \(start) ~ \(end)" : "\(start) ~ \(end)",
            ongoing: ongoing
        )
    }

    private static func loadEvents() -> [DayEvent] {
        // Prefer UserDefaults suite (same path EventStore always writes).
        if let text = AppGroup.loadEventsJSON(), !text.isEmpty {
            let pair = DayEventCodec.shared.parseBackup(text: text)
            let list = (pair.first as? [DayEvent]) ?? []
            if !list.isEmpty { return list }
        }
        // Fallback: legacy file path if suite is empty
        if let url = AppGroup.eventsFileURL,
           let data = try? Data(contentsOf: url),
           !data.isEmpty {
            let text = String(decoding: data, as: UTF8.self)
            let pair = DayEventCodec.shared.parseBackup(text: text)
            return (pair.first as? [DayEvent]) ?? []
        }
        return []
    }

    private static func tagLabel(_ display: DayEventDisplay) -> String {
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

    /// Midnight-aligned timeline entries for the next few days.
    static func timeline() -> Timeline<DayWidgetEntry> {
        let calendar = Calendar.current
        let now = Date()
        var entries: [DayWidgetEntry] = []
        for dayOffset in 0..<4 {
            guard let entryDate = calendar.date(
                byAdding: .day,
                value: dayOffset,
                to: calendar.startOfDay(for: now)
            ) else { continue }
            entries.append(
                DayWidgetEntry(
                    date: entryDate,
                    nearest: nearestDay(),
                    holiday: holiday()
                )
            )
        }
        return Timeline(entries: entries, policy: .atEnd)
    }
}
