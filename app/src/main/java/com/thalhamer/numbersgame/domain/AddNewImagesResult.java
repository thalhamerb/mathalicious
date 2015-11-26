package com.thalhamer.numbersgame.domain;

import com.thalhamer.numbersgame.enums.NumTile;

import java.util.List;

/**
 * Created by Brian on 11/25/2015.
 */
public class AddNewImagesResult {

    private List<Tile> currCol;
    private int firstRowWithoutImage;
    private List<NumTile> gameTilesToUse;
    private int numOfImagesToAdd;
    private boolean dueToPower;

    public List<Tile> getCurrCol() {
        return currCol;
    }

    public void setCurrCol(List<Tile> currCol) {
        this.currCol = currCol;
    }

    public int getFirstRowWithoutImage() {
        return firstRowWithoutImage;
    }

    public void setFirstRowWithoutImage(int firstRowWithoutImage) {
        this.firstRowWithoutImage = firstRowWithoutImage;
    }

    public List<NumTile> getGameTilesToUse() {
        return gameTilesToUse;
    }

    public void setGameTilesToUse(List<NumTile> gameTilesToUse) {
        this.gameTilesToUse = gameTilesToUse;
    }

    public int getNumOfImagesToAdd() {
        return numOfImagesToAdd;
    }

    public void setNumOfImagesToAdd(int numOfImagesToAdd) {
        this.numOfImagesToAdd = numOfImagesToAdd;
    }

    public boolean isDueToPower() {
        return dueToPower;
    }

    public void setDueToPower(boolean dueToPower) {
        this.dueToPower = dueToPower;
    }
}
