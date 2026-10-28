import Foundation
import Shared

/// Local store backed by App Group so WidgetKit can read the same events.
/// Domain math stays in the KMP `Shared` module.
@MainActor
final class EventStore: ObservableObject {
    @Published private(set) var events: [DayEvent] = []
    @Published var message: String?

    private let seededKey = "nod.demo_seeded.v2"

    init() {
        load()
        seedIfNeeded()
        // Ensure widget can always see current data (covers first install / migration).
        persist(force: true)
    }

    func display(for event: DayEvent) -> DayEventDisplay {
        DateCalculator.shared.computeDisplay(event: event, ref: DateCalculator.shared.today())
    }

    func sortedDisplays() -> [DayEventDisplay] {
        Array(DateCalculator.shared.sortEvents(list: events.map { display(for: $0) }))
    }

    func event(id: String) -> DayEvent? {
        events.first { $0.id == id }
    }

    func upsert(_ event: DayEvent) {
        if let idx = events.firstIndex(where: { $0.id == event.id }) {
            events[idx] = event
        } else {
            events.append(event)
        }
        persist()
        Task { ReminderScheduler.shared.schedule(event: event) }
    }

    func remove(id: String) {
        events.removeAll { $0.id == id }
        persist()
        Task { ReminderScheduler.shared.cancel(id: id) }
    }

    func clearAll() {
        let ids = events.map { $0.id }
        events = []
        UserDefaults.standard.set(true, forKey: seededKey)
        persist()
        Task { ReminderScheduler.shared.cancelAll(ids: ids) }
    }

    func toggleStar(id: String) {
        guard let current = event(id: id) else { return }
        let now = Int64(Date().timeIntervalSince1970 * 1000)
        if current.starred {
            upsert(copy(current, starred: false, starRank: nil, updatedAt: now))
        } else {
            let maxRank = events
                .filter { $0.starred }
                .compactMap { $0.starRank?.int32Value }
                .max() ?? 0
            upsert(copy(current, starred: true, starRank: KotlinInt(value: maxRank + 1), updatedAt: now))
        }
    }

    func exportJSON() -> String {
        DayEventCodec.shared.serialize(events: events)
    }

    func importJSON(_ text: String, replace: Bool) {
        let pair = DayEventCodec.shared.parseBackup(text: text)
        let incoming = (pair.first as? [DayEvent]) ?? []
        let note = pair.second as? String
        if replace {
            events = incoming
        } else {
            events = Array(DayEventCodec.shared.merge(local: events, incoming: incoming))
        }
        persist()
        if let note, !note.isEmpty { message = note }
    }

    func createId() -> String {
        "e_\(Int(Date().timeIntervalSince1970 * 1000))_\(Int.random(in: 0..<1_000_000))"
    }

    private func copy(
        _ e: DayEvent,
        title: String? = nil,
        date: String? = nil,
        note: String?? = nil,
        starred: Bool? = nil,
        starRank: KotlinInt?? = nil,
        repeatUnit: RepeatUnit? = nil,
        repeatInterval: Int32? = nil,
        remindEnabled: Bool? = nil,
        remindHour: Int32? = nil,
        remindMinute: Int32? = nil,
        updatedAt: Int64? = nil
    ) -> DayEvent {
        let resolvedNote: String?
        if let note { resolvedNote = note } else { resolvedNote = e.note }
        let resolvedRank: KotlinInt?
        if let starRank { resolvedRank = starRank } else {
            resolvedRank = e.starRank
        }
        return DayEvent(
            id: e.id,
            title: title ?? e.title,
            date: date ?? e.date,
            note: resolvedNote,
            starred: starred ?? e.starred,
            starRank: resolvedRank,
            repeatUnit: repeatUnit ?? e.repeatUnit,
            repeatInterval: repeatInterval ?? e.repeatInterval,
            remindEnabled: remindEnabled ?? e.remindEnabled,
            remindHour: remindHour ?? e.remindHour,
            remindMinute: remindMinute ?? e.remindMinute,
            createdAt: e.createdAt,
            updatedAt: updatedAt ?? e.updatedAt
        )
    }

    private func load() {
        // Prefer App Group storage for widget visibility.
        if let text = AppGroup.loadEventsJSON(), !text.isEmpty {
            let pair = DayEventCodec.shared.parseBackup(text: text)
            if let list = pair.first as? [DayEvent], !list.isEmpty {
                events = list
                return
            }
        }
        // Legacy documents migration
        let docs = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
            .appendingPathComponent("nod-events.json")
        if let data = try? Data(contentsOf: docs), !data.isEmpty {
            let text = String(decoding: data, as: UTF8.self)
            let pair = DayEventCodec.shared.parseBackup(text: text)
            if let list = pair.first as? [DayEvent], !list.isEmpty {
                events = list
                AppGroup.saveEventsJSON(text)
            }
        }
    }

    private func persist(force: Bool = false) {
        let text = DayEventCodec.shared.serialize(events: events)
        AppGroup.saveEventsJSON(text)
        if force || !events.isEmpty {
            AppGroup.reloadWidgets()
        }
    }

    private func seedIfNeeded() {
        // Re-seed if shared storage is empty (e.g. reinstall / App Group not ready yet).
        if events.isEmpty, !AppGroup.hasEventsJSON() {
            events = DemoEvents.shared.create(now: Int64(Date().timeIntervalSince1970 * 1000))
            UserDefaults.standard.set(true, forKey: seededKey)
            return
        }
        guard !UserDefaults.standard.bool(forKey: seededKey) else { return }
        if events.isEmpty {
            events = DemoEvents.shared.create(now: Int64(Date().timeIntervalSince1970 * 1000))
        }
        UserDefaults.standard.set(true, forKey: seededKey)
    }
}
