package com.mx.android.password.db;

import android.content.Context;
import android.util.Log;

import com.mx.android.password.entity.Account;
import com.mx.android.password.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2017-08-14.
 */

public class PWDBHelper extends DataBaseHelper {
    public static final String AccountStr = "create table tb_account (" +
            "guidPW VARCHAR(50) primary key, " +
            "rowIndex integer, " +
            "accountType VARCHAR(50), " +
            "title VARCHAR(50), " +
            "userName VARCHAR(50), " +
            "passWord VARCHAR(50), " +
            "time VARCHAR(50), " +
            "memoInfo text, " +
            "img BLOB)";
    private static final String PWDbName = "pw.db";
    private static final int DBVERSION = 1;
    private static PWDBHelper mPWDBHelper;

    private PWDBHelper(Context context) {
        super(context);
    }

    public static PWDBHelper getInstance(Context context) {
        if (mPWDBHelper == null) {
            synchronized (DataBaseHelper.class) {
                if (mPWDBHelper == null) {
                    mPWDBHelper = new PWDBHelper(context);
                    if (mPWDBHelper.getDB() == null || !mPWDBHelper.getDB().isOpen()) {
                        mPWDBHelper.open();
                    }
                }
            }
        }
        return mPWDBHelper;
    }

    /**
     * 更新数据库
     *
     * @param context 上下文
     * @param account bean
     * @return 成功返回true
     */
    public static boolean updateAccount(Context context, Account account) {
        return mPWDBHelper.update("tb_account",
                new String[]{"accountType", "title", "userName", "passWord", "time", "memoInfo", "img"},
                new Object[]{account.getAccountType(), account.getTitle(), account.getUserName(), account.getPassWord(), account.getTime(), account.getMemoInfo(), account.getImg()},
                new String[]{"guidPW"},
                new String[]{account.getGuidPW()});
    }

    //获取最大行号
    public static int getMaxIndex(Context context) {
        Map list = mPWDBHelper.queryItemMap("select max(rowIndex) maxrowIndex from tb_account", null);
        Object value = list.get("maxrowIndex");
        if (value == null) return 0;
        return (Integer) value + 1;
    }

    public static boolean save(Context context, Account account) {
        return mPWDBHelper.insert("tb_account",
                new String[]{"guidPW", "rowIndex", "accountType", "title", "userName", "passWord", "time", "memoInfo", "img"},
                new Object[]{account.getGuidPW(), account.getRowIndex(), account.getAccountType(), account.getTitle(), account.getUserName(), account.getPassWord(), account.getTime(), account.getMemoInfo(), account.getImg()});
    }

    public static ArrayList<Account> selector(Context context, String guidPW) {
        ArrayList<Account> accountList = new ArrayList<>();
        List<Map> list;
        if (!Utils.StringEmpty(guidPW)) {
            list = mPWDBHelper.queryListMap("select * from tb_account where guidPW=?", new String[]{guidPW});
        } else {
            list = mPWDBHelper.queryListMap("select * from tb_account", null);
        }
        for (Map account : list) {

            Account at = new Account((String) account.get("accountType"), (String) account.get("title"), (String) account.get("userName"),
                    (String) account.get("passWord"), (String) account.get("time"), (String) account.get("memoInfo"));
            at.setImg((byte[])account.get("img"));
            at.setGuidPW((String) account.get("guidPW"));
            at.setRowIndex((Integer) account.get("rowIndex"));

            accountList.add(at);
        }
        return accountList;
    }

    public static List<String> getTypeList(Context context) {
        ArrayList<String> accountList = new ArrayList<>();
        List<Map> list;

        list = mPWDBHelper.queryListMap("select accountType from tb_account group by accountType", null);

        for (Map account : list) {
            accountList.add((String) account.get("accountType"));
        }
        return accountList;
    }

    public static ArrayList<Account> filterAccount(Context context, String accountType) {
        ArrayList<Account> accountList = new ArrayList<>();
        List<Map> list;
        if (!Utils.StringEmpty(accountType)) {
            list = mPWDBHelper.queryListMap("select * from tb_account where accountType=?", new String[]{accountType});
        } else {
            list = mPWDBHelper.queryListMap("select * from tb_account", null);
        }
        for (Map account : list) {

            Account at = new Account((String) account.get("accountType"), (String) account.get("title"), (String) account.get("userName"),
                    (String) account.get("passWord"), (String) account.get("time"), (String) account.get("memoInfo"));
            at.setImg((byte[]) account.get("img"));
            at.setGuidPW((String) account.get("guidPW"));
            at.setRowIndex((Integer) account.get("rowIndex"));
            accountList.add(at);
        }
        return accountList;
    }

    //交换行号
    public static void onSwapAccount(Context context, Account account1, Account account2) {
        int rowIndex = account1.getRowIndex();

        mPWDBHelper.update("tb_account",
                new String[]{"rowIndex"}, new Object[]{account2.getRowIndex()},
                new String[]{"guidPW"}, new String[]{account1.getGuidPW()});

        mPWDBHelper.update("tb_account",
                new String[]{"rowIndex"}, new Object[]{rowIndex},
                new String[]{"guidPW"}, new String[]{account2.getGuidPW()});
    }

    public static void delete(Context context, Account account) {
        mPWDBHelper.delete("tb_account", new String[]{"guidPW"}, new String[]{account.getGuidPW()});
    }

    public static void backup(Context context, String dirPath) {
        String realmPath = "";
        SimpleDateFormat curDateTime = new SimpleDateFormat("yyyyMMddHHmmss");
        String exportRealmFileName = dirPath + "/" + "backup" + curDateTime.format(new Date()) + ".db";

//        String dbFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PWDbName;
        String dbFileName = context.getDatabasePath(PWDbName).getAbsolutePath();

        File backup = new File(exportRealmFileName);
        try {
            backup.createNewFile();
            copyBundledRealmFile(context, dbFileName, exportRealmFileName);
            Log.d("backup", "ok");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("backup", "fail");
        }
    }

    public static void restore(Context context, String filePath) {
        PWDBHelper.getInstance(context).close();
        mPWDBHelper = null;

        String dbFileName = context.getDatabasePath(PWDbName).getAbsolutePath();
        try {
            copyBundledRealmFile(context, filePath, dbFileName);
            PWDBHelper.getInstance(context);
            Log.d("backup", "ok");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("backup", "fail");
        }
    }

    public static String copyBundledRealmFile(Context context, String oldFilePath, String outFileName) {
        try {
            File file = new File(outFileName);
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

    @Override
    protected int getMDbVersion(Context context) {
        return DBVERSION;
    }

    @Override
    protected String getDbName(Context context) {
        return PWDbName;
    }


    @Override
    protected String[] getDbCreateSql(Context context) {
        String[] a = new String[1];
        a[0] = AccountStr;
        return a;
    }

    @Override
    protected String[] getDbUpdateSql(Context context) {
        return new String[0];
    }
}
