package com.mx.android.password.customview;

import com.mx.android.password.entity.LockBean;

/**
 * Created by Administrator on 2016-07-17.
 */
public interface CheckLockAView extends BaseView {
    void initLockPatternView();

    void lockDisplayError();

    void showMsg(LockBean lockBean);
}
