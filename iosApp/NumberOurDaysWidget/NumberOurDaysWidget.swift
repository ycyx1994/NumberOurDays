import SwiftUI
import Shared
import WidgetKit

// MARK: - Theme (mirror of app tokens; widget cannot import app target)

private enum WTheme {
    static let n0 = Color(red: 0xFA / 255.0, green: 0xF7 / 255.0, blue: 0xF2 / 255.0)
    static let n50 = Color(red: 0xF3 / 255.0, green: 0xEE / 255.0, blue: 0xE6 / 255.0)
    static let n300 = Color(red: 0xB5 / 255.0, green: 0xA9 / 255.0, blue: 0x9A / 255.0)
    static let n500 = Color(red: 0x7A / 255.0, green: 0x6F / 255.0, blue: 0x62 / 255.0)
    static let n700 = Color(red: 0x4A / 255.0, green: 0x42 / 255.0, blue: 0x38 / 255.0)
    static let n900 = Color(red: 0x1C / 255.0, green: 0x19 / 255.0, blue: 0x16 / 255.0)
    static let accent = Color(red: 0xD4 / 255.0, green: 0x55 / 255.0, blue: 0x3A / 255.0)
}

// MARK: - Day count widget

struct DayCountProvider: TimelineProvider {
    func placeholder(in context: Context) -> DayWidgetEntry {
        DayWidgetEntry(
            date: Date(),
            nearest: NearestDayModel(
                id: "demo",
                tag: "下一个 · 倒数",
                title: "纪念日",
                days: 42,
                meta: "2026-12-01"
            ),
            holiday: nil
        )
    }

    func getSnapshot(in context: Context, completion: @escaping (DayWidgetEntry) -> Void) {
        if context.isPreview {
            completion(placeholder(in: context))
        } else {
            completion(
                DayWidgetEntry(
                    date: Date(),
                    nearest: WidgetDataProvider.nearestDay(),
                    holiday: WidgetDataProvider.holiday()
                )
            )
        }
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<DayWidgetEntry>) -> Void) {
        completion(WidgetDataProvider.timeline())
    }
}

struct DayCountWidgetView: View {
    let entry: DayWidgetEntry
    @Environment(\.widgetFamily) private var family

    var body: some View {
        Group {
            if let model = entry.nearest {
                content(model)
                    .widgetURL(URL(string: "numberourdays://event/\(model.id)"))
            } else {
                empty
                    .widgetURL(URL(string: "numberourdays://"))
            }
        }
    }

    @ViewBuilder
    private func content(_ model: NearestDayModel) -> some View {
        switch family {
        case .systemMedium:
            medium(model)
        default:
            small(model)
        }
    }

