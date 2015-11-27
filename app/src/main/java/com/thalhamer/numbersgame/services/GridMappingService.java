package com.thalhamer.numbersgame.services;import android.animation.Animator;import android.animation.AnimatorListenerAdapter;import android.animation.ValueAnimator;import android.graphics.Canvas;import android.graphics.Color;import android.graphics.Paint;import android.graphics.RectF;import android.os.Handler;import android.util.Log;import android.view.animation.AccelerateInterpolator;import android.view.animation.LinearInterpolator;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.R;import com.thalhamer.numbersgame.domain.FloatingPointsDisplay;import com.thalhamer.numbersgame.domain.GameDataHolder;import com.thalhamer.numbersgame.domain.GridData;import com.thalhamer.numbersgame.domain.Image;import com.thalhamer.numbersgame.domain.LevelInfo;import com.thalhamer.numbersgame.domain.Tile;import com.thalhamer.numbersgame.enums.CalcType;import com.thalhamer.numbersgame.enums.GameState;import com.thalhamer.numbersgame.enums.GameType;import com.thalhamer.numbersgame.enums.ScoreType;import com.thalhamer.numbersgame.enums.sounds.SoundEnum;import com.thalhamer.numbersgame.viewhelper.GameConstants;import com.thalhamer.numbersgame.viewhelper.GameStateHolder;import com.thalhamer.numbersgame.viewhelper.MainGamePanel;import com.thalhamer.numbersgame.viewhelper.TouchStateHolder;import java.util.ArrayList;import java.util.List;import javax.inject.Inject;import javax.inject.Singleton;/** * performs grid mapping services * <p/> * Created by Brian on 1/25/2015. */@Singletonpublic class GridMappingService {//    public static final int BOUNCE_TIME = 100;    @Inject    GridService gridService;    @Inject    StatsService statsService;    @Inject    GameDataHolder gameDataHolder;    @Inject    GameEndService gameEndService;    @Inject    SoundService soundService;    //    private float previousDropFactor = 0;    private int numOfBlockDropsCompleted = 0;    private int numOfBlocksToDrop = 0;    private Paint gridBottomLinePaint;    public void setInitialBoard() {        initializeGridBottomLine();        initializeTiles();        initializeImages();    }    private void initializeTiles() {        MainGamePanel gamePanel = gameDataHolder.getGamePanel();        GridData gridData = gameDataHolder.getLevelInfo().getGridData();        Float panelPixelHeightForGrid = getGridHeight(gamePanel);        Float tileWidth = getRectangleWidth(gamePanel, gridData);        Float tileHeight = getRectangleHeight(gamePanel, gridData);        float currYPixel = getTopOfGrid(gamePanel) + panelPixelHeightForGrid - tileHeight;        float currXPixel = 0;        RectF currentRectF;        for (ArrayList<Tile> currentCol : gridData.getGrid()) {            for (Tile tile : currentCol) {                currentRectF = new RectF(currXPixel, currYPixel, currXPixel + tileWidth, currYPixel + tileHeight);                tile.setRectF(currentRectF);                if (CalcType.MULT_OPER.equals(gameDataHolder.getLevelInfo().getCalcType())) {                    setOperForCurrTile(tile, currentRectF, tileWidth, tileHeight, currYPixel, currXPixel);                }                currYPixel -= tileHeight;            }            currYPixel = getTopOfGrid(gamePanel) + panelPixelHeightForGrid - tileHeight;            currXPixel += tileWidth;        }    }    private void initializeImages() {        MainGamePanel gamePanel = gameDataHolder.getGamePanel();        GridData gridData = gameDataHolder.getLevelInfo().getGridData();        Float panelPixelHeightForGrid = getGridHeight(gamePanel);        Float imageWidth = getRectangleWidth(gamePanel, gridData);        Float imageHeight = getRectangleHeight(gamePanel, gridData);        float currYPixel = getTopOfGrid(gamePanel) + panelPixelHeightForGrid - imageHeight;        float currXPixel = 0;        RectF currentRectF;        for (ArrayList<Tile> currentCol : gridData.getGrid()) {            for (Tile tile : currentCol) {                if (tile.getImage() != null) {                    currentRectF = new RectF(currXPixel, currYPixel, currXPixel + imageWidth, currYPixel + imageHeight);                    tile.getImage().setRectF(currentRectF);                }                currYPixel -= imageHeight;            }            currYPixel = getTopOfGrid(gamePanel) + panelPixelHeightForGrid - imageHeight;            currXPixel += imageWidth;        }    }    public void drawGridBottomLine(Canvas canvas) {        MainGamePanel gamePanel = gameDataHolder.getGamePanel();        float gridBottom = getTopOfGrid(gamePanel) + getGridHeight(gamePanel);        canvas.drawLine(0f, gridBottom, gamePanel.getWidth(), gridBottom, gridBottomLinePaint);    }    private void initializeGridBottomLine() {        gridBottomLinePaint = new Paint();        gridBottomLinePaint.setColor(Color.parseColor("#E6E6E6"));        gridBottomLinePaint.setStrokeWidth(App.getContext().getResources().getDimensionPixelSize(R.dimen.gridBottomLineWidth));        gridBottomLinePaint.setStyle(Paint.Style.STROKE);        gameDataHolder.getLevelInfo().getGridData().setGridBottomLine(gridBottomLinePaint);    }    public Animator explosionAnimation(final List<Tile> tilesToExplodeExcitingly) {        final MainGamePanel gamePanel = gameDataHolder.getGamePanel();        final GridData gridData = gameDataHolder.getLevelInfo().getGridData();        final float halfImageWidth = getRectangleWidth(gamePanel, gridData) / 2;        final float halfImageHeight = getRectangleHeight(gamePanel, gridData) / 2;        soundService.playSound(SoundEnum.EXPLOSION);        for (Tile tile : tilesToExplodeExcitingly) {            float horizCenter = tile.getRectF().centerX();            float vertCenter = tile.getRectF().centerY();            tile.setExplosionRectF(new RectF(horizCenter, vertCenter, horizCenter, vertCenter));        }        gameDataHolder.setShowingExplosions(true);        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);        animator.setDuration(GameConstants.EXPLOSION_DURATION);        animator.setInterpolator(new AccelerateInterpolator());        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {            @Override            public void onAnimationUpdate(ValueAnimator animation) {                Float scaleFactor = (Float) animation.getAnimatedValue();                for (Tile tile : tilesToExplodeExcitingly) {                    RectF rectF = tile.getExplosionRectF();                    rectF.left = tile.getRectF().centerX() - (halfImageWidth * scaleFactor);                    rectF.right = tile.getRectF().centerX() + (halfImageWidth * scaleFactor);                    rectF.top = tile.getRectF().centerY() - (halfImageHeight * scaleFactor);                    rectF.bottom = tile.getRectF().centerY() + (halfImageHeight * scaleFactor);                }            }        });        animator.addListener(new AnimatorListenerAdapter() {            public void onAnimationEnd(Animator animation) {                gameDataHolder.setShowingExplosions(false);                for (Tile tile : tilesToExplodeExcitingly) {                    tile.setExplosionRectF(null);                }            }        });        animator.start();        return animator;    }    public Animator shrinkTouchedTiles(final List<Tile> tilesToRemove, final boolean dueToPower) {        MainGamePanel gamePanel = gameDataHolder.getGamePanel();        final GridData gridData = gameDataHolder.getLevelInfo().getGridData();        final float imageWidth = getRectangleWidth(gamePanel, gridData);        final float imageHeight = getRectangleHeight(gamePanel, gridData);        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);        animator.setDuration(GameConstants.SHRINK_DURATION);        animator.setInterpolator(new LinearInterpolator());        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {            @Override            public void onAnimationUpdate(ValueAnimator animation) {                Float scaleFactor = (Float) animation.getAnimatedValue();                for (Tile tile : tilesToRemove) {                    if (tile.getImage() != null) {                        RectF rectF = tile.getImage().getRectF();                        float newXdistFromCtr = imageWidth / 2 * scaleFactor;                        float xCenter = rectF.centerX();                        float newYdistFromCtr = imageHeight / 2 * scaleFactor;                        float yCenter = rectF.centerY();                        rectF.set(xCenter - newXdistFromCtr, yCenter - newYdistFromCtr, xCenter + newXdistFromCtr, yCenter + newYdistFromCtr);                    }                }            }        });        animator.addListener(new AnimatorListenerAdapter() {            public void onAnimationEnd(Animator animation) {                gridService.nullifyTileImages(tilesToRemove);                gridService.setImageDropDistances();                gridService.imageShift();                GameType gameType = gameDataHolder.getLevelInfo().getGameType();                if (!GameType.DROP.equals(gameType)) {                    gridService.addNewImagesToGrid(GridMappingService.this, false, dueToPower);                }                dropAllBlocks();            }        });        animator.start();        return animator;    }    public void dropAllBlocks() {        numOfBlockDropsCompleted = 0;        numOfBlocksToDrop = 0;        boolean hasBlocksToDrop = false;        final LevelInfo levelInfo = gameDataHolder.getLevelInfo();        for (ArrayList<Tile> currentCol : levelInfo.getGridData().getGrid()) {            for (Tile tile : currentCol) {                Image tileImage = tile.getImage();                if (tileImage != null && tileImage.getPlacesToDrop() != 0) {                    numOfBlocksToDrop++;                    hasBlocksToDrop = true;                    imageDropAnimation(tile.getImage(), currentCol);                }            }        }        if (!hasBlocksToDrop) {            afterAllBlocksDropped();        }    }    private Animator imageDropAnimation(final Image image, final ArrayList<Tile> currentCol) {        final MainGamePanel gamePanel = gameDataHolder.getGamePanel();        final GridData gridData = gameDataHolder.getLevelInfo().getGridData();        Integer placesToDrop = image.getPlacesToDrop();        Long timeBetweenEachImageMove = 100L;   //change this to change speed of drop        Long accelerationTime = timeBetweenEachImageMove * placesToDrop;        float distanceForOneBlock = getRectangleHeight(gamePanel, gridData);        Float finalDistanceToDrop = (distanceForOneBlock * placesToDrop);        if (image.isNewImage()) {            finalDistanceToDrop += getTopOfGrid(gamePanel);            image.setNewImage(false);        }        ValueAnimator animator = ValueAnimator.ofFloat(0, finalDistanceToDrop);        animator.setDuration(accelerationTime);        animator.setInterpolator(new AccelerateInterpolator());        ValueAnimator.setFrameDelay(24);        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {            private float originalRectFtop = image.getRectF().top;            private float originalRectFbottom = image.getRectF().bottom;            @Override            public void onAnimationUpdate(ValueAnimator animation) {                Float addedShift = (Float) animation.getAnimatedValue();                RectF rectF = image.getRectF();                rectF.set(rectF.left, originalRectFtop + addedShift, rectF.right, originalRectFbottom + addedShift);            }        });        animator.addListener(new AnimatorListenerAdapter() {            public synchronized void onAnimationEnd(Animator animation) {                numOfBlockDropsCompleted++;                if (numOfBlockDropsCompleted >= numOfBlocksToDrop) {                    afterAllBlocksDropped();                }            }        });        animator.start();        return animator;    }    private void afterAllBlocksDropped() {        GameType gameType = gameDataHolder.getLevelInfo().getGameType();        ScoreType scoreType = gameDataHolder.getLevelInfo().getScoreType();        gridService.clearDropDistances();        if (ScoreType.CHARACTER_FACES.equals(scoreType)) {            if (dropAnyCharactersAtBottomOfGrid()) {                return;  //return since starts at shrinking animation again and comes through            }        }        if (gameType != GameType.TIMED) {            gameEndService.evaluateEndGame();        }        if (GameStateHolder.getGameState().equals(GameState.GRID_LOCKED)) {            GameStateHolder.setGameState(GameState.RUNNING);        }        TouchStateHolder.setTouchState(GridData.TouchState.ENABLED);        Log.d("GridMappingService", "touch enabled");    }    /**     * drops and characters at bottom of grid     *     * @return if need to drop characters     */    private boolean dropAnyCharactersAtBottomOfGrid() {        List<Tile> bottomRowTilesWithCharFaces = gridService.getTilesWithCharactersOnBottomGridRow();        if (bottomRowTilesWithCharFaces.size() > 0) {            statsService.updateCharacterFacesCount(bottomRowTilesWithCharFaces.size());            shrinkTouchedTiles(bottomRowTilesWithCharFaces, false);            return true;        }        return false;    }    public void showFloatingPoints() {        final MainGamePanel gamePanel = gameDataHolder.getGamePanel();        final LevelInfo levelInfo = gameDataHolder.getLevelInfo();        if (levelInfo.getScoreType().equals(ScoreType.POINTS)) {            final List<Tile> touchedTiles = levelInfo.getGridData().getTouchedTiles();            Tile centerTile = touchedTiles.get((int) Math.ceil(touchedTiles.size() / 2));            final float startXPixel = centerTile.getImage().getRectF().left +                    (centerTile.getImage().getRectF().right - centerTile.getImage().getRectF().left) * 0.20f;            final float startYPixel = centerTile.getImage().getRectF().centerY();            Long scoreToAdd = statsService.getScoreToAdd(touchedTiles);            final FloatingPointsDisplay floatingPointsDisplay = new FloatingPointsDisplay(scoreToAdd, startXPixel, startYPixel);            levelInfo.getStats().setFloatingPointsDisplay(floatingPointsDisplay);            float tileHeight = getRectangleHeight(gamePanel, levelInfo.getGridData());            ValueAnimator animator = ValueAnimator.ofFloat(0, tileHeight / 4);            animator.setDuration(1000);            animator.setInterpolator(new LinearInterpolator());            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {                @Override                public void onAnimationUpdate(ValueAnimator animation) {                    Float scaleFactor = (Float) animation.getAnimatedValue();                    floatingPointsDisplay.setyPixelLoc(startYPixel - scaleFactor);                }            });            animator.addListener(new AnimatorListenerAdapter() {                public void onAnimationEnd(Animator animation) {                    levelInfo.getStats().setFloatingPointsDisplay(null);                }            });            animator.start();        }    }    public void doubleBlinkAnimation(final List<Tile> touchedTiles) {        final Handler blinkHandler = new Handler();        Runnable blinkRunnable = new Runnable() {            int count = 0;            boolean setTouched = false;            @Override            public void run() {                gridService.setTouchedAttrForTiles(touchedTiles, setTouched);                if (count < 4) {                    setTouched = !setTouched;                    count++;                    blinkHandler.postDelayed(this, 200);                } else {                    TouchStateHolder.setTouchState(GridData.TouchState.ENABLED);                }            }        };        blinkHandler.postDelayed(blinkRunnable, 300);    }    private void setOperForCurrTile(Tile tile, RectF currentRectF, Float tileWidth, Float tileHeight, float currYPixel, float currXPixel) {        float fractOfTile = 4;        float fractOfTileWidth = tileWidth / fractOfTile;        float fractOfTileHeight = tileHeight / fractOfTile;        if (tile.getLeftOperationImage() != null) {            RectF currLeftOperRectF = new RectF(currXPixel - fractOfTileWidth, currentRectF.centerY() - fractOfTileHeight,                    currXPixel + fractOfTileWidth, currentRectF.centerY() + fractOfTileHeight);            tile.getLeftOperationImage().setRectF(currLeftOperRectF);        }        if (tile.getTopOperationImage() != null) {            RectF currTopOperRectF = new RectF(currentRectF.centerX() - fractOfTileWidth, currYPixel - fractOfTileHeight,                    currentRectF.centerX() + fractOfTileWidth, currYPixel + fractOfTileHeight);            tile.getTopOperationImage().setRectF(currTopOperRectF);        }    }    public Float getRectangleWidth(MainGamePanel gamePanel, GridData gridData) {        return gamePanel.getWidth() / (float) gridData.getNumOfColumns();    }    public Float getRectangleHeight(MainGamePanel gamePanel, GridData gridData) {        return getGridHeight(gamePanel) / (float) gridData.getNumOfRows();    }    public Float getTopOfGrid(MainGamePanel gamePanel) {        return gamePanel.getHeight() * 0.07f;    }    public Float getGridHeight(MainGamePanel gamePanel) {        return gamePanel.getHeight() * 0.65f;    }    public void setNewImageRectangleForInitialDrop(Tile currTile, int newImagesCount, Image newImage) {        MainGamePanel gamePanel = gameDataHolder.getGamePanel();        GridData gridData = gameDataHolder.getLevelInfo().getGridData();        Float imageWidth = getRectangleWidth(gamePanel, gridData);        Float imageHeight = getRectangleHeight(gamePanel, gridData);        float left = currTile.getColNum() * imageWidth;        float right = left + imageWidth;        float bottom = 0 - (imageHeight * newImagesCount);        float top = bottom - imageHeight;        newImage.setRectF(new RectF(left, top, right, bottom));    }}