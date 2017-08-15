package com.mx.android.password.customview;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Administrator on 2017-08-09.
 */

public interface FilterAView {
    void finishActivity();
    void initRecycler(LinearLayoutManager linearLayoutManager, RecyclerView.Adapter adapter);
}
