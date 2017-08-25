package com.mx.android.password.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.util.IUnreadCountCallback;
import com.mx.android.password.R;
import com.mx.android.password.customview.PassWordAView;
import com.mx.android.password.db.PWDBHelper;
import com.mx.android.password.entity.Constants;
import com.mx.android.password.entity.EventCenter;
import com.mx.android.password.utils.MyApplication;
import com.mx.android.password.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mxuan on 2016-07-10.
 */
public class PassWordPreImpl implements ActivityPresenter, NavigationView.OnNavigationItemSelectedListener {

    private static long DOUBLE_CLICK_TIME = 0L;
    private final Context mContext;
    private final PassWordAView mIndexView;
    private Handler handler = new Handler(Looper.getMainLooper());

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
                mIndexView.backup();
                break;
            case R.id.nav_restore:
                mIndexView.restore();
                break;
            case R.id.nav_setting:
                mIndexView.SysSetting();
                break;
            default:
                break;
        }
        mIndexView.closeNavTool();
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

    public void waitclick() {
        final PopupWindow popupWindow = new PopupWindow();
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        View view = LayoutInflater.from(mContext).inflate(R.layout.wait_popup, null);
        popupWindow.setContentView(view);
        popupWindow.showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "还原成功", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
                EventCenter eventCenter = new EventCenter(Constants.EVEN_BUS.INDEX_EVENT_SUCCESS, true);
                EventBus.getDefault().post(eventCenter);
            }
        }, 2000);
    }

    public void backup(String dirPath) {
        PWDBHelper.backup(mContext, dirPath);
    }

    public void autoBackup() {
        boolean autobackup = (boolean) SPUtils.get(mContext, Constants.SETTING.AUTO_BACKUP, true);
        if (!autobackup) return;

        SimpleDateFormat curDateTime = new SimpleDateFormat("yyyyMMdd");
        String filename = "backup" + curDateTime.format(new Date());

        String dirPath = ((MyApplication) ((Activity) mContext).getApplication()).GetBackdir();
        File[] myFile = new File(dirPath).listFiles();
        if (myFile != null) {
            for (File f : myFile) {
                //过滤目录
                if (f.isDirectory() && (!f.isHidden())) continue;
                if (f.isFile() && (!f.isHidden())) {
                    int find = f.getPath().indexOf(filename);
                    if (find > -1) return;
                }
            }
        }

        PWDBHelper.backup(mContext, dirPath);
    }

    public void restore(String filePath) {
        PWDBHelper.restore(mContext, filePath);
        waitclick();
    }

    public void getFeedbackUnreadCount() {
        //如果500ms内init未完成, openFeedbackActivity会失败, 可以延长时间>500ms
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FeedbackAPI.getFeedbackUnreadCount(new IUnreadCountCallback() {
                    @Override
                    public void onSuccess(final int unreadCount) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (unreadCount <= 0) return;
                                Toast toast = Toast.makeText(mContext, "发现未读的反馈，未读数：" + unreadCount,
                                        Toast.LENGTH_SHORT);
                                toast.show();
                                FeedbackAPI.openFeedbackActivity();
                            }
                        });
                    }

                    @Override
                    public void onError(int i, String s) {
                    }
                });
            }
        }, 5000);
    }
}
