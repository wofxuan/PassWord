package com.mx.android.password.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.mx.android.password.R;
import com.mx.android.password.activity.EditActivity;
import com.mx.android.password.adapter.PWViewAdapter;
import com.mx.android.password.db.PWDBHelper;
import com.mx.android.password.entity.Account;
import com.mx.android.password.entity.Constants;
import com.mx.android.password.entity.EventCenter;
import com.mx.android.password.entity.LoginTypeFView;

import java.util.ArrayList;

/**
 * Created by mxuan on 2016-07-11.
 */
public class PassWordFImpl implements FragmentPresenter, PWViewAdapter.OnRecyclerItemClickListener {

    private final Context mContext;
    private final LoginTypeFView mLoginTypeFView;
    private PWViewAdapter mAdapter;
    private ArrayList<Account> selector;
    private boolean isCreate;

    public PassWordFImpl(Context context, LoginTypeFView view) {
        mContext = context;
        mLoginTypeFView = view;
    }

    @Override
    public void onFirstUserVisible() {
        isCreate = true;
        selector = selector();
        mAdapter = new PWViewAdapter(mContext, selector, mLoginTypeFView);
        mAdapter.setOnRecyclerItemClick(this);
        mLoginTypeFView.initRecycler(new LinearLayoutManager(mContext), mAdapter);
        if (null != selector && selector.size() > 0) {
            mLoginTypeFView.hideException();
        } else {
            mLoginTypeFView.showException();
        }
    }

    private ArrayList<Account> selector() {
        return PWDBHelper.selector(mContext, "");
    }

    private ArrayList<Account> filterAccount(String accountType) {
        return PWDBHelper.filterAccount(mContext, accountType);
    }

    public void onSwapAccount(int old, int target) {
        PWDBHelper.onSwapAccount(mContext, selector.get(old), selector.get(target));
    }

    @Override
    public void onUserVisible() {
    }

    @Override
    public void onUserInvisible() {
    }

    public void onEventComing(EventCenter eventCenter) {
        Log.d("onEventComing", "");
        if (isCreate) {
            Log.d("onEventComing", "isCreate");
            if (eventCenter.getEventCode() == Constants.EVEN_BUS.INDEX_EVENT_SUCCESS) {
                Log.d("onEventComing", "INDEX_EVENT_SUCCESS");
                boolean data = (boolean) eventCenter.getData();
                if (data) {
                    selector = selector();
                    if (null != selector && selector.size() > 0) {
                        mLoginTypeFView.hideException();
                        mAdapter.addAll(selector);
                    } else {
                        mLoginTypeFView.showException();
                        mAdapter.clearData();
                    }
                    String title = mContext.getResources().getString(R.string.app_name);
                    mLoginTypeFView.setToolBar(title);
                }
                mLoginTypeFView.runUi(mAdapter);
            } else if (eventCenter.getEventCode() == Constants.EVEN_BUS.CHANGE_PASS_WORD_SHOW) {
//                mAdapter.notifyDataSetChanged();
                mLoginTypeFView.runUi(mAdapter);
            } else if (eventCenter.getEventCode() == Constants.EVEN_BUS.FILTER_EVENT_SUCCESS) {
                String accountType = (String) eventCenter.getData();
                selector = filterAccount(accountType);
                if (null != selector && selector.size() > 0) {
                    mLoginTypeFView.hideException();
                    mAdapter.addAll(selector);
                } else {
                    mLoginTypeFView.showException();
                    mAdapter.clearData();
                }
                mLoginTypeFView.runUi(mAdapter);
                mLoginTypeFView.setToolBar(accountType);
            }
        }
    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        mLoginTypeFView.readGo(EditActivity.class, Constants.VIEW_MODE, selector.get(position).getGuidPW());
    }

    public void getArgus(Bundle arguments) {
//        position = arguments.getString("position");
    }
}

