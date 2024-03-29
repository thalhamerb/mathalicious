package com.thalhamer.numbersgame.enums;

import com.thalhamer.numbersgame.R;

/**
 * these are anything that doesn't fit in other enums that need to go on game explanation
 *
 * Created by Brian on 12/19/2015.
 */
public enum LevelExplanationExtras implements LevelExplanation {

    POWER_CLEAR("Multiple Tile Clear - click and drag to an adjacent tile to clear all tiles of that number.  " +
            "Hint: the more tiles you clear, the more that fall into board.", R.drawable.explain_power);

    private String description;
    private int explanationId;
    private String gameExplanationTitle;

    LevelExplanationExtras(String description, int explanationId) {
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
