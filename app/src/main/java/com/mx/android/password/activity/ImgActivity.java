package com.mx.android.password.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.mx.android.password.R;

public class ImgActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img);
        ImageView imageview = (ImageView)findViewById(R.id.bigImg);
        Intent intent = getIntent();
        byte[] bitmap = intent.getByteArrayExtra("bitmap");

        imageview.setImageBitmap(BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length));
    }
}
