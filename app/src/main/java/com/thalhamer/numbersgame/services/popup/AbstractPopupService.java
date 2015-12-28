package com.thalhamer.numbersgame.services.popup;import android.view.Gravity;import android.view.ViewGroup;import android.widget.PopupWindow;import android.widget.RelativeLayout;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.R;import com.thalhamer.numbersgame.domain.PopupResult;/** * abstract perform service * <p/> * Created by Brian on 8/19/2015. */public abstract class AbstractPopupService {    protected PopupWindow createPopupWindow(final PopupResult popupResult) {        final PopupWindow popupWindow = new PopupWindow(popupResult.getPopupView());        if (isFullScreen()) {            popupWindow.setWidth((int) (App.getDisplayWidth() * .95f));            popupWindow.setHeight((int) (App.getDisplayHeight() * .95f));        } else {            popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);        }        popupWindow.setFocusable(true);        popupWindow.setOutsideTouchable(false);        popupWindow.setAnimationStyle(R.style.popupAnimation);        popupWindow.showAtLocation(popupResult.getCurrentView(), Gravity.CENTER, 0, 0);        //set background dimming        final ViewGroup currentView = popupResult.getCurrentView();        final RelativeLayout dimmingLayout = (RelativeLayout) popupResult.getActivity().getLayoutInflater().inflate(R.layout.dim_fragment, currentView, false);        currentView.addView(dimmingLayout);        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {            @Override            public void onDismiss() {                currentView.removeView(dimmingLayout);            }        });        popupResult.setPopupWindow(popupWindow);        return popupWindow;    }    public abstract boolean isFullScreen();}