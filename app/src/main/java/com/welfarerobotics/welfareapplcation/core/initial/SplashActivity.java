package com.welfarerobotics.welfareapplcation.core.initial;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.google.firebase.database.FirebaseDatabase;
import com.kinda.alert.KAlertDialog;
import com.welfarerobotics.welfareapplcation.R;
import com.welfarerobotics.welfareapplcation.core.BaseActivity;
import com.welfarerobotics.welfareapplcation.core.main.MainActivity;
import com.welfarerobotics.welfareapplcation.entity.Server;
import com.welfarerobotics.welfareapplcation.entity.ServerCache;
import com.welfarerobotics.welfareapplcation.entity.User;
import com.welfarerobotics.welfareapplcation.entity.UserCache;
import com.welfarerobotics.welfareapplcation.util.DeviceId;
import com.welfarerobotics.welfareapplcation.util.FirebaseHelper;
import com.welfarerobotics.welfareapplcation.util.NetworkUtil;
import com.welfarerobotics.welfareapplcation.util.Sound;

public class SplashActivity extends BaseActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Sound.get().start(this, R.raw.intro); // 효과음 재생
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_fade_in);
        findViewById(R.id.welfare_logo).startAnimation(fadeInAnimation); // 애니메이션 재생
        SharedPreferences pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
        boolean firstUser = pref.getBoolean("isFirst", true);

        Handler handler = new Handler();
        if (firstUser) {
            handler.postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, GreetingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }, 3000);
        } else {
            if (NetworkUtil.isOnline(this)) {
                FirebaseHelper.get().connect(FirebaseDatabase.getInstance().getReference("server"), snapshot -> {
                    Server server = snapshot.getValue(Server.class);
                    ServerCache.setInstance(server);
                    FirebaseHelper.get().connect(FirebaseDatabase.getInstance().getReference("user")
                            .child(DeviceId.getInstance(getApplicationContext()).getUUID()), dataSnapshots -> {
                        User user = dataSnapshots.getValue(User.class);
                        UserCache.setInstance(user);
                        handler.postDelayed(() -> {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }, 2000);
                    });
                });
            } else {
                handler.postDelayed(() -> {
                    KAlertDialog pDialog = new KAlertDialog(this);
                    pDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                    pDialog.setTitleText("네트워크 연결 에러");
                    pDialog.setContentText("와이파이 네트워크에 연결해야\n원활한 사용이 가능합니다.");
                    pDialog.setCancelable(false);
                    pDialog.setConfirmText("확인");
                    pDialog.setConfirmClickListener(kAlertDialog -> startActivity(new Intent(this, InitialWifiActivity.class)));
                    pDialog.show();
                }, 2000);
            }
        }
    }
}