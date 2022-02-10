import 'package:flutter/foundation.dart';

void log(Object message) {
  if (kDebugMode) {
    print(message is String ? message : message.toString());
  }
}
