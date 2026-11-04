# Keep WebViewClient and related classes for Live Edit compatibility
-keep class com.example.fieldteam.presentation.webview.** { *; }
-keepclassmembers class com.example.fieldteam.presentation.webview.** { *; }

# Keep WebView classes to prevent Live Edit crashes
-keep class android.webkit.** { *; }
-keepclassmembers class android.webkit.** { *; }

# Keep androidx.core.net.Uri Kt extensions
-keep class androidx.core.net.** { *; }
