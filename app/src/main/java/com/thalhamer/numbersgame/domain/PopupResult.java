package com.thalhamer.numbersgame.domain;

import android.app.Activity;
import android.view.View;
import android.widget.PopupWindow;

import com.thalhamer.numbersgame.services.TimerService;
import com.thalhamer.numbersgame.util.IabHelper;

/**
 * object holder for popups
 * <p/>
 * Created by Brian on 12/27/2015.
 */
public class PopupResult {
    //set before popupService
    private Activity activity;

    //set in popupService
    private View popupView;
    private PopupWindow popupWindow;

    //popup specific fields
    private Boolean duringGameStart;
    private LevelData nextLevelData;
    private boolean allTasksCompleted = true;
    private IabHelper mHelper;
    private TimerService timerService;

    public PopupResult(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
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

    public LevelData getNextLevelData() {
        return nextLevelData;
    }

    public void setNextLevelData(LevelData nextLevelData) {
        this.nextLevelData = nextLevelData;
    }

    public boolean isAllTasksCompleted() {
        return allTasksCompleted;
    }

    public void setAllTasksCompleted(boolean allTasksCompleted) {
        this.allTasksCompleted = allTasksCompleted;
    }

    public IabHelper getmHelper() {
        return mHelper;
    }

    public void setmHelper(IabHelper mHelper) {
        this.mHelper = mHelper;
    }

    public TimerService getTimerService() {
        return timerService;
    }

    public void setTimerService(TimerService timerService) {
        this.timerService = timerService;
    }
}
