package com.thalhamer.numbersgame.domain;

import java.util.List;

/**
 * GridMapping for image drop flow
 * <p/>
 * Created by Brian on 12/21/2015.
 */
public class GridMapping {

    private boolean touchTask;
    private boolean dueToPower;
    private List<Tile> tilesToRemove;

    public boolean isTouchTask() {
        return touchTask;
    }

    public void setTouchTask(boolean touchTask) {
        this.touchTask = touchTask;
    }

    public boolean isDueToPower() {
        return dueToPower;
    }

    public void setDueToPower(boolean dueToPower) {
        this.dueToPower = dueToPower;
    }

    public List<Tile> getTilesToRemove() {
        return tilesToRemove;
    }

    public void setTilesToRemove(List<Tile> tilesToRemove) {
        this.tilesToRemove = tilesToRemove;
    }
}
