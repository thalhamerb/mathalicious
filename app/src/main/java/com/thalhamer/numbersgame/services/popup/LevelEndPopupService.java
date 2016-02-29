package com.thalhamer.numbersgame.services.popup;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thalhamer.numbersgame.Activity.ChooseLevelActivity;
import com.thalhamer.numbersgame.Activity.LevelActivity;
import com.thalhamer.numbersgame.R;
import com.thalhamer.numbersgame.domain.GameDataHolder;
import com.thalhamer.numbersgame.domain.LevelData;
import com.thalhamer.numbersgame.domain.LevelInfo;
import com.thalhamer.numbersgame.domain.PopupResult;
import com.thalhamer.numbersgame.domain.StarsInfo;
import com.thalhamer.numbersgame.domain.Stats;
import com.thalhamer.numbersgame.enums.Character;
import com.thalhamer.numbersgame.enums.MessageLocation;
import com.thalhamer.numbersgame.enums.MessageType;
import com.thalhamer.numbersgame.enums.sounds.SoundEnum;
import com.thalhamer.numbersgame.services.GridTileDataService;
import com.thalhamer.numbersgame.services.MessageService;
import com.thalhamer.numbersgame.services.SectionUnlockService;
import com.thalhamer.numbersgame.services.SoundService;
import com.thalhamer.numbersgame.services.StarsAndUnlockService;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * service
 * <p/>
 * Created by Brian on 11/14/2015.
 */
@Singleton
public class LevelEndPopupService extends AbstractPopupService {

    public static final int TIME_BETWEEN_MESSAGE_WRITE = 40;

    @Inject
    GameDataHolder gameDataHolder;
    @Inject
    StarsAndUnlockService starsAndUnlockService;
    @Inject
    MessageService messageService;
    @Inject
    SoundService soundService;
    @Inject
    LevelInfoPopupService levelInfoPopupService;
    @Inject
    SectionUnlockService sectionUnlockService;

    GridTileDataService gridTileDataService = new GridTileDataService();

    @Inject
    public LevelEndPopupService() {
    }

    public void buildPopupWindow(PopupResult popupResult) {
        Activity activity = popupResult.getActivity();
        ViewGroup currentView = (ViewGroup) activity.findViewById(android.R.id.content);
        View popupView = activity.getLayoutInflater().inflate(R.layout.popup_end_level, currentView, false);
        popupResult.setPopupView(popupView);

        LevelData nextLevelsData = starsAndUnlockService.getNextLevel(gameDataHolder.getLevelData());
        popupResult.setNextLevelData(nextLevelsData);
        int numOfStars = setStarsAndScore(popupResult);
        setExtraTasks(popupResult);
        createPopupWindow(popupResult);
        createEndGameThoughtBubbleAndButtons(popupResult, numOfStars);

        if (numOfStars == 3) {
            soundService.playSound(R.raw.oh_yea);
        }
    }

    private void setExtraTasks(PopupResult popupResult) {
        LevelInfo levelInfo = gameDataHolder.getLevelInfo();
        View popupView = popupResult.getPopupView();

        //get and reset extra task related views
        LinearLayout taskLayout = (LinearLayout) popupView.findViewById(R.id.otherTasks);
        taskLayout.removeAllViews();
        TextView extraTasksTitle = (TextView) popupView.findViewById(R.id.extraTasks);
        extraTasksTitle.setVisibility(View.GONE);

        if (levelInfo.getGridTileData() != null) {
            Map<Object, String> extraTasks = gridTileDataService.getGridTileDataDescriptions(levelInfo.getGridTileData());
            if (!extraTasks.isEmpty()) {
                extraTasksTitle.setVisibility(View.VISIBLE);
                boolean allTasksCompleted = gridTileDataService.setExtraTaskDescriptions(popupResult.getActivity(),
                        extraTasks, extraTasksTitle, taskLayout, levelInfo.getGridData());
                popupResult.setAllTasksCompleted(allTasksCompleted);
            }
        }
    }

