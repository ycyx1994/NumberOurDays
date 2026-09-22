# NumberOurDays release ProGuard
# Compose / reflection-light app; keep JSON and app domain models.
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*
-keep class com.numberourdays.app.data.** { *; }
-keep class com.numberourdays.app.domain.** { *; }
-dontwarn org.json.**
-keep class org.json.** { *; }
