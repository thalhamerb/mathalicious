package com.thalhamer.numbersgame.services.popup;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.thalhamer.numbersgame.R;
import com.thalhamer.numbersgame.domain.GameDataHolder;
import com.thalhamer.numbersgame.domain.GridData;
import com.thalhamer.numbersgame.domain.PopupResult;
import com.thalhamer.numbersgame.enums.GameExplanation;
import com.thalhamer.numbersgame.enums.sounds.SoundEnum;
import com.thalhamer.numbersgame.viewhelper.TouchStateHolder;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * service
 * <p/>
 * Created by Brian on 11/14/2015.
 */
@Singleton
public class LevelExplanationPopupService extends AbstractPopupService {

    @Inject
    GameDataHolder gameDataHolder;

    public PopupWindow buildPopupWindow(PopupResult popupResult, List<GameExplanation> enums) {
        Activity activity = popupResult.getActivity();
        ViewGroup currentView = popupResult.getCurrentView();
        FrameLayout popupLayout = (FrameLayout) activity.getLayoutInflater().inflate(R.layout.popup_level_explanation,
                currentView, false);
        popupResult.setPopupView(popupLayout);

        List<View> views = createViews(popupResult, enums);
        PopupWindow popupWindow = createPopupWindow(popupResult);
        popupResult.setPopupWindow(popupWindow);
        setAllViewsButtons(popupResult, views);
        return popupWindow;
    }

    private void setAllViewsButtons(final PopupResult popupResult, final List<View> views) {
        final Animation slideOutLeft = AnimationUtils.loadAnimation(popupResult.getActivity(), R.anim.slide_out_left);
        final Animation slideInRight = AnimationUtils.loadAnimation(popupResult.getActivity(), R.anim.slide_in_right);
        for (int i = 0; i < views.size(); i++) {
            final View view = views.get(i);
            if (i > 0) {
                view.setVisibility(View.GONE);
            }

            Button continueButton = (Button) view.findViewById(R.id.continueButton);
            if (i < views.size() - 1) {
                continueButton.setText("Next");
                final Integer currViewIndex = i;
                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SoundEnum.CLICK1.getMediaPlayer().start();
                        view.setVisibility(View.GONE);
                        view.startAnimation(slideOutLeft);
                        View nextView = views.get(currViewIndex + 1);
                        nextView.setVisibility(View.VISIBLE);
                        nextView.startAnimation(slideInRight);
                    }
                });
            } else {
                continueButton.setText("Got it!");
                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SoundEnum.CLICK1.getMediaPlayer().start();
                        if (gameDataHolder != null && popupResult.getDuringGameStart()) {
                            gameDataHolder.setPopupScreenOpen(false);
                            TouchStateHolder.setTouchState(GridData.TouchState.ENABLED);
                            gameDataHolder.getLevelActivity().startGame();
                        }
                        popupResult.getPopupWindow().dismiss();
                    }
                });
            }
        }
    }

    @NonNull
    private List<View> createViews(PopupResult popupResult, List<GameExplanation> enums) {
        final List<View> gameExplanationViews = Lists.newArrayList();
        for (GameExplanation enumObject : enums) {
            final View enumView = popupResult.getActivity().getLayoutInflater().inflate(R.layout.popup_level_explanation_fragment, popupResult.getCurrentView(), false);
            TextView title = (TextView) enumView.findViewById(R.id.title);
            TextView description = (TextView) enumView.findViewById(R.id.description);
            ImageView imageView = (ImageView) enumView.findViewById(R.id.image);

            title.setText(enumObject.getGameExplanationTitle());
            description.setText(enumObject.getDescription());
            imageView.setImageResource(enumObject.getExplanationId());

            ((FrameLayout) popupResult.getPopupView()).addView(enumView);
            gameExplanationViews.add(enumView);
        }
        return gameExplanationViews;
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }
}
