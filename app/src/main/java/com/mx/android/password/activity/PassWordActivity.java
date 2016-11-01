package com.mx.android.password.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;
import com.mx.android.password.R;
import com.mx.android.password.activity.base.BaseActivity;
import com.mx.android.password.adapter.PWContentAdapter;
import com.mx.android.password.customview.PassWordAView;
import com.mx.android.password.entity.Constants;
import com.mx.android.password.entity.EventCenter;
import com.mx.android.password.presenter.PassWordPreImpl;
import com.mx.android.password.utils.DFSelectActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

public class PassWordActivity extends BaseActivity implements PassWordAView {
    private static final int INDEX_REQUEST_CODE = 1;
    private static final int SETTING_REQUEST_CODE = 2;
    private static final int BACKUP_REQUEST_CODE = 3;
    private static final int RESTORE_REQUEST_CODE = 4;
    private static final int EDIT_SAVE = 1;
    private int SUCCESS = 1;
    private PassWordPreImpl mIndexPre;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private int INDEX_EVENT_SUCCESS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIndexPre = new PassWordPreImpl(this, this);
        mIndexPre.onCreate(savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        RxView.clicks(fab).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe((aVoid) -> this.readyGoForResult(EditActivity.class));

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
//        navigationView.setCheckedItem(R.id.nav_login_type);
        navigationView.setNavigationItemSelectedListener(mIndexPre);
    }

    @Override
    protected void initToolbar() {
        Toolbar  mToolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.common_toolbar);

        super.initToolBar(mToolBar);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_pass_word;
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
        return true;
    }

    @Override
    public void initDrawerToggle() {
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, (DrawerLayout) findViewById(R.id.drawerLayout),
                (Toolbar) findViewById(R.id.common_toolbar), 0, 0) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        ((DrawerLayout) findViewById(R.id.drawerLayout)).setDrawerListener(mActionBarDrawerToggle);
    }

    @Override
    public void initXViewPager() {
        ((ViewPager) findViewById(R.id.content)).setOffscreenPageLimit(3);

        PWContentAdapter pWContentAdapter = new PWContentAdapter(getSupportFragmentManager());
        ((ViewPager) findViewById(R.id.content)).setAdapter(pWContentAdapter);
    }

    @Override
    public void readyGoForResult(Class clazz) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra("CREATE_MODE", Constants.CREATE_MODE);
        startActivityForResult(intent, INDEX_REQUEST_CODE);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mActionBarDrawerToggle != null) {
            mActionBarDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mActionBarDrawerToggle != null) {
            mActionBarDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                SysSetting();
                return true;
            case R.id.about:
//                Intent intent = new Intent(this, AboutActivity.class);
//                startActivity(intent);
                return true;
            case R.id.exitApp:
                kill();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void SysSetting() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivityForResult(intent, SETTING_REQUEST_CODE);
    }

    @Override
    public void showSnackBar(String msg) {
        Toolbar  mToolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.common_toolbar);
        Snackbar.make(mToolBar, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void kill() {
        finish();
    }

    @Override
    public void backup(){
        android.support.v7.app.AlertDialog.Builder builder = null;
        builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("备份");
        builder.setMessage("是否确定备份当前数据？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", (DialogInterface dialog, int which) -> {
            Intent intent = new Intent(this, DFSelectActivity.class);
            intent.putExtra("type", 2);
            intent.putExtra("result_code", BACKUP_REQUEST_CODE);
            intent.putExtra("defaultDir", this.getFilesDir().toString());
            startActivityForResult(intent, BACKUP_REQUEST_CODE);
        });
        builder.show();
    };

    @Override
    public void restore(){
        android.support.v7.app.AlertDialog.Builder builder = null;
        builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("还原");
        builder.setMessage("还原后当前数据将被清除，切无法恢复，是否确定还原？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", (DialogInterface dialog, int which) -> {
            Intent intent = new Intent(this, DFSelectActivity.class);
            intent.putExtra("type", 1);
            intent.putExtra("defaultDir", this.getFilesDir().toString());
//            intent.putExtra("fileType", [""]);
            intent.putExtra("result_code", RESTORE_REQUEST_CODE);
            startActivityForResult(intent, RESTORE_REQUEST_CODE);
        });
        builder.show();
    };
    @Override
    protected void onEventComing(EventCenter eventCenter) {
        if (eventCenter.getEventCode() == Constants.EVEN_BUS.CHANGE_THEME) {
            reload(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INDEX_REQUEST_CODE) {
            if (resultCode == EDIT_SAVE && resultCode == SUCCESS) {
                EventCenter eventCenter = new EventCenter(INDEX_EVENT_SUCCESS, true);
                EventBus.getDefault().post(eventCenter);
            }
        } else if (requestCode == SETTING_REQUEST_CODE) {

        } else if (requestCode == BACKUP_REQUEST_CODE){
            mIndexPre.backup(data.getStringExtra("selectPath"));
        } else if (requestCode == RESTORE_REQUEST_CODE){
            mIndexPre.restore("");
        }
    }

    @Override
    public void onBackPressed() {
        android.support.v4.widget.DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return ;
        }

        if (mIndexPre.onBackPress()) {
            super.onBackPressed();
        }
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
