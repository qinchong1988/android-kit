package com.bmbstack.kit.app;

import com.bmbstack.kit.util.Utils;

/**
 * Created by wangming on 4/18/17.
 */

public class App extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
