package com.mx.android.password.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mx.android.password.R;
import com.mx.android.password.utils.ZoomImageView;

public class ImgActivity extends AppCompatActivity {
    private Bitmap mBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img);
        ZoomImageView zoomImg = (ZoomImageView) findViewById(R.id.bigImg);
        Intent intent = getIntent();
        String imgPath = intent.getStringExtra("bitmappath");

        mBitmap = BitmapFactory.decodeFile(imgPath);
        zoomImg.setImage(mBitmap);
//        bitmap.recycle();//不能释放

//        zoomImg.setImageURI(Uri.fromFile(new File(imgPath)));
//        byte[] bitmap = intent.getByteArrayExtra("bitmap");
//        imageview.setImageBitmap(BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBitmap.recycle();
    }
}
