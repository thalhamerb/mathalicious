package com.thalhamer.numbersgame.services;import android.os.Handler;import android.util.Log;import android.view.MotionEvent;import com.thalhamer.numbersgame.Exception.HandlerException;import com.thalhamer.numbersgame.domain.GameDataHolder;import com.thalhamer.numbersgame.domain.GridData;import com.thalhamer.numbersgame.domain.LevelInfo;import com.thalhamer.numbersgame.domain.Tile;import com.thalhamer.numbersgame.enums.CalcType;import com.thalhamer.numbersgame.enums.GameState;import com.thalhamer.numbersgame.enums.MessageLocation;import com.thalhamer.numbersgame.enums.MessageType;import com.thalhamer.numbersgame.enums.NumTile;import com.thalhamer.numbersgame.enums.OperType;import com.thalhamer.numbersgame.enums.ScoreType;import com.thalhamer.numbersgame.enums.TileAttribute;import com.thalhamer.numbersgame.enums.sounds.SoundEnum;import com.thalhamer.numbersgame.viewhelper.GameStateHolder;import com.thalhamer.numbersgame.viewhelper.TouchStateHolder;import java.util.ArrayList;import java.util.List;import javax.inject.Inject;import javax.inject.Singleton;/** * Motion event service * <p/> * Created by Brian on 1/19/2015. */@Singletonpublic class MotionEventService {    @Inject    GridMappingService gridMappingService;    @Inject    StatsService statsService;    @Inject    GameDataHolder gameDataHolder;    @Inject    PowerService powerService;    @Inject    GridService gridService;    @Inject    InAppPurchaseService inAppPurchaseService;    @Inject    MessageService messageService;    @Inject    SoundService soundService;    private int numOfRetries = 0;    private Runnable blockDropRunnable = new Runnable() {        @Override        public void run() {            processValidInput();        }    };    public boolean handleEvent(MotionEvent event) {        if (event.getAction() == MotionEvent.ACTION_DOWN) {            actionDownEvent(event);        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {            actionMoveEvent(event);        } else if (event.getAction() == MotionEvent.ACTION_UP) {            actionUpEvent(event);        }        return true;    }    private void actionDownEvent(MotionEvent event) {        GridData gridData = gameDataHolder.getLevelInfo().getGridData();        ArrayList<ArrayList<Tile>> grid = gridData.getGrid();        gridData.setTouchedTiles(null);        gridData.setCurrentTouchedTile(null);        outerloop:        for (ArrayList<Tile> currGridCol : grid) {            for (Tile currTile : currGridCol) {                if (currTile.getImage() != null && currTile.getImage().getCharacter() == null                        && currTile.getImage().getRectF().contains(event.getX(), event.getY())) {                    gridData.setTouchedTiles(new ArrayList<Tile>());                    gridData.getTouchedTiles().add(currTile);                    gridData.setCurrentTouchedTile(currTile);                    currTile.setTouched(true);                    soundService.playTileCountBeep(gridData.getTouchedTiles());                    break outerloop;                }            }        }    }    private void actionMoveEvent(MotionEvent event) {        GridData gridData = gameDataHolder.getLevelInfo().getGridData();        if (gridData.getCurrentTouchedTile() != null) {            int colNum = gridData.getCurrentTouchedTile().getColNum();            int rowNum = gridData.getCurrentTouchedTile().getRowNum();            if (gridData.getTouchedTiles() != null) {                if (removeAdjTileFromTouchedTilesIfValid(event)) {                    return;                } else if (addAdjTileToTouchedTilesIfValid(event, colNum + 1, rowNum)) {                    return;                } else if (addAdjTileToTouchedTilesIfValid(event, colNum, rowNum + 1)) {                    return;                } else if (addAdjTileToTouchedTilesIfValid(event, colNum - 1, rowNum)) {                    return;                } else if (addAdjTileToTouchedTilesIfValid(event, colNum, rowNum - 1)) {                    return;                }            }        }    }    private boolean removeAdjTileFromTouchedTilesIfValid(MotionEvent event) {        //if person moves back, it deletes previous tile        GridData gridData = gameDataHolder.getLevelInfo().getGridData();        List<Tile> touchedTiles = gridData.getTouchedTiles();        if (touchedTiles.size() > 1 &&                touchedTiles.get(touchedTiles.size() - 2).getImage().getRectF().contains(event.getX(), event.getY())) {            Tile previousTile = touchedTiles.get(touchedTiles.size() - 2);            Tile currTouchedTile = gridData.getCurrentTouchedTile();            touchedTiles.remove(currTouchedTile);            currTouchedTile.setTouched(false);            gridData.setCurrentTouchedTile(previousTile);            soundService.playTileCountBeep(touchedTiles);            return true;        }        return false;    }    private boolean addAdjTileToTouchedTilesIfValid(MotionEvent event, int colNum, int rowNum) {        GridData gridData = gameDataHolder.getLevelInfo().getGridData();        ArrayList<ArrayList<Tile>> grid = gridData.getGrid();        if (colNum < gridData.getNumOfColumns() && colNum >= 0 && rowNum < gridData.getNumOfRows() && rowNum >= 0) {            Tile adjTile = grid.get(colNum).get(rowNum);            if (adjTile.getImage() != null && adjTile.getImage().getCharacter() == null                    && !gridData.getTouchedTiles().contains(adjTile)) {                if (adjTile.getImage().getRectF().contains(event.getX(), event.getY())) {                    gridData.getTouchedTiles().add(adjTile);                    gridData.setCurrentTouchedTile(adjTile);                    adjTile.setTouched(true);                    soundService.playTileCountBeep(gridData.getTouchedTiles());                    return true;                }            }        }        return false;    }    private synchronized void actionUpEvent(MotionEvent event) {        LevelInfo levelInfo = gameDataHolder.getLevelInfo();        GridData gridData = levelInfo.getGridData();        if (gridData.getTouchedTiles() != null) {            if (checkTilesCalculateToCorrectValue(levelInfo)) {                TouchStateHolder.setTouchState(GridData.TouchState.DISABLED);                processValidInput();            } else if (gridData.getTouchedTiles().size() == 1) {                checkPowers(gridData.getTouchedTiles().get(0));                for (Tile tile : gridData.getTouchedTiles()) {                    tile.setTouched(false);                }            } else {                TouchStateHolder.setTouchState(GridData.TouchState.DISABLED);                gridMappingService.doubleBlinkAnimation(gridData.getTouchedTiles());                showNegativeCharacterMessage(levelInfo);                soundService.playSound(SoundEnum.NEGATIVE_BEEP);            }        }        //handle power touch event        powerService.resetAndCheckActive(event, inAppPurchaseService);    }    private void checkPowers(Tile touchedTile) {        try {            powerService.performPowerIfAnyActive(touchedTile, gridMappingService);        } catch (InterruptedException e) {            e.printStackTrace();        }    }    private void processValidInput() {        try {            gameDataHolder.getLock().lock();            if (GameStateHolder.getGameState() != GameState.GRID_LOCKED) {                Log.d("MotionEventService", "touch disabled");                GameStateHolder.setGameState(GameState.GRID_LOCKED);                List<Tile> touchedTiles = gameDataHolder.getLevelInfo().getGridData().getTouchedTiles();                gridService.setTouchedAttrForTiles(touchedTiles, false);                ScoreType scoreType = gameDataHolder.getLevelInfo().getScoreType();                if (ScoreType.POINTS.equals(scoreType) || ScoreType.TILE_SEQUENCE_LENGTH.equals(scoreType)) {                    statsService.updateStats(touchedTiles);                }                showPositiveCharacterMessage(touchedTiles);                reduceTileAttribute(touchedTiles);                reduceAdjBlocks(touchedTiles);                gridMappingService.shrinkTouchedTiles(touchedTiles);                gridMappingService.showFloatingPoints();            } else {                if (numOfRetries < 25) {                    gameDataHolder.getBlockDropHandler().postDelayed(blockDropRunnable, 25);                } else {                    throw new HandlerException("MotionEventService: Runaway exception!!! :(");                }            }            gameDataHolder.getLock().unlock();        } catch (InterruptedException | HandlerException e) {            e.printStackTrace();        }    }    private void reduceAdjBlocks(List<Tile> touchedTiles) {        for (Tile tile : touchedTiles) {            getAndReduceAdjBlock(tile.getColNum() - 1, tile.getRowNum());            getAndReduceAdjBlock(tile.getColNum(), tile.getRowNum() - 1);            getAndReduceAdjBlock(tile.getColNum() + 1, tile.getRowNum());            getAndReduceAdjBlock(tile.getColNum(), tile.getRowNum() + 1);        }    }    private void getAndReduceAdjBlock(int colNum, int rowNum) {        GridData gridData = gameDataHolder.getLevelInfo().getGridData();        if (colNum >= 0 && rowNum >= 0 && colNum < gridData.getNumOfColumns() && rowNum < gridData.getNumOfRows()) {            Tile tile = gridData.getGrid().get(colNum).get(rowNum);            if (TileAttribute.blockTileAttributes().contains(tile.getTileAttribute())) {                TileAttribute newTileAttr = TileAttribute.reduceTileAttribute(tile.getTileAttribute());                tile.setTileAttribute(newTileAttr);            }        }    }    private void reduceTileAttribute(List<Tile> touchedTiles) {        for (Tile tile : touchedTiles) {            if (tile.getTileAttribute() != null) {                TileAttribute newTileAttr = TileAttribute.reduceTileAttribute(tile.getTileAttribute());                tile.setTileAttribute(newTileAttr);            }        }    }    private void showPositiveCharacterMessage(List<Tile> touchedTiles) {        //if it's not already opened        if (!gameDataHolder.isShowThoughtBubble()) {            if (touchedTiles.size() >= 5) {                //pos game phrases                String message = messageService.getRandomGameMessage(null, MessageType.POSITIVE, MessageLocation.DURING_GAME);                setMessage(message);                soundService.playSound(SoundEnum.POSITIVE_BEEP_02);            } else if (touchedTiles.size() >= 3) {                //normal game phrases                String message = messageService.getRandomGameMessage(null, MessageType.NORMAL, MessageLocation.DURING_GAME);                setMessage(message);                soundService.playSound(SoundEnum.POSITIVE_BEEP_01);            }        }    }    private void showNegativeCharacterMessage(LevelInfo levelInfo) {        if (!gameDataHolder.isShowThoughtBubble()) {            String message = String.format("That score adds up to %d.", getTilesTotal(levelInfo));            setMessage(message);        }    }    private void setMessage(String message) {        gameDataHolder.getThoughtBubbleString().clear();        gameDataHolder.getThoughtBubbleString().append(message);        gameDataHolder.setShowThoughtBubble(true);        initThoughtBubbleCloseTimer(3000);    }    private void initThoughtBubbleCloseTimer(long timeToClose) {        Runnable runnable = new Runnable() {            @Override            public void run() {                gameDataHolder.setShowThoughtBubble(false);            }        };        new Handler().postDelayed(runnable, timeToClose);    }    private int getTilesTotal(LevelInfo levelInfo) {        int total = 0;        if (CalcType.ADD.equals(levelInfo.getCalcType())) {            for (Tile tile : levelInfo.getGridData().getTouchedTiles()) {                NumTile numTile = tile.getImage().getNumTile();                if (numTile != null) {                    total += numTile.value;                }            }        } else if (CalcType.SUBTRACT.equals(levelInfo.getCalcType())) {            List<Tile> touchedTiles = levelInfo.getGridData().getTouchedTiles();            total = touchedTiles.get(0).getImage().getNumTile().value;            for (int i = 1; i < touchedTiles.size(); i++) {                NumTile numTile = touchedTiles.get(i).getImage().getNumTile();                if (numTile != null) {                    total -= numTile.value;                }            }        } else if (CalcType.MULTIPLY.equals(levelInfo.getCalcType())) {            total = 1;            for (Tile tile : levelInfo.getGridData().getTouchedTiles()) {                NumTile numTile = tile.getImage().getNumTile();                if (numTile != null) {                    total *= numTile.value;                }            }        } else if (CalcType.MULT_OPER.equals(levelInfo.getCalcType())) {            total = evaluateMultOper(levelInfo);        }        return total;    }    private boolean checkTilesCalculateToCorrectValue(LevelInfo levelInfo) {        return getTilesTotal(levelInfo) == levelInfo.getGridData().getNumToAddUpTo();    }    private Integer evaluateMultOper(LevelInfo levelInfo) {        List<Tile> touchedTiles = levelInfo.getGridData().getTouchedTiles();        Integer total = 0;        NumTile numTile = touchedTiles.get(0).getImage().getNumTile();        if (numTile != null) {            total = numTile.value;        }        for (int i = 1; i < touchedTiles.size(); i++) {            Tile currTile = touchedTiles.get(i);            Tile prevTile = touchedTiles.get(i - 1);            NumTile currNumTile = currTile.getImage().getNumTile();            if (currNumTile != null) {                int tileValue = currNumTile.value;                if (prevTile.getColNum() < currTile.getColNum()) {                    total = performCalcBasedOnOper(total, tileValue, currTile.getLeftOperationImage().getOperation());                } else if (prevTile.getColNum() > currTile.getColNum()) {                    total = performCalcBasedOnOper(total, tileValue, prevTile.getLeftOperationImage().getOperation());                } else if (prevTile.getRowNum() < currTile.getRowNum()) {                    total = performCalcBasedOnOper(total, tileValue, prevTile.getTopOperationImage().getOperation());                } else if (prevTile.getRowNum() > currTile.getRowNum()) {                    total = performCalcBasedOnOper(total, tileValue, currTile.getTopOperationImage().getOperation());                }            }        }        return total;    }    private Integer performCalcBasedOnOper(Integer total, int tileValue, OperType operType) {        if (OperType.ADD.equals(operType)) {            total += tileValue;        } else if (OperType.SUBTRACT.equals(operType)) {            total -= tileValue;        } else if (OperType.MULTIPLY.equals(operType)) {            total *= tileValue;        }        return total;    }}