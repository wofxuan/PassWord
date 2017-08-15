package com.mx.android.password.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mx.android.password.R;

import java.util.List;

/**
 * Created by Administrator on 2017-08-09.
 */

public class PWFilterAdapter extends RecyclerView.Adapter<PWFilterHolder> {
    private OnItemClickLitener mOnItemClickLitener;
    private Context mContext;
    private List<String> mAdapterDatas;

    public PWFilterAdapter(Context context, List<String> mDatas) {
        mContext = context;
        mAdapterDatas = mDatas;
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public PWFilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PWFilterHolder holder = new PWFilterHolder(LayoutInflater.from(
                mContext).inflate(R.layout.filter_item, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(PWFilterHolder holder, int position) {
        holder.tv.setText(mAdapterDatas.get(position));

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mAdapterDatas.size();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }
}