    private func small(_ model: NearestDayModel) -> some View {
        VStack(alignment: .leading, spacing: 2) {
            Text(model.tag)
                .font(.system(size: 11))
                .foregroundStyle(WTheme.n500)
                .lineLimit(1)
            Text(model.title)
                .font(.system(size: 14, weight: .bold))
                .foregroundStyle(WTheme.n900)
                .lineLimit(1)
            Spacer(minLength: 0)
            Text("\(model.days)")
                .font(.system(size: 40, weight: .bold, design: .rounded))
                .foregroundStyle(WTheme.accent)
                .minimumScaleFactor(0.6)
                .lineLimit(1)
            Text(model.meta)
                .font(.system(size: 11))
                .foregroundStyle(WTheme.n500)
                .lineLimit(1)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
        .padding(12)
        .modifier(NodWidgetBackground(color: WTheme.n50))
    }

    private func medium(_ model: NearestDayModel) -> some View {
        HStack(spacing: 16) {
            VStack(alignment: .leading, spacing: 4) {
                Text(model.tag)
                    .font(.system(size: 11))
                    .foregroundStyle(WTheme.n500)
                Text(model.title)
                    .font(.system(size: 15, weight: .bold))
                    .foregroundStyle(WTheme.n900)
                    .lineLimit(2)
                Text(model.meta)
                    .font(.system(size: 12))
                    .foregroundStyle(WTheme.n500)
            }
            Spacer(minLength: 8)
            HStack(alignment: .lastTextBaseline, spacing: 2) {
                Text("\(model.days)")
                    .font(.system(size: 48, weight: .bold, design: .rounded))
                    .foregroundStyle(WTheme.accent)
                    .minimumScaleFactor(0.5)
                    .lineLimit(1)
                Text("天")
                    .font(.system(size: 13, weight: .medium))
                    .foregroundStyle(WTheme.n700)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
        .padding(16)
        .modifier(NodWidgetBackground(color: WTheme.n50))
    }

    private var empty: some View {
        VStack(spacing: 6) {
            Text("还没有纪念日")
                .font(.system(size: 14, weight: .bold))
                .foregroundStyle(WTheme.n500)
            Text("点开添加一个吧")
                .font(.system(size: 11))
                .foregroundStyle(WTheme.n300)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .modifier(NodWidgetBackground(color: WTheme.n50))
    }
}

struct DayCountWidget: Widget {
    var body: some WidgetConfiguration {
        StaticConfiguration(kind: "DayCountWidget", provider: DayCountProvider()) { entry in
            DayCountWidgetView(entry: entry)
        }
        .configurationDisplayName("纪念日倒数")
        .description("首页最近一个纪念日的倒数天数。")
        .supportedFamilies([.systemSmall, .systemMedium])
    }
}

// MARK: - Holiday widget

struct HolidayProvider: TimelineProvider {
    func placeholder(in context: Context) -> DayWidgetEntry {
        DayWidgetEntry(
            date: Date(),
            nearest: nil,
            holiday: HolidayWidgetModel(
                id: "demo",
                title: "国庆节",
                days: 12,
                meta: "10-01 ~ 10-07",
                ongoing: false
            )
        )
    }

    func getSnapshot(in context: Context, completion: @escaping (DayWidgetEntry) -> Void) {
        if context.isPreview {
            completion(placeholder(in: context))
        } else {
            completion(
                DayWidgetEntry(
                    date: Date(),
                    nearest: WidgetDataProvider.nearestDay(),
                    holiday: WidgetDataProvider.holiday()
                )
            )
        }
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<DayWidgetEntry>) -> Void) {
        completion(WidgetDataProvider.timeline())
    }
}

struct HolidayWidgetView: View {
    let entry: DayWidgetEntry
    @Environment(\.widgetFamily) private var family

    var body: some View {
        Group {
            if let model = entry.holiday {
                content(model)
                    .widgetURL(URL(string: "numberourdays://holidays"))
            } else {
                empty
                    .widgetURL(URL(string: "numberourdays://holidays"))
            }
        }
    }

    @ViewBuilder
    private func content(_ model: HolidayWidgetModel) -> some View {
        switch family {
        case .systemMedium:
            medium(model)
        default:
            small(model)
        }
    }

    private func small(_ model: HolidayWidgetModel) -> some View {
        VStack(alignment: .leading, spacing: 2) {
            Text("法定假日")
                .font(.system(size: 11, weight: .bold))
                .foregroundStyle(WTheme.accent)
            Text(model.title)
                .font(.system(size: 13, weight: .bold))
                .foregroundStyle(WTheme.n900)
                .lineLimit(1)
            Spacer(minLength: 0)
            HStack(alignment: .lastTextBaseline, spacing: 2) {
                Text("\(model.days)")
                    .font(.system(size: 34, weight: .bold, design: .rounded))
                    .foregroundStyle(WTheme.accent)
                    .minimumScaleFactor(0.5)
                    .lineLimit(1)
                Text("天")
                    .font(.system(size: 12, weight: .medium))
                    .foregroundStyle(WTheme.n700)
            }
            Text(model.ongoing ? "假期中" : "距放假")
                .font(.system(size: 11))
                .foregroundStyle(WTheme.n500)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
        .padding(12)
        .modifier(NodWidgetBackground(color: WTheme.n0))
    }

    private func medium(_ model: HolidayWidgetModel) -> some View {
        HStack(spacing: 16) {
            VStack(alignment: .leading, spacing: 4) {
                Text("法定假日")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundStyle(WTheme.accent)
                Text(model.title)
                    .font(.system(size: 15, weight: .bold))
                    .foregroundStyle(WTheme.n900)
                    .lineLimit(1)
                Text(model.meta)
                    .font(.system(size: 12))
                    .foregroundStyle(WTheme.n500)
                    .lineLimit(2)
            }
            Spacer(minLength: 8)
            HStack(alignment: .lastTextBaseline, spacing: 2) {
                Text("\(model.days)")
                    .font(.system(size: 44, weight: .bold, design: .rounded))
                    .foregroundStyle(WTheme.accent)
                    .minimumScaleFactor(0.5)
                    .lineLimit(1)
                Text(model.ongoing ? "假期中" : "天")
                    .font(.system(size: 13, weight: .medium))
                    .foregroundStyle(WTheme.n700)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(16)
        .modifier(NodWidgetBackground(color: WTheme.n0))
    }

    private var empty: some View {
        VStack(spacing: 6) {
            Text("法定假日")
                .font(.system(size: 11, weight: .bold))
                .foregroundStyle(WTheme.n300)
            Text("暂无假期数据")
                .font(.system(size: 13, weight: .bold))
                .foregroundStyle(WTheme.n500)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .modifier(NodWidgetBackground(color: WTheme.n0))
    }
}

/// iOS 17+ uses containerBackground; iOS 16 falls back to plain background.
private struct NodWidgetBackground: ViewModifier {
    let color: Color

    func body(content: Content) -> some View {
        if #available(iOS 17.0, *) {
            content.containerBackground(for: .widget) { color }
        } else {
            content.background(color)
        }
    }
}

struct HolidayWidget: Widget {
    var body: some WidgetConfiguration {
        StaticConfiguration(kind: "HolidayWidget", provider: HolidayProvider()) { entry in
            HolidayWidgetView(entry: entry)
        }
        .configurationDisplayName("法定假日")
        .description("下一个法定假期的倒数天数。")
        .supportedFamilies([.systemSmall, .systemMedium])
    }
}

// MARK: - Bundle

@main
struct NumberOurDaysWidgetsBundle: WidgetBundle {
    var body: some Widget {
        DayCountWidget()
        HolidayWidget()
    }
}
