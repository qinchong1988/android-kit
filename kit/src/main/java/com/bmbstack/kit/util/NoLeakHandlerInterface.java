package com.bmbstack.kit.util;

import android.os.Message;

public interface NoLeakHandlerInterface {
  boolean isValid();

  void handleMessage(Message msg);
}
