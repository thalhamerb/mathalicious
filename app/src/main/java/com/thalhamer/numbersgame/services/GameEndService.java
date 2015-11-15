package com.thalhamer.numbersgame.services;import android.app.Activity;import android.os.AsyncTask;import android.util.Log;import android.view.ViewGroup;import com.thalhamer.numbersgame.R;import com.thalhamer.numbersgame.domain.GameDataHolder;import com.thalhamer.numbersgame.domain.GameUnlock;import com.thalhamer.numbersgame.domain.Stats;import com.thalhamer.numbersgame.enums.GameState;import com.thalhamer.numbersgame.enums.GameType;import com.thalhamer.numbersgame.services.mail.GMailSender;import com.thalhamer.numbersgame.services.popup.EndGamePopupService;import com.thalhamer.numbersgame.services.popup.SectionUnlockedPopupService;import com.thalhamer.numbersgame.viewhelper.GameStateHolder;import javax.inject.Inject;import javax.inject.Singleton;/** * Game end service * <p/> * Created by Brian on 4/12/2015. */@Singletonpublic class GameEndService {    @Inject    GameDataHolder gameDataHolder;    @Inject    GridService gridService;    @Inject    PowerService powerService;    @Inject    StarsAndUnlockService starsAndUnlockService;    @Inject    SectionUnlockedPopupService sectionUnlockedPopupService;    @Inject    EndGamePopupService endGamePopupService;    @Inject    MessageService messageService;    @Inject    SavedDataService savedDataService;    public void evaluateEndGame() {        GameType gameType = gameDataHolder.getLevelInfo().getGameType();        Stats stats = gameDataHolder.getLevelInfo().getStats();        if (gameType.equals(GameType.MOVES)) {            if (stats.getNumOfMovesLeft() == 0) {                startEndGameProcess();            }        } else if (gameType.equals(GameType.DROP)) {            if (gridService.topRowHasImage()) {                gameDataHolder.getBlockDropHandler().removeCallbacksAndMessages(null);                startEndGameProcess();            }        }    }    public void startEndGameProcess() {        GameStateHolder.setGameState(GameState.AFTER_GAME_DONE);        powerService.savePowers();        int numOfStarsEarned = starsAndUnlockService.evaluateAndSaveStarsEarned();        sendScoresEmailIfThresholdReached(numOfStarsEarned > 0);        if (starsAndUnlockService.unlockNextLevelIfLocked(gameDataHolder.getLevelData()) != null) {            GameUnlock gameUnlock = starsAndUnlockService.checkAndPerformSectionUnlock();            Activity gameActivity = gameDataHolder.getGameActivity();            if (gameUnlock != null) {                sectionUnlockedPopupService.createSectionUnlockedPopup(gameActivity, (ViewGroup) gameActivity.findViewById(R.id.main_game_activity), gameUnlock);            } else {                //Note: if not called here, will be called after pressing OK on Opened Level Screen                endGamePopupService.createEndGamePopup(gameActivity, (ViewGroup) gameActivity.findViewById(R.id.main_game_activity));            }        } else {            //TODO call beat game popup screen!!  (Don't do until done with rest of app)        }    }    private void sendScoresEmailIfThresholdReached(boolean wonLevel) {        String level = savedDataService.constructLevelNameWithoutLevelPrefix(gameDataHolder.getLevelData());        Long score = gameDataHolder.getLevelInfo().getStats().getScore();        messageService.saveScoreResult(level, score, wonLevel);        if (messageService.reachedNotYetSentThreshold()) {            final String body = messageService.getNotYetSentEmailBody();            AsyncTask.execute(new Runnable() {                @Override                public void run() {                    try {                        GMailSender sender = new GMailSender("countEmUp85@gmail.com", "soccerballl");                        sender.sendMail("Score Results", body, "countEmUp85@gmail.com", "countEmUp85@gmail.com");                        messageService.updateAllToHaveBeenSent();                    } catch (Exception e) {                        Log.e("Send mail error", e.getMessage(), e);                    }                }            });        }    }}