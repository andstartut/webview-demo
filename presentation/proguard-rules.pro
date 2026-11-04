# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep WebViewClient and related classes for Live Edit compatibility
-keep class com.example.fieldteam.presentation.webview.** { *; }
-keepclassmembers class com.example.fieldteam.presentation.webview.** { *; }

# Keep WebView classes to prevent Live Edit crashes
-keep class android.webkit.** { *; }
-keepclassmembers class android.webkit.** { *; }

# Keep androidx.core.net.Uri Kt extensions
-keep class androidx.core.net.** { *; }
