package com.mx.android.password.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.mx.android.password.R;
import com.mx.android.password.activity.base.BaseActivity;
import com.mx.android.password.adapter.PWFilterAdapter;
import com.mx.android.password.customview.FilterAView;
import com.mx.android.password.entity.Constants;
import com.mx.android.password.entity.EventCenter;
import com.mx.android.password.presenter.FilterImpl;

import butterknife.BindView;
import butterknife.OnClick;

public class FilterActivity extends BaseActivity implements FilterAView {
    private static final int SUCCESS = 1;
    private static final int ERROR = 0;

    @BindView(R.id.filterView)
    RecyclerView mRecyclerView;

    private PWFilterAdapter mAdapter;
    private FilterImpl mFilterImpl;
    private int result_code = 0;
    private Boolean mIsShowAll;

    @OnClick(R.id.cancelBtn)
    public void onClick(View view) {
        finishActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        result_code = intent.getIntExtra("result_code", 0);
        mIsShowAll = intent.getBooleanExtra("ShowAll", true);

        mFilterImpl = new FilterImpl(this, this);
        mFilterImpl.onCreate(savedInstanceState);
        mFilterImpl.getIntent(getIntent());
    }

    @Override
    public void initRecycler(LinearLayoutManager linearLayoutManager, RecyclerView.Adapter adapter) {
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
//        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mFilterImpl.onOptionItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finishActivity() {
        Intent intent = new Intent();
        intent.putExtra("accountType", mFilterImpl.getAccountType());
        setResult(result_code, intent);
        finish();
    }

    @Override
    public boolean isShowAll() {
        return mIsShowAll;
    }

    @Override
    protected void initToolbar() {
        Toolbar mToolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.common_toolbar);
        super.initToolBar(mToolBar);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_filter;
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
        return true;
    }


    @Override
    protected void onEventComing(EventCenter eventCenter) {
        if (eventCenter.getEventCode() == Constants.EVEN_BUS.CHANGE_THEME) {
            reload(false);
        }
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return false;
    }
}