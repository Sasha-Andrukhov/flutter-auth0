package io.flutter.plugins.flutterauth0;

import android.app.Activity;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import java.util.Map;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

import io.flutter.plugins.flutterauth0.common.AuthenticationFactory;

/** Flutter plugin for Auth0. */
public class FlutterAuth0Plugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private static final String TAG = FlutterAuth0Plugin.class.getName();
    private static final String CHANNEL = "io.flutter.plugins/auth0";
    private static final int REQUEST = 1;
    private static Result response;
    private MethodChannel channel;
    private String packageName;
    private Activity activity;
    public static Class activityClass;

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        response = result;
        if (call.method.equals("authorize")) {
            authorize(call);
        } else if (call.method.equals("parameters")) {
            getOauthParameters(result);
        } else if (call.method.equals("bundleIdentifier")) {
            getBundleIdentifier(result);
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        channel = new MethodChannel(binding.getBinaryMessenger(), CHANNEL);
        channel.setMethodCallHandler(this);
        packageName = AuthenticationFactory.getIdentifier(binding.getApplicationContext());
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        activity = binding.getActivity();
        activityClass = activity.getClass();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        activity = null;
        activityClass = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        activity = binding.getActivity();
        activityClass = activity.getClass();
    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
        activityClass = null;
    }

    @SuppressWarnings("unchecked")
    public void getBundleIdentifier(Result result) {
        result.success(packageName);
    }

    private void getOauthParameters(Result result) {
        Map<String, Object> params = AuthenticationFactory.getAuthParameters();
        result.success(params);
    }

    private void authorize(MethodCall call) {
        final String url = (String) call.arguments;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(activity, Uri.parse(url));
    }

    public static void resolve(String code, String error) {
        if (error != null) {
            response.error("ACTIVITY_FAILURE", error, null);
        }
        response.success(code);
    }

    public static Class getActivity() {
        return activityClass;
    }
}