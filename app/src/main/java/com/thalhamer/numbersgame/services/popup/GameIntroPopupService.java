package com.thalhamer.numbersgame.services.popup;

import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import com.thalhamer.numbersgame.R;
import com.thalhamer.numbersgame.domain.PopupResult;
import com.thalhamer.numbersgame.enums.sounds.SoundEnum;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * game intro popup service
 * <p/>
 * Created by Brian on 12/25/2015.
 */
@Singleton
public class GameIntroPopupService extends AbstractPopupService {

    @Inject
    public GameIntroPopupService() {
    }

    public PopupWindow buildPopupWindow(PopupResult popupResult) {
        View popupView = popupResult.getActivity().getLayoutInflater().inflate(R.layout.popup_game_intro, popupResult.getCurrentView(), false);
        popupResult.setPopupView(popupView);
        final PopupWindow popupWindow = createPopupWindow(popupResult);

        Button okButton = (Button) popupView.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SoundEnum.CLICK1.getMediaPlayer().start();
                popupWindow.dismiss();
            }
        });

        return popupWindow;
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }
}
