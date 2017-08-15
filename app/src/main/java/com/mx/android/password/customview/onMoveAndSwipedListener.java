package com.mx.android.password.customview;

/**
 * Created by Administrator on 2017-08-07.
 */

public interface onMoveAndSwipedListener {
    boolean onItemMove(int fromPosition , int toPosition);
    void onItemDismiss(int position);
}
