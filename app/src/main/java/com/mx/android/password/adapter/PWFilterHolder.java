package com.mx.android.password.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mx.android.password.R;

/**
 * Created by Administrator on 2017-08-09.
 */

public class PWFilterHolder extends RecyclerView.ViewHolder {
    TextView tv;

    public PWFilterHolder(View view) {
        super(view);
        tv = (TextView) view.findViewById(R.id.id_num);
    }
}
