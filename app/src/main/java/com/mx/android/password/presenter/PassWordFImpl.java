package com.mx.android.password.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.mx.android.password.activity.EditActivity;
import com.mx.android.password.adapter.PWViewAdapter;
import com.mx.android.password.db.PWDBHelper;
import com.mx.android.password.entity.Account;
import com.mx.android.password.entity.Constants;
import com.mx.android.password.entity.EventCenter;
import com.mx.android.password.entity.LoginTypeFView;

import java.util.ArrayList;

import static com.mx.android.password.entity.Constants.EVEN_BUS.CHANGE_PASS_WORD_SHOW;
import static com.mx.android.password.entity.Constants.EVEN_BUS.FILTER_EVENT_SUCCESS;
import static com.mx.android.password.entity.Constants.EVEN_BUS.INDEX_EVENT_SUCCESS;

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

    public void onSwapAccount(Account account1, Account account2) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PWDBHelper.onSwapAccount(mContext, account1, account2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
            if (eventCenter.getEventCode() == INDEX_EVENT_SUCCESS) {
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
                }
                mLoginTypeFView.runUi(mAdapter);
            } else if (eventCenter.getEventCode() == CHANGE_PASS_WORD_SHOW) {
//                mAdapter.notifyDataSetChanged();
                mLoginTypeFView.runUi(mAdapter);
            } else if (eventCenter.getEventCode() == FILTER_EVENT_SUCCESS) {
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

