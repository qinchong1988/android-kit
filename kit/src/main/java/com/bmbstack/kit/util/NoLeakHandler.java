package com.bmbstack.kit.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

public class NoLeakHandler implements NoLeakHandlerInterface {
  private final NoLeakHandlerInterface host;
  private final WeakRefHandler handler;

  public NoLeakHandler() {
    host = this;
    handler = new WeakRefHandler(host);
  }

  public NoLeakHandler(NoLeakHandlerInterface host) {
    this.host = host;
    handler = new WeakRefHandler(this.host);
  }

  public NoLeakHandler(Looper looper) {
    host = this;
    handler = new WeakRefHandler(looper, host);
  }

  public NoLeakHandler(Looper looper, NoLeakHandlerInterface host) {
    this.host = host;
    handler = new WeakRefHandler(looper, this.host);
  }

  public final Handler handler() {
    return handler;
  }

  private final NoLeakHandlerInterface innerHandler() {
    return host;
  }

  public final void removeMessages(int what) {
    handler().removeMessages(what);
  }

  public final void post(Runnable r) {
    handler().post(r);
  }

  public final void postDelayed(Runnable r, long delayMillis) {
    handler().postDelayed(r, delayMillis);
  }

  public final boolean sendEmptyMessageDelayed(int what, long delayMillis) {
    return handler().sendEmptyMessageDelayed(what, delayMillis);
  }

  public final boolean sendEmptyMessageDelayedWithRef(int what, long delayMillis) {
    Message msg = Message.obtain(handler(), what, innerHandler());
    return handler().sendMessageDelayed(msg, delayMillis);
  }

  public final Message obtainMessage(int what, int arg1, int arg2) {
    return handler().obtainMessage(what, arg1, arg2);
  }

  public final boolean sendMessage(Message msg) {
    return handler().sendMessage(msg);
  }

  public final boolean sendMessageAtFrontOfQueue(Message msg) {
    return handler().sendMessageAtFrontOfQueue(msg);
  }

  public final boolean sendEmptyMessage(int what) {
    return handler().sendEmptyMessage(what);
  }

  public final void removeCallbacksAndMessages(Object token) {
    handler().removeCallbacksAndMessages(token);
  }

  public final boolean hasMessages(int what) {
    return handler().hasMessages(what);
  }

  public final Message obtainMessage() {
    return handler().obtainMessage();
  }

  public final Message obtainMessage(int what) {
    return handler().obtainMessage(what);
  }

  public final Message obtainMessage(int what, Object obj) {
    return handler().obtainMessage(what, obj);
  }

  public final Message obtainMessage(int what, int arg1, int arg2, Object obj) {
    return handler().obtainMessage(what, arg1, arg2, obj);
  }

  public final Looper getLooper() {
    return handler().getLooper();
  }

  public final boolean sendMessageDelayed(Message msg, long delayMillis) {
    return handler().sendMessageDelayed(msg, delayMillis);
  }

  @Override public boolean isValid() {
    return true;
  }

  @Override public void handleMessage(Message msg) {
  }

  private static class WeakRefHandler extends Handler {
    private final WeakReference<NoLeakHandlerInterface> host;

    public WeakRefHandler(NoLeakHandlerInterface host) {
      this.host = new WeakReference<>(host);
    }

    public WeakRefHandler(Looper looper, NoLeakHandlerInterface host) {
      super(looper);
      this.host = new WeakReference<>(host);
    }

    @Override public void handleMessage(Message msg) {
      NoLeakHandlerInterface host = (null != this.host) ? this.host.get() : null;
      if ((null != host) && host.isValid()) {
        host.handleMessage(msg);
      }
    }
  }
}
