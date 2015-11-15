package com.thalhamer.numbersgame.services.popup;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.thalhamer.numbersgame.Activity.GameActivity;
import com.thalhamer.numbersgame.Factory.App;
import com.thalhamer.numbersgame.R;
import com.thalhamer.numbersgame.domain.LevelData;
import com.thalhamer.numbersgame.domain.StarsInfo;
import com.thalhamer.numbersgame.enums.ScoreType;
import com.thalhamer.numbersgame.enums.sounds.SoundEnum;
import com.thalhamer.numbersgame.services.AdvertisementService;
import com.thalhamer.numbersgame.services.GridTileDataService;
import com.thalhamer.numbersgame.services.JsonService;
import com.thalhamer.numbersgame.services.SavedDataService;
import com.thalhamer.numbersgame.services.StarsAndUnlockService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * service
 * <p/>
 * Created by Brian on 11/14/2015.
 */
@Singleton
public class LevelInfoPopupService extends AbstractPopupService {

    private SavedDataService savedDataService = new SavedDataService();
    private JsonService jsonService = new JsonService();
    GridTileDataService gridTileDataService = new GridTileDataService();
    AdvertisementService advertisementService = new AdvertisementService();

    @Inject //so works with app module
    public LevelInfoPopupService() {
    }

    public PopupWindow createLevelInfoPopup(final Activity activity, final LevelData levelData,
                                            ViewGroup currentView, boolean hideStartButton) throws JSONException, IOException {

        JSONObject levelObject = getJsonObject(levelData);
        View popupView = activity.getLayoutInflater().inflate(R.layout.popup_level_info, currentView, false);
        setTitleAndDescription(activity, levelData, levelObject, popupView);
        setStarsInfo(levelData, levelObject, popupView);
        return initiateButtonsAndCreatePopupWindow(activity, levelData, currentView, hideStartButton, popupView);
    }

    private JSONObject getJsonObject(LevelData levelData) throws IOException, JSONException {
        String sectionName = savedDataService.constructSectionName(levelData.getEpic(), levelData.getSection());
        String sectionJsonInfo = jsonService.getJsonDataFromResource(sectionName, "raw");
        JSONArray jsonArray = new JSONArray(sectionJsonInfo);
        return jsonArray.getJSONObject(levelData.getLevel() - 1);
    }

    private void setTitleAndDescription(Activity activity, LevelData levelData, JSONObject levelObject, View popupView) throws JSONException {
        TextView levelTextView = (TextView) popupView.findViewById(R.id.popUpLevelTitle);
        String text = String.format("Level %s-%s", levelData.getSection(), levelData.getLevel());
        levelTextView.setText(text);

        TextView gameTypetextView = (TextView) popupView.findViewById(R.id.gameType);
        String gameDescription = jsonService.getGameDescription(levelObject);
        gameTypetextView.setText(gameDescription);

        //get and reset extra task related views
        LinearLayout taskLayout = (LinearLayout) popupView.findViewById(R.id.otherTasks);
        taskLayout.removeAllViews();
        TextView extraTasksTitle = (TextView) popupView.findViewById(R.id.extraTasks);
        extraTasksTitle.setVisibility(View.GONE);

        Integer[][] gridTileData = gridTileDataService.getGridTileData(levelObject);
        if (gridTileData != null) {
            Map<Object, String> extraTasks = gridTileDataService.getGridTileDataDescriptions(gridTileData);
            if (!extraTasks.isEmpty()) {
                extraTasksTitle.setVisibility(View.VISIBLE);
                gridTileDataService.setExtraTaskDescriptionsInLinearLayout(activity, extraTasks, taskLayout, null);
            }
        }
    }

    private void setStarsInfo(LevelData levelData, JSONObject levelObject, View popupView) throws JSONException {
        String levelName = savedDataService.constructLevelName(levelData);
        StarsInfo starsInfo = new StarsAndUnlockService().getStarsInfo(levelObject, levelName);

        ScoreType scoreType = ScoreType.valueOf(levelObject.getString("scoreType"));
        TextView star1TextView = (TextView) popupView.findViewById(R.id.star1Points);
        star1TextView.setText(String.format("%d %s", starsInfo.getMinForOneStar(), scoreType.getPopupSuffix()));
        TextView star2TextView = (TextView) popupView.findViewById(R.id.star2Points);
        star2TextView.setText(String.format("%d %s", starsInfo.getMinForTwoStars(), scoreType.getPopupSuffix()));
        TextView star3TextView = (TextView) popupView.findViewById(R.id.star3Points);
        star3TextView.setText(String.format("%d %s", starsInfo.getMinForThreeStars(), scoreType.getPopupSuffix()));

        GridLayout gridLayout = (GridLayout) popupView.findViewById(R.id.currStarsGrid);
        for (int i = 0; i < gridLayout.getColumnCount(); i++) {
            ImageView imageView = (ImageView) gridLayout.getChildAt(i);
            if (Integer.valueOf((String) imageView.getTag()) > starsInfo.getCurrentNumOfStars()) {
                imageView.setBackgroundResource(R.mipmap.star_empty_blk);
            }
        }
    }

    private PopupWindow initiateButtonsAndCreatePopupWindow(final Activity activity, final LevelData levelData,
                                                            ViewGroup currentView, boolean hideStartButton, View popupView) {

        Button startButton = (Button) popupView.findViewById(R.id.startButton);
        if (hideStartButton) {
            startButton.setVisibility(View.INVISIBLE);
        } else {
            startButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    SoundEnum.CLICK1.getMediaPlayer().start();
                    loadAdvertisementAndStartGameActivity(activity, levelData);
                }
            });
        }

        final PopupWindow popupWindow = createPopupWindow(popupView, currentView, activity, false);

        ImageButton cancelButton = (ImageButton) popupView.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEnum.CLICK1.getMediaPlayer().start();
                popupWindow.dismiss();
            }
        });
        return popupWindow;
    }

    private void loadAdvertisementAndStartGameActivity(final Activity activity, final LevelData levelData) {
        if (App.getmInterstitialAd().isLoaded()) {
            App.getmInterstitialAd().setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            advertisementService.requestNewInterstitialAd();
                            startGameActivity(activity, levelData);
                        }
                    });
                }
            });

            App.getmInterstitialAd().show();

        } else {
            advertisementService.requestNewInterstitialAd();
            startGameActivity(activity, levelData);
        }
    }

    private void startGameActivity(Activity activity, LevelData levelData) {
        Intent intent = new Intent(activity, GameActivity.class);
        intent.putExtra(activity.getString(R.string.epic), levelData.getEpic());
        intent.putExtra(activity.getString(R.string.section), levelData.getSection());
        intent.putExtra(activity.getString(R.string.CHOSEN_LEVEL), levelData.getLevel());
        activity.startActivity(intent);
    }
}
