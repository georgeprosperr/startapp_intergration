import 'dart:async';

import 'package:flutter/services.dart';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

typedef void BannerCreatedCallback(BannerController controller);
typedef StringToVoidFunc = void Function(String);
const String PLUGIN_KEY = "vn.momo.plugin.startapp.StartAppBannerPlugin";

class StartappIntergration {

  static const MethodChannel _channel = const MethodChannel('startapp_intergration');
//  static const platform = const MethodChannel(PLUGIN_KEY);
  static VoidCallback onVideoCompleted;
  static VoidCallback onReceiveAd;
  static StringToVoidFunc onFailedToReceiveAd;

  static showInterstitialAd() async {
    await _channel.invokeMethod('showAd');
  }

  static showRewardedAd({VoidCallback onVideoCompleted,
    VoidCallback onReceiveAd,
    StringToVoidFunc onFailedToReceiveAd}) async {
    StartappIntergration.onVideoCompleted = onVideoCompleted;
    _channel.setMethodCallHandler(_handleMethod);
    await _channel.invokeMethod('showRewardedAd');
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

class AdBanner extends StatefulWidget {
  const AdBanner({
    Key key,
    this.onCreated,
  }) : super(key: key);

  final BannerCreatedCallback onCreated;

  @override
  State<AdBanner> createState() => _BannerState();
}

class _BannerState extends State<AdBanner> {
  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return Container(
          width: 300.0,
          height: 70.0,
          child: AndroidView(
            viewType: PLUGIN_KEY,
            onPlatformViewCreated: _onPlatformViewCreated,
          ));
    }
    return Text('$defaultTargetPlatform is no need showing ads');
  }

  void _onPlatformViewCreated(int id) {
    BannerController controller = new BannerController._(id);
    controller.loadAd();
    if (widget.onCreated == null) {
      return;
    }
    widget.onCreated(controller);
  }
}

class BannerController {
  BannerController._(int id)
      : _channel = new MethodChannel('${PLUGIN_KEY}_$id');

  final MethodChannel _channel;

  Future<void> loadAd() async {
    return _channel.invokeMethod('loadAd');
  }
}

