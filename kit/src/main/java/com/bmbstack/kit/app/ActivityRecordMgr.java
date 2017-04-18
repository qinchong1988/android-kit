package com.bmbstack.kit.app;

import android.app.Activity;

import com.bmbstack.kit.log.Logger;

import java.util.ArrayList;
import java.util.List;

public class ActivityRecordMgr {

  private static final String TAG = ActivityRecordMgr.class.getSimpleName();

  private ActivityRecordMgr() {
  }

  public static ActivityRecordMgr getInstance() {
    return InstanceHolder.INSTANCE;
  }

  public int size() {
    return mAppRecords.size();
  }

  private static class InstanceHolder {
    private static ActivityRecordMgr INSTANCE = new ActivityRecordMgr();
  }

  private List<ActivityRecord> mAppRecords = new ArrayList<>();

  enum ActivityState {
    onCreate(10), onStart(8), onResume(6), onPause(4), onStop(2), onDestroy(0);

    ActivityState(int level) {
      int vLevel = level;
    }

    int vLevel = -1; //可视等级
  }

  public static class ActivityRecord {
    Activity activity;
    ActivityState state;

    public ActivityRecord(Activity activity, ActivityState state) {
      this.activity = activity;
      this.state = state;
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ActivityRecord that = (ActivityRecord) o;
      return activity.equals(that.activity);
    }

    @Override public int hashCode() {
      return activity.hashCode();
    }

    @Override public String toString() {
      return "ActivityRecord{" +
          "activity=" + activity +
          ", state=" + state +
          '}';
    }
  }

  private synchronized void dump() {
    for (int i = mAppRecords.size() - 1; i >= 0; i--) {
      ActivityRecord record = mAppRecords.get(i);
      Logger.v(TAG, "Activity Stack[" + i + "]" + record.activity + " # lifeState=" + record.state);
    }
  }

  /**
   * 判断当前Activity处于Resumed状态
   */
  private synchronized boolean isAppResumed() {
    for (int i = mAppRecords.size() - 1; i >= 0; i--) {
      ActivityRecord record = mAppRecords.get(i);
      if (record.state == ActivityState.onResume) {
        return true;
      }
    }
    return false;
  }

  private synchronized void changeState(Activity activity, ActivityState state) {
    for (int i = mAppRecords.size() - 1; i >= 0; i--) {
      ActivityRecord record = mAppRecords.get(i);
      if (record.activity == activity) {
        record.state = state;
        break;
      }
    }
    dump();
  }

  public synchronized void onCreate(Activity activity) {
    if (activity == null) {
      return;
    }
    mAppRecords.add(new ActivityRecord(activity, ActivityState.onCreate));
    dump();
  }

  public void onStart(Activity activity) {
    changeState(activity, ActivityState.onStart);
  }

  public void onResume(Activity activity) {
    if (activity == null) {
      return;
    }
    changeState(activity, ActivityState.onResume);
  }

  public void onPause(Activity activity) {
    if (activity == null) {
      return;
    }
    changeState(activity, ActivityState.onPause);
  }

  public void onStop(Activity activity) {
    if (activity == null) {
      return;
    }
    changeState(activity, ActivityState.onStop);
  }

  public void onDestroy(Activity activity) {
    if (activity == null) {
      return;
    }
    removeActivity(activity);
  }

  private synchronized void removeActivity(Activity activity) {
    for (int i = mAppRecords.size() - 1; i >= 0; i--) {
      ActivityRecord record = mAppRecords.get(i);
      if (record.activity == activity) {
        Logger.v(TAG, "remove Activity stack[" + i + "]" + record.activity);
        mAppRecords.remove(i);
        break;
      }
    }
  }

  public synchronized void finishAllActivity() {
    List<ActivityRecord> toFinishActivity = new ArrayList<>();
    toFinishActivity.addAll(mAppRecords);
    for (ActivityRecord record : toFinishActivity) {
      Logger.v(TAG, "finish " + record);
      record.activity.finish();
    }
  }
}