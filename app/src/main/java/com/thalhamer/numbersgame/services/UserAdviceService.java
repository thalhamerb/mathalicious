package com.thalhamer.numbersgame.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

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
    public static final String RATED = "Rated_";
    public static final String DONE = "Done";

    private int numOfMovesWithoutUsingPowerTile = 0;
    private int numOfMovesWithoutLargeSequence = 0;

    @Inject
    GameDataHolder gameDataHolder;
    @Inject
    MessageService messageService;
    @Inject
    GridService gridService;
    @Inject
    SavedDataService savedDataService;

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

    public boolean shouldAskForRating(int epic, int section) {
        boolean isSectionToAsk = (epic == 1 && section == 3) || (epic == 2 && section <= 2);
        boolean alreadyAskedForSection = savedDataService.containsKey(RATED + epic + section);
        boolean hasRated = savedDataService.containsKey(RATED + DONE);
        return isSectionToAsk && !alreadyAskedForSection && !hasRated;
    }

    public void createRateModal(final Activity activity, int epic, int section) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder
                .setMessage("Enjoying the game? Einstein would like you to rate the app if you haven't already!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String url = "https://play.google.com/store/apps/details?id=com.thalhamer.numbersgame&hl=en";
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        activity.startActivity(intent);
                    }
                })
                .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNeutralButton("Already did!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        savedDataService.saveKey(RATED + DONE, 1);
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        savedDataService.saveKey(RATED + epic + section, 1);

    }

}
