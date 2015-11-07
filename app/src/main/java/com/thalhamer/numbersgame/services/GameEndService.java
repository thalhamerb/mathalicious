package com.thalhamer.numbersgame.services;import android.app.Activity;import android.view.ViewGroup;import com.thalhamer.numbersgame.R;import com.thalhamer.numbersgame.domain.GameDataHolder;import com.thalhamer.numbersgame.domain.GameUnlock;import com.thalhamer.numbersgame.domain.Stats;import com.thalhamer.numbersgame.enums.GameState;import com.thalhamer.numbersgame.enums.GameType;import com.thalhamer.numbersgame.viewhelper.GameStateHolder;import javax.inject.Inject;import javax.inject.Singleton;/** * Game end service * <p/> * Created by Brian on 4/12/2015. */@Singletonpublic class GameEndService {    @Inject    GameDataHolder gameDataHolder;    @Inject    GridService gridService;    @Inject    PowerService powerService;    @Inject    StarsAndUnlockService starsAndUnlockService;    @Inject    PopupService popupService;    public void evaluateEndGame() {        GameType gameType = gameDataHolder.getLevelInfo().getGameType();        Stats stats = gameDataHolder.getLevelInfo().getStats();        if (gameType.equals(GameType.MOVES)) {            if (stats.getNumOfMovesLeft() == 0) {                startEndGameProcess();            }        } else if (gameType.equals(GameType.DROP)) {            if (gridService.topRowHasImage()) {                gameDataHolder.getBlockDropHandler().removeCallbacksAndMessages(null);                startEndGameProcess();            }        }    }    public void startEndGameProcess() {        GameStateHolder.setGameState(GameState.AFTER_GAME_DONE);        powerService.savePowers();        starsAndUnlockService.evaluateAndSaveStarsEarned();        if (starsAndUnlockService.unlockNextLevelIfLocked(gameDataHolder.getLevelData()) != null) {            GameUnlock gameUnlock = starsAndUnlockService.checkAndPerformSectionUnlock();            Activity gameActivity = gameDataHolder.getGameActivity();            if (gameUnlock != null) {                popupService.createSectionUnlockedPopup(gameActivity, (ViewGroup) gameActivity.findViewById(R.id.main_game_activity), gameUnlock);            } else {                //Note: if not called here, will be called after pressing OK on Opened Level Screen                popupService.createEndGamePopup(gameActivity, (ViewGroup) gameActivity.findViewById(R.id.main_game_activity));            }        } else {            //TODO call beat game popup screen!!  (Don't do until done with rest of app)        }    }}