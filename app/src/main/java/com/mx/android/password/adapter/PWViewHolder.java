package com.mx.android.password.adapter;

import android.graphics.Color;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.jakewharton.rxbinding.view.RxView;
import com.mx.android.password.R;
import com.mx.android.password.customview.onStateChangedListener;

import java.util.concurrent.TimeUnit;

/**
 * Created by mxuan on 2016-07-11.
 */
public class PWViewHolder extends RecyclerView.ViewHolder implements onStateChangedListener {

    private final TextView mTitleTextView;
    private final TextView mNoteContentTextView;
    private final TextView mMemoInfo;
    private final MaterialRippleLayout ripple;
    private final TextView mPassWordTextView;
    private final FrameLayout mMemoInfoContent;
    private final ImageView mImageType;
    private final ImageView mMoveimg;
    private OnRippleClick onRippleClick;

    public PWViewHolder(View parent) {
        super(parent);
        ripple = (MaterialRippleLayout) parent.findViewById(R.id.ripple);
        mTitleTextView = (TextView) parent.findViewById(R.id.main_item_title);
        mNoteContentTextView = (TextView) parent.findViewById(R.id.main_item_name);
        mPassWordTextView = (TextView) parent.findViewById(R.id.main_item_password);
        mMemoInfo = (TextView) parent.findViewById(R.id.memoInfo);
        mMemoInfoContent = (FrameLayout) parent.findViewById(R.id.main_item_note_container);
        mImageType = (ImageView) parent.findViewById(R.id.imageType);
        mMoveimg = (ImageView) parent.findViewById(R.id.moveimg);
        RxView.clicks(ripple).throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe(
                aVoid -> onRippleClick.onRippleClick(ripple)
        );
    }

    public void setMemoInfoContentVisibility(boolean visibility) {
        if (visibility) {
            mMemoInfoContent.setVisibility(View.VISIBLE);
        } else {
            mMemoInfoContent.setVisibility(View.GONE);
        }
    }

    public View getTouchView() {
        return mMoveimg;
    }

    @Override
    public void onItemSelected() {
        //设置item的背景颜色为浅灰色
        getTouchView().setBackgroundColor(Color.LTGRAY);
    }

    @Override
    public void onItemClear() {
        //恢复item的背景颜色
        getTouchView().setBackgroundColor(0);
    }

    public void setLabelText(CharSequence text) {
        setTextView(mTitleTextView, text);
    }

    public void setLabelText(int text) {
        setTextView(mTitleTextView, text);
    }

    public void setContentText(CharSequence text) {
        setTextView(mNoteContentTextView, text);
    }

    public void setContentText(int text) {
        setTextView(mNoteContentTextView, text);
    }

    public void setMemoInfo(CharSequence text) {
        setTextView(mMemoInfo, text);
    }

    public TextView getPassWordTextView() {
        return mPassWordTextView;
    }

    public void setPassWordTextView(String passWord) {
        setTextView(mPassWordTextView, passWord);
    }

    private void setTextView(TextView view, CharSequence text) {
        if (view == null)
            return;
        if (TextUtils.isEmpty(text))
            view.setVisibility(View.GONE);
        view.setText(text);
    }

    private void setTextView(TextView view, @StringRes int text) {
        if (view == null || text <= 0)
            return;
        view.setText(text);
    }

    public void setOnRippleClickListener(OnRippleClick onRippleClick) {
        this.onRippleClick = onRippleClick;
    }

    public interface OnRippleClick {
        void onRippleClick(View view);
    }
}