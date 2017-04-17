package com.bmbstack.kit.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bmbstack.kit.umeng.AuthCallback;
import com.bmbstack.kit.umeng.QQInfo;
import com.bmbstack.kit.umeng.ShareCallback;
import com.bmbstack.kit.umeng.SinaInfo;
import com.bmbstack.kit.umeng.UmengUtils;
import com.bmbstack.kit.umeng.WeixinInfo;
import com.bmbstack.kit.util.ChannelUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.text)
    Button text;
    @Bind(R.id.music)
    Button music;
    @Bind(R.id.video)
    Button video;
    @Bind(R.id.activity_main)
    LinearLayout activityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Umeng设置
        String umengChannelId = ChannelUtils.getChannel(this);
        UmengUtils.init(getApplicationContext(),
                umengChannelId, true,
                SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE);
        UmengUtils.setKeySecretWeixin("wx70dd76d9bc92354e", "8b6b66781eb561a29ff73e39817e7d26");
        UmengUtils.setKeySecretQQ("1106036220", "FFxH3KPMjVwAl7Y1");

        ButterKnife.bind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengUtils.analysisOnPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengUtils.analysisOnResume(this);
    }

    public void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.text, R.id.music, R.id.video, R.id.login_qq, R.id.login_sina, R.id.login_weixin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text:
                UmengUtils.shareTxt(this, "uid", "title", "this is content", "https://gold.xitu.io/",
                        "http://static.codeceo.com/images/2015/02/34426f99991154e63015e9e0278638ee.jpg",
                        new ShareCallback() {
                            @Override
                            public void onResult(SHARE_MEDIA share_media) {
                                toast("success" + share_media.toString());

                            }

                            @Override
                            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                                toast("onError" + share_media.toString());
                            }

                            @Override
                            public void onCancel(SHARE_MEDIA share_media) {
                                toast("onCancel" + share_media.toString());
                            }
                        });
                break;
            case R.id.music:
                UmengUtils.shareMusic(this, "uid", "风吹麦浪", "李健情歌小王子", "http://pic2.ooopic.com/11/45/78/11b1OOOPICc9.jpg",
                        "http://static-dev.qxinli.com/audio/248_20170206_161304/MP3File.mp3", "https://www.baidu.com/",
                        new ShareCallback() {
                            @Override
                            public void onResult(SHARE_MEDIA share_media) {
                                toast("success" + share_media.toString());

                            }

                            @Override
                            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                                toast("onError" + share_media.toString());
                            }

                            @Override
                            public void onCancel(SHARE_MEDIA share_media) {
                                toast("onCancel" + share_media.toString());
                            }
                        });
                break;
            case R.id.video:
                UmengUtils.shareVideo(this, "uid", "劲爆视频", "给力女司机停车转坏八两奥迪", "http://static.codeceo.com/images/2015/02/34426f99991154e63015e9e0278638ee.jpg",
                        "http://v1.365yg.com/9fc5496a036e2fe82bf813d96fbedaaf/58a2d09f/video/m/220cce02e12c941430fa3f15a7d110f7bc0114320100001a607842bbc9/",
                        "http://www.toutiao.com/a6386833685304312066/",
                        new ShareCallback() {
                            @Override
                            public void onResult(SHARE_MEDIA share_media) {
                                toast("success" + share_media.toString());
                            }

                            @Override
                            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                                toast("onError" + share_media.toString());
                            }

                            @Override
                            public void onCancel(SHARE_MEDIA share_media) {
                                toast("onCancel" + share_media.toString());
                            }
                        });


                break;
            case R.id.login_qq:
                UmengUtils.loginByQQ(this, new AuthCallback<QQInfo>() {
                    @Override
                    public void onComplete(int var2, QQInfo info) {
                        Log.e("dd", info.toString());

                    }

                    @Override
                    public void onError(int var2, Throwable var3) {

                    }

                    @Override
                    public void onCancel(int var2) {

                    }
                });
                break;
            case R.id.login_sina:
                UmengUtils.loginBySina(this, new AuthCallback<SinaInfo>() {
                    @Override
                    public void onComplete(int var2, SinaInfo info) {
                        Log.e("dd", info.toString());

                    }

                    @Override
                    public void onError(int var2, Throwable var3) {

                    }

                    @Override
                    public void onCancel(int var2) {

                    }
                });
                break;
            case R.id.login_weixin:
                UmengUtils.loginByWeixin(this, new AuthCallback<WeixinInfo>() {
                    @Override
                    public void onComplete(int var2, WeixinInfo info) {
                        Log.e("dd", info.toString());

                    }

                    @Override
                    public void onError(int var2, Throwable var3) {

                    }

                    @Override
                    public void onCancel(int var2) {

                    }
                });
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengUtils.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UmengUtils.onDestroy(this);
    }
}
