package com.mx.android.password.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.mx.android.password.R;
import com.mx.android.password.activity.base.BaseActivity;
import com.mx.android.password.customview.CheckLockAView;
import com.mx.android.password.entity.EventCenter;
import com.mx.android.password.entity.LockBean;
import com.mx.android.password.presenter.CheckLockAImpl;
import com.mx.android.password.widget.LockPatternView;

import java.util.List;

public class CheckLockActivity extends BaseActivity implements CheckLockAView, LockPatternView.OnPatternListener {

    LockPatternView mLockPatternView;
    private CheckLockAImpl mCheckLockA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLockPatternView = (LockPatternView) findViewById(R.id.lockPatternView);
        mCheckLockA = new CheckLockAImpl(this, this);
        mCheckLockA.onCreate(savedInstanceState);
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }

    @Override
    public void showMsg(LockBean lockBean) {
        TextView textView = (TextView) findViewById(R.id.show_text);
        textView.setTextColor(lockBean.getColor());
        textView.setText(lockBean.getWarn());
        textView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_x));
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return false;
    }

    @Override
    protected void onEventComing(EventCenter eventCenter) {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_check_lock;
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

    @Override
    public void initLockPatternView() {
        mLockPatternView.setOnPatternListener(this);
    }


    @Override
    public void lockDisplayError() {
        mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
    }

    @Override
    public void onPatternStart() {
    }

    @Override
    public void onPatternCleared() {

    }

    @Override
    public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

    }

    @Override
    public void onPatternDetected(List<LockPatternView.Cell> pattern) {
        mCheckLockA.check(pattern);
    }

    @Override
    public void showSnackBar(String msg) {

    }
    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(0, 0);
    }

    @Override
    public void readyGoThenKill(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        finish();
    }

    @Override
    public void kill() {

    }
}
