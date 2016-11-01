package com.mx.android.password.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.mx.android.password.fragment.PWFragment;

/**
 * Created by mxuan on 2016-07-11.
 */
public class PWContentAdapter extends FragmentPagerAdapter {
    FragmentManager fm;

    public PWContentAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new PWFragment();
        Bundle argus = new Bundle();
        argus.putInt("position", position);
        fragment.setArguments(argus);
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

//    //核心方法
//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        if (true) {//根据需求添加更新标示，UI更新完成后改回false，看不懂的回家种田
//            //得到缓存的fragment
//            Fragment fragment = (Fragment) super.instantiateItem(container, position);
//            //得到tag，这点很重要
//            String fragmentTag = fragment.getTag(); //这里的tag是系统自己生产的，我们直接取就可以
//            //如果这个fragment需要更新
//            FragmentTransaction ft = fm.beginTransaction();
//            //移除旧的fragment
//            ft.remove(fragment);
//            //换成新的fragment
//            fragment = new PWFragment();
//            Bundle argus = new Bundle();
//            argus.putInt("position", position);
//            fragment.setArguments(argus);
//            //添加新fragment时必须用前面获得的tag，这点很重要
//            ft.add(container.getId(), fragment, fragmentTag);
//            ft.attach(fragment);
//            ft.commit();
//
//            return fragment;
//        } else {
//            return super.instantiateItem(container, position);
//        }
//    }
//
//    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }
//
//    @Override
//    public void notifyDataSetChanged() {
//        super.notifyDataSetChanged();
//    }
}
