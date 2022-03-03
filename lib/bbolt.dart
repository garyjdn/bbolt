import 'dart:async';
import 'dart:typed_data';
import 'dart:convert';

import 'package:flutter/services.dart';

import 'helpers.dart';

class Bbolt {
  static const MethodChannel _channel = MethodChannel('bbolt');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<Uint8List?> getKey(String bucket, String key) async {
    final Uint8List? value =
        await _channel.invokeMethod('get', {"bucket": bucket, "key": key});
    log("received value for key $key");
    return value;
  }

  static Future<void> putKey(String bucket, String key, Uint8List value) async {
    await _channel.invokeMethod('put', {
      "bucket": bucket,
      "key": key,
      "value": value,
    });
    log("put value for key $key");
  }

  static Future<void> deleteKey(String bucket, String key) async {
    await _channel.invokeMethod('delete', {"bucket": bucket, "key": key});
    log("delete key $key");
  }

  static Future<void> createBucketIfNotExists(String bucket) async {
    log("creating bucket $bucket");
    await _channel.invokeMethod('createBucketIfNotExists', {"bucket": bucket});
    log("created bucket $bucket");
  }

  static Future<List<String>> getKeysByPrefix(
    String bucket,
    String prefix,
  ) async {
    final Uint8List? encodedKeysList = await _channel
        .invokeMethod('getKeysByPrefix', {"bucket": bucket, "prefix": prefix});

    if (encodedKeysList == null) {
      return <String>[];
    }

    // Parse a list of keys from the returned byte array. The keys are null
    // separated.
    var keys = <String>[];
    var currentKey = <int>[];
    for (var k in encodedKeysList) {
      if (k == 0) {
        keys.add(utf8.decode(currentKey));
        currentKey.clear();
      } else {
        currentKey.add(k);
      }
    }
    return keys;
  }
}
