/** * */package com.thalhamer.numbersgame.viewhelper;import android.graphics.Canvas;import android.graphics.Color;import android.graphics.ColorFilter;import android.graphics.LightingColorFilter;import android.graphics.Paint;import android.view.SurfaceHolder;import com.thalhamer.numbersgame.domain.GameDataHolder;import com.thalhamer.numbersgame.domain.LevelInfo;import com.thalhamer.numbersgame.domain.Tile;import com.thalhamer.numbersgame.enums.CalcType;import com.thalhamer.numbersgame.services.GridMappingService;import com.thalhamer.numbersgame.services.PowerService;import com.thalhamer.numbersgame.services.SpecialTileService;import com.thalhamer.numbersgame.services.StatsService;import java.util.ArrayList;import dagger.ObjectGraph;public class MainThread extends Thread {    // Surface holder that can access the physical surface    private SurfaceHolder surfaceHolder;    // The actual view that handles inputs and draws to the surface    private MainGamePanel gamePanel;    private boolean running;    private StatsService statsService;    private GameDataHolder gameDataHolder;    private Paint lightenBitmapPaint;    private PowerService powerService;    private GridMappingService gridMappingService;    private SpecialTileService specialTileService;    public void setRunning(boolean running) {        this.running = running;    }    public MainThread(MainGamePanel gamePanel, ObjectGraph objectGraph) {        this.surfaceHolder = gamePanel.getHolder();        this.gamePanel = gamePanel;        this.statsService = objectGraph.get(StatsService.class);        this.gameDataHolder = objectGraph.get(GameDataHolder.class);        this.powerService = objectGraph.get(PowerService.class);        this.gridMappingService = objectGraph.get(GridMappingService.class);        this.specialTileService = objectGraph.get(SpecialTileService.class);        lightenBitmapPaint = new Paint();        ColorFilter filter = new LightingColorFilter(0xFFFFFFFF, 0x00666666);        lightenBitmapPaint.setColorFilter(filter);    }    @Override    public void run() {        Canvas canvas;        while (running) {            canvas = null;            // try locking the canvas for exclusive pixel editing in the surface            try {                canvas = this.surfaceHolder.lockCanvas();                if (canvas != null) {                    synchronized (surfaceHolder) {                        canvas.drawColor(Color.WHITE);//                        GridData gridData = gameDataHolder.getLevelInfo().getGridData();//                        canvas.drawRoundRect(gridData.getGridFrameRectF(), 3, 3, gridData.getGridFramePaint());                        LevelInfo levelInfo = gameDataHolder.getLevelInfo();                        if (levelInfo != null) {                            ArrayList<ArrayList<Tile>> grid = drawGrid(canvas, levelInfo);                            drawOperators(canvas, levelInfo, grid);                            //draw stats and powers                            powerService.drawPowers(canvas, lightenBitmapPaint);                            statsService.drawStats(canvas);                            statsService.drawFloatingPoints(canvas);                            specialTileService.drawSpecialTileAnimations(canvas);                        }                    }                }            } finally {                // in case of an exception the surface is not left in an inconsistent state                if (canvas != null) {                    surfaceHolder.unlockCanvasAndPost(canvas);                }                try {                    Thread.sleep(20);                } catch (InterruptedException e) {                    e.printStackTrace();                }            }        }    }    private ArrayList<ArrayList<Tile>> drawGrid(Canvas canvas, LevelInfo levelInfo) {        ArrayList<ArrayList<Tile>> grid = levelInfo.getGridData().getGrid();        //draw tile attributes        if (gameDataHolder.getLevelInfo().getGridTileData() != null) {            for (ArrayList<Tile> currentCol : grid) {                for (Tile tile : currentCol) {                    if (tile.getTileAttribute() != null) {                        canvas.drawBitmap(tile.getTileAttribute().getBitmap(), null, tile.getRectF(), null);                    }                }            }        }        //draw tile images        for (ArrayList<Tile> currentCol : grid) {            for (Tile tile : currentCol) {                if (tile.getImage() != null && tile.getImage().getRectF() != null) {                    Paint paint;                    if (tile.isTouched()) {                        paint = lightenBitmapPaint;                    } else {                        paint = null;                    }                    if (tile.getImage().getNumTile() != null) {                        canvas.drawBitmap(tile.getImage().getNumTile().bitmap, null,                                tile.getImage().getRectF(), paint);                    } else if (tile.getImage().getCharacter() != null) {                        canvas.drawBitmap(tile.getImage().getCharacter().getFaceBitmap(), null,                                tile.getImage().getRectF(), paint);                    } else if (tile.getImage().getSpecialTile() != null) {                        canvas.drawBitmap(tile.getImage().getSpecialTile().getBitmap(), null,                                tile.getImage().getRectF(), paint);                    }                }            }        }        gridMappingService.drawGridBottomLine(canvas);        return grid;    }    private void drawOperators(Canvas canvas, LevelInfo levelInfo, ArrayList<ArrayList<Tile>> grid) {        if (CalcType.MULT_OPER.equals(levelInfo.getCalcType())) {            for (ArrayList<Tile> currentCol : grid) {                for (Tile tile : currentCol) {                    if (tile.getTopOperationImage() != null) {                        canvas.drawBitmap(tile.getTopOperationImage().getOperation().getBitmap(), null,                                tile.getTopOperationImage().getRectF(), null);                    }                    if (tile.getLeftOperationImage() != null) {                        canvas.drawBitmap(tile.getLeftOperationImage().getOperation().getBitmap(), null,                                tile.getLeftOperationImage().getRectF(), null);                    }                }            }        }    }}