package com.mx.android.password.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.mx.android.password.R;
import com.mx.android.password.customview.EditAView;
import com.mx.android.password.entity.God;
import com.mx.android.password.entity.RealmHelper;
import com.mx.android.password.utils.TimeUtils;

import java.util.ArrayList;

import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * Created by mxuan on 2016-07-12.
 */
public class EditAImpl implements ActivityPresenter,
        TextWatcher, AdapterView.OnItemSelectedListener,
        View.OnFocusChangeListener, DialogInterface.OnClickListener, SwipeBackLayout.SwipeListener {

    private final Context mContext;
    private final EditAView mEditAView;
    private int mPosition = 0;
    private int createMode;
    private boolean isEdit;
    private boolean isCreate;
    private God mGodInfo;
    private int positionType;
    private String mPositiveButtonMsg;
    private int p;

    public EditAImpl(Context context, EditAView view) {
        mContext = context;
        mEditAView = view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mEditAView.getSwipeBack().addSwipeListener(this);

    }

    @Override
    public void getIntent(Intent intent) {
        createMode = intent.getIntExtra("CREATE_MODE", 1);
        switch (createMode) {
            case 0:// 查看
                p = intent.getIntExtra("position", 0);
                // 密码类型
                mPosition = positionType = intent.getIntExtra("positionType", 0);
                ArrayList<God> selector = selector(positionType);
                mGodInfo = selector.get(p);
                mEditAView.initViewModel(mGodInfo, positionType);
                mEditAView.setToolBarTitle(R.string.view_mode);
                mEditAView.setTime(TimeUtils.getTime(mGodInfo.getTime()));
                isEdit = false;
                break;
            case 1:// 添加
                isCreate = true;
                mEditAView.initCreateModel();
                break;
        }
    }

    private ArrayList<God> selector(int positionType) {
        return RealmHelper.getInstances(mContext).selector(mContext, positionType);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String titleName = mEditAView.getTitleName();
        String userName = mEditAView.getUserName();
        String passWord = mEditAView.getPassWord();
        if (!TextUtils.isEmpty(titleName)) {
            mEditAView.setItemMenuVisible(true, R.id.done);
        } else {
            mEditAView.setItemMenuVisible(false, R.id.done);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public boolean onOptionItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                saveData();
                return true;
            case R.id.del:
                mPositiveButtonMsg = "确定";
                mEditAView.showDialog("删除后不能恢复，是否删除该本条记录？", mPositiveButtonMsg);
                return true;
            case android.R.id.home:
                return comeBack();
            default:
                return false;
        }
    }

    public boolean comeBack() {
        if (isEdit) {
            String userName = mEditAView.getUserName();
            String passWord = mEditAView.getPassWord();
            mEditAView.hideKeyBoard();
            if (positionType != mPosition || !TextUtils.equals(userName, mGodInfo.getUserName()) || !TextUtils.equals(passWord, mGodInfo.getPassWord())) {
                mPositiveButtonMsg = "保存";
                mEditAView.showDialog("密码还未保存，是否先保存在退出", mPositiveButtonMsg);
            } else {
                mEditAView.finishActivity();
            }
        } else {
            mEditAView.hideKeyBoard();
            mEditAView.finishActivity();
        }
        return true;
    }

    private void saveData() {
        String titleName = mEditAView.getTitleName();
        String userName = mEditAView.getUserName();
        String passWord = mEditAView.getPassWord();
        String memoInfo = mEditAView.getMemoInfo();
        byte[] img = mEditAView.getImg();

        God god = new God(mPosition, titleName, userName, passWord, TimeUtils.getCurrentTimeInLong(), memoInfo);
        god.setImg(img);
        switch (createMode) {
            case 0:
                if (!RealmHelper.update(mContext, god)) {
                    mEditAView.showSnackToast("修改失败");
                    mEditAView.hideKeyBoard();
                    return;
                }
                break;
            case 1:
                if (RealmHelper.save(mContext, god)) {
                    mEditAView.showSnackToast("保存失败，已经存在-" + god.getTitle() + "-的标题");
                    mEditAView.hideKeyBoard();
                    return;
                }
                break;
        }

        mEditAView.hideKeyBoard();
        mEditAView.finishActivity();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        isEdit = true;
        mPosition = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            isEdit = true;
            mEditAView.setToolBarTitle(R.string.edit_mode);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == dialog.BUTTON_POSITIVE) {
            if (TextUtils.equals(mPositiveButtonMsg, "确定")) {
                RealmHelper.delete(mContext, mGodInfo, p);
                mEditAView.finishActivity();
            } else {
                saveData();
            }
        } else if (which == dialog.BUTTON_NEGATIVE) {
            if (!TextUtils.equals(mPositiveButtonMsg, "确定")) {
                mEditAView.hideKeyBoard();
                mEditAView.finishActivity();
            }
        }

    }

    @Override
    public void onScrollStateChange(int state, float scrollPercent) {
        mEditAView.hideKeyBoard();
    }

    @Override
    public void onEdgeTouch(int edgeFlag) {

    }

    @Override
    public void onScrollOverThreshold() {

    }

}
