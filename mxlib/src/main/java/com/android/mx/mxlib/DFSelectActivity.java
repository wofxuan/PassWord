package com.android.mx.mxlib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DFSelectActivity extends AppCompatActivity implements View.OnClickListener {
    public final static int TypeOpen = 1;
    public final static int TypeSave = 2;

    public final static int FileType = 1;
    public final static int DirType = 2;

    private static String openDirHis = "";//记录上次打开的目录
    private static List<String> openHisList = new ArrayList<String>();//记录打开的文件.

    FileListAdapter Adapter;
    ArrayList<FileItem> arr = new ArrayList<FileItem>();
    private ListView list;
    private String path;
    private TextView curPath;
    private Button home, back, ok;
    private LinearLayout titleView;
    private int doType = 1;
    private String[] fileType = null;//为null的时候为操作文件夹
    //动态更新ListView
    Runnable add = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            arr.clear();
            //必须得用这种方法为arr赋值才能更新
            List<FileItem> temp = getDirs(path);
            for (int i = 0; i < temp.size(); i++)
                arr.add(temp.get(i));
            Adapter.notifyDataSetChanged();
        }
    };
    private int result_code = 0;
    private AdapterView.OnItemClickListener lvLis = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            FileItem temp = (FileItem) arg0.getItemAtPosition(arg2);
            if (temp.getmName().equals(".."))
                path = getSubDir(path);
            else if (path.equals("/"))
                path = path + temp.getmName();
            else
                path = path + "/" + temp.getmName();

            curPath.setText(path);

            if (temp.getmType().equals(FileType)) {
                onClick(ok);
                return;
            }

            Handler handler = new Handler();
            handler.post(add);
        }
    };

    public static String getOpenDirHis() {
        if (openDirHis.equals("")) {
            return getSDPath();
        } else {
            return openDirHis;
        }
    }

    public static void setOpenDirHis(String openDir) {
        openDirHis = openDir;
    }

    //加入打开历史
    public static void addHisFile(String aFilPath) {
        openHisList.add(aFilPath);
    }

    //获取打开历史
    public static List<String> getHisFileLidt() {
        return openHisList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfselect);

        Intent intent = getIntent();
        doType = intent.getIntExtra("type", 1);
        result_code = intent.getIntExtra("result_code", 0);
        String defaultDir = intent.getStringExtra("defaultDir");
        fileType = intent.getStringArrayExtra("fileType");
        if ((defaultDir != null) && (defaultDir != "")) {
            path = defaultDir;
        } else {
            path = getRootDir();
        }
        curPath = (TextView) findViewById(R.id.path);
        curPath.setText(path);

        arr = (ArrayList<FileItem>) getDirs(path);
//        Adapter = new ArrayAdapter<FileItem>(this, R.layout.file_list, R.id.title, arr);
        Adapter = new FileListAdapter(this);

        list = (ListView) findViewById(R.id.list_dir);
        list.setAdapter(Adapter);

        list.setOnItemClickListener(lvLis);

        home = (Button) findViewById(R.id.btn_home);
        home.setOnClickListener(this);

        back = (Button) findViewById(R.id.btn_back);
        back.setOnClickListener(this);

        ok = (Button) findViewById(R.id.btn_ok);
        ok.setOnClickListener(this);

        if ((doType == TypeOpen) & (fileType != null)) {
            ok.setVisibility(View.GONE);
        }
    }

    private List<FileItem> getDirs(String ipath) {
        List<FileItem> file = new ArrayList<FileItem>();
        File[] myFile = new File(ipath).listFiles();
        if (myFile == null) {
            file.add(new FileItem(DirType, ".."));

        } else
            for (File f : myFile) {
                //过滤目录
                if (f.isDirectory() && (!f.isHidden())) {
                    String tempf = f.toString();
                    int pos = tempf.lastIndexOf("/");
                    String subTemp = tempf.substring(pos + 1, tempf.length());
                    file.add(new FileItem(DirType, subTemp));
                }
                //过滤知道类型的文件
                if (f.isFile() && fileType != null && (!f.isHidden())) {
                    for (int i = 0; i < fileType.length; i++) {
                        int typeStrLen = fileType[i].length();

                        String fileName = f.getPath().substring(f.getPath().length() - typeStrLen);
                        if (fileName.toLowerCase().equals(fileType[i]) || fileType[i].equals("*.*")) {
                            file.add(new FileItem(FileType, f.toString().substring(path.length() + 1, f.toString().length())));
                        }
                    }
                }
            }

        if (file.size() == 0)
            file.add(new FileItem(DirType, ".."));
        Collections.sort(file, new Comparator<FileItem>() {
            @Override
            public int compare(FileItem o1, FileItem o2) {
                if (o1.getmType().intValue() >= o2.getmType().intValue()) {
                    if (o1.getmType().intValue() > o2.getmType().intValue()) {
                        return -1;
                    } else {
                        return o1.getmName().toUpperCase().compareTo(o2.getmName().toUpperCase());
                    }
                } else {
                    return 1;
                }
//                return o1.getmName().compareTo(o2.getmName());
            }
        });
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

            if ((doType == TypeOpen) & (fileType != null)) {
                addHisFile(path);
            }
            setOpenDirHis(getSubDir(path));
            setResult(result_code, intent);
            finish();
        }
    }

    private static String getSDPath() {
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

    class FileItem {
        private Integer mType;
        private String mName;

        public FileItem(Integer AType, String AName) {
            mType = AType;
            mName = AName;
        }

        public String getmName() {
            return mName;
        }

        public void setmName(String mName) {
            this.mName = mName;
        }

        public Integer getmType() {
            return mType;
        }

        public void setmType(Integer mType) {
            this.mType = mType;
        }
    }

    public final class ViewHolder {
        public ImageView fileimg;
        public TextView filename;
    }

    public class FileListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public FileListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return arr.size();
        }

        @Override
        public FileItem getItem(int arg0) {
            // TODO Auto-generated method stub
            return arr.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.file_list, null);
                holder.fileimg = (ImageView) convertView.findViewById(R.id.fileimg);
                holder.filename = (TextView) convertView.findViewById(R.id.filename);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (arr.get(position).getmType().equals(FileType)) {
                holder.fileimg.setImageResource(R.drawable.file48px);
            } else {
                holder.fileimg.setImageResource(R.drawable.folder48px);
            }

            holder.filename.setText(arr.get(position).getmName());
            return convertView;
        }

    }
}
