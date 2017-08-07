package com.mx.android.password.entity;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.RealmSchema;

/**
 * Created by mxuan on 2016-07-11.
 */
public class RealmHelper {
    private final static String TAG = RealmMigration.class.getName();
    private static RealmHelper instances;
    private Context mContext;

    private RealmHelper(Context context) {
        mContext = context;
    }

    public static RealmHelper getInstances(Context context) {
        synchronized (RealmHelper.class) {
            if (instances == null) {
                instances = new RealmHelper(context);
            }
        }
        return instances;
    }

    private static void closeConnect(Realm realm) {
        if (null != realm) {
            try {
                realm.close();
                realm = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class MyMigration implements RealmMigration {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
            Log.e(null, "oldVersion:" + oldVersion + " newVersion:" + newVersion);
            RealmSchema schema = realm.getSchema();
            if (newVersion == 2) {
//                schema.get("User").addField("desc", String.class);
            }
        }
    }

    public static Realm getInstance(Context context) {
        Realm realm;
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
//                .schemaVersion(0)
                .migration(new MyMigration())
                .build();
        return realm = Realm.getInstance(realmConfig);
//        return Realm.getInstance(context);
    }

//    private static RealmConfiguration secure(Context context) {
//        byte[] key = new byte[64];
//        new SecureRandom().nextBytes(key);
//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
//                .encryptionKey(key)
//                .build();
//
//        // Start with a clean slate every time
//        Realm.deleteRealm(realmConfiguration);
//        return realmConfiguration;
//    }

    public static ArrayList<God> selector(Context context, int godType) {
        Realm realm = getInstance(context);
        try {
            RealmQuery<God> realmQuery = realm.where(God.class);
//        RealmQuery<God> godRealmQuery = realmQuery.equalTo("godType", godType);
            RealmResults<God> realmResults = realmQuery.findAll();
            if (realmResults != null && realmResults.size() > 0) {
                ArrayList<God> godList = new ArrayList<>();
                for (God god : realmResults) {
                    God at = new God(god.getGodType(), god.getTitle(), god.getUserName(),
                            god.getPassWord(), god.getTime(), god.getMemoInfo());
                    at.setImg(god.getImg());
                    godList.add(at);
                }
                Collections.reverse(godList);

                return godList;
            }
            return null;
        } finally {
            realm.close();
        }
    }

    public static boolean save(Context context, God god) {
        if (check(context, god)) {
            return true;
        }
        Realm realm = getInstance(context);
        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(god);
            realm.commitTransaction();
            return false;
        } finally {
            realm.close();
        }
    }

    private static boolean check(Context context, God god) {
        int godType = god.getGodType();
        Realm realm = getInstance(context);
        try {
            RealmQuery<God> realmQuery = realm.where(God.class);
            RealmQuery<God> godRealmQuery = realmQuery.equalTo("godType", godType);
            RealmResults<God> title = godRealmQuery.contains("title", god.getTitle()).findAll();
            if (title != null && title.size() > 0) {
                return true;
            }
            return false;
        } finally {
            realm.close();
        }


    }

    /**
     * 更新数据库
     *
     * @param context 上下文
     * @param god     bean
     * @return 成功返回true
     */
    public static boolean update(Context context, God god) {
        Realm realm = getInstance(context);
        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(god);
            realm.commitTransaction();
            return true;
        } finally {
            realm.close();
        }
    }

    public static void delete(Context context, God god, int position) {
        Realm realm = getInstance(context);
        try {
            RealmQuery<God> realmQuery = realm.where(God.class);
            RealmQuery<God> godRealmQuery = realmQuery.equalTo("godType", god.getGodType());
            RealmResults<God> realmResults = godRealmQuery.findAll();
            if (realmResults != null) {
                int size = realmResults.size() - 1;
                int i = size - position;
                realm.beginTransaction();
//                realmResults.remove(i);
                realmResults.deleteFromRealm(i);
                realm.commitTransaction();
            }
        } finally {
            realm.close();
        }
    }

    public static void backup(Context context, String dirPath) {
        Realm realm = getInstance(context);
        try {
            File exportRealmFile = null;
//            File exportRealmPATH = context.getExternalFilesDir(null);
            SimpleDateFormat curDateTime = new SimpleDateFormat("yyyyMMddHHmmss");
            String exportRealmFileName = "default" + curDateTime.format(new Date()) + ".realm";
//            try {
            // create a backup file
            exportRealmFile = new File(dirPath + "/" + exportRealmFileName);
            // if backup file already exists, delete it
            exportRealmFile.delete();
            // copy current realm to backup file
            realm.writeCopyTo(exportRealmFile);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            String msg = "File exported to Path: " + context.getExternalFilesDir(null);
        } finally {
            realm.close();
        }

    }

    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static void restore(Context context, String filePath) {
        File exportRealmPATH = context.getExternalFilesDir(null);
        String FileName = "default.realm";
        String restoreFilePath = context.getExternalFilesDir(null) + "/" + FileName;
        if (fileExists(exportRealmPATH.toString()) != false) {
            copyBundledRealmFile(context, restoreFilePath, FileName);
            Toast.makeText(context, "Data restore is done", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Data restore is done");
        }
    }

    public static String copyBundledRealmFile(Context context, String oldFilePath, String outFileName) {
        try {
            File file = new File(context.getFilesDir(), outFileName);
            Log.d(TAG, "context.getFilesDir() = " + context.getFilesDir().toString());
            FileOutputStream outputStream = new FileOutputStream(file);
            FileInputStream inputStream = new FileInputStream(new File(oldFilePath));

            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
