package com.bmbstack.kit.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bmbstack.kit.api.APIHandler;
import com.bmbstack.kit.app.account.AccountMgr;
import com.bmbstack.kit.app.api.API;
import com.bmbstack.kit.app.api.CreateUser;
import com.bmbstack.kit.app.api.Home;
import com.bmbstack.kit.app.api.WeightToday;
import com.bmbstack.kit.app.storage.CommonTraySp;
import com.bmbstack.kit.util.SizeUtils;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrListener;
import com.r0adkll.slidr.model.SlidrPosition;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangming on 4/18/17.
 */

public class HttpActivity extends BaseActivity {

    @BindView(R.id.btHome)
    Button btHome;
    @BindView(R.id.tvHome)
    TextView tvHome;

    @BindView(R.id.btCreateUser)
    Button btCreateUser;
    @BindView(R.id.tvCreateUser)
    TextView tvCreateUser;

    @BindView(R.id.btGetWeight)
    Button btGetWeight;
    @BindView(R.id.tvGetWeight)
    TextView tvGetWeight;

    public static void launch(Activity activity) {
        activity.startActivity(new Intent(activity, HttpActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);

        ButterKnife.bind(this);

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

        btHome.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //API test
                API.INST.home(APIHandler.createObserver(HttpActivity.this, true, new APIHandler.APIObserver<Home.Resp>() {
                    @Override
                    public void onSuccess(Home.Resp value) {
                        tvHome.setText(value.data.title);
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
            }
        });

        btCreateUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final CreateUser.Req req = new CreateUser.Req();
                req.accountType = "weixin";
                req.openID = "olDvtjkAgHqEuAKxCyXuSseSLE-w";
                req.nickname = "王明";
                req.headPhoto = "http://q3.qlogo.cn/g?b=qq&k=5Aic8NYK3WfAibOTkmAPiagfg&s=100&t=1483302448";
                req.sex = 1;
                req.city = "北京市";
                //API test
                API.INST.createUser(req, APIHandler.createObserver(HttpActivity.this, true, new APIHandler.APIObserver<CreateUser.Resp>() {
                    @Override
                    public void onSuccess(CreateUser.Resp value) {
                        tvCreateUser.setText(value.data.token);
                        if (value.isValid()) {
                            value.data.user.setToken(value.data.token);
                            AccountMgr.getInstance().saveUser(value.data.user);
                            CommonTraySp.saveThirdLoginInfo(req);
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
            }
        });

        btGetWeight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //API test
                API.INST.weightToday(APIHandler.createObserver(HttpActivity.this, true, new APIHandler.APIObserver<WeightToday>() {
                    @Override
                    public void onSuccess(WeightToday value) {
                        tvGetWeight.setText(String.valueOf(value.data.weight));
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
            }
        });
    }


}
