package com.mx.android.password.customview;

/**
 * Created by mxuan on 2016-07-10.
 */
public interface BaseView {
    void readyGoThenKill(Class clazz);
    void kill();
    void showSnackBar(String msg);
}
