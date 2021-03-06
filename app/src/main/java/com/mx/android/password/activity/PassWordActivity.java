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

import com.android.mx.mxlib.DFSelectActivity;
import com.jakewharton.rxbinding.view.RxView;
import com.mx.android.password.R;
import com.mx.android.password.activity.base.BaseActivity;
import com.mx.android.password.adapter.PWContentAdapter;
import com.mx.android.password.customview.PassWordAView;
import com.mx.android.password.entity.Constants;
import com.mx.android.password.entity.EventCenter;
import com.mx.android.password.presenter.PassWordPreImpl;
import com.mx.android.password.utils.MyApplication;
import com.mx.android.password.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.mx.android.password.entity.Constants.EVEN_BUS.CHANGE_THEME;
import static com.mx.android.password.entity.Constants.EVEN_BUS.FILTER_EVENT_SUCCESS;
import static com.mx.android.password.entity.Constants.EVEN_BUS.INDEX_EVENT_SUCCESS;
import static com.mx.android.password.presenter.FilterImpl.FILTERALL;

public class PassWordActivity extends BaseActivity implements PassWordAView {
    private static final int INDEX_REQUEST_CODE = 1;
    private static final int SETTING_REQUEST_CODE = 2;
    private static final int BACKUP_REQUEST_CODE = 3;
    private static final int RESTORE_REQUEST_CODE = 4;
    private static final int FILTER_REQUEST_CODE = 5;
    private static final int EXPORT_REQUEST_CODE = 6;
    private static final int IMPORT_REQUEST_CODE = 7;
    private static final int EDIT_SAVE = 1;
    @BindView(R.id.drawerLayout)
    public DrawerLayout drawerLayout_main;
    @BindView(R.id.navigationView)
    public NavigationView navigationView;
    private int SUCCESS = 1;
    private PassWordPreImpl mIndexPre;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private Toolbar mToolBar;
    private MenuItem mMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIndexPre = new PassWordPreImpl(this, this);
        mIndexPre.onCreate(savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        RxView.clicks(fab).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe((aVoid) -> this.readyGoForResult(EditActivity.class));

//        navigationView.setCheckedItem(R.id.nav_login_type);
        navigationView.setNavigationItemSelectedListener(mIndexPre);

        mIndexPre.getFeedbackUnreadCount();
        mIndexPre.autoBackup();
    }

    @Override
    protected void initToolbar() {
        mToolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.common_toolbar);
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
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout_main, (Toolbar) findViewById(R.id.common_toolbar), 0, 0) {
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
        drawerLayout_main.setDrawerListener(mActionBarDrawerToggle);
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
    public void closeNavTool() {
        drawerLayout_main.closeDrawer(navigationView);
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
        mMenuItem = menu.findItem(R.id.action_more);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.setting:
//                SysSetting();
//                return true;
//            case R.id.filter:
        Intent intent = new Intent(this, FilterActivity.class);
        startActivityForResult(intent, FILTER_REQUEST_CODE);
        return true;
//            case R.id.exitApp:
//                kill();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
    }

    @Override
    public void SysSetting() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivityForResult(intent, SETTING_REQUEST_CODE);
    }

    @Override
    public void showSnackBar(String msg) {
        Toolbar mToolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.common_toolbar);
        Snackbar.make(mToolBar, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void kill() {
        finish();
    }

    @Override
    public void doHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    @Override
    public void dataExport() {
        Intent intent = new Intent(this, DFSelectActivity.class);
        intent.putExtra("type", 2);
        intent.putExtra("result_code", EXPORT_REQUEST_CODE);
        intent.putExtra("defaultDir", ((MyApplication) getApplication()).GetBackdir());

        showAlertDialog("导出", "是否确定导出当前数据？", intent, EXPORT_REQUEST_CODE);
    }

    @Override
    public void dataImport() {
        Intent intent = new Intent(this, DFSelectActivity.class);
        intent.putExtra("type", 1);
        intent.putExtra("defaultDir", ((MyApplication) getApplication()).GetBackdir());
        intent.putExtra("fileType", new String[]{"txt"});
        intent.putExtra("result_code", IMPORT_REQUEST_CODE);

        showAlertDialog("导入", "是否确定导入数据到当前程序？", intent, IMPORT_REQUEST_CODE);
    }


    private void showAlertDialog(String title, String message, Intent intent, int requestCode) {
        android.support.v7.app.AlertDialog.Builder builder = null;
        builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", (DialogInterface dialog, int which) -> {
            if (intent != null) startActivityForResult(intent, requestCode);
        });
        builder.show();
    }

    @Override
    public void backup() {
        Intent intent = new Intent(this, DFSelectActivity.class);
        intent.putExtra("type", 2);
        intent.putExtra("result_code", BACKUP_REQUEST_CODE);
        intent.putExtra("defaultDir", ((MyApplication) getApplication()).GetBackdir());

        showAlertDialog("备份", "是否确定备份当前数据？", intent, BACKUP_REQUEST_CODE);
    }

    @Override
    public void restore() {
        Intent intent = new Intent(this, DFSelectActivity.class);
        intent.putExtra("type", 1);
        intent.putExtra("defaultDir", ((MyApplication) getApplication()).GetBackdir());
        intent.putExtra("fileType", new String[]{"db"});
        intent.putExtra("result_code", RESTORE_REQUEST_CODE);

        showAlertDialog("还原", "原后当前数据将被清除，且无法恢复，是否确定还原？", intent, RESTORE_REQUEST_CODE);
    }

    @Override
    protected void onEventComing(EventCenter eventCenter) {
        if (eventCenter.getEventCode() == CHANGE_THEME) {
            reload(false);
        } else if (eventCenter.getEventCode() == INDEX_EVENT_SUCCESS) {
            mToolBar.setTitle(getString(R.string.app_name));
        } else if (eventCenter.getEventCode() == FILTER_EVENT_SUCCESS) {
            String accountType = (String) eventCenter.getData();
            if (accountType.equals("")) accountType = getString(R.string.app_name);
            mToolBar.setTitle(accountType);
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

        } else {
            if (data == null) return;
            if (requestCode == BACKUP_REQUEST_CODE) {
                String selectPath = data.getStringExtra("selectPath");
                if (Utils.StringEmpty(selectPath)) return;
                mIndexPre.backup(selectPath);
            } else if (requestCode == RESTORE_REQUEST_CODE) {
                String filePath = data.getStringExtra("selectPath");
                if (Utils.StringEmpty(filePath)) return;
                mIndexPre.restore(filePath);
            } else if (requestCode == FILTER_REQUEST_CODE) {
                String accountType = data.getStringExtra("accountType");
                if (Utils.StringEmpty(accountType)) return;
                if (accountType.equals(FILTERALL)) accountType = "";
//                CrashReport.testJavaCrash();
                EventCenter eventCenter = new EventCenter(FILTER_EVENT_SUCCESS, accountType);
                EventBus.getDefault().post(eventCenter);
            } else if (requestCode == EXPORT_REQUEST_CODE) {
                String selectPath = data.getStringExtra("selectPath");
                if (Utils.StringEmpty(selectPath)) return;
                mIndexPre.dataExport(selectPath);
            } else if (requestCode == IMPORT_REQUEST_CODE) {
                String selectPath = data.getStringExtra("selectPath");
                if (Utils.StringEmpty(selectPath)) return;
                mIndexPre.dataImport(selectPath);
            }
        }
    }

    @Override
    public void onBackPressed() {
        android.support.v4.widget.DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
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
