package com.mx.android.password.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.mx.android.password.R;
import com.mx.android.password.activity.base.BaseSwipeBackActivity;
import com.mx.android.password.entity.EventCenter;
import com.mx.android.password.fragment.SettingFragment;

public class SettingActivity extends BaseSwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFragment();
    }
    private void setFragment() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new SettingFragment())
                .commit();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_setting;
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

    @Override
    protected void onEventComing(EventCenter eventCenter) {

    }

    @Override
    protected void initToolbar() {
        Toolbar mToolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.common_toolbar);
        initToolBar(mToolBar);
        mToolBar.setTitle("设置");
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return true;
    }
}
