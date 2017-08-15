package com.mx.android.password.presenter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.text.TextUtils;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.mx.android.password.R;
import com.mx.android.password.activity.AboutActivity;
import com.mx.android.password.activity.CreateLockActivity;
import com.mx.android.password.customview.SettingAView;
import com.mx.android.password.entity.Constants;
import com.mx.android.password.entity.EventCenter;
import com.mx.android.password.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;

//import com.umeng.fb.FeedbackAgent;
//import com.umeng.fb.fragment.FeedbackFragment;
//import com.umeng.update.UmengUpdateAgent;
//import com.umeng.update.UmengUpdateListener;
//import com.umeng.update.UpdateResponse;
//import com.umeng.update.UpdateStatus;


/**
 * Created by  Created by mxuan on 2016-07-10.
 */
public class SettingFImpl implements FragmentPresenter{

    private final Context mContext;
    private final SettingAView settingAView;
    private Boolean isOpen;
    private boolean isOpenShow;

    public SettingFImpl(Context context, SettingAView view) {
        mContext = context;
        settingAView = view;
    }

    @Override
    public void onFirstUserVisible() {
        settingAView.findView();
        isOpen = (Boolean) SPUtils.get(mContext, Constants.SETTING.OPEN_GESTURE, true);
        isOpenShow = (Boolean) SPUtils.get(mContext, Constants.SETTING.OPEN_PASS_WORD_SHOW, true);
        settingAView.initState(isOpen);
        settingAView.initOpenShow(isOpenShow);
    }

    @Override
    public void onUserVisible() {

    }

    @Override
    public void onUserInvisible() {
    }

    public void onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();

        if (TextUtils.equals(key, "开启手势密码")) {
            isOpen = !isOpen;
            SPUtils.put(mContext, Constants.SETTING.OPEN_GESTURE, isOpen);
        } else if (TextUtils.equals(key, "首页密码可见")) {
            isOpenShow = !isOpenShow;
            SPUtils.put(mContext, Constants.SETTING.OPEN_PASS_WORD_SHOW, isOpenShow);
            EventBus.getDefault().post(new EventCenter(Constants.EVEN_BUS.CHANGE_PASS_WORD_SHOW));
        }else if (TextUtils.equals(key, "修改手势密码")) {
            Intent intent = new Intent(mContext, CreateLockActivity.class);
            intent.putExtra("CREATE_MODE", Constants.UPDATE_GESTURE);
            settingAView.readyGo(CreateLockActivity.class,intent);
        } else if (TextUtils.equals(key, "更换主题")) {
            settingAView.showChangeThemeDialog();
        } else if (TextUtils.equals(key, "意见反馈")) {
            FeedbackAPI.openFeedbackActivity();
        } else if (TextUtils.equals(key, "给应用点赞~")) {
            giveFavor();
        } else if (TextUtils.equals(key, "关于")) {
            Bundle bundle = new Bundle();
            settingAView.go2(AboutActivity.class, bundle);
        }
    }

    public void onThemeChoose(int position) {
        SPUtils.put(mContext, mContext.getResources().getString(R.string.change_theme_key), position);
        EventBus.getDefault().post(new EventCenter(Constants.EVEN_BUS.CHANGE_THEME));
        settingAView.reCreate();
    }

    private void giveFavor(){
        try{
            Uri uri = Uri.parse("market://details?id="+ mContext.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }catch(ActivityNotFoundException e){
            e.printStackTrace();
        }
    }
}
