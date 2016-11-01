package com.mx.android.password.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.mx.android.password.R;
import com.mx.android.password.activity.PassWordActivity;
import com.mx.android.password.customview.CreateLockAView;
import com.mx.android.password.entity.Constants;
import com.mx.android.password.entity.LockBean;
import com.mx.android.password.utils.LockPatternUtils;
import com.mx.android.password.utils.SPUtils;
import com.mx.android.password.utils.ShortLockPatternUtils;
import com.mx.android.password.widget.LockPatternView;

import java.util.List;

/**
 * Created by mxuan on 2016-07-10.
 */
public class CreateLockActivityImpl implements ActivityPresenter {
    private static final String CREATE_LOCK_SUCCESS = "CREATE_LOCK_SUCCESS";
    private final Context mContext;
//    private final ActivityCreateLockBinding mBinding;
    private LockBean lockBeanText;
    private final CreateLockAView mCreateLockAView;
    private boolean isFinishOnce = false;
    private int createMode;

    public CreateLockActivityImpl(Context context, CreateLockAView view) {
        mContext = context;
        mCreateLockAView = view;
//        mBinding = binding;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        lockBeanText = new LockBean(mContext.getString(R.string.create_activity_warn));
        lockBeanText.setColor(Color.WHITE);
        mCreateLockAView.showMsg(lockBeanText);
        mCreateLockAView.initLockPatternView();
    }

    @Override
    public void getIntent(Intent intent) {
        createMode = intent.getIntExtra("CREATE_MODE", Constants.CREATE_MODE);
    }
    public void fingerPress() {
        lockBeanText.setWarn(mContext.getString(R.string.finger_press));
        mCreateLockAView.showMsg(lockBeanText);
//        mBinding.setLockWarn(lockBeanText);
    }

    public void fingerFirstUpError() {
        lockBeanText.setWarn(mContext.getString(R.string.finger_up_first_error));
        mCreateLockAView.showMsg(lockBeanText);
//        mBinding.setLockWarn(lockBeanText);
    }

    public void fingerFirstUpSuccess() {
        lockBeanText.setWarn(mContext.getString(R.string.finger_up_first_success));
        mCreateLockAView.showMsg(lockBeanText);
//        mBinding.setLockWarn(lockBeanText);
    }

    public void fingerSecondUpError() {
        lockBeanText.setWarn(mContext.getString(R.string.finger_up_second_error));
        mCreateLockAView.showMsg(lockBeanText);
//        mBinding.setLockWarn(lockBeanText);
    }

    public void fingerSecondUpSucess() {
        lockBeanText.setWarn(mContext.getString(R.string.finger_up_second_success));
        mCreateLockAView.showMsg(lockBeanText);
//        mBinding.setLockWarn(lockBeanText);
    }

    public void check(List<LockPatternView.Cell> pattern) {
        if (pattern == null) return;

        if (pattern.size() < LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {

            if (!isFinishOnce) {
                fingerFirstUpError();
            } else {
                fingerSecondUpError();
            }
            mCreateLockAView.lockDisplayError();

        } else {

            if (!isFinishOnce) {
                fingerFirstUpSuccess();
                LockPatternUtils instances = LockPatternUtils.getInstances(mContext);
                ShortLockPatternUtils shortLockPatternUtils = ShortLockPatternUtils.getInstances(mContext);
                if (createMode == Constants.CREATE_GESTURE) {
                    instances.saveLockPattern(pattern);
                } else if (createMode == Constants.UPDATE_GESTURE) {
                    shortLockPatternUtils.saveLockPattern(pattern);
                }
                mCreateLockAView.clearPattern();
                isFinishOnce = true;
            } else {
                LockPatternUtils instances = LockPatternUtils.getInstances(mContext);
                ShortLockPatternUtils shortLockPatternUtils = ShortLockPatternUtils.getInstances(mContext);
                if (createMode == Constants.CREATE_GESTURE) {

                    if (instances.checkPattern(pattern)) {
                        fingerSecondUpSucess();
                        SPUtils.put(mContext, CREATE_LOCK_SUCCESS, true);
                        mCreateLockAView.readyGoThenKill(PassWordActivity.class);
                    } else {
                        fingerSecondUpError();
                        mCreateLockAView.lockDisplayError();
                    }
                    isFinishOnce = false;
                } else if (createMode == Constants.UPDATE_GESTURE) {
                    if (shortLockPatternUtils.checkPattern(pattern)) {
                        instances.saveLockPattern(pattern);
                        fingerSecondUpSucess();
                        SPUtils.put(mContext, CREATE_LOCK_SUCCESS, true);
                        mCreateLockAView.setResults(1);
                        mCreateLockAView.kill();
                    } else {
                        fingerSecondUpError();
                        mCreateLockAView.lockDisplayError();
                    }
                    isFinishOnce = false;
                }
            }
        }
    }

    public void onBack() {
        if (createMode == Constants.UPDATE_GESTURE) {
            mCreateLockAView.setResults(0);
        }
    }
}
