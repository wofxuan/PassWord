package com.mx.android.password.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.mx.android.password.entity.Account;
import com.mx.android.password.entity.Constants;
import com.mx.android.password.entity.EventCenter;
import com.mx.android.password.utils.MyApplication;
import com.mx.android.password.utils.SPUtils;
import com.mx.android.password.utils.TimeUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.mx.android.password.entity.Constants.EVEN_BUS.INDEX_EVENT_SUCCESS;

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
            case R.id.nav_export:
                mIndexView.dataExport();
                break;
            case R.id.nav_import:
                mIndexView.dataImport();
                break;
            case R.id.nav_help:
                mIndexView.doHelp();
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
                Toast.makeText(mContext, "刷新成功", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
                EventCenter eventCenter = new EventCenter(INDEX_EVENT_SUCCESS, true);
                EventBus.getDefault().post(eventCenter);
            }
        }, 2000);
    }

    public void dataExport(String dirPath) {
        ArrayList<Account> list = PWDBHelper.selector(mContext, "");

        SimpleDateFormat curDateTime = new SimpleDateFormat("yyyyMMddhhmmss");
        String filename = "dataExport" + curDateTime.format(new Date()) + ".txt";

        String fileImgName = dirPath + "/dataExport" + curDateTime.format(new Date()) + "IMG";
        File fileimg = new File(fileImgName);
        if (!fileimg.exists())
            fileimg.mkdir();

        StringBuffer buffer = new StringBuffer();
        buffer.append("类型,标题,用户名,密码,备注\r\n");
        for (Account a : list) {
            buffer.append(a.getAccountType() + "," + a.getTitle() + "," + a.getUserName() +
                    "," + a.getPassWord() + "," + a.getMemoInfo() + "\r\n");

            Account bimg = PWDBHelper.selector(mContext, a.getGuidPW()).get(0);
            if (bimg.getImg() != null) {
                if (bimg.getImg().length != 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bimg.getImg(), 0, bimg.getImg().length);

                    File f = new File(fileImgName + "/" + bimg.getAccountType() + "-" + bimg.getTitle() + ".png");
                    if (f.exists()) {
                        f.delete();
                    }
                    try {
                        FileOutputStream out = new FileOutputStream(f);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                        bitmap.recycle();
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        try {
            String data = buffer.toString();

            File file = new File(dirPath, filename);
            OutputStream out = new FileOutputStream(file);

            byte b[] = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};//utf-8
            out.write(b);

            out.write(data.getBytes());
            out.close();
            Toast.makeText(mContext, "文件导出成功！", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataImport(String dirPath) {
        try {
            File urlFile = new File(dirPath);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String strLine = br.readLine();//标题栏

            PWDBHelper db = PWDBHelper.getInstance(mContext);
            //开启事务
            db.beginTransaction();
            try {
                while ((strLine = br.readLine()) != null) {
                    String restrLine = strLine.replace(" ", "");//去掉所用空格
                    List<String> list = Arrays.asList(restrLine.split(","));
                    if (list.size() >= 4) {
                        Account account = new Account(list.get(0), list.get(1), list.get(2), list.get(3), TimeUtils.getCurrentTimeInString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")), "");
                        if (list.size() >= 5) account.setMemoInfo(list.get(4));
                        account.setImg(new byte[0]);
                        account.setRowIndex(PWDBHelper.getMaxIndex(mContext));
                        account.setGuidPW(UUID.randomUUID().toString().toUpperCase());
                        if (!PWDBHelper.save(mContext, account)) {
                            Toast.makeText(mContext, "保存文件失败！类容<" + restrLine + ">", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                db.setTransactionSuccessful();//设置事务标志为成功，当结束事务时就会提交事务
                waitclick();
            } catch (Exception e) {
                throw (e);
            } finally {
                db.endTransaction();//结束事务
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Toast.makeText(mContext, "自动备份成功", Toast.LENGTH_SHORT).show();
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
