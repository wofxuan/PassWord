package com.mx.android.password.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.mx.android.password.R;
import com.mx.android.password.entity.Constants;
import com.mx.android.password.entity.God;
import com.mx.android.password.utils.SPUtils;
import com.mx.android.password.utils.TimeUtils;
import com.mx.android.password.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mxuan on 2016-07-11.
 */
public class PWViewAdapter extends RecyclerView.Adapter<PWViewHolder> {
    private final Context mContext;
    private List<God> mGodList = new ArrayList<>();
    private OnRecyclerItemClickListener listener;
    private boolean isOpen;
    private int lastAnimatedPosition = -1;

    public PWViewAdapter(Context context, ArrayList<God> godArrayList) {
        mContext = context;
        if (godArrayList != null) {
            mGodList.addAll(godArrayList);
        }
    }

    @Override
    public PWViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_password_item, parent, false);
        return new PWViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PWViewHolder holder, int position) {
        runEnterAnimation(holder.itemView, position);
        holder.setLabelText(mGodList.get(position).getTitle());
        holder.setContentText(mGodList.get(position).getUserName());
        if (isOpen) {
            holder.setPassWordTextView(mGodList.get(position).getPassWord());
        } else {
            holder.setPassWordTextView("*********");
        }
        String memoInfo = mGodList.get(position).getMemoInfo();
        if (!memoInfo.equals("")) {
            holder.setMemoInfoContentVisibility(true);
            holder.setMemoInfo(mGodList.get(position).getMemoInfo());
        } else {
            holder.setMemoInfoContentVisibility(false);
        }
        int godType = mGodList.get(position).getGodType();
        switch (godType) {
            case 0:
                holder.setMoRen(mContext);
                break;
            case 1:
                holder.setYouXiang(mContext);
                break;
            case 2:
                holder.setCard(mContext);
                break;
        }
        final int mPosition = position;
        holder.setTimeText(TimeUtils.getConciseTime((mGodList.get(position).getTime()), mContext));
        holder.setOnRippleClickListener(new PWViewHolder.OnRippleClick() {
            @Override
            public void onRippleClick(View view) {
                if (listener != null) {
                    listener.onRecyclerItemClick(view, mPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        isOpen = (boolean) SPUtils.get(mContext, Constants.SETTING.OPEN_PASS_WORD_SHOW, true);
        return mGodList == null ? 0 : mGodList.size();
    }

    public void addOneTop(God god) {
        mGodList.add(0, god);
    }

    public void addAll(ArrayList<God> godArrayList) {
        mGodList.clear();
        mGodList.addAll(godArrayList);
    }

    public void clearData() {
        mGodList.clear();
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
