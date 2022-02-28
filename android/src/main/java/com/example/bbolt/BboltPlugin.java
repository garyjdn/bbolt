package com.example.bbolt;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import mobile.BoltDB;

/** BboltPlugin */
public class BboltPlugin implements FlutterPlugin, MethodCallHandler {
  private static BoltDB db;
  static boolean created = false;
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  /// Either registerWith or onAttachedToEngine will be called
  //@SuppressWarnings("deprecation")
  //public static void registerWith(Registrar registrar) {
  //  channel = new MethodChannel(registrar.messenger(), "bbolt");
  //  channel.setMethodCallHandler(new BboltPlugin());

  //  File appFiles = registrar.activeContext().getFilesDir();
  //  if(!created){
  //    db = new BoltDB(appFiles.getAbsolutePath());
  //    created = true;
  //  }
  //}

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "bbolt");
    channel.setMethodCallHandler(this);

    File appFiles = flutterPluginBinding.	getApplicationContext().getFilesDir();
    if(!created){
      db = new BoltDB(appFiles.getAbsolutePath());
      created = true;
    }
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("get")) {
      try {
        result.success(db.get(call.<String>argument("bucket"), call.<String>argument("key")));
      } catch (Exception e) {
        result.error("error", e.getMessage(), null);
      }
    } else if (call.method.equals("delete")) {
      try {
        db.delete(call.<String>argument("bucket"), call.<String>argument("key"));
        result.success(null);
      } catch (Exception e) {
        result.error("error", e.getMessage(), null);
      }
    } else if (call.method.equals("put")) {
      try {
        db.put(
            call.<String>argument("bucket"),
            call.<String>argument("key"),
            call.<byte[]>argument("value"));
        result.success(null);
      } catch (Exception e) {
        result.error("error", e.getMessage(), null);
      }
    } else if (call.method.equals("createBucketIfNotExists")) {
      try {
        db.createBucketIfNotExists(call.<String>argument("bucket"));
        result.success(null);
      } catch (Exception e) {
        result.error("error", e.getMessage(), null);
      }
    } else if (call.method.equals("getKeysByPrefix")) {
      try {
        result.success(
          db.getKeysByPrefix(call.<String>argument("bucket"), call.<String>argument("prefix")));
      } catch (Exception e) {
        result.error("error", e.getMessage(), null);
      }
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
