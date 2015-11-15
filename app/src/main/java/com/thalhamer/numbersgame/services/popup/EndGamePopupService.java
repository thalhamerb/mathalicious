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
import com.thalhamer.numbersgame.Activity.GameActivity;
import com.thalhamer.numbersgame.R;
import com.thalhamer.numbersgame.domain.GameDataHolder;
import com.thalhamer.numbersgame.domain.LevelData;
import com.thalhamer.numbersgame.domain.LevelInfo;
import com.thalhamer.numbersgame.domain.StarsInfo;
import com.thalhamer.numbersgame.domain.Stats;
import com.thalhamer.numbersgame.enums.Character;
import com.thalhamer.numbersgame.enums.MessageLocation;
import com.thalhamer.numbersgame.enums.MessageType;
import com.thalhamer.numbersgame.enums.sounds.SoundEnum;
import com.thalhamer.numbersgame.services.GridTileDataService;
import com.thalhamer.numbersgame.services.MessageService;
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
public class EndGamePopupService extends AbstractPopupService {

    public static final int TIME_BETWEEN_MESSAGE_WRITE = 40;

    @Inject
    GameDataHolder gameDataHolder;
    @Inject
    StarsAndUnlockService starsAndUnlockService;
    @Inject
    MessageService messageService;

    GridTileDataService gridTileDataService = new GridTileDataService();

    public void createEndGamePopup(Activity activity, ViewGroup currentView) {
        final View popupView = activity.getLayoutInflater().inflate(R.layout.popup_end_game, currentView, false);
        LevelInfo levelInfo = gameDataHolder.getLevelInfo();

        int numOfStars = setStarsAndScore(popupView, levelInfo);
        setExtraTasks(activity, popupView, levelInfo);
        createPopupWindow(popupView, currentView, activity, true);
        createEndGameThoughtBubbleAndButtons(popupView, numOfStars, activity);
    }

    private void setExtraTasks(Activity activity, View popupView, LevelInfo levelInfo) {
        //get and reset extra task related views
        LinearLayout taskLayout = (LinearLayout) popupView.findViewById(R.id.otherTasks);
        taskLayout.removeAllViews();
        TextView extraTasksTitle = (TextView) popupView.findViewById(R.id.extraTasks);
        extraTasksTitle.setVisibility(View.GONE);

        if (levelInfo.getGridTileData() != null) {
            Map<Object, String> extraTasks = gridTileDataService.getGridTileDataDescriptions(levelInfo.getGridTileData());
            if (!extraTasks.isEmpty()) {
                extraTasksTitle.setVisibility(View.VISIBLE);
                gridTileDataService.setExtraTaskDescriptionsInLinearLayout(activity, extraTasks, taskLayout, levelInfo.getGridData());
            }
        }
    }

    private int setStarsAndScore(View popupView, LevelInfo levelInfo) {
        Stats stats = levelInfo.getStats();
        StarsInfo starsInfo = stats.getStarsInfo();
        int numOfStars = starsAndUnlockService.getNumOfStarsEarned(starsInfo, levelInfo.getStats().getScore());
        GridLayout gridLayout = (GridLayout) popupView.findViewById(R.id.starGrid);
        for (int i = 0; i < gridLayout.getColumnCount(); i++) {
            ImageView imageView = (ImageView) gridLayout.getChildAt(i);
            if (Integer.valueOf((String) imageView.getTag()) > numOfStars) {
                imageView.setBackgroundResource(R.mipmap.star_empty_blk);
            }
        }

        TextView scoreTextView = (TextView) popupView.findViewById(R.id.score);
        String scoreSuffix = levelInfo.getScoreType().getPopupSuffix();
        String score = String.format("Score: %s " + scoreSuffix, stats.getScore());
        scoreTextView.setText(score);
        return numOfStars;
    }

    private void createEndGameThoughtBubbleAndButtons(final View popupView, int numOfStars, final Activity activity) {
        final TextView thoughtBubbleText = (TextView) popupView.findViewById(R.id.thought_bubble_text);
        MessageType messageType = numOfStars > 1 ? MessageType.POSITIVE : MessageType.NEGATIVE;
        Character character = Character.getCharacterFromEpic(gameDataHolder.getLevelData().getEpic());
        final String message = messageService.getRandomGameMessage(character, messageType, MessageLocation.AFTER_GAME);
        startTimedMessageCreation(popupView, activity, thoughtBubbleText, message);
    }

    private void startTimedMessageCreation(final View popupView, final Activity activity, final TextView thoughtBubbleText, final String message) {
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
                    activateEndGameButtons(popupView, activity);
                } else {
                    textHandler.postDelayed(this, TIME_BETWEEN_MESSAGE_WRITE);
                }
            }
        };

        textHandler.postDelayed(textRunnable, TIME_BETWEEN_MESSAGE_WRITE);
    }

    private void activateEndGameButtons(View popupView, final Activity activity) {
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
                Intent intent = new Intent(activity, GameActivity.class);
                intent.putExtra(activity.getString(R.string.CHOSEN_LEVEL), levelData.getLevel());
                intent.putExtra(activity.getString(R.string.epic), levelData.getEpic());
                intent.putExtra(activity.getString(R.string.section), levelData.getSection());
                activity.startActivity(intent);
            }
        });

        Button nextButton = (Button) popupView.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEnum.CLICK1.getMediaPlayer().start();
                Intent intent = new Intent(activity, GameActivity.class);
                LevelData nextLevelData = starsAndUnlockService.getNextLevel(gameDataHolder.getLevelData());
                intent.putExtra(activity.getString(R.string.CHOSEN_LEVEL), nextLevelData.getLevel());
                intent.putExtra(activity.getString(R.string.epic), nextLevelData.getEpic());
                intent.putExtra(activity.getString(R.string.section), nextLevelData.getSection());
                activity.startActivity(intent);
            }
        });
    }
}
