package com.mx.android.password.utils;

import android.app.Application;
import android.os.Environment;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.facebook.stetho.Stetho;
import com.mx.android.password.db.PWDBHelper;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;

import static com.mx.android.password.entity.Constants.PS_Backdir;

/**
 * Created by Administrator on 2017-08-04.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "94a09746d5", false);

        FeedbackAPI.init(this, "24581823", "e3b0281dc333c369b22caa173e23a6c5");
//        FeedbackAPI.setDefaultUserContactInfo("13800000001");
//        FeedbackAPI.setBackIcon(R.mipmap.feedback);

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + PS_Backdir);
        if (!file.exists())
            file.mkdir();

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

        PWDBHelper.getInstance(this);
    }

    public String GetBackdir() {
        return Environment.getExternalStorageDirectory().getPath() + "/" + PS_Backdir;
//        return this.getExternalFilesDir(null).getPath();
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        PWDBHelper.getInstance(this).close();
        super.onTerminate();
    }
}
