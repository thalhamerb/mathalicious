package com.thalhamer.numbersgame.services;

import com.thalhamer.numbersgame.domain.GameDataHolder;
import com.thalhamer.numbersgame.domain.Tile;
import com.thalhamer.numbersgame.enums.GameType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * user advice service
 */
@Singleton
public class UserAdviceService {

    private static final int MAX_MOVES_WITHOUT_USING_TILES = 6;
    private static final int MAX_MOVES_WITHOUT_LARGE_SEQUENCE = 4;
    private static final int MIN_LARGE_SEQUENCE_QUANTITY = 5;

    private int numOfMovesWithoutUsingPowerTile = 0;
    private int numOfMovesWithoutLargeSequence = 0;

    @Inject
    GameDataHolder gameDataHolder;
    @Inject
    MessageService messageService;

    @Inject
    GridService gridService;

    @Inject
    UserAdviceService() {
    }

    public void giveAdvice(List<Tile> tiles) {
        if (GameType.MOVES.equals(gameDataHolder.getLevelInfo().getGameType()) && isBeginner()) {
            updateAdvice(tiles);
            giveAdvice();
        }
    }

    private boolean isBeginner() {
        return gameDataHolder.getLevelData().getEpic() == 1 && gameDataHolder.getLevelData().getSection() <= 2;
    }

    private void updateAdvice(List<Tile> tiles) {
        if (gridService.gridHasPowerEnum()) {
            numOfMovesWithoutUsingPowerTile++;
        } else {
            numOfMovesWithoutUsingPowerTile = 0;
        }

        if (tiles.size() < MIN_LARGE_SEQUENCE_QUANTITY) {
            numOfMovesWithoutLargeSequence++;
        } else {
            numOfMovesWithoutLargeSequence = 0;
        }
    }

    private void giveAdvice() {
        if (numOfMovesWithoutUsingPowerTile >= MAX_MOVES_WITHOUT_USING_TILES) {
            if (messageService.initCharacterMessage("You haven't used the power tile. Click info button for help.")) {
                numOfMovesWithoutUsingPowerTile = 0;
            }

        } else if (numOfMovesWithoutLargeSequence >= MAX_MOVES_WITHOUT_LARGE_SEQUENCE) {
            if (messageService.initCharacterMessage("Use more tiles per move for higher scores.")) {
                numOfMovesWithoutLargeSequence = 0;
            }
        }
    }

    public void resetNumOfMovesWithoutUsingPowerTile() {
        numOfMovesWithoutUsingPowerTile = 0;
    }


}
