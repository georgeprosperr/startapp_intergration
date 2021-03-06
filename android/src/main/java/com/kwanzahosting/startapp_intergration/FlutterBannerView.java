package com.kwanzahosting.startapp_intergration;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.startapp.android.publish.ads.banner.Banner;
import com.startapp.android.publish.ads.banner.BannerListener;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

/**
 * @author dungvu
 * @since 2019-06-04
 */
public class FlutterBannerView implements PlatformView, MethodChannel.MethodCallHandler {
    private final FrameLayout bannerContainer;

    FlutterBannerView(Context context, BinaryMessenger messenger) {
        bannerContainer = new FrameLayout(context);
        new MethodChannel(messenger, "startapp_banner")
                .setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
        switch (methodCall.method) {
            case "loadAd":
                Banner banner = new Banner(StartappIntergrationPlugin.activity(), new BannerListener() {
                    @Override
                    public void onReceiveAd(View banner) {
                        updateContent(banner);
                    }

                    @Override
                    public void onFailedToReceiveAd(View banner) {
                        updateContent(banner);
                    }

                    @Override
                    public void onClick(View banner) {
                        updateContent(banner);
                    }

                    @Override
                    public void onImpression(View view) {}
                });
                banner.loadAd(300, 50);
                break;
            default:
                result.notImplemented();
        }
    }

    private void updateContent(View banner) {
        bannerContainer.removeAllViews();
        bannerContainer.addView(banner);
    }

    @Override
    public View getView() {
        return bannerContainer;
    }

    @Override
    public void dispose() {
        bannerContainer.removeAllViews();
    }

}
