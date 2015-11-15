package com.thalhamer.numbersgame.services.popup;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.google.common.collect.Lists;
import com.thalhamer.numbersgame.Activity.GameActivity;
import com.thalhamer.numbersgame.Factory.App;
import com.thalhamer.numbersgame.R;
import com.thalhamer.numbersgame.domain.GameDataHolder;
import com.thalhamer.numbersgame.domain.IapPower;
import com.thalhamer.numbersgame.enums.PowerEnum;
import com.thalhamer.numbersgame.enums.sounds.SoundEnum;
import com.thalhamer.numbersgame.services.InAppPurchaseService;
import com.thalhamer.numbersgame.util.IabHelper;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * service
 * <p/>
 * Created by Brian on 11/14/2015.
 */
@Singleton
public class IAPPopupService extends AbstractPopupService {

    @Inject
    GameDataHolder gameDataHolder;

    public PopupWindow createIapStorePopup(final Activity activity, ViewGroup currentView, PowerEnum powerEnum, InAppPurchaseService inAppPurchaseService) {
        gameDataHolder.setPopupScreenOpen(true);
        if (activity instanceof GameActivity) {
            ((GameActivity) activity).pauseGame();
        }

        //initialize layout
        final RelativeLayout fullLayout = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.buy_power_full_store, currentView, false);
        fullLayout.removeView(fullLayout.findViewById(R.id.samplePowerTab));
        fullLayout.removeView(fullLayout.findViewById(R.id.samplePowerImage));

        IabHelper mHelper = null;
        if (activity instanceof GameActivity) {
            mHelper = ((GameActivity) activity).getmHelper();
        }

        List<IapPower> iapPowers = Lists.newArrayList();
        int firstTabImageMargin = App.getContext().getResources().getDimensionPixelSize(R.dimen.buy_power_tabImage1_left);
        int tabImageIncrement = App.getContext().getResources().getDimensionPixelSize(R.dimen.buy_power_tabImage_increment);
        int currentTabImageMargin = firstTabImageMargin;
        List<ImageView> viewsToAlwaysKeepInFront = Lists.newArrayList();
        View tabToInitiallyBringToFront = null;

        //create tab views (the main event)
        for (PowerEnum currentPowerEnum : PowerEnum.values()) {
            View powerView = createIapBuyView(activity, currentView, currentPowerEnum);
            fullLayout.addView(powerView);
            ImageView clickableTabImage = createPowerTabImage(activity, PowerEnum.CLEAR_ONE_ENUM, currentTabImageMargin);
            currentTabImageMargin += tabImageIncrement;
            fullLayout.addView(clickableTabImage);
            viewsToAlwaysKeepInFront.add(clickableTabImage);
            if (powerEnum.equals(currentPowerEnum)) {
                tabToInitiallyBringToFront = powerView;
            }
            iapPowers.add(new IapPower(powerView, clickableTabImage, currentPowerEnum));
        }

        inAppPurchaseService.setPowerBuyDetails(activity, mHelper, iapPowers);
        final PopupWindow popupWindow = createPopupWindow(fullLayout, currentView, activity, false);

        final ImageButton cancelButton = (ImageButton) fullLayout.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEnum.CLICK1.getMediaPlayer().start();
                popupWindow.dismiss();
                if (activity instanceof GameActivity) {
                    gameDataHolder.setPopupScreenOpen(false);
                    Log.d("Popupservice", "resumeGame - createIapStore");
                    ((GameActivity) activity).resumeGame();
                }
            }
        });
        viewsToAlwaysKeepInFront.add(cancelButton);

        //create tab image button listeners
        for (IapPower iapPower : iapPowers) {
            createTabPowerImageClickListener(iapPower.getClickableTabImage(), iapPower.getPowerView(), viewsToAlwaysKeepInFront);
        }

        if (tabToInitiallyBringToFront != null) {
            tabToInitiallyBringToFront.bringToFront();
        }
        bringImageViewsToFront(viewsToAlwaysKeepInFront);
        return popupWindow;
    }

    private View createIapBuyView(Activity activity, ViewGroup currentView, PowerEnum powerEnum) {
        final View powerView = activity.getLayoutInflater().inflate(R.layout.buy_power, currentView, false);
        powerView.setBackgroundResource(powerEnum.getTabImageId());
        ImageView powerImage = (ImageView) powerView.findViewById(R.id.powerImage);
        powerImage.setBackgroundResource(powerEnum.getImageResourceId());
        return powerView;
    }

    private ImageView createPowerTabImage(Activity activity, PowerEnum powerEnum, int marginLeft) {
        ImageView tabImage = new ImageView(activity.getApplicationContext());
        tabImage.setBackgroundResource(powerEnum.getImageResourceId());

        int sideLength = App.getContext().getResources().getDimensionPixelSize(R.dimen.buy_power_buyImage_nestedImage_sideLength);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(sideLength, sideLength);
        tabImage.setLayoutParams(lp);
        int marginTop = App.getContext().getResources().getDimensionPixelSize(R.dimen.buy_power_tabImages_top);
        lp.setMargins(marginLeft, marginTop, 0, 0);
        tabImage.setLayoutParams(lp);
        return tabImage;
    }

    private void createTabPowerImageClickListener(ImageView powerTabImage, final View viewToLinkTo, final List<ImageView> bringForwardViews) {
        powerTabImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEnum.CLICK1.getMediaPlayer().start();
                viewToLinkTo.bringToFront();
                bringImageViewsToFront(bringForwardViews);
            }
        });
    }

    private void bringImageViewsToFront(final List<ImageView> bringForwardViews) {
        for (ImageView imageView : bringForwardViews) {
            imageView.bringToFront();
        }
    }
}
