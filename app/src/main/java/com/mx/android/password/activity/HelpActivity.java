package com.mx.android.password.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.mx.android.password.R;
import com.mx.android.password.activity.base.BaseActivity;
import com.mx.android.password.entity.Constants;
import com.mx.android.password.entity.EventCenter;

import butterknife.BindView;

public class HelpActivity extends BaseActivity {

    @BindView(R.id.helpMemo)
    public TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StringBuffer memo = new StringBuffer();

        memo.append("1.备份和还原的默认目录在SD卡的PSBackdir目录里面。\n");
        memo.append("2.可以通过类别进行过滤。\n");
        memo.append("3.长按主界面一记录可以拖动进行排序。\n");
        memo.append("4.导出的文本通过逗号分隔，如果是有图片的记录会统一生成到同路径的(文本名称IMG)的目录里面，" +
                "图片的名称是（类型-标题）.png。导入的文本格式要相同（可以先导出查看格式例子）。暂时不支持导入图片。\n");
        memo.append("5.如果打开自动备份会在每天第一次登陆备份文件，备份的目录在默认目录。\n");

        mTextView.setText(memo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initToolbar() {
        Toolbar mToolBar = (android.support.v7.widget.Toolbar) findViewById(R.id.common_toolbar);
        super.initToolBar(mToolBar);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_help;
    }

    @Override
    protected boolean isApplyTranslucency() {
        return true;
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
        if (eventCenter.getEventCode() == Constants.EVEN_BUS.CHANGE_THEME) {
            reload(false);
        }
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return false;
    }
}
