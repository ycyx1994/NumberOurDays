import SwiftUI
import Shared

@main
struct NumberOurDaysApp: App {
    @StateObject private var store = EventStore()
    @State private var deepLinkEventId: String?
    @State private var deepLinkHolidays = false

    var body: some Scene {
        WindowGroup {
            HomeView(store: store, deepLinkEventId: $deepLinkEventId, deepLinkHolidays: $deepLinkHolidays)
                .tint(NodTheme.accent)
                .preferredColorScheme(.light)
                .task {
                    await ReminderScheduler.shared.requestAuthorization()
                    ReminderScheduler.shared.rescheduleAll(events: store.events)
                }
                .onOpenURL { url in
                    handleDeepLink(url)
                }
        }
    }

    private func handleDeepLink(_ url: URL) {
        guard url.scheme == "numberourdays" else { return }
        if url.host == "holidays" || url.path.contains("holidays") {
            deepLinkHolidays = true
            return
        }
        // numberourdays://event/<id>
        let id: String
        if url.host == "event" {
            id = url.lastPathComponent.isEmpty ? url.pathComponents.last ?? "" : url.lastPathComponent
        } else {
            id = url.lastPathComponent
        }
        if !id.isEmpty, store.event(id: id) != nil {
            deepLinkEventId = id
        }
    }
}
