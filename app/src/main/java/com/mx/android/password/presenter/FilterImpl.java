package com.mx.android.password.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;

import com.mx.android.password.adapter.PWFilterAdapter;
import com.mx.android.password.customview.FilterAView;
import com.mx.android.password.db.PWDBHelper;

import java.util.List;

/**
 * Created by Administrator on 2017-08-09.
 */

public class FilterImpl implements ActivityPresenter {
    public static final String FILTERALL = "全部";
    private final Context mContext;
    private final FilterAView mFilterView;
    private List<String> mDatas;
    private PWFilterAdapter mAdapter;
    private String mAccountType = "";

    public FilterImpl(Context context, FilterAView view) {
        this.mContext = context;
        mFilterView = view;

        initData();
        ;
        mAdapter = new PWFilterAdapter(mContext, mDatas);
        mFilterView.initRecycler(new LinearLayoutManager(mContext), mAdapter);
        mAdapter.setOnItemClickLitener(new PWFilterAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(mContext, mDatas.get(position) + " click",
//                        Toast.LENGTH_SHORT).show();
                mAccountType = mDatas.get(position);
                mFilterView.finishActivity();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    protected void initData() {
        mDatas = PWDBHelper.getTypeList(mContext);
        if (mFilterView.isShowAll()) mDatas.add(0, FILTERALL);
    }

    @Override
    public void getIntent(Intent intent) {

    }

    public String getAccountType() {
        return mAccountType;
    }

    public boolean onOptionItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return comeBack();
            default:
                return false;
        }
    }

    public boolean comeBack() {
        mFilterView.finishActivity();
        return true;
    }
}
