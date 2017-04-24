package com.bmbstack.kit.app.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bmbstack.kit.app.AppEnv;
import com.bmbstack.kit.log.Logger;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;
import java.util.concurrent.Executors;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

import static com.bmbstack.kit.app.dao.DBOpenHelper.DB_NAME;

public enum DBManager {
    INST;

    private static final String TAG = DBManager.class.getSimpleName();
    private boolean mInited = false;
    private Context mContext = null;
    private DaoMaster.OpenHelper mOpenHelper = null;
    private SQLiteDatabase mDB = null;

    private DaoMaster mDaoMaster = null;
    private DaoSession mDaoSession = null;
    private Scheduler mScheduler; // DB in SingleThread
    private User mCacheUser;

    DBManager() {
    }

    public Scheduler dbScheduler() {
        return mScheduler;
    }

    public void init(Context context) {
        if (!mInited || mContext == null) {
            this.mContext = context;
            mOpenHelper = new DBOpenHelper(mContext, DB_NAME, null);
            mDB = mOpenHelper.getWritableDatabase();
            mDaoMaster = new DaoMaster(mDB);
            mDaoSession = mDaoMaster.newSession();
            mScheduler = Schedulers.from(Executors.newSingleThreadExecutor());
            QueryBuilder.LOG_SQL = AppEnv.DEBUG;
            QueryBuilder.LOG_VALUES = AppEnv.DEBUG;
        }
    }

    public void onUpgrade(SQLiteDatabase db) {
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoMaster getDaoMaster() {
        return mDaoMaster;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public void saveUser(User user) {
        UserDao userDao = mDaoSession.getUserDao();
        userDao.insertOrReplace(user);
        mCacheUser = user;
        Logger.v(TAG, "user=" + user);
    }

    public User getUser() {
        if (mCacheUser != null) {
            return mCacheUser;
        }
        UserDao userDao = mDaoSession.getUserDao();
        List<User> users = userDao.loadAll();
        if (users.size() > 0) {
            mCacheUser = users.get(0);
            return mCacheUser;
        }
        return null;
    }

    public void clearUser() {
        mCacheUser = null;
        UserDao userDao = mDaoSession.getUserDao();
        userDao.deleteAll();
    }
}