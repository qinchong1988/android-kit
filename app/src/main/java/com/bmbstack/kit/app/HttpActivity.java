package com.bmbstack.kit.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bmbstack.kit.util.SizeUtils;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrListener;
import com.r0adkll.slidr.model.SlidrPosition;

/**
 * Created by wangming on 4/18/17.
 */

public class HttpActivity extends BaseActivity {

    public static void launch(Activity activity) {
        activity.startActivity(new Intent(activity, HttpActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);


        // Build the slidr config
        int primary = getResources().getColor(R.color.colorPrimaryDark);
        int secondary = getResources().getColor(R.color.colorPrimary);
        SlidrConfig config = new SlidrConfig.Builder()
                .primaryColor(primary)
                .secondaryColor(secondary)
                .position(SlidrPosition.LEFT)
                .scrimEndAlpha(0f)
                .velocityThreshold(2400)
                .distanceThreshold(0.25f)
                .edge(true)
                .edgeSize(0.18f) // The % of the screen that counts as the edge, default 18%
                .touchSize(SizeUtils.dp2px(32))
                .listener(new SlidrListener() {
                    @Override
                    public void onSlideStateChanged(int state) {
                        setStatusBar();
                    }

                    @Override
                    public void onSlideChange(float percent) {
                    }

                    @Override
                    public void onSlideOpened() {
                    }

                    @Override
                    public void onSlideClosed() {
                    }
                })
                .build();

        // Attach the Slidr Mechanism to this activity
        Slidr.attach(this, config);
    }
}
