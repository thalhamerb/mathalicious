/** * */package com.thalhamer.numbersgame.viewhelper;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.graphics.Canvas;import android.graphics.Color;import android.graphics.ColorFilter;import android.graphics.LightingColorFilter;import android.graphics.Paint;import android.support.annotation.NonNull;import android.view.SurfaceHolder;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.R;import com.thalhamer.numbersgame.domain.GameDataHolder;import com.thalhamer.numbersgame.domain.GridData;import com.thalhamer.numbersgame.domain.LevelInfo;import com.thalhamer.numbersgame.domain.Tile;import com.thalhamer.numbersgame.services.GridMappingService;import com.thalhamer.numbersgame.services.PowerService;import com.thalhamer.numbersgame.services.SpecialTileService;import com.thalhamer.numbersgame.services.StatsService;import java.util.ArrayList;import javax.inject.Inject;public class MainThread extends Thread {    @Inject    StatsService statsService;    @Inject    PowerService powerService;    @Inject    GridMappingService gridMappingService;    @Inject    SpecialTileService specialTileService;    @Inject    GameDataHolder gameDataHolder;    // Surface holder that can access the physical surface    private SurfaceHolder surfaceHolder;    // The actual view that handles inputs and draws to the surface    private MainGamePanel gamePanel;    private boolean running;    private Paint lightenBitmapPaint;    private Bitmap explosionBitmap = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.explosion);    public void setRunning(boolean running) {        this.running = running;    }    public MainThread(MainGamePanel gamePanel) {        this.surfaceHolder = gamePanel.getHolder();        this.gamePanel = gamePanel;        App.getLevelActivityComponent().injectMainThread(this);        lightenBitmapPaint = new Paint();        ColorFilter filter = new LightingColorFilter(0xFFFFFFFF, 0x00666666);        lightenBitmapPaint.setColorFilter(filter);    }    @Override    public void run() {        while (running) {            if (gameDataHolder.isGameRunning()) {                performCanvasDraw();            } else {                try {                    Thread.sleep(20);                } catch (InterruptedException e) {                    e.printStackTrace();                }            }        }    }    private void performCanvasDraw() {        Canvas canvas = null;        // try locking the canvas for exclusive pixel editing in the surface        try {            canvas = this.surfaceHolder.lockCanvas();            if (canvas != null) {                synchronized (surfaceHolder) {                    canvas.drawColor(Color.WHITE);                    LevelInfo levelInfo = gameDataHolder.getLevelInfo();                    if (levelInfo != null) {                        drawGrid(canvas, levelInfo);                        //draw stats and powers                        powerService.drawPowers(canvas, lightenBitmapPaint);                        statsService.drawStats(canvas);                        statsService.drawFloatingPoints(canvas);                        specialTileService.drawSpecialTileAnimations(canvas);                        drawExplosions(canvas, levelInfo.getGridData(), gameDataHolder.isShowingExplosions());                    }                }            }        } catch (Throwable t) {            //who cares because it will be drawn in another 20 seconds anyway        } finally {            // in case of an exception the surface is not left in an inconsistent state            if (canvas != null) {                surfaceHolder.unlockCanvasAndPost(canvas);            }            try {                Thread.sleep(20);            } catch (InterruptedException e) {                e.printStackTrace();            }        }    }    private void drawExplosions(Canvas canvas, GridData gridData, boolean showExplosions) {        if (showExplosions) {            for (ArrayList<Tile> currCol : gridData.getGrid()) {                for (Tile tile : currCol) {                    if (tile.getExplosionRectF() != null) {                        canvas.drawBitmap(explosionBitmap, null, tile.getExplosionRectF(), null);                    }                }            }        }    }    private void drawGrid(Canvas canvas, LevelInfo levelInfo) {        gridMappingService.drawGridLines(canvas);        drawTilesAndImages(canvas, levelInfo);    }    @NonNull    private void drawTilesAndImages(Canvas canvas, LevelInfo levelInfo) {        ArrayList<ArrayList<Tile>> grid = levelInfo.getGridData().getGrid();        //draw tile images        for (ArrayList<Tile> currentCol : grid) {            for (Tile tile : currentCol) {                if (tile.getImage() != null && tile.getImage().getRectF() != null) {                    Paint paint;                    if (tile.isTouched()) {                        paint = lightenBitmapPaint;                    } else {                        paint = null;                    }                    if (tile.getImage().getNumTile() != null) {                        canvas.drawBitmap(tile.getImage().getNumTile().getBitmap(), null,                                tile.getImage().getRectF(), paint);                    } else if (tile.getImage().getCharacter() != null) {                        canvas.drawBitmap(tile.getImage().getCharacter().getFaceBitmap(), null,                                tile.getImage().getRectF(), paint);                    } else if (tile.getImage().getSpecialTile() != null) {                        canvas.drawBitmap(tile.getImage().getSpecialTile().getBitmap(), null,                                tile.getImage().getRectF(), paint);                    } else if (tile.getImage().getPowerEnum() != null) {                        canvas.drawBitmap(tile.getImage().getPowerEnum().getBitmap(), null,                                tile.getImage().getRectF(), paint);                    }                }            }        }        //draw tile attributes        if (gameDataHolder.getLevelInfo().getGridTileData() != null) {            for (ArrayList<Tile> currentCol : grid) {                for (Tile tile : currentCol) {                    if (tile.getTileAttribute() != null) {                        canvas.drawBitmap(tile.getTileAttribute().getBitmap(), null, tile.getRectF(), null);                    }                }            }        }    }}