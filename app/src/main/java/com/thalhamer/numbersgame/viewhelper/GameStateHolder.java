package com.thalhamer.numbersgame.viewhelper;

import com.thalhamer.numbersgame.enums.GameState;

/**
 * Created by Brian on 12/23/2015.
 */
public class GameStateHolder {
    private static GameState gameState;

    public static GameState getGameState() {
        return gameState;
    }

    public static void setGameState(GameState gameStateSet) {
        gameState = gameStateSet;
    }
}
