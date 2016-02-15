package com.thalhamer.numbersgame.services;import android.app.Activity;import android.os.AsyncTask;import android.util.Log;import android.view.ViewGroup;import com.thalhamer.numbersgame.R;import com.thalhamer.numbersgame.domain.GameDataHolder;import com.thalhamer.numbersgame.domain.LevelData;import com.thalhamer.numbersgame.domain.PopupResult;import com.thalhamer.numbersgame.domain.SectionUnlock;import com.thalhamer.numbersgame.domain.Stats;import com.thalhamer.numbersgame.enums.GameState;import com.thalhamer.numbersgame.enums.GameType;import com.thalhamer.numbersgame.services.mail.MailSend;import com.thalhamer.numbersgame.services.popup.LevelEndPopupService;import com.thalhamer.numbersgame.services.popup.SectionUnlockPopupService;import com.thalhamer.numbersgame.viewhelper.GameStateHolder;import javax.inject.Inject;import javax.inject.Singleton;/** * Game end service * <p/> * Created by Brian on 4/12/2015. */@Singletonpublic class LevelEndService {    @Inject    GameDataHolder gameDataHolder;    @Inject    GridService gridService;    @Inject    PowerService powerService;    @Inject    StarsAndUnlockService starsAndUnlockService;    @Inject    SectionUnlockPopupService sectionUnlockPopupService;    @Inject    LevelEndPopupService levelEndPopupService;    @Inject    MessageService messageService;    @Inject    SavedDataService savedDataService;    @Inject    SectionUnlockService sectionUnlockService;    @Inject    public LevelEndService() {    }    public void evaluateEndGame() {        GameType gameType = gameDataHolder.getLevelInfo().getGameType();        Stats stats = gameDataHolder.getLevelInfo().getStats();        if (gameType.equals(GameType.MOVES)) {            if (stats.getNumOfMovesLeft() == 0) {                startEndGameProcess();            }        } else if (gameType.equals(GameType.DROP)) {            if (gridService.topRowHasImage()) {                gameDataHolder.getBlockDropHandler().removeCallbacksAndMessages(null);                startEndGameProcess();            }        }    }    public void startEndGameProcess() {        GameStateHolder.setGameState(GameState.AFTER_GAME_DONE);        powerService.savePowers();        int numOfStarsEarned = starsAndUnlockService.evaluateAndSaveStarsEarned();        //TODO take out following line when publish app        sendScoresEmailIfThresholdReached(numOfStarsEarned);        if (starsAndUnlockService.unlockNextLevelIfLocked(gameDataHolder.getLevelData()) != null) {            SectionUnlock sectionUnlock = sectionUnlockService.checkAndPerformSectionUnlock();            Activity gameActivity = gameDataHolder.getLevelActivity();            PopupResult popupResult = new PopupResult(gameActivity, (ViewGroup) gameActivity.findViewById(R.id.level_activity));            LevelData nextLevelsData = starsAndUnlockService.getNextLevel(gameDataHolder.getLevelData());            popupResult.setNextLevelData(nextLevelsData);            if (sectionUnlock != null) {                sectionUnlockPopupService.buildPopupWindow(popupResult, sectionUnlock);            } else {                //Note: if not called here, will be called after pressing OK on Opened Level Screen                levelEndPopupService.buildPopupWindow(popupResult);            }        }    }    private void sendScoresEmailIfThresholdReached(int numOfStarsEarned) {        String level = savedDataService.constructLevelNameWithoutLevelPrefix(gameDataHolder.getLevelData());        Long score = gameDataHolder.getLevelInfo().getStats().getScore();        messageService.saveScoreResult(level, score, numOfStarsEarned);        if (messageService.reachedSendThreshold()) {            final String body = messageService.getScoreStatsEmailBody();            AsyncTask.execute(new Runnable() {                @Override                public void run() {                    try {                        MailSend sender = new MailSend("countEmUp85@gmail.com", "soccerballl");                        sender.sendMail("Score Results", body, "countEmUp85@gmail.com", "countEmUp85@gmail.com");                        messageService.updateAllToHaveBeenSent();                    } catch (Exception e) {                        Log.e("Send mail error", e.getMessage(), e);                    }                }            });        }    }}