package com.thalhamer.numbersgame.services;import android.app.Activity;import android.graphics.Canvas;import android.graphics.Color;import android.graphics.LinearGradient;import android.graphics.Paint;import android.graphics.RectF;import android.graphics.Shader;import android.util.Log;import android.view.MotionEvent;import android.view.ViewGroup;import com.google.common.collect.Lists;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.R;import com.thalhamer.numbersgame.domain.GameDataHolder;import com.thalhamer.numbersgame.domain.GridData;import com.thalhamer.numbersgame.domain.Power;import com.thalhamer.numbersgame.domain.Tile;import com.thalhamer.numbersgame.enums.GameState;import com.thalhamer.numbersgame.enums.PowerEnum;import com.thalhamer.numbersgame.enums.sounds.SoundEnum;import com.thalhamer.numbersgame.services.popup.IAPPopupService;import com.thalhamer.numbersgame.viewhelper.ActivityHelper;import com.thalhamer.numbersgame.viewhelper.GameStateHolder;import com.thalhamer.numbersgame.viewhelper.MainGamePanel;import com.thalhamer.numbersgame.viewhelper.TouchStateHolder;import java.util.List;import javax.inject.Inject;import javax.inject.Singleton;/** * Power service * <p/> * Created by Brian on 4/24/2015. */@Singletonpublic class PowerService {    @Inject    GridService gridService;    @Inject    GameDataHolder gameDataHolder;    @Inject    SavedDataService savedDataService;    @Inject    IAPPopupService iapPopupService;    @Inject    SoundService soundService;    private ActivityHelper activityHelper = new ActivityHelper();    private Paint powerCountPaint;    private Paint topBarPaint;    public PowerService() {        powerCountPaint = new Paint();        powerCountPaint.setTextSize(App.getContext().getResources().getDimensionPixelSize(R.dimen.powerCountFontSize));        powerCountPaint.setColor(Color.BLACK);        activityHelper.setMainGameFontToViews(powerCountPaint);        topBarPaint = new Paint();        int shaderYdistance = App.getContext().getResources().getDimensionPixelSize(R.dimen.topBarShaderDistance);        Shader shader = new LinearGradient(0, 0, 0, shaderYdistance, App.getContext().getResources().getColor(R.color.gameBackground1),                App.getContext().getResources().getColor(R.color.gameBackground2), Shader.TileMode.MIRROR);        topBarPaint.setShader(shader);    }    public void performPowerIfAnyActive(Tile tile, GridMappingService gridMappingService) throws InterruptedException {        Power activePower = getActivePower();        if (activePower != null) {            TouchStateHolder.setTouchState(GridData.TouchState.DISABLED);            switch (activePower.getPowerEnum().getPowerNum()) {                case 1:                    performBlockDeletion(tile, Lists.newArrayList(tile), gridMappingService);                    break;                case 2:                    List<Tile> tilesToRemove = gridService.getAllTilesWithNum(tile);                    performBlockDeletion(tile, tilesToRemove, gridMappingService);                    break;            }            setPowerQuantity(activePower);            tile.setTouched(false);        }    }    private void performBlockDeletion(final Tile tile, final List<Tile> tilesToRemove, final GridMappingService gridMappingService) {        try {            gameDataHolder.getLock().lock();            if (GameStateHolder.getGameState() != GameState.GRID_LOCKED) {                Log.d("InitialLevelService", "touch disabled");                GameStateHolder.setGameState(GameState.GRID_LOCKED);                tile.setTouched(false);                gridMappingService.shrinkTouchedTiles(tilesToRemove);            } else {                Runnable blockDropRunnable = new Runnable() {                    @Override                    public void run() {                        performBlockDeletion(tile, tilesToRemove, gridMappingService);                    }                };                gameDataHolder.getBlockDropHandler().postDelayed(blockDropRunnable, 25);                Log.d("MotionEventService", "delayed");            }            gameDataHolder.getLock().unlock();        } catch (InterruptedException e) {            e.printStackTrace();        }    }    private void setPowerQuantity(Power activePower) {        activePower.setQuantity(activePower.getQuantity() - 1);        savedDataService.saveKey(activePower.getPowerEnum().toString(), activePower.getQuantity());    }    public void updateStoredPowerQuantity(PowerEnum powerEnum, int numToAdd) {        if (savedDataService == null) {            savedDataService = new SavedDataService();        }        int currentPowerCount = savedDataService.getIntKeyValue(powerEnum.toString(), -1);        int valueToSave = currentPowerCount + numToAdd;        savedDataService.saveKey(powerEnum.toString(), valueToSave);        //if game is in session, must update visual quantity immediately        if (gameDataHolder != null) {            for (Power power : gameDataHolder.getLevelInfo().getPowers()) {                if (powerEnum.equals(power.getPowerEnum())) {                    power.setQuantity(power.getQuantity() + numToAdd);                }            }        }    }    public boolean willHaveMoreThanMaxPowerQuantity(PowerEnum powerEnum, int numToAdd) {        if (savedDataService == null) {            savedDataService = new SavedDataService();        }        int currentQuantity = savedDataService.getIntKeyValue(powerEnum.toString(), -1);        return (currentQuantity + numToAdd) > 99;    }    public void resetAndCheckActive(MotionEvent event, InAppPurchaseService inAppPurchaseService) {        for (Power power : gameDataHolder.getLevelInfo().getPowers()) {            if (power.getRectF().contains(event.getX(), event.getY())) {                if (power.isTouched()) {                    power.setTouched(false);                } else if (power.getQuantity() > 0) {                    power.setTouched(true);                } else {                    Activity gameActivity = gameDataHolder.getGameActivity();                    iapPopupService.createIapStorePopup(gameActivity, (ViewGroup) gameActivity.findViewById(R.id.main_game_activity), power.getPowerEnum(), inAppPurchaseService);                }                soundService.playSound(SoundEnum.CLICK1);            } else {                power.setTouched(false);            }        }    }    public Power getActivePower() {        for (Power power : gameDataHolder.getLevelInfo().getPowers()) {            if (power.isTouched()) {                return power;            }        }        return null;    }    public void setPowers() {        MainGamePanel gamePanel = gameDataHolder.getGamePanel();        RectF currentRectF;        float currXPixel = 0.01f * gamePanel.getWidth();        Float tileHeight = getTileHeight(gamePanel);        Float tileWidth = getTileWidth(gamePanel);        for (Power power : gameDataHolder.getLevelInfo().getPowers()) {            Float topOfTile = 0f;            currentRectF = new RectF(currXPixel, topOfTile, currXPixel + tileWidth, topOfTile + tileHeight);            power.setRectF(currentRectF);            currXPixel += tileWidth * 2.1;        }    }    public void drawPowers(Canvas canvas, Paint lightenPaint) {        MainGamePanel gamePanel = gameDataHolder.getGamePanel();        //top bar        canvas.drawRect(0f, 0f, gamePanel.getWidth(), getTileHeight(gamePanel), topBarPaint);        Paint paint;        for (Power power : gameDataHolder.getLevelInfo().getPowers()) {            if (power.isTouched()) {                paint = lightenPaint;            } else {                paint = null;            }            canvas.drawBitmap(power.getPowerEnum().getBitmap(), null, power.getRectF(), paint);            canvas.drawText(power.getQuantity().toString(), power.getRectF().right + (getTileWidth(gamePanel) * .1f),                    power.getRectF().centerY(), powerCountPaint);        }    }    public Float getTileWidth(MainGamePanel gamePanel) {        return getTileHeight(gamePanel);    }    public Float getTileHeight(MainGamePanel gamePanel) {        return gamePanel.getHeight() * 0.06f;    }    public List<Power> getListOfPowers() {        List<Power> powers = Lists.newArrayList();        for (PowerEnum powerEnumBitmapEnum : PowerEnum.values()) {            int powerQuantity = savedDataService.getIntKeyValue(powerEnumBitmapEnum.toString(), 0);            Power power = new Power(powerEnumBitmapEnum, powerQuantity);            powers.add(power);        }        return powers;    }    public void savePowers() {        for (Power power : gameDataHolder.getLevelInfo().getPowers()) {            savedDataService.saveKey(power.getPowerEnum().toString(), power.getQuantity());        }    }}