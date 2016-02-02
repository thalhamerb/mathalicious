package com.thalhamer.numbersgame.enums;

import com.thalhamer.numbersgame.R;

/**
 * these are anything that doesn't fit in other enums that need to go on game explanation
 *
 * Created by Brian on 12/19/2015.
 */
public enum GameExplanationExtras implements GameExplanation {

    //TODO replace image id with permenant one
    POWER_CLEAR("Multiple Tile Clear - occasionally this power will fall into grid.  Click and drag to adjacent " +
            "tile to clear all tiles of that number.", R.drawable.explain_power_clear);

    private String description;
    private int explanationId;
    private String gameExplanationTitle;

    GameExplanationExtras(String description, int explanationId) {
        this.description = description;
        this.explanationId = explanationId;
        this.gameExplanationTitle = "Specials";
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getExplanationId() {
        return explanationId;
    }

    @Override
    public String getGameExplanationTitle() {
        return gameExplanationTitle;
    }
}
