package com.mx.android.password.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mx.android.password.R;
import com.mx.android.password.activity.base.BaseSwipeBackActivity;
import com.mx.android.password.customview.EditAView;
import com.mx.android.password.entity.Account;
import com.mx.android.password.entity.EventCenter;
import com.mx.android.password.presenter.EditAImpl;
import com.mx.android.password.utils.BitmapUtils;
import com.mx.android.password.utils.MyApplication;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import me.imid.swipebacklayout.lib.SwipeBackLayout;

public class EditActivity extends BaseSwipeBackActivity implements EditAView {

    private static final int SUCCESS = 1;
    private static final int ERROR = 0;

    MaterialEditText mTypeEdt;
    //     @Bind(R.id.title_edit_text)
    MaterialEditText mTitleEdt;
    //    @Bind(R.id.userName)
    MaterialEditText mUserNameEdt;
    //    @Bind(R.id.passWord)
    MaterialEditText mPassWordEdt;
    //    @Bind(R.id.view)
    LinearLayout mView;
    //    @Bind(R.id.memo)
    MaterialEditText mMemoInfo;

    ImageView mImg;
    TextView mNoimg;
    private EditAImpl mEditImpl;
    private Menu mMenu;
    private AlertDialog alertDialog;
    private String mGuidPW;

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        ;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTypeEdt = (MaterialEditText) findViewById(R.id.type_edit_text);
        mPassWordEdt = (MaterialEditText) findViewById(R.id.passWord);
        mTitleEdt = (MaterialEditText) findViewById(R.id.title_edit_text);
        mUserNameEdt = (MaterialEditText) findViewById(R.id.userName);

        mView = (LinearLayout) findViewById(R.id.view);
        mMemoInfo = (MaterialEditText) findViewById(R.id.memo);
        mImg = (ImageView) findViewById(R.id.img);
        mNoimg = (TextView) findViewById(R.id.noImg);

