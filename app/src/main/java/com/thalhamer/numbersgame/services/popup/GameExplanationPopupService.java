package com.thalhamer.numbersgame.services.popup;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
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
import com.thalhamer.numbersgame.enums.CalcType;
import com.thalhamer.numbersgame.enums.ScoreType;
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
public class GameExplanationPopupService extends AbstractPopupService {

    @Inject
    GameDataHolder gameDataHolder;

    public PopupWindow createGameExplanationPopup(Activity activity, ViewGroup currentView, List<Object> enums) {
        FrameLayout popupLayout = (FrameLayout) activity.getLayoutInflater().inflate(R.layout.popup_game_explanation,
                currentView, false);
        List<View> views = createViews(activity, enums, popupLayout);
        PopupWindow popupWindow = createPopupWindow(popupLayout, currentView, activity, true);
        setAllViewsButtons(activity, views, popupWindow);
        return popupWindow;
    }

    private void setAllViewsButtons(Activity activity, final List<View> views, final PopupWindow popupWindow) {
        final Animation slideOutLeft = AnimationUtils.loadAnimation(activity, R.anim.slide_out_left);
        final Animation slideInRight = AnimationUtils.loadAnimation(activity, R.anim.slide_in_right);
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
                        if (gameDataHolder != null) {
                            gameDataHolder.setPopupScreenOpen(false);
                            TouchStateHolder.setTouchState(GridData.TouchState.ENABLED);
                            Log.d("Popupservice", "resumeGame - createGameExplanationpopup");
                            gameDataHolder.getGameActivity().startGame();
                        }
                        popupWindow.dismiss();
                    }
                });
            }
        }
    }

    @NonNull
    private List<View> createViews(Activity activity, List<Object> enums, FrameLayout popupLayout) {
        final List<View> gameExplanationViews = Lists.newArrayList();
        for (Object enumObject : enums) {
            final View enumView = activity.getLayoutInflater().inflate(R.layout.game_explanation, popupLayout, false);
            TextView title = (TextView) enumView.findViewById(R.id.title);
            TextView description = (TextView) enumView.findViewById(R.id.description);
            ImageView imageView = (ImageView) enumView.findViewById(R.id.image);

            if (enumObject instanceof CalcType) {
                title.setText("Operation");
                description.setText(((CalcType) enumObject).getDescription());
                imageView.setImageResource(((CalcType) enumObject).getExplanationId());
            } else if (enumObject instanceof ScoreType) {
                title.setText("Score");
                description.setText(((ScoreType) enumObject).getFullGameDescription());
                imageView.setImageResource(((ScoreType) enumObject).getExplanationId());
            }

            popupLayout.addView(enumView);
            gameExplanationViews.add(enumView);
        }
        return gameExplanationViews;
    }
}
