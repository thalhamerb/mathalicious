package com.thalhamer.numbersgame.enums;import com.thalhamer.numbersgame.R;import com.thalhamer.numbersgame.viewhelper.GameConstants;/** * Calculation type for game * <p/> * Created by Brian on 4/25/2015. */public enum CalcType implements GameExplanation {    ADD("Add", "Add - slide over adjacent blocks until adds up to specified value.",            R.mipmap.explain_add, R.raw.start_game_add, GameConstants.TILE_BIAS_ADD),    SUBTRACT("Subtract", "Subtract - first block is the number you start with. Slide over adjacent blocks to subtract from this.",            R.mipmap.explain_subtract, R.raw.start_game_subtract, GameConstants.TILE_BIAS_SUBTRACT),    MULTIPLY("Multiply", "Multiply - slide over adjacent blocks until multiplies to specified value.",            R.mipmap.explain_multiply, R.raw.start_game_multiply, GameConstants.TILE_BIAS_MULTIPLY);    private String calculation;    private String description;    private int explanationId;    private int startGameResourceId;    private float newImageBias;    private String gameExplanationTitle;    CalcType(String calculation, String description, int explanationId, int startGameResourceId, float newImageBias) {        this.calculation = calculation;        this.description = description;        this.explanationId = explanationId;        this.startGameResourceId = startGameResourceId;        this.newImageBias = newImageBias;        this.gameExplanationTitle = "Operation";    }    public String getCalculation() {        return calculation;    }    public int getStartGameResourceId() {        return startGameResourceId;    }    public float getNewImageBias() {        return newImageBias;    }    @Override    public String getDescription() {        return description;    }    @Override    public int getExplanationId() {        return explanationId;    }    @Override    public String getGameExplanationTitle() {        return gameExplanationTitle;    }}