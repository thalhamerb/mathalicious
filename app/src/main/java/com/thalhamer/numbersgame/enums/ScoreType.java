package com.thalhamer.numbersgame.enums;import com.thalhamer.numbersgame.R;import com.thalhamer.numbersgame.viewhelper.GameConstants;/** * Score type * <p/> * Created by Brian on 7/29/2015. */public enum ScoreType implements GameExplanation {    POINTS(" for most points", "points", "Points",            "Points - get largest sequence of tiles possible in one move.  For every extra tile, your score doubles up to a max of " +                    GameConstants.MAX_NUM_OF_TILES_FOR_SCORE + " tiles!",            R.mipmap.explain_points),    CHARACTER_FACES(" to clear character faces", "faces", "Faces",            "Drop Faces - get as many character faces as you can to reach bottom of screen!",            R.mipmap.explain_faces),    TILE_SEQUENCE_LENGTH(" to get sequences of %s tiles", "sequences", "Seq of %s",            "Tile Sequences - get sequences of tiles of at least the minimum length in one move!",            R.mipmap.explain_sequences);    private String gameDescriptionEnd;    private String popupSuffix;    private String gameActivityLabel;    private String description;    private int explanationId;    private String gameExplanationTitle;    ScoreType(String gameDescriptionEnd, String popupSuffix, String gameActivityLabel, String description, int explanationId) {        this.gameDescriptionEnd = gameDescriptionEnd;        this.popupSuffix = popupSuffix;        this.gameActivityLabel = gameActivityLabel;        this.description = description;        this.explanationId = explanationId;        this.gameExplanationTitle = "Score";    }    public String getGameDescriptionEnd() {        return gameDescriptionEnd;    }    public String getPopupSuffix() {        return popupSuffix;    }    public String getGameActivityLabel() {        return gameActivityLabel;    }    @Override    public String getDescription() {        return description;    }    @Override    public int getExplanationId() {        return explanationId;    }    @Override    public String getGameExplanationTitle() {        return gameExplanationTitle;    }}