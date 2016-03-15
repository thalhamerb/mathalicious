package com.thalhamer.numbersgame.services.popup;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.thalhamer.numbersgame.Factory.App;
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
        ViewGroup currentView = (ViewGroup) activity.findViewById(android.R.id.content);
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
                PopupResult levelEndPopupResult = new PopupResult(popupResult.getActivity());
                levelEndPopupService.buildPopupWindow(levelEndPopupResult);
            }
        });
    }

    private void setPowersEarned(PopupResult popupResult, SectionUnlock sectionUnlock) {
        Activity activity = popupResult.getActivity();
        //hide all views so can unhide ones you want to show
        GridLayout gridLayout = (GridLayout) popupResult.getPopupView().findViewById(R.id.powerGrid);
        int count = gridLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = gridLayout.getChildAt(i);
            child.setVisibility(View.GONE);
        }
        //show unlocked powers
        for (Power power : sectionUnlock.getPowers()) {
            String powerEnumName = power.getPowerEnum().toString();

            int imageId = App.getContext().getResources().getIdentifier(powerEnumName + "_image", "id", App.getContext().getPackageName());
            ImageView imageView = (ImageView) popupResult.getPopupView().findViewById(imageId);
            imageView.setVisibility(View.VISIBLE);

            int textId = App.getContext().getResources().getIdentifier(powerEnumName + "_qty", "id", App.getContext().getPackageName());
            TextView textView = (TextView) popupResult.getPopupView().findViewById(textId);
            String quantityText = String.format(" x%d", power.getQuantity());
            textView.setText(quantityText);
            textView.setVisibility(View.VISIBLE);
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
