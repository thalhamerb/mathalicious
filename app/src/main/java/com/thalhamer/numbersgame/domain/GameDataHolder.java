package com.thalhamer.numbersgame.domain;import android.os.Handler;import android.text.SpannableStringBuilder;import com.thalhamer.numbersgame.Activity.GameActivity;import com.thalhamer.numbersgame.viewhelper.MainGamePanel;import javax.inject.Singleton;/** * holds game data * <p/> * Created by Brian on 4/12/2015. */@Singletonpublic class GameDataHolder {    private LevelData levelData;    private LevelInfo levelInfo;    private MainGamePanel gamePanel;    private Lock lock = new Lock();    private boolean gameRunning = false;    private Handler countUpHandler;    private Handler blockDropHandler;    private AfterGameTouchableObjects afterGameTouchableObjects;    private GameActivity gameActivity;    private boolean popupScreenOpen = false;    private boolean okToShowGameExplain = false;    private boolean showThoughtBubble = false;    private SpannableStringBuilder thoughtBubbleString = new SpannableStringBuilder();    //getters and setters    public LevelData getLevelData() {        return levelData;    }    public void setLevelData(LevelData levelData) {        this.levelData = levelData;    }    public LevelInfo getLevelInfo() {        return levelInfo;    }    public void setLevelInfo(LevelInfo levelInfo) {        this.levelInfo = levelInfo;    }    public MainGamePanel getGamePanel() {        return gamePanel;    }    public void setGamePanel(MainGamePanel gamePanel) {        this.gamePanel = gamePanel;    }    public Lock getLock() {        return lock;    }    public void setLock(Lock lock) {        this.lock = lock;    }    public boolean isGameRunning() {        return gameRunning;    }    public void setGameRunning(boolean gameRunning) {        this.gameRunning = gameRunning;    }    public Handler getCountUpHandler() {        return countUpHandler;    }    public void setCountUpHandler(Handler countUpHandler) {        this.countUpHandler = countUpHandler;    }    public Handler getBlockDropHandler() {        return blockDropHandler;    }    public void setBlockDropHandler(Handler blockDropHandler) {        this.blockDropHandler = blockDropHandler;    }    public AfterGameTouchableObjects getAfterGameTouchableObjects() {        return afterGameTouchableObjects;    }    public void setAfterGameTouchableObjects(AfterGameTouchableObjects afterGameTouchableObjects) {        this.afterGameTouchableObjects = afterGameTouchableObjects;    }    public GameActivity getGameActivity() {        return gameActivity;    }    public void setGameActivity(GameActivity gameActivity) {        this.gameActivity = gameActivity;    }    public boolean isPopupScreenOpen() {        return popupScreenOpen;    }    public void setPopupScreenOpen(boolean popupScreenOpen) {        this.popupScreenOpen = popupScreenOpen;    }    public boolean isOkToShowGameExplain() {        return okToShowGameExplain;    }    public void setOkToShowGameExplain(boolean okToShowGameExplain) {        this.okToShowGameExplain = okToShowGameExplain;    }    public boolean isShowThoughtBubble() {        return showThoughtBubble;    }    public void setShowThoughtBubble(boolean showThoughtBubble) {        this.showThoughtBubble = showThoughtBubble;    }    public SpannableStringBuilder getThoughtBubbleString() {        return thoughtBubbleString;    }    public void setThoughtBubbleString(SpannableStringBuilder thoughtBubbleString) {        this.thoughtBubbleString = thoughtBubbleString;    }}