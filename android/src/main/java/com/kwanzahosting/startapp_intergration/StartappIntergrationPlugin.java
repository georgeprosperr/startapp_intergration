package com.kwanzahosting.startapp_intergration;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;


import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.AutoInterstitialPreferences;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.startapp.android.publish.adsCommon.VideoListener;
import com.startapp.android.publish.adsCommon.adListeners.AdDisplayListener;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** StartappIntergrationPlugin */
public class StartappIntergrationPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private static StartAppAd startAppAd;
  private static Activity mainActivity;
  String startAppId = "203495032";

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "startapp_intergration");
    channel.setMethodCallHandler(this);
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "startapp_intergration");
    channel.setMethodCallHandler(new StartappIntergrationPlugin());

    registrar.platformViewRegistry()
            .registerViewFactory("com.kwanzahosting.startapp_intergration.StartappIntergrationPlugin", new BannerFactory(registrar.messenger()));

  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method) {
      case "showAd":
        StartAppAd.showAd(mainActivity);
        result.success(null);
        break;
      case "showRewardedAd":
        startAppAd.setVideoListener(() -> {
          channel.invokeMethod("onVideoCompleted", null);
          Log.d("onVideoCompleted", "Complete");
        });
        startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
          @Override
          public void onReceiveAd(Ad ad) {
            startAppAd.showAd();
            channel.invokeMethod("onReceiveAd", null);
          }

          @Override
          public void onFailedToReceiveAd(Ad arg0) {
            channel.invokeMethod("onFailedToReceiveAd",
                    arg0.getErrorMessage());
            Log.e("StartAppPlugin",
                    "Failed to load rewarded video with reason: "
                            + arg0.getErrorMessage());
          }
        });
        result.success(null);
        break;
      default:
        result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
    mainActivity = activityPluginBinding.getActivity();
    startAppAd = new StartAppAd(activityPluginBinding.getActivity());

    StartAppSDK.init(mainActivity, startAppId, false);
    StartAppAd.disableSplash();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
  }

  @Override
  public void onDetachedFromActivity() {
  }

  public static Activity activity() {
    return mainActivity;
  }
}
