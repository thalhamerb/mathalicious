package com.thalhamer.numbersgame.enums;/** * Game type * <p/> * Created by Brian on 3/29/2015. */public enum GameType {    MOVES(" in %s moves.", "Moves"),    TIMED(" in %s minutes.", "Time"),    DROP(" before tiles reach top.", null);    private String gameDescriptionEnd;    private String gameActivityLabel;    GameType(String gameDescriptionEnd, String gameActivityLabel) {        this.gameDescriptionEnd = gameDescriptionEnd;        this.gameActivityLabel = gameActivityLabel;    }    public String getGameDescriptionEnd() {        return gameDescriptionEnd;    }    public String getGameActivityLabel() {        return gameActivityLabel;    }}