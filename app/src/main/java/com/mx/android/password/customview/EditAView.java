package com.mx.android.password.customview;

import android.view.MenuItem;

import com.mx.android.password.entity.God;

import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * Created by mxuan on 2016-07-12.
 */
public interface EditAView {
    void initCreateModel();
    void initEditModel();
    void initViewModel(God god, int positionType);
    String getTitleName();
    String getUserName();
    String getPassWord();
    String getMemoInfo();
    byte[] getImg();
    void setTime(String time);
    void showSnackToast(String msg);
    void setItemMenuVisible(boolean visible, int menuItemId);
    void finishActivity();
    void hideKeyBoard();
    void setToolBarTitle(int resId);
    void showDialog(String msg, String positiveMsg);
    SwipeBackLayout getSwipeBack();
}