    private int setStarsAndScore(PopupResult popupResult) {
        LevelInfo levelInfo = gameDataHolder.getLevelInfo();
        View popupView = popupResult.getPopupView();
        Stats stats = levelInfo.getStats();
        StarsInfo starsInfo = stats.getStarsInfo();
        int numOfStars = starsAndUnlockService.getNumOfStarsEarned(starsInfo, levelInfo.getStats().getScore());
        GridLayout gridLayout = (GridLayout) popupView.findViewById(R.id.starGrid);
        for (int i = 0; i < gridLayout.getColumnCount(); i++) {
            ImageView imageView = (ImageView) gridLayout.getChildAt(i);
            if (Integer.valueOf((String) imageView.getTag()) > numOfStars) {
                imageView.setBackgroundResource(R.drawable.star_empty_blk);
            }
        }

        TextView scoreTextView = (TextView) popupView.findViewById(R.id.score);
        String scoreSuffix = levelInfo.getScoreType().getPopupSuffix();
        String score = String.format("Score: %s " + scoreSuffix, stats.getScore());
        scoreTextView.setText(score);
        return numOfStars;
    }

    private void createEndGameThoughtBubbleAndButtons(PopupResult popupResult, int numOfStars) {
        TextView thoughtBubbleText = (TextView) popupResult.getPopupView().findViewById(R.id.thought_bubble_text);
        MessageType messageType = numOfStars > 1 ? MessageType.POSITIVE : MessageType.NEGATIVE;
        Character character = Character.getCharacterFromEpic(gameDataHolder.getLevelData().getEpic());
        ImageView characterImage = (ImageView) popupResult.getPopupView().findViewById(R.id.character);
        characterImage.setImageBitmap(character.fullBodyBitmap());
        String message;
        if (popupResult.isAllTasksCompleted()) {
            message = messageService.getRandomGameMessage(character, messageType, MessageLocation.AFTER_GAME);
        } else {
            message = "You don't get any stars because you didn't complete all of the requirements.";
        }

        initTimedMessageAndButtons(popupResult, thoughtBubbleText, message);
    }

    private void initTimedMessageAndButtons(final PopupResult popupResult, final TextView thoughtBubbleText, final String message) {
        if (!sectionUnlockService.isSectionUnlocked(popupResult.getNextLevelData())) {
            Button nextButton = (Button) popupResult.getPopupView().findViewById(R.id.nextButton);
            nextButton.setVisibility(View.INVISIBLE);
        }

        final Handler textHandler = new Handler();
        Runnable textRunnable = new Runnable() {
            private String currentText = "";
            private int currentMessageIndex = 0;

            @Override
            public void run() {
                currentText += message.charAt(currentMessageIndex);
                thoughtBubbleText.setText(currentText);
                currentMessageIndex++;

                if (currentMessageIndex == message.length()) {
                    activateEndGameButtons(popupResult);
                } else {
                    textHandler.postDelayed(this, TIME_BETWEEN_MESSAGE_WRITE);
                }
            }
        };

        textHandler.postDelayed(textRunnable, TIME_BETWEEN_MESSAGE_WRITE);
    }

    private void activateEndGameButtons(final PopupResult popupResult) {
        View popupView = popupResult.getPopupView();
        final Activity activity = popupResult.getActivity();
        ImageButton levelScreenButton = (ImageButton) popupView.findViewById(R.id.levelScreenButton);
        levelScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEnum.CLICK1.getMediaPlayer().start();
                LevelData levelData = gameDataHolder.getLevelData();
                Intent intent = new Intent(activity, ChooseLevelActivity.class);
                intent.putExtra(activity.getString(R.string.epic), levelData.getEpic());
                intent.putExtra(activity.getString(R.string.section), levelData.getSection());
                activity.startActivity(intent);
            }
        });

        Button retryButton = (Button) popupView.findViewById(R.id.retryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEnum.CLICK1.getMediaPlayer().start();
                LevelData levelData = gameDataHolder.getLevelData();
                Intent intent = new Intent(activity, LevelActivity.class);
                intent.putExtra(activity.getString(R.string.CHOSEN_LEVEL), levelData.getLevel());
                intent.putExtra(activity.getString(R.string.epic), levelData.getEpic());
                intent.putExtra(activity.getString(R.string.section), levelData.getSection());
                activity.startActivity(intent);
            }
        });

        Button nextButton = (Button) popupView.findViewById(R.id.nextButton);
        if (sectionUnlockService.isSectionUnlocked(popupResult.getNextLevelData())) {
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SoundEnum.CLICK1.getMediaPlayer().start();
                    popupResult.getPopupWindow().dismiss();
                    LevelData nextLevelData = starsAndUnlockService.getNextLevel(gameDataHolder.getLevelData());
                    PopupResult levelInfoPopupResult = new PopupResult(popupResult.getActivity());
                    levelInfoPopupService.buildPopupWindow(levelInfoPopupResult, nextLevelData, true, true);
                }
            });
        }
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }
}
