import Foundation
import Shared

/// Local JSON store. Domain math stays in the KMP `Shared` module.
@MainActor
final class EventStore: ObservableObject {
    @Published private(set) var events: [DayEvent] = []
    @Published var message: String?

    private let fileURL: URL
    private let seededKey = "nod.demo_seeded.v2"

    init() {
        let dir = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        fileURL = dir.appendingPathComponent("nod-events.json")
        load()
        seedIfNeeded()
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
        save()
        Task { ReminderScheduler.shared.schedule(event: event) }
    }

    func remove(id: String) {
        events.removeAll { $0.id == id }
        save()
        Task { ReminderScheduler.shared.cancel(id: id) }
    }

    func clearAll() {
        let ids = events.map { $0.id }
        events = []
        UserDefaults.standard.set(true, forKey: seededKey)
        save()
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
        save()
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
        guard let data = try? Data(contentsOf: fileURL), !data.isEmpty else { return }
        let text = String(decoding: data, as: UTF8.self)
        let pair = DayEventCodec.shared.parseBackup(text: text)
        if let list = pair.first as? [DayEvent], !list.isEmpty {
            events = list
        }
    }

    private func save() {
        let text = DayEventCodec.shared.serialize(events: events)
        try? text.data(using: .utf8)?.write(to: fileURL, options: .atomic)
    }

    private func seedIfNeeded() {
        guard !UserDefaults.standard.bool(forKey: seededKey) else { return }
        if events.isEmpty {
            events = DemoEvents.shared.create(now: Int64(Date().timeIntervalSince1970 * 1000))
        }
        UserDefaults.standard.set(true, forKey: seededKey)
        save()
    }
}
