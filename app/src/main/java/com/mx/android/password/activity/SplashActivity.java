package com.mx.android.password.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.mx.android.password.R;
import com.mx.android.password.activity.base.BaseActivity;
import com.mx.android.password.entity.Constants;
import com.mx.android.password.entity.EventCenter;
import com.mx.android.password.utils.SPUtils;

public class SplashActivity extends BaseActivity {
    private static final String CREATE_LOCK_SUCCESS = "CREATE_LOCK_SUCCESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                pullActivity();
            }
        }, 0);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initToolbar() {

    }

    @Override
    protected boolean isApplyTranslucency() {
        return true;
    }

    @Override
    protected boolean isApplyButterKnife() {
        return true;
    }

    @Override
    protected boolean isApplyEventBus() {
        return false;
    }

    private void pullActivity() {
        Boolean isSuccess = (Boolean) SPUtils.get(this, CREATE_LOCK_SUCCESS, false);
        Intent intent = null;
        if (!isSuccess) {
            intent = new Intent(this, CreateLockActivity.class);
            intent.putExtra("CREATE_MODE", Constants.CREATE_GESTURE);
        } else {
            boolean isOpen = (boolean) SPUtils.get(this, Constants.SETTING.OPEN_GESTURE, true);
            if (isOpen) {
                intent = new Intent(this, CheckLockActivity.class);
            } else {
                intent = new Intent(this, PassWordActivity.class);
            }
        }
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onEventComing(EventCenter eventCenter) {

    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return false;
    }
}
