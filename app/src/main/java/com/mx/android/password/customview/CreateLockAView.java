package com.mx.android.password.customview;

import com.mx.android.password.entity.LockBean;

/**
 * Created by mxuan on 2016-07-10.
 */
public interface CreateLockAView extends BaseView {

    void initLockPatternView();

    void lockDisplayError();

    void setResults(int isSuccess);

    void clearPattern();

    void showMsg(LockBean lockBean);

}
