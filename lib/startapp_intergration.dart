import 'dart:async';

import 'package:flutter/services.dart';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

//typedef void BannerCreatedCallback(BannerController controller);
typedef StringToVoidFunc = void Function(String);
const String PLUGIN_KEY = "vn.momo.plugin.startapp.StartAppBannerPlugin";

class StartappIntergration {

  static const MethodChannel _channel = const MethodChannel('startapp_intergration');
  static const platform = const MethodChannel(PLUGIN_KEY);
  static VoidCallback onVideoCompleted;
  static VoidCallback onReceiveAd;
  static StringToVoidFunc onFailedToReceiveAd;

  static showInterstitialAd() async {
    await platform.invokeMethod('showAd');
  }

  static showRewardedAd({VoidCallback onVideoCompleted,
    VoidCallback onReceiveAd,
    StringToVoidFunc onFailedToReceiveAd}) async {
    StartappIntergration.onVideoCompleted = onVideoCompleted;
    platform.setMethodCallHandler(_handleMethod);
    await platform.invokeMethod('showRewardedAd');
  }

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }



  static Future<dynamic> _handleMethod(MethodCall call) {
    switch (call.method) {
      case "onVideoCompleted":
        if (onVideoCompleted != null) {
          onVideoCompleted();
        }
        break;
      case "onReceiveAd":
        if (onReceiveAd != null) {
          onReceiveAd();
        }
        break;
      case "onFailedToReceiveAd":
        if (onFailedToReceiveAd != null) {
          onFailedToReceiveAd(call.arguments);
        }
        break;
    }
    return Future<dynamic>.value(null);
  }
}
