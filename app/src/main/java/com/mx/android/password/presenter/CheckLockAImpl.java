package com.mx.android.password.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.mx.android.password.R;
import com.mx.android.password.activity.PassWordActivity;
import com.mx.android.password.customview.CheckLockAView;
import com.mx.android.password.entity.LockBean;
import com.mx.android.password.utils.LockPatternUtils;
import com.mx.android.password.widget.LockPatternView;

import java.util.List;

/**
 * Created by Administrator on 2016-07-17.
 */
public class CheckLockAImpl implements ActivityPresenter {
    private final Context mContext;
    private final CheckLockAView mCheckView;
    private LockBean lockBean;

    public CheckLockAImpl(Context context, CheckLockAView view) {
        mContext = context;
        mCheckView = view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        lockBean = new LockBean(mContext.getString(R.string.check_default));
        lockBean.setColor(ContextCompat.getColor(mContext, R.color.actionbar_title_color));
        mCheckView.showMsg(lockBean);
        mCheckView.initLockPatternView();
    }

    @Override
    public void getIntent(Intent intent) {

    }

    public void check(List<LockPatternView.Cell> pattern) {
        if (pattern == null) return;

        LockPatternUtils instances = LockPatternUtils.getInstances(mContext);
        if (instances.checkPattern(pattern)) {
            mCheckView.readyGoThenKill(PassWordActivity.class);
        } else {
            mCheckView.lockDisplayError();
            lockBean.setWarn(mContext.getString(R.string.check_error));
            lockBean.setColor(ContextCompat.getColor(mContext, R.color.text_red));

            mCheckView.showMsg(lockBean);
        }
    }
}
