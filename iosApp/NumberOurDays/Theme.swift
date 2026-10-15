import SwiftUI
import Shared

enum NodTheme {
    static let n0 = Color(hex: 0xFAF7F2)
    static let n50 = Color(hex: 0xF3EEE6)
    static let n100 = Color(hex: 0xE8E0D4)
    static let n300 = Color(hex: 0xB5A99A)
    static let n500 = Color(hex: 0x7A6F62)
    static let n700 = Color(hex: 0x4A4238)
    static let n900 = Color(hex: 0x1C1916)
    static let accent = Color(hex: 0xD4553A)
}

extension Color {
    init(hex: UInt32) {
        self.init(
            red: Double((hex >> 16) & 0xFF) / 255.0,
            green: Double((hex >> 8) & 0xFF) / 255.0,
            blue: Double(hex & 0xFF) / 255.0
        )
    }
}

enum HomeUnitPrefs {
    private static let key = "home_unit"

    static var unit: DurationUnit {
        get {
            guard let raw = UserDefaults.standard.string(forKey: key) else { return .day }
            switch raw {
            case "MONTH": return .month
            case "YEAR": return .year
            default: return .day
            }
        }
        set {
            switch newValue {
            case .month: UserDefaults.standard.set("MONTH", forKey: key)
            case .year: UserDefaults.standard.set("YEAR", forKey: key)
            default: UserDefaults.standard.set("DAY", forKey: key)
            }
        }
    }
}
