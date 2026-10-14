import SwiftUI
import Shared

@main
struct NumberOurDaysApp: App {
    @StateObject private var store = EventStore()

    var body: some Scene {
        WindowGroup {
            HomeView(store: store)
                .tint(NodTheme.accent)
                .preferredColorScheme(.light)
                .task {
                    await ReminderScheduler.shared.requestAuthorization()
                    ReminderScheduler.shared.rescheduleAll(events: store.events)
                }
        }
    }
}
