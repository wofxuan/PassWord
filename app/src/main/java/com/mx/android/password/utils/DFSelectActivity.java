package com.mx.android.password.utils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mx.android.password.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DFSelectActivity extends AppCompatActivity implements View.OnClickListener {
    public final static int TypeOpen = 1;
    public final static int TypeSave = 2;
    ArrayAdapter<String> Adapter;
    ArrayList<String> arr = new ArrayList<String>();
    private ListView list;
    private String path;
    private TextView curPath;
    private Button home, back, ok;
    private LinearLayout titleView;
    private int type = 1;
    private String[] fileType = null;
    private int result_code = 0;
    //动态更新ListView
    Runnable add = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            arr.clear();
            //必须得用这种方法为arr赋值才能更新
            List<String> temp = getDirs(path);
            for (int i = 0; i < temp.size(); i++)
                arr.add(temp.get(i));
            Adapter.notifyDataSetChanged();
        }
    };
    private AdapterView.OnItemClickListener lvLis = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            String temp = (String) arg0.getItemAtPosition(arg2);
            if (temp.equals(".."))
                path = getSubDir(path);
            else if (path.equals("/"))
                path = path + temp;
            else
                path = path + "/" + temp;

            curPath.setText(path);

            Handler handler = new Handler();
            handler.post(add);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfselect);

        Intent  intent = getIntent();  ;
        type = intent.getIntExtra("type", 1);
        result_code  = intent.getIntExtra("result_code", 0);
        String defaultDir = intent.getStringExtra("defaultDir");
        fileType = intent.getStringArrayExtra("fileType");
        if ((defaultDir != null) && (defaultDir != "")) {
            path = defaultDir;
        } else {
            path = getRootDir();
        }
        curPath = (TextView)findViewById(R.id.path);
        curPath.setText(path);
        arr = (ArrayList<String>) getDirs(path);
        Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr);

        list = (ListView) findViewById(R.id.list_dir);
        list.setAdapter(Adapter);

        list.setOnItemClickListener(lvLis);

        home = (Button) findViewById(R.id.btn_home);
        home.setOnClickListener(this);

        back = (Button) findViewById(R.id.btn_back);
        back.setOnClickListener(this);

        ok = (Button) findViewById(R.id.btn_ok);
        ok.setOnClickListener(this);
    }

    private List<String> getDirs(String ipath) {
        List<String> file = new ArrayList<String>();
        File[] myFile = new File(ipath).listFiles();
        if (myFile == null) {
            file.add("..");

        } else
            for (File f : myFile) {
                //过滤目录
                if (f.isDirectory()) {
                    String tempf = f.toString();
                    int pos = tempf.lastIndexOf("/");
                    String subTemp = tempf.substring(pos + 1, tempf.length());
                    ;
                    file.add(subTemp);
                }
                //过滤知道类型的文件
                if (f.isFile() && fileType != null) {
                    for (int i = 0; i < fileType.length; i++) {
                        int typeStrLen = fileType[i].length();

                        String fileName = f.getPath().substring(f.getPath().length() - typeStrLen);
                        if (fileName.toLowerCase().equals(fileType[i])) {
                            file.add(f.toString().substring(path.length() + 1, f.toString().length()));
                        }
                    }
                }
            }

        if (file.size() == 0)
            file.add("..");
        return file;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == home.getId()) {
            path = getRootDir();
            curPath.setText(path);
            Handler handler = new Handler();
            handler.post(add);
        } else if (v.getId() == back.getId()) {
            path = getSubDir(path);
            curPath.setText(path);
            Handler handler = new Handler();
            handler.post(add);
        } else if (v.getId() == ok.getId()) {
            Intent intent = new Intent();
            intent.putExtra("back", "Back Data");
            intent.putExtra("selectPath", path);
            setResult(result_code, intent);
            finish();
//            if (type == TypeSave)
//                path = path + "/" + curPath.getEditableText().toString();
//            Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
        }
    }

    private String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
        }
        if (sdDir == null) {
            return null;
        }
        return sdDir.toString();

    }

    private String getRootDir() {
        String root = "/";

        path = getSDPath();
        if (path == null)
            path = "/";

        return root;
    }

    private String getSubDir(String path) {
        int pos = path.lastIndexOf("/");

        if (pos == path.length()) {
            path = path.substring(0, path.length() - 1);
            pos = path.lastIndexOf("/");
        }

        String subpath = path.substring(0, pos);

        if (pos == 0)
            subpath = "/";

        return subpath;
    }
}
