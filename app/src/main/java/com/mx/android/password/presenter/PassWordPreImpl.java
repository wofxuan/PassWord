package com.mx.android.password.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;

import com.mx.android.password.R;
import com.mx.android.password.customview.PassWordAView;
import com.mx.android.password.entity.EventCenter;
import com.mx.android.password.entity.RealmHelper;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by mxuan on 2016-07-10.
 */
public class PassWordPreImpl implements ActivityPresenter, NavigationView.OnNavigationItemSelectedListener {

    private static long DOUBLE_CLICK_TIME = 0L;
    private final Context mContext;
    private final PassWordAView mIndexView;
    private int currentSelectedItem = 0;

    public PassWordPreImpl(Context context, PassWordAView view) {
        this.mContext = context;
        mIndexView = view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mIndexView.initDrawerToggle();
        mIndexView.initXViewPager();
    }

    @Override
    public void getIntent(Intent intent) {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_backup:
                currentSelectedItem = 0;
                mIndexView.backup();
                break;
            case R.id.nav_restore:
                currentSelectedItem = 1;
                mIndexView.restore();
                break;
            case R.id.nav_setting:
                mIndexView.SysSetting();
                break;
            default:
                break;
        }
        item.setChecked(false);
        return true;
    }

    public boolean onBackPress() {
        if (System.currentTimeMillis() - DOUBLE_CLICK_TIME > 2000) {
            DOUBLE_CLICK_TIME = System.currentTimeMillis();
            mIndexView.showSnackBar("再按一次退出~~");
            return false;
        } else {
            return true;
        }
    }

    public void backup(String dirPath) {
        RealmHelper.backup(mContext, dirPath);
    }

    public void restore(String filePath) {
        RealmHelper.restore(mContext, filePath);
        EventCenter eventCenter = new EventCenter(1, true);
        EventBus.getDefault().post(eventCenter);
    }
}
