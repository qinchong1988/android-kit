package com.bmbstack.kit.app.dao;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.util.Log;

import com.bmbstack.kit.log.Logger;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public enum DBManager {
    INST;

    private static final String TAG = DBManager.class.getSimpleName();

    private Scheduler mScheduler; // DB in SingleThread
    private User mCacheUser;

    private AppDatabase mDb;

    private final AtomicBoolean mInitializing = new AtomicBoolean(true);

    public AppDatabase getDatabase() {
        return mDb;
    }

    public UserDao getOrmDao() {
        return mDb.userDao();
    }

    public void initDb(Application application) {
        Log.d(TAG, "Creating DB from " + Thread.currentThread().getName());
        if (!mInitializing.compareAndSet(true, false)) {
            return; // Already initializing
        }
        mScheduler = Schedulers.from(Executors.newSingleThreadExecutor());
        Log.d(TAG,
                "Starting init " + Thread.currentThread().getName());
        AppDatabase db = Room.databaseBuilder(application,
                AppDatabase.class, AppDatabase.DATABASE_NAME).build();
        Log.d(TAG,
                "DB was populated in thread " + Thread.currentThread().getName());
        mDb = db;
    }

    DBManager() {
    }

    public void saveUser(User user) {
        mCacheUser = user;
        Observable.just(user).observeOn(mScheduler).subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                getOrmDao().insertAll(user);
                Logger.v(TAG, "user=" + user);
            }
        });
    }

    public User getUser() {
        if (mCacheUser != null) {
            return mCacheUser;
        }
        Observable.empty().observeOn(mScheduler).subscribe(new Observer<Object>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object value) {
                List<User> users = getOrmDao().getAll();
                if (users.size() > 0) {
                    mCacheUser = users.get(0);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        return null;
    }

    public void clearUser() {
        mCacheUser = null;
        Single.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                getOrmDao().delete(getOrmDao().getAll());
                return null;
            }
        }).observeOn(mScheduler);
    }
}