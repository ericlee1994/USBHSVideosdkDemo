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

#保留代码行号，方便异常信息的追踪
-keepattributes SourceFile,LineNumberTable
#-renamesourcefileattribute SourceFile

-keep enum com.ainemo.sdk.NemoSDKListener** {
    **[] $VALUES;
    public *;
    }
-keepclassmembers enum * { *; }
-keepnames class * implements java.io.Serializable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
    }
-keep class com.ainemo.sdk.model.Settings{*;}
-keep class com.ainemo.sdk.NemoSDK{
    public *;
    }
-keep class com.google.gson.stream.** {*;}
-keep class com.google.gson.** {*;}
-keep class com.google.gson.Gson {*;}
-keep class com.google.gson.examples.android.model.** {*;}
-keep class io.reactivex.internal.util.**{*;}

-keep class android.utils.RestartHandler{*;}
-keep class com.ainemo.sdk.utils.SignatureHelper{*;}
-keep class com.ainemo.sdk.module.ConnectNemoCallback{*;}
-keep class com.ainemo.sdk.NemoReceivedCallListener{*;}
-keep class com.ainemo.sdk.NemoSDKListener{*;}
-keep class com.ainemo.sdk.module.data.VideoInfo{*;}
-keep class com.ainemo.sdk.module.push.**{*;}
-keep class com.ainemo.sdk.module.rest.**{*;}
-keep class com.ainemo.sdk.NemoSDKErrorCode{*;}

-keep class com.ainemo.sdk.otf.** {*;}
-keep class com.ainemo.a.**{*;}
-keep class com.ainemo.sdk.module.**{*;}
-keep class android.http.b{*;}
-keep class vulture.module.call.**{*;}
-keep class android.log.**{*;}
-keep class com.wa.util.**{*;}

-dontwarn com.ainemo.a.**
-dontwarn com.ainemo.sdk.module.**
-dontwarn android.http.b
-dontwarn com.ainemo.sdk.otf.**
-dontwarn vulture.module.call.**
-dontwarn com.serenegiant.**

#使用GSON、fastjson等框架时，所写的JSON对象类不混淆，否则无法将JSON解析成对应的对象
-keepclassmembers class * {
    public <init>(org.json.JSONObject);
    }

-keep class com.shgbit.android.heysharevideo.activity.HSVideoSDK{*;}
-keep class com.shgbit.android.heysharevideo.util.GBLog{*;}
-keep class com.shgbit.android.heysharevideo.bean.**{*;}
-keep class com.shgbit.android.heysharevideo.json.**{*;}
-keep class com.shgbit.android.heysharevideo.addressaar.AddressCallBack{*;}
-keep class com.shgbit.android.heysharevideo.callback.HSSDKListener{*;}
-keep class com.shgbit.android.heysharevideo.callback.HSSDKInstantListener{*;}
-keep class com.shgbit.android.heysharevideo.callback.HSSDKReserveListener{*;}
-keep class com.shgbit.android.heysharevideo.callback.HSSDKMeetingListener{*;}
-keep class com.shgbit.android.heysharevideo.interactmanager.ServerInteractManager{*;}

#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class **.R$* {
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}