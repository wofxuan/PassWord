package com.mx.android.password.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.LinearLayout;

import com.mx.android.password.R;
import com.mx.android.password.adapter.PWViewAdapter;
import com.mx.android.password.customview.onMoveAndSwipedListener;
import com.mx.android.password.customview.onStateChangedListener;
import com.mx.android.password.entity.EventCenter;
import com.mx.android.password.entity.LoginTypeFView;
import com.mx.android.password.presenter.PassWordFImpl;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

//import butterknife.Bind;

/**
 * Created by mxuan on 2016-07-11.
 */
public class PWFragment extends BaseFragment implements LoginTypeFView {
    private static final int INDEX_FRAGMENT_REQUEST_CODE = 2;
    private static final int EDIT_SAVE = 1;
    private static final int SUCCESS = 1;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.exception)
    LinearLayout mException;
    private PassWordFImpl mIndexFImpl;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    public void startDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void setToolBar(String mTitle) {
//        ((AppCompatActivity)mActivity).getActionBar().setTitle(mTitle);
    }

    @Override
    public void runUi(RecyclerView.Adapter adapter) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIndexFImpl = new PassWordFImpl(mActivity, this);
        mIndexFImpl.getArgus(getArguments());
    }

    @Override
    protected void onFirstUserVisible() {
        mIndexFImpl.onFirstUserVisible();
    }

    @Override
    protected void onUserVisible() {
        mIndexFImpl.onUserVisible();
    }

    @Override
    protected void onUserInvisible() {
        mIndexFImpl.onUserInvisible();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_login_type;
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
        mIndexFImpl.onEventComing(eventCenter);
    }

    @Override
    public void initRecycler(LinearLayoutManager linearLayoutManager, RecyclerView.Adapter adapter) {
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //关联ItemTouchHelper和RecyclerView
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback((PWViewAdapter) adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void readGo(Class clazz, int type, String mGuidPW) {
        Intent intent = new Intent(mActivity, clazz);
        intent.putExtra("CREATE_MODE", type);
        intent.putExtra("GuidPW", mGuidPW);
        startActivityForResult(intent, INDEX_FRAGMENT_REQUEST_CODE);
    }

    @Override
    public void hideException() {
        mException.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showException() {
        mException.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INDEX_FRAGMENT_REQUEST_CODE) {
            if (resultCode == EDIT_SAVE && resultCode == SUCCESS) {
                EventCenter eventCenter = new EventCenter(EDIT_SAVE, true);
                EventBus.getDefault().post(eventCenter);
            }
        }
    }

    public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
        private onMoveAndSwipedListener mAdapter;

        public SimpleItemTouchHelperCallback(onMoveAndSwipedListener listener) {
            mAdapter = listener;
        }

        /**
         * 这个方法是用来设置我们拖动的方向以及侧滑的方向的
         */
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            //如果是ListView样式的RecyclerView
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                //设置拖拽方向为上下
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                //设置侧滑方向为从左到右和从右到左都可以
//                final int swipeFlags = (ItemTouchHelper.START | ItemTouchHelper.END);
                final int swipeFlags = 0;
                //将方向参数设置进去
                return makeMovementFlags(dragFlags, swipeFlags);
            } else {//如果是GridView样式的RecyclerView
                //设置拖拽方向为上下左右
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                //不支持侧滑
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }

        /**
         * 当我们拖动item时会回调此方法
         */
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //如果两个item不是一个类型的，我们让他不可以拖拽
            if (viewHolder.getItemViewType() != target.getItemViewType()) {
                return false;
            }
            //回调adapter中的onItemMove方法
            mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            mIndexFImpl.onSwapAccount(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        /**
         * 当我们侧滑item时会回调此方法
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            //回调adapter中的onItemDismiss方法
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }

        /**
         * 当状态改变时回调此方法
         */
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            //当前状态不是idel（空闲）状态时，说明当前正在拖拽或者侧滑
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                //看看这个viewHolder是否实现了onStateChangedListener接口
                if (viewHolder instanceof onStateChangedListener) {
                    onStateChangedListener listener = (onStateChangedListener) viewHolder;
                    //回调ItemViewHolder中的onItemSelected方法来改变item的背景颜色
                    listener.onItemSelected();
                }
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        /**
         * 当用户拖拽完或者侧滑完一个item时回调此方法，用来清除施加在item上的一些状态
         */
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            if (viewHolder instanceof onStateChangedListener) {
                onStateChangedListener listener = (onStateChangedListener) viewHolder;
                listener.onItemClear();
            }
        }
    }
}
