package com.bmbstack.kit.app.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bmbstack.kit.util.ToastUtils;

import org.greenrobot.greendao.database.StandardDatabase;

public class DBOpenHelper extends DaoMaster.OpenHelper {

    static final String TAG = "DBOpenHelper";
    static final String DB_NAME = "android-kit";
    private final Context mContext;

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
        mContext = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading schema from version " + oldVersion + " to " + newVersion);
        // init mDaoMaster onUpgrade
        DBManager.INST.onUpgrade(db);
        try {
            doUpgrade(db, oldVersion, newVersion);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.error("数据库升级错误，请修复!");
            reCreateDB(db);
        }
    }

    private void reCreateDB(SQLiteDatabase db) {
        DaoMaster.dropAllTables(new StandardDatabase(db), true);
        DaoMaster.createAllTables(new StandardDatabase(db), false);
    }

    private void doUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws Exception {
        // check down-grade case
        if (oldVersion > newVersion) {
            reCreateDB(db);
            return;
        }
        Log.i(TAG, "Upgrading DB Over Success oldVersion=" + oldVersion);
    }
}