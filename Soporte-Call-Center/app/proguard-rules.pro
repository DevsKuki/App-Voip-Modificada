# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep public class * extends androidx.fragment.app.Fragment { *; }
-dontwarn com.google.errorprone.annotations.Immutable

# To prevent following errors:
#ERROR: Missing classes detected while running R8. Please add the missing classes or apply additional keep rules that are generated in /builds/BC/public/linphone-android/app/build/outputs/mapping/release/missing_rules.txt.
#ERROR: R8: Missing class org.bouncycastle.jsse.BCSSLParameters (referenced from: void okhttp3.internal.platform.BouncyCastlePlatform.configureTlsExtensions(javax.net.ssl.SSLSocket, java.lang.String, java.util.List) and 1 other context)
#Missing class org.bouncycastle.jsse.BCSSLSocket (referenced from: void okhttp3.internal.platform.BouncyCastlePlatform.configureTlsExtensions(javax.net.ssl.SSLSocket, java.lang.String, java.util.List) and 5 other contexts)
#Missing class org.bouncycastle.jsse.provider.BouncyCastleJsseProvider (referenced from: void okhttp3.internal.platform.BouncyCastlePlatform.<init>())
#Missing class org.conscrypt.Conscrypt$Version (referenced from: boolean okhttp3.internal.platform.ConscryptPlatform$Companion.atLeastVersion(int, int, int))
#Missing class org.conscrypt.Conscrypt (referenced from: boolean okhttp3.internal.platform.ConscryptPlatform$Companion.atLeastVersion(int, int, int) and 4 other contexts)
#Missing class org.conscrypt.ConscryptHostnameVerifier (referenced from: okhttp3.internal.platform.ConscryptPlatform$DisabledHostnameVerifier)
#Missing class org.openjsse.javax.net.ssl.SSLParameters (referenced from: void okhttp3.internal.platform.OpenJSSEPlatform.configureTlsExtensions(javax.net.ssl.SSLSocket, java.lang.String, java.util.List))
#Missing class org.openjsse.javax.net.ssl.SSLSocket (referenced from: void okhttp3.internal.platform.OpenJSSEPlatform.configureTlsExtensions(javax.net.ssl.SSLSocket, java.lang.String, java.util.List) and 1 other context)
#Missing class org.openjsse.net.ssl.OpenJSSE (referenced from: void okhttp3.internal.platform.OpenJSSEPlatform.<init>())
#> Task :app:lintVitalAnalyzeRelease
#FAILURE: Build failed with an exception.
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# Evita advertencias innecesarias
-dontwarn retrofit2.**

# Retrofit (evita la ofuscación de las clases de Retrofit)
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }

# Si estás utilizando Gson como convertidor
-keep class com.google.gson.** { *; }
-keep class com.google.** { *; }
-dontwarn com.google.gson.**

# Mantén los campos serializados con @SerializedName en Gson
-keepclassmembers class ** {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Ofuscar los nombres de paquetes para ocultar la estructura interna
-flattenpackagehierarchy

# Ofusca todo el paquete de la aplicación
-keep class !call.upea.sensitive.**,** { *; }

# Evita la ofuscación de clases importantes (públicas o que necesiten reflección)
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Ofusca las cadenas literales en el código para dificultar la extracción de claves
-keepattributes SourceFile,LineNumberTable

# Proteger clases que manejan claves o credenciales sensibles
-keep class call.upea.seguridad.** { *; }

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn com.google.api.client.http.GenericUrl
-dontwarn com.google.api.client.http.HttpHeaders
-dontwarn com.google.api.client.http.HttpRequest
-dontwarn com.google.api.client.http.HttpRequestFactory
-dontwarn com.google.api.client.http.HttpResponse
-dontwarn com.google.api.client.http.HttpTransport
-dontwarn com.google.api.client.http.javanet.NetHttpTransport$Builder
-dontwarn com.google.api.client.http.javanet.NetHttpTransport
-dontwarn java.lang.reflect.AnnotatedType
-dontwarn javax.lang.model.element.Modifier
-dontwarn org.joda.time.Instant
