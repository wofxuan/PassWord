package com.mx.android.password.customview;

/**
 * Created by mxuan on 2016-07-10.
 */
public interface PassWordAView {
    void initDrawerToggle();
    void initXViewPager();
    void readyGoForResult(Class clazz);
    void SysSetting();
    void showSnackBar(String msg);
    void backup();
    void restore();
    void kill();
    void closeNavTool();
}