        mImg.setOnClickListener((View v) -> {
            android.support.v7.app.AlertDialog.Builder builder = null;
            builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setTitle("选择操作");
            builder.setItems(new String[]{"查看", "删除"}, (DialogInterface dialog, int which) -> {
                switch (which) {
                    case 0: {
                        Intent intent = new Intent(this, ImgActivity.class);
                        Bitmap bitmap = ((BitmapDrawable) mImg.getDrawable()).getBitmap();

                        File f = new File(getPhotopath());
                        if (f.exists()) {
                            f.delete();
                        }
                        try {
                            FileOutputStream out = new FileOutputStream(f);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
//                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                            intent.putExtra("bitmap", baos.toByteArray());
                            intent.putExtra("bitmappath", getPhotopath());
                            bitmap.recycle();
                            out.flush();
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        startActivity(intent);
                        break;
                    }
                    case 1: {
                        mImg.setVisibility(View.GONE);
                        mNoimg.setVisibility(View.VISIBLE);
                        break;
                    }
                }

            });
            ;
            builder.show();
        });

        mNoimg.setOnClickListener((View v) -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            File out = new File(getPhotopath());
            Uri uri = Uri.fromFile(out);
            // 获取拍照后未压缩的原图片，并保存在uri路径中
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, 1);
        });

        initToolbar();

        mEditImpl = new EditAImpl(this, this);
        mEditImpl.onCreate(savedInstanceState);
        mEditImpl.getIntent(getIntent());
    }

    /**
     * 获取原图片存储路径
     *
     * @return
     */
    private String getPhotopath() {
        // 照片全路径
        String fileName = "";
        // 文件夹路径
        String pathUrl = ((MyApplication) getApplication()).GetBackdir() + "/";
        String imageName = "imageTest.jpg";
        File file = new File(pathUrl);
        file.mkdirs();// 创建文件夹
        fileName = pathUrl + imageName;
        return fileName;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        menuItem = menu.getItem(0);
        mMenu = menu;
        //添加新项目
        if (getIntent().getIntExtra("CREATE_MODE", 1) == 1) {
            setItemMenuVisible(false, R.id.del);
            setItemMenuVisible(false, R.id.done);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mEditImpl.onOptionItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onEventComing(EventCenter eventCenter) {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_edit;
    }

    @Override
    protected void initToolbar() {
        Toolbar mToolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.common_toolbar);
        initToolBar(mToolBar);
        setToolBarTitle(R.string.create_mode);
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
        return false;
    }

    @Override
    public void initCreateModel() {
        mTypeEdt.requestFocus();
        showKeyBoard();
        addEdtChangeListener();
    }

    private void addEdtChangeListener() {
        mTypeEdt.addTextChangedListener(mEditImpl);
        mTitleEdt.addTextChangedListener(mEditImpl);
        mUserNameEdt.addTextChangedListener(mEditImpl);
        mPassWordEdt.addTextChangedListener(mEditImpl);
        mMemoInfo.addTextChangedListener(mEditImpl);
    }

    @Override
    public void initEditModel() {

    }

    @Override
    public void initViewModel(Account account) {
        mView.setFocusable(true);
        mView.setFocusableInTouchMode(true);
        hideKeyBoard();
        mTypeEdt.setText(account.getAccountType());
        mTypeEdt.setEnabled(false);
        mTitleEdt.setText(account.getTitle());
        mUserNameEdt.setText(account.getUserName());
        mPassWordEdt.setText(account.getPassWord());
        mMemoInfo.setText(account.getMemoInfo());
        mGuidPW = account.getGuidPW();

        mUserNameEdt.setOnFocusChangeListener(mEditImpl);
        mPassWordEdt.setOnFocusChangeListener(mEditImpl);
        mMemoInfo.setOnFocusChangeListener(mEditImpl);
        addEdtChangeListener();
        if (account.getImg() != null) {
            if (account.getImg().length != 0) {
                mImg.setImageBitmap(BitmapFactory.decodeByteArray(account.getImg(), 0, account.getImg().length));
                mNoimg.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public String getGuidPW() {
        return mGuidPW;
    }

    @Override
    public String getAccountType() {
        return mTypeEdt.getText().toString().trim();
    }

    @Override
    public String getTitleName() {
        return mTitleEdt.getText().toString().trim();
    }

    @Override
    public String getUserName() {
        return mUserNameEdt.getText().toString().trim();
    }

    @Override
    public String getPassWord() {
        return mPassWordEdt.getText().toString().trim();
    }

    @Override
    public String getMemoInfo() {
        return mMemoInfo.getText().toString().trim();
    }

    @Override
    public byte[] getImg() {
        if (mNoimg.getVisibility() == View.GONE) {
//            Bitmap image = ((BitmapDrawable) mImg.getDrawable()).getBitmap();

            Bitmap image = BitmapUtils.decodeSampledBitmapFromResource(getResources(), getPhotopath(), mImg.getWidth(), mImg.getHeight());
            delCacheImge();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, baos);
            image.recycle();
            return baos.toByteArray();
        } else {
            return new byte[0];
        }
    }

    @Override
    public void setTime(String time) {

    }

    @Override
    public void showSnackToast(String msg) {
        Toolbar mToolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.common_toolbar);
        Snackbar.make(mToolBar, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setItemMenuVisible(boolean visible, int menuItemId) {
        MenuItem menuItem = mMenu.findItem(menuItemId);
        if (menuItem != null) {
            menuItem.setVisible(visible);
        }
    }

    @Override
    public void finishActivity() {
        setResult(SUCCESS);
        finish();
    }

    private void showKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void hideKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mPassWordEdt.getWindowToken(), 0);
    }

    @Override
    public void setToolBarTitle(int resId) {
        Toolbar mToolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.common_toolbar);
        mToolBar.setTitle(getResources().getString(resId));
    }

    @Override
    public void showDialog(String msg, String positiveMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage(msg);//
        builder.setPositiveButton(positiveMsg, mEditImpl);
        builder.setNegativeButton("取消", mEditImpl);
        alertDialog = builder.show();
    }

    @Override
    public void onBackPressed() {
        mEditImpl.comeBack();
    }

    @Override
    public SwipeBackLayout getSwipeBack() {
        return getSwipeBackLayout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                Log.v("TestFile",
                        "SD card is not avaiable/writeable right now.");
                return;
            }

//            Bundle bundle = data.getExtras();
//            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = 1;
            File file = new File(getPhotopath());
/**
 * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
 */
            int degree = readPictureDegree(file.getAbsolutePath());
            Bitmap cameraBitmap = BitmapFactory.decodeFile(getPhotopath(), bitmapOptions);
            Bitmap bitmap = cameraBitmap;
/**
 * 把图片旋转为正的方向
 */
//            bitmap = rotaingImageView(degree, bitmap);

//            DisplayMetrics dm = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(dm);
//            Bitmap bitmap = getBitmapFromUrl(getPhotopath(), dm.widthPixels, dm.heightPixels);
            bitmap = rotaingImageView(degree, bitmap);
            //saveScalePhoto(bitmap);
//            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            delCacheImge();
            mImg.setImageBitmap(bitmap);// 将图片显示在ImageView里
            mNoimg.setVisibility(View.GONE);
        }
    }

    private void delCacheImge() {
        File delFile = new File(getPhotopath());
        delFile.delete();

        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(getPhotopath())));
        this.sendBroadcast(scanIntent);
    }

    /**
     * 根据路径获取图片资源（已缩放）
     *
     * @param url    图片存储路径
     * @param width  缩放的宽度
     * @param height 缩放的高度
     * @return
     */
    private Bitmap getBitmapFromUrl(String url, double width, double height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false
        Bitmap bitmap = BitmapFactory.decodeFile(url);
        // 防止OOM发生
        options.inJustDecodeBounds = false;
        int mWidth = bitmap.getWidth();
        int mHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = 1;
        float scaleHeight = 1;
//        try {
//            ExifInterface exif = new ExifInterface(url);
//            String model = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        // 按照固定宽高进行缩放
        // 这里希望知道照片是横屏拍摄还是竖屏拍摄
        // 因为两种方式宽高不同，缩放效果就会不同
        // 这里用了比较笨的方式
        if (mWidth <= mHeight) {
            scaleWidth = (float) (width / mWidth);
            scaleHeight = (float) (height / mHeight);
        } else {
            scaleWidth = (float) (height / mWidth);
            scaleHeight = (float) (width / mHeight);
        }
//        matrix.postRotate(90); /* 翻转90度 */
        // 按照固定大小对图片进行缩放
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, mWidth, mHeight, matrix, true);
        // 用完了记得回收
        bitmap.recycle();
        return newBitmap;
    }

    /**
     * 存储缩放的图片
     *
     * @param bitmap 图片数据
     */
    private void saveScalePhoto(Bitmap bitmap) {
        // 照片全路径
        String fileName = "";
        // 文件夹路径
        String pathUrl = ((MyApplication) getApplication()).GetBackdir() + "/";
        String imageName = "imageScale.jpg";
        FileOutputStream fos = null;
        File file = new File(pathUrl);
        file.mkdirs();// 创建文件夹
        fileName = pathUrl + imageName;
        try {
            fos = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
