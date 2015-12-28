package com.thalhamer.numbersgame.domain;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * object holder for popups
 * <p/>
 * Created by Brian on 12/27/2015.
 */
public class PopupResult {
    //set before popupService
    private Activity activity;
    private ViewGroup currentView;

    //set in popupService
    private View popupView;
    private PopupWindow popupWindow;

    //popup specific fields
    private Boolean duringGameStart;

    public PopupResult(Activity activity, ViewGroup currentView) {
        this.activity = activity;
        this.currentView = currentView;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public ViewGroup getCurrentView() {
        return currentView;
    }

    public void setCurrentView(ViewGroup currentView) {
        this.currentView = currentView;
    }

    public View getPopupView() {
        return popupView;
    }

    public void setPopupView(View popupView) {
        this.popupView = popupView;
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public void setPopupWindow(PopupWindow popupWindow) {
        this.popupWindow = popupWindow;
    }

    public Boolean getDuringGameStart() {
        return duringGameStart;
    }

    public void setDuringGameStart(Boolean duringGameStart) {
        this.duringGameStart = duringGameStart;
    }
}
