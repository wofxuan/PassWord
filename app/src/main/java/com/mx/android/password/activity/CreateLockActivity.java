package com.mx.android.password.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.mx.android.password.R;
import com.mx.android.password.activity.base.BaseActivity;
import com.mx.android.password.customview.CreateLockAView;
import com.mx.android.password.entity.EventCenter;
import com.mx.android.password.entity.LockBean;
import com.mx.android.password.presenter.CreateLockActivityImpl;
import com.mx.android.password.widget.LockPatternView;

import java.util.List;

public class CreateLockActivity extends BaseActivity implements CreateLockAView, LockPatternView.OnPatternListener {

    private CreateLockActivityImpl mLockActivity;
    private LockPatternView mLockPatternView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLockPatternView = (LockPatternView)findViewById(R.id.lockPatternView);
        mLockActivity = new CreateLockActivityImpl(this, this);
        mLockActivity.onCreate(savedInstanceState);
        mLockActivity.getIntent(getIntent());
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_create_lock;
    }

    @Override protected void initToolbar() {

    }

    @Override protected boolean isApplyTranslucency() {
        return true;
    }

    @Override protected boolean isApplyButterKnife() {
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
    public void setResults(int isSuccess) {
        setResult(isSuccess);
    }

    @Override
    public void clearPattern() {
        mLockPatternView.clearPattern();
    }

    @Override
    public void showMsg(LockBean lockBean) {
        TextView textView = (TextView)findViewById(R.id.showMsg);
        textView.setTextColor(lockBean.getColor());
        textView.setText(lockBean.getWarn());
    }

    @Override
    public void readyGoThenKill(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        finish();
    }
    @Override
    public void kill() {
        finish();
    }

    @Override
    public void showSnackBar(String msg) {
//        Snackbar.make(mLockPatternView, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override public void onPatternStart() {
        mLockActivity.fingerPress();
    }

    @Override public void onPatternCleared() {

    }

    @Override public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

    }

    @Override public void onPatternDetected(List<LockPatternView.Cell> pattern) {
        mLockActivity.check(pattern);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mLockActivity.onBack();
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
