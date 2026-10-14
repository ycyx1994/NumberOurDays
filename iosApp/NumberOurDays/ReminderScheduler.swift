import Foundation
import UserNotifications
import Shared

/// Local notifications aligned with Shared ReminderTiming (same slots as Android).
@MainActor
final class ReminderScheduler {
    static let shared = ReminderScheduler()

    private let center = UNUserNotificationCenter.current()

    func requestAuthorization() async {
        _ = try? await center.requestAuthorization(options: [.alert, .sound, .badge])
    }

    func schedule(event: DayEvent) {
        cancel(id: event.id)
        guard event.remindEnabled else { return }

        let nowMs = Int64(Date().timeIntervalSince1970 * 1000)
        let zone = Kotlinx_datetimeTimeZone.companion.currentSystemDefault()
        guard let fire = ReminderTiming.shared.nextReminderMillis(
            anchorDate: event.date,
            unit: event.repeatUnit,
            interval: event.repeatInterval,
            now: nowMs,
            hourOfDay: event.remindHour,
            minuteOfHour: event.remindMinute,
            zone: zone,
            notBefore: nowMs
        ) else { return }

        let millis: Int64
        if let boxed = fire as? NSNumber {
            millis = boxed.int64Value
        } else {
            millis = Int64(fire.int64Value)
        }
        let fireDate = Date(timeIntervalSince1970: TimeInterval(millis) / 1000.0)
        guard fireDate > Date() else { return }

        let content = UNMutableNotificationContent()
        let display = DateCalculator.shared.computeDisplay(event: event, ref: DateCalculator.shared.today())
        content.title = display.title
        content.body = "今天 · \(display.label)"
        content.sound = .default

        let interval = fireDate.timeIntervalSinceNow
        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: max(interval, 1), repeats: false)
        let request = UNNotificationRequest(identifier: event.id, content: content, trigger: trigger)
        center.add(request)
    }

    func cancel(id: String) {
        center.removePendingNotificationRequests(withIdentifiers: [id])
    }

    func cancelAll(ids: [String]) {
        center.removePendingNotificationRequests(withIdentifiers: ids)
    }

    /// Debug: fire one notification after delay.
    func scheduleDebugReminder(after seconds: TimeInterval = 60) {
        let content = UNMutableNotificationContent()
        content.title = "一分钟提醒"
        content.body = "本地通知链路正常"
        content.sound = .default
        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: max(seconds, 1), repeats: false)
        let request = UNNotificationRequest(identifier: "debug_reminder", content: content, trigger: trigger)
        center.add(request)
    }

    func rescheduleAll(events: [DayEvent]) {
        for e in events {
            schedule(event: e)
        }
    }
}
