package com.thalhamer.numbersgame.domain;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import java.util.List;

/**
 * view page result object
 */
public class ViewPagerResult {

    private FragmentActivity activity;
    private List<Fragment> fragments;
    private ViewPager pager;
    private TextView leftArrow;
    private TextView rightArrow;

    public FragmentActivity getActivity() {
        return activity;
    }

    public void setActivity(FragmentActivity activity) {
        this.activity = activity;
    }

    public List<Fragment> getFragments() {
        return fragments;
    }

    public void setFragments(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    public ViewPager getPager() {
        return pager;
    }

    public void setPager(ViewPager pager) {
        this.pager = pager;
    }

    public TextView getLeftArrow() {
        return leftArrow;
    }

    public void setLeftArrow(TextView leftArrow) {
        this.leftArrow = leftArrow;
    }

    public TextView getRightArrow() {
        return rightArrow;
    }

    public void setRightArrow(TextView rightArrow) {
        this.rightArrow = rightArrow;
    }
}
