# Retrofit
-keepattributes Signature, InnerClasses, AnnotationDefault
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class com.toxa.pureradio.data.model.** { *; }
-keep class com.toxa.pureradio.network.** { *; }

# Coil
-keep class coil.** { *; }

# Media3
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**
