package com.mx.android.password.entity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by mxuan on 2016-07-11.
 */
public interface LoginTypeFView {
    void initRecycler(LinearLayoutManager linearLayoutManager, RecyclerView.Adapter adapter);
    void readGo(Class clazz, int type, String mGuidPW);
    void hideException();
    void showException();
    void startDrag(RecyclerView.ViewHolder viewHolder);//拖动移动位置
    void setToolBar(String mTitle);//设置标题
    void runUi(RecyclerView.Adapter adapter);//更新UI
}
