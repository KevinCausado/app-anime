# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class **$$serializer { *; }
-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *** Companion;
    *** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# Apollo
-keep class com.apollographql.apollo.** { *; }
-dontwarn com.apollographql.**

# Hilt / Dagger
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keepclasseswithmembernames class * { @javax.inject.* <fields>; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**

# Coil
-dontwarn coil.**

# Media3 ExoPlayer
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Model classes (no ofuscar DTOs)
-keep class com.kevindev.animeapp.core.model.** { *; }
-keep class com.kevindev.animeapp.core.network.consumet.**Dto { *; }

# WorkManager
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.CoroutineWorker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
