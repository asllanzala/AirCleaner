# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/liunan/Documents/Develop/SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#  Library JARs.
#-keep class de.greenrobot.dao.** { *; }
#-keep interface de.greenrobot.dao.** { *; }

-dontwarn com.baidu.**
-keep class com.baidu.** { *; }
-keep interface com.baidu.** { *; }

-dontwarn com.tecent.**
-keep class com.tecent.** { *; }
-keep interface com.tecent.** { *; }

-dontwarn com.microsoft.**
-keep class com.microsoft.** { *; }
-keep interface com.microsoft.** { *; }

-dontwarn com.umeng.**
-keep class com.umeng.** { *; }
-keep interface com.umeng.** { *; }

-dontwarn com.sina.**
-keep class com.sina.** { *; }
-keep interface com.sina.** { *; }

-dontwarn okio.Okio.**
-keep class okio.Okio.** { *; }
-keep interface okio.Okio.** { *; }

-dontwarn org.apache.**
-keep class org.apache.** { *; }
-keep interface org.apache.** { *; }

-dontwarn u.aly.**
-keep class u.aly.** { *; }
-keep interface u.aly.** { *; }

-dontwarn u.upd.**
-keep class u.upd.** { *; }
-keep interface u.upd.** { *; }

# If android-support-v4.jar is used
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }


# The official support library.
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**

# Library projects.
#-keep class com.actionbarsherlock.** { *; }
#-keep interface com.actionbarsherlock.** { *; }