package com.thalhamer.numbersgame.Activity;import android.app.Activity;import android.content.Intent;import android.media.AudioManager;import android.os.Bundle;import android.os.Handler;import android.util.Log;import android.view.View;import android.view.ViewGroup;import android.view.Window;import android.view.WindowManager;import android.view.animation.Animation;import android.view.animation.AnimationUtils;import android.widget.Button;import android.widget.RelativeLayout;import com.google.android.gms.ads.AdListener;import com.google.android.gms.ads.AdRequest;import com.google.android.gms.ads.InterstitialAd;import com.google.common.collect.Lists;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.Modules.AppModule;import com.thalhamer.numbersgame.R;import com.thalhamer.numbersgame.domain.GameDataHolder;import com.thalhamer.numbersgame.domain.GridData;import com.thalhamer.numbersgame.domain.LevelData;import com.thalhamer.numbersgame.domain.LevelInfo;import com.thalhamer.numbersgame.enums.GameState;import com.thalhamer.numbersgame.enums.sounds.SoundEnum;import com.thalhamer.numbersgame.services.InAppPurchaseService;import com.thalhamer.numbersgame.services.SavedDataService;import com.thalhamer.numbersgame.services.TimerService;import com.thalhamer.numbersgame.services.popup.GameExplanationPopupService;import com.thalhamer.numbersgame.services.popup.LevelInfoPopupService;import com.thalhamer.numbersgame.util.IabHelper;import com.thalhamer.numbersgame.viewhelper.GameStateHolder;import com.thalhamer.numbersgame.viewhelper.TouchStateHolder;import org.json.JSONException;import java.io.IOException;import java.util.List;import dagger.ObjectGraph;/** * Game activity */public class GameActivity extends Activity {    public static final String TAG = "GameActivity";    private ObjectGraph objectGraph;    private GameDataHolder gameDataHolder;    private SavedDataService savedDataService;    private boolean pauseScreenOpen = false;    private Animation animShowPauseScreen;    private Animation animHidePauseScreen;    private RelativeLayout pauseScreen;    private GameExplanationPopupService gameExplanationPopupService;    private LevelInfoPopupService levelInfoPopupService;    private IabHelper mHelper;    private InAppPurchaseService inAppPurchaseService;    @Override    public void onCreate(Bundle savedInstanceState) {        Log.d("game activity", "on create");        super.onCreate(savedInstanceState);        setVolumeControlStream(AudioManager.STREAM_MUSIC);        Intent intent = getIntent();        int level = intent.getIntExtra(getString(R.string.CHOSEN_LEVEL), 0);        int epic = intent.getIntExtra(getString(R.string.epic), 0);        int section = intent.getIntExtra(getString(R.string.section), 0);        TouchStateHolder.setTouchState(GridData.TouchState.DISABLED);        GameStateHolder.setGameState(GameState.RUNNING);        //set window params        requestWindowFeature(Window.FEATURE_NO_TITLE);        // makes it full screen        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);        ObjectGraph objectGraph = ObjectGraph.create(new AppModule());        this.objectGraph = objectGraph;        App.setObjectGraph(objectGraph);        savedDataService = objectGraph.get(SavedDataService.class);        gameExplanationPopupService = objectGraph.get(GameExplanationPopupService.class);        levelInfoPopupService = objectGraph.get(LevelInfoPopupService.class);        inAppPurchaseService = objectGraph.get(InAppPurchaseService.class);        LevelData levelData = new LevelData(epic, section, level);        gameDataHolder = objectGraph.get(GameDataHolder.class);        gameDataHolder.setLevelData(levelData);        gameDataHolder.setGameActivity(this);        setContentView(R.layout.main_game_activity);        initPauseScreen();        initAdvertisement();        mHelper = inAppPurchaseService.getInAppPurchaseHelper(this);    }    private void initAdvertisement() {        //first game they play will set ad        if (App.getmInterstitialAd() == null) {            App.setmInterstitialAd(new InterstitialAd(this));            App.getmInterstitialAd().setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));            App.getmInterstitialAd().setAdListener(new AdListener() {                @Override                public void onAdClosed() {                    requestNewInterstitial();                    App.setAdClosed(true);                }            });        }        //load advertisement        if (App.getmInterstitialAd().isLoaded()) {            App.getmInterstitialAd().show();            //create listener runnable for when ad is closed            Runnable runnable = new Runnable() {                @Override                public void run() {                    if (App.isAdClosed()) {                        initNewGameDescriptionPopup();                        App.setAdClosed(false);                        TouchStateHolder.setTouchState(GridData.TouchState.ENABLED);                    } else {                        new Handler().postDelayed(this, 1000);                    }                }            };            new Handler().postDelayed(runnable, 1000);        } else {            requestNewInterstitial();            initNewGameDescriptionPopup();            App.setAdClosed(true);            TouchStateHolder.setTouchState(GridData.TouchState.ENABLED);        }    }    @Override    protected void onActivityResult(int requestCode, int resultCode, Intent data) {        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);        // Pass on the activity result to the helper for handling        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {            // not handled, so handle it ourselves (here's where you'd            // perform any handling of activity results not related to in-app            // billing...            super.onActivityResult(requestCode, resultCode, data);        } else {            Log.d(TAG, "onActivityResult handled by IABUtil.");        }    }    private void requestNewInterstitial() {        //TODO switch out test ones when ready to deploy        if (!App.getmInterstitialAd().isLoaded()) {            AdRequest adRequest = new AdRequest.Builder()                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)  // All emulators                    .addTestDevice("D6431951621DB594FC023DCEFCEA6E44")  // My Galaxy Nexus test phone                    .build();            App.getmInterstitialAd().loadAd(adRequest);        }    }    public GameDataHolder getGameDataHolder() {        return gameDataHolder;    }    @Override    protected void onPause() {        pauseGame();        Log.d("game activity", "on pause");        super.onPause();    }    @Override    protected void onResume() {        if (!gameDataHolder.isPopupScreenOpen()) {            Log.d("game activity", "resumeGame onResume():@Override");            resumeGame();        }        super.onResume();    }    @Override    protected void onDestroy() {        Log.d("game activity", "Activity is being destroyed");        super.onDestroy();        destroy();    }    private void destroy() {        GameStateHolder.setGameState(GameState.GRID_LOCKED);        TouchStateHolder.setTouchState(GridData.TouchState.DISABLED);        //clear out in app purchase link        if (mHelper != null) mHelper.dispose();        mHelper = null;    }    public void pauseGame() {        Log.d("Game activity", "called pauseGame - (resumeGame)");        objectGraph.get(TimerService.class).stopTimersAndHandlerCallbacks();    }    public void resumeGame() {        Log.d("Game activity", "called resumeGame");        if (gameDataHolder.getLevelInfo() != null) {            objectGraph.get(TimerService.class).startTimersAndHandlerCallbacks();        }    }    private void initPauseScreen() {        pauseScreen = (RelativeLayout) findViewById(R.id.mainGamePause);        animShowPauseScreen = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);        animHidePauseScreen = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);        final LevelData levelData = gameDataHolder.getLevelData();        final LevelInfo levelInfo = gameDataHolder.getLevelInfo();        Button mainGameExitButton = (Button) findViewById(R.id.mainGameExitButton);        mainGameExitButton.setOnClickListener(new View.OnClickListener() {            public void onClick(View view) {                SoundEnum.CLICK1.getMediaPlayer().start();                Intent intent = new Intent(GameActivity.this, ChooseLevelActivity.class);                intent.putExtra(getString(R.string.epic), levelData.getEpic());                intent.putExtra(getString(R.string.section), levelData.getSection());                destroy();                startActivity(intent);            }        });        Button mainGameInfoButton = (Button) findViewById(R.id.mainGameInfoButton);        mainGameInfoButton.setOnClickListener(new View.OnClickListener() {            public void onClick(View view) {                try {                    SoundEnum.CLICK1.getMediaPlayer().start();                    levelInfoPopupService.createLevelInfoPopup(GameActivity.this, levelData, (ViewGroup) findViewById(R.id.main_game_activity), true);                } catch (JSONException | IOException e) {                    e.printStackTrace();                }            }        });        Button mainGameExplanationButton = (Button) findViewById(R.id.mainGameExplanationButton);        mainGameExplanationButton.setOnClickListener(new View.OnClickListener() {            public void onClick(View view) {                SoundEnum.CLICK1.getMediaPlayer().start();                List<Object> enums = Lists.newArrayList();                enums.add(levelInfo.getCalcType());                enums.add(levelInfo.getScoreType());                gameExplanationPopupService.createGameExplanationPopup(GameActivity.this, (ViewGroup) findViewById(R.id.main_game_activity), enums);            }        });        Button mainGameUnpauseButton = (Button) findViewById(R.id.mainGameUnpauseButton);        mainGameUnpauseButton.setOnClickListener(new View.OnClickListener() {            public void onClick(View view) {                SoundEnum.CLICK1.getMediaPlayer().start();                onBackPressed();            }        });    }    private void initNewGameDescriptionPopup() {        LevelInfo levelInfo = gameDataHolder.getLevelInfo();        final List<Object> enums = Lists.newArrayList();        if (!savedDataService.containsKey(levelInfo.getCalcType().toString())) {            savedDataService.saveKey(levelInfo.getCalcType().toString(), 1);            enums.add(levelInfo.getCalcType());        }        if (!savedDataService.containsKey(levelInfo.getScoreType().toString())) {            savedDataService.saveKey(levelInfo.getScoreType().toString(), 1);            enums.add(levelInfo.getScoreType());        }        if (enums.size() > 0) {            gameDataHolder.setPopupScreenOpen(true);            Runnable initRunnable = new Runnable() {                @Override                public void run() {                    gameExplanationPopupService.createGameExplanationPopup(GameActivity.this, (ViewGroup) findViewById(R.id.main_game_activity), enums);                }            };            new Handler().postDelayed(initRunnable, 500);        } else {            Log.d("Game activity", "resumeGame  - initNewGameDescriptionPopup()");            gameDataHolder.setPopupScreenOpen(false);            resumeGame();        }    }    @Override    public void onBackPressed() {        if (!gameDataHolder.isPopupScreenOpen()) {            if (!pauseScreenOpen) {                pauseScreen.setVisibility(View.VISIBLE);                pauseScreen.startAnimation(animShowPauseScreen);                pauseScreenOpen = true;                pauseGame();            } else {                pauseScreen.setVisibility(View.GONE);                pauseScreen.startAnimation(animHidePauseScreen);                pauseScreenOpen = false;                Log.d("game activity", "resumeGame - onBackPressed()");                resumeGame();            }        }    }    public IabHelper getmHelper() {        return mHelper;    }}