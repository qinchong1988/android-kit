package com.bmbstack.kit.app;

import android.os.SystemClock;

import com.bmbstack.kit.app.dao.DBManager;
import com.bmbstack.kit.log.Logger;
import com.bmbstack.kit.umeng.UmengUtils;
import com.bmbstack.kit.util.ChannelUtils;
import com.bmbstack.kit.util.ResourceUtils;
import com.facebook.stetho.Stetho;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by wangming on 4/18/17.
 */

public class App extends BaseApplication {

    private static final String TAG = "App";
    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        Logger.v(TAG, "initInApp begin");
        long st = SystemClock.uptimeMillis();

        // Umeng设置
        String umengChannelId = ChannelUtils.getChannel(this);
        UmengUtils.setKeySecretWeixin("wx967daebe835fbeac", "5bb696d9ccd75a38c8a0bfe0675559b3");
        UmengUtils.setKeySecretQQ("100424468", "c7394704798a158208a74ab60104f0ba");
        UmengUtils.init(getApplicationContext(),
                umengChannelId, true,
                SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE);

        AppEnv.DEBUG = BuildConfig.DEBUG;

        Client.init(this);
        Client.requestChannel(ResourceUtils.getMetaDataValue(this, "BaiduMobAd_CHANNEL"));

        AppHook.checkUpgrade();

        if (AppEnv.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

        DBManager.INST.initDb(this);

        Logger.v(TAG, "initInApp end cost = " + (SystemClock.uptimeMillis() - st) + " ms");
    }

    public static App instance() {
        return mInstance;
    }
}
