package com.thalhamer.numbersgame.services.popup;

import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.thalhamer.numbersgame.R;
import com.thalhamer.numbersgame.domain.PopupResult;
import com.thalhamer.numbersgame.domain.Power;
import com.thalhamer.numbersgame.domain.SectionUnlock;
import com.thalhamer.numbersgame.enums.Character;
import com.thalhamer.numbersgame.enums.sounds.SoundEnum;

import javax.inject.Inject;

/**
 * service
 * <p/>
 * Created by Brian on 11/14/2015.
 */
public class SectionUnlockPopupService extends AbstractPopupService {

    @Inject
    LevelEndPopupService levelEndPopupService;

    @Inject
    public SectionUnlockPopupService() {
    }

    public void buildPopupWindow(PopupResult popupResult, SectionUnlock sectionUnlock) {
        Activity activity = popupResult.getActivity();
        ViewGroup currentView = popupResult.getCurrentView();

        final View popupView = activity.getLayoutInflater().inflate(R.layout.popup_unlocked_section, currentView, false);
        popupResult.setPopupView(popupView);

        setTitle(popupResult, sectionUnlock);
        setPowersEarned(popupResult, sectionUnlock);

        createPopupWindow(popupResult);
        setButtons(popupResult);
    }

    private void setButtons(final PopupResult popupResult) {
        Button okButton = (Button) popupResult.getPopupView().findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEnum.CLICK1.getMediaPlayer().start();
                popupResult.getPopupWindow().dismiss();
                PopupResult levelEndPopupResult = new PopupResult(popupResult.getActivity(), popupResult.getCurrentView());
                levelEndPopupService.buildPopupWindow(levelEndPopupResult);
            }
        });
    }

    private void setPowersEarned(PopupResult popupResult, SectionUnlock sectionUnlock) {
        Activity activity = popupResult.getActivity();
        GridLayout gridLayout = (GridLayout) popupResult.getPopupView().findViewById(R.id.powerGrid);
        gridLayout.removeAllViews();
        for (Power power : sectionUnlock.getPowers()) {
            ImageView imageView = new ImageView(activity.getBaseContext());
            int imageLength = activity.getResources().getDimensionPixelSize(R.dimen.unlocked_screen_gridImage_sideLength);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(imageLength, imageLength);
            imageView.setLayoutParams(lp);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackgroundResource(power.getPowerEnum().getImageResourceId());
            gridLayout.addView(imageView);

            TextView numOfPowers = new TextView(activity.getBaseContext());
            int powerTextSize = (int) (activity.getResources().getDimension(R.dimen.unlocked_screen_power_textSize) / activity.getResources().getDisplayMetrics().density);
            numOfPowers.setTextSize(TypedValue.COMPLEX_UNIT_SP, powerTextSize);
            numOfPowers.setText(String.format("%s%d", " x", power.getQuantity()));
            numOfPowers.setTextColor(Color.WHITE);
            gridLayout.addView(numOfPowers);
        }
    }

    private void setTitle(PopupResult popupResult, SectionUnlock sectionUnlock) {
        TextView title = (TextView) popupResult.getPopupView().findViewById(R.id.title);
        Character character = Character.getCharacterFromEpic(sectionUnlock.getEpic());
        if (character != null) {
            String text = String.format("You unlocked %s's Section %s!", character.getName(), sectionUnlock.getSection());
            title.setText(text);
        }
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }
}
