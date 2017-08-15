package com.mx.android.password.adapter;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.mx.android.password.R;
import com.mx.android.password.customview.onMoveAndSwipedListener;
import com.mx.android.password.entity.Account;
import com.mx.android.password.entity.Constants;
import com.mx.android.password.entity.LoginTypeFView;
import com.mx.android.password.utils.SPUtils;
import com.mx.android.password.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mxuan on 2016-07-11.
 */
public class PWViewAdapter extends RecyclerView.Adapter<PWViewHolder> implements onMoveAndSwipedListener {
    private final Context mContext;
    private List<Account> mAccountList = new ArrayList<>();
    private OnRecyclerItemClickListener listener;
    private boolean isOpen;
    private int lastAnimatedPosition = -1;
    private LoginTypeFView mLoginTypeFView;

    public PWViewAdapter(Context context, ArrayList<Account> accountArrayList, LoginTypeFView view) {
        mContext = context;
        if (accountArrayList != null) {
            mAccountList.addAll(accountArrayList);
        }
        mLoginTypeFView = view;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //交换mItems数据的位置
        Collections.swap(mAccountList, fromPosition, toPosition);
        //交换RecyclerView列表中item的位置
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        //删除mItems数据
        mAccountList.remove(position);
        //删除RecyclerView列表对应item
        notifyItemRemoved(position);
    }

    @Override
    public PWViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_password_item, parent, false);
        return new PWViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PWViewHolder holder, int position) {
        runEnterAnimation(holder.itemView, position);
        holder.setLabelText(mAccountList.get(position).getTitle());
        holder.setContentText(mAccountList.get(position).getUserName());
        if (isOpen) {
            holder.setPassWordTextView(mAccountList.get(position).getPassWord());
        } else {
            holder.setPassWordTextView("*********");
        }
        String memoInfo = mAccountList.get(position).getMemoInfo();
        if (!memoInfo.equals("")) {
            holder.setMemoInfoContentVisibility(true);
            holder.setMemoInfo(mAccountList.get(position).getMemoInfo());
        } else {
            holder.setMemoInfoContentVisibility(false);
        }
        final int mPosition = position;
        holder.setOnRippleClickListener(new PWViewHolder.OnRippleClick() {
            @Override
            public void onRippleClick(View view) {
                if (listener != null) {
                    listener.onRecyclerItemClick(view, mPosition);
                }
            }
        });

        //拖动item时候要用的
        holder.getTouchView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //如果按下
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    //回调RecyclerListFragment中的startDrag方法
                    //让mItemTouchHelper执行拖拽操作
                    mLoginTypeFView.startDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        isOpen = (boolean) SPUtils.get(mContext, Constants.SETTING.OPEN_PASS_WORD_SHOW, true);
        return mAccountList == null ? 0 : mAccountList.size();
    }

    public void addOneTop(Account account) {
        mAccountList.add(0, account);
    }

    public void addAll(ArrayList<Account> accountArrayList) {
        mAccountList.clear();
        mAccountList.addAll(accountArrayList);
    }

    public void clearData() {
        mAccountList.clear();
    }


    public void setOnRecyclerItemClick(OnRecyclerItemClickListener onItemClickListener) {
        listener = onItemClickListener;
    }

    private void runEnterAnimation(View view, int position) {
        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(Utils.getScreenHeight(mContext));
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(1500)
                    .start();
        }
    }

    public interface OnRecyclerItemClickListener {
        void onRecyclerItemClick(View view, int position);
    }
}
