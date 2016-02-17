package com.thalhamer.numbersgame.services;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.graphics.Canvas;import android.graphics.Color;import android.graphics.LinearGradient;import android.graphics.Matrix;import android.graphics.Paint;import android.graphics.RectF;import android.graphics.Shader;import android.graphics.Typeface;import android.text.DynamicLayout;import android.text.Layout;import android.text.TextPaint;import android.view.View;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.R;import com.thalhamer.numbersgame.domain.FloatingPointsDisplay;import com.thalhamer.numbersgame.domain.GameActivityDataBox;import com.thalhamer.numbersgame.domain.GameDataHolder;import com.thalhamer.numbersgame.domain.LevelInfo;import com.thalhamer.numbersgame.domain.StarMeter;import com.thalhamer.numbersgame.domain.StarsInfo;import com.thalhamer.numbersgame.domain.Stats;import com.thalhamer.numbersgame.domain.Tile;import com.thalhamer.numbersgame.enums.Character;import com.thalhamer.numbersgame.enums.GameType;import com.thalhamer.numbersgame.enums.OtherGameImages;import com.thalhamer.numbersgame.enums.ScoreType;import com.thalhamer.numbersgame.viewhelper.ActivityHelper;import com.thalhamer.numbersgame.viewhelper.GameConstants;import java.util.List;import java.util.concurrent.TimeUnit;import javax.inject.Inject;import javax.inject.Singleton;/** * stats related service * <p/> * Created by Brian on 2/2/2015. */@Singletonpublic class StatsService {    private Paint floatingPointPaint;    private Paint floatingPntBorderPaint;    private Paint titlePaint;    private StarMeter starMeter;    private GameActivityDataBox scoreBox;    private GameActivityDataBox gameLimitBox;    private Matrix characterMatrix;    private Paint characterPaint;    private Bitmap charBitmap;    private boolean gameInitialized = false;    private ActivityHelper activityHelper = new ActivityHelper();    private Bitmap thoughtBubble = null;    private RectF thoughtBubbleRectF = null;    private float thoughtWidth = 0;    private DynamicLayout thoughtDynamicLayout = null;    /////CONSTANTS FOR STATS/////    //horizontal    public static final float STATS_BOX_LEFT = 0.01f;    public static final float STATS_BOX_RIGHT = 0.50f;    public static final float SCORE_BOX_PADDING = 0.01f;    public static final float CHARACTER_LEFT = 0.60f;    public static final float THOUGHT_LEFT = 0.05f;    public static final float THOUGHT_RIGHT = 0.60f;    public static final float THOUGHT_PADDING_HORIZ = 0.02f;    //302 x 200    //vertical    public static final float SCORE_BOX_TOP = 0.83f;    public static final float SCORE_BOX_BOTTOM = 0.93f;    public static final float STAR_METER_TOP = 0.94f;    public static final float STAR_METER_BOTTOM = 0.98f;    public static final float CHARACTER_TOP = 0.75f;    public static final float CHARACTER_BOTTOM = .99f;    public static final float THOUGHT_TOP = 0.73f;    public static final float THOUGHT_BOTTOM = 0.83f;    public static final float THOUGHT_PADDING_VERT = 0.007f;    /////END OF CONSTANTS FOR STATS/////    @Inject    GameDataHolder gameDataHolder;    @Inject    public StatsService() {        Typeface tf = Typeface.createFromAsset(App.getContext().getAssets(), "fonts/bubblegum.ttf");        floatingPointPaint = new Paint();        floatingPointPaint.setTextSize(App.getContext().getResources().getDimensionPixelSize(R.dimen.floatingPointFontSize));        floatingPointPaint.setColor(App.getContext().getResources().getColor(R.color.gameBackground1));        floatingPointPaint.setTypeface(tf);        floatingPointPaint.setShadowLayer(0.7f, -6, 6, Color.BLACK);        floatingPntBorderPaint = new Paint();        floatingPntBorderPaint.setTextSize(App.getContext().getResources().getDimensionPixelSize(R.dimen.floatingPointFontSize));        floatingPntBorderPaint.setColor(Color.parseColor("#A5DDF0"));        floatingPntBorderPaint.setTypeface(tf);        floatingPntBorderPaint.setStyle(Paint.Style.STROKE);        floatingPntBorderPaint.setStrokeWidth(4);        titlePaint = new Paint();        titlePaint.setTextSize(App.getContext().getResources().getDimensionPixelSize(R.dimen.gameTitleFontSize));        titlePaint.setTextAlign(Paint.Align.CENTER);        activityHelper.setMainGameFontToViews(titlePaint);    }    private void initializeStarMeter() {        starMeter = new StarMeter();        int statsBorderWidth = App.getContext().getResources().getDimensionPixelSize(R.dimen.statsBorderWidth);        Paint framePaint = new Paint();        framePaint.setColor(Color.BLUE);        framePaint.setStrokeWidth(statsBorderWidth);        framePaint.setStyle(Paint.Style.STROKE);        starMeter.setFramePaint(framePaint);        Paint backgroundPaint = new Paint();        backgroundPaint.setStyle(Paint.Style.FILL);        int starMeterShaderDistance = App.getContext().getResources().getDimensionPixelSize(R.dimen.starMeterShaderDistance);        Shader shader = new LinearGradient(0, 0, 0, starMeterShaderDistance,                App.getContext().getResources().getColor(R.color.gameBackground1),                App.getContext().getResources().getColor(R.color.gameBackground2), Shader.TileMode.MIRROR);        backgroundPaint.setShader(shader);        starMeter.setBackgroundPaint(backgroundPaint);        float frameLeft = getHorizPixelLocation(STATS_BOX_LEFT);        float frameRight = getHorizPixelLocation(STATS_BOX_RIGHT);        float frameTop = getVertPixelLocation(STAR_METER_TOP);        float frameBottom = getVertPixelLocation(STAR_METER_BOTTOM);        RectF frameRectF = new RectF(frameLeft, frameTop, frameRight, frameBottom);        starMeter.setFrameRectF(frameRectF);        float barLeft = frameLeft + statsBorderWidth / 2;        float barTop = frameTop + statsBorderWidth / 2;        float barRight = frameRight - statsBorderWidth / 2;        float barBottom = frameBottom - statsBorderWidth / 2;        RectF fillRectF = new RectF(barLeft, barTop, barRight, barBottom);        starMeter.setFillRectF(fillRectF);        StarsInfo starsInfo = gameDataHolder.getLevelInfo().getStats().getStarsInfo();        starMeter.setPixelPerScore((barRight - barLeft) / starsInfo.getMinForThreeStars());        Paint fillPaint = new Paint();        Shader fillShader = new LinearGradient(0, 0, 0, starMeterShaderDistance, Color.parseColor("#33CC33"), Color.parseColor("#1F7A1F"), Shader.TileMode.MIRROR);        fillPaint.setShader(fillShader);        starMeter.setFillPaint(fillPaint);        Paint linePaint = new Paint();        linePaint.setColor(Color.BLUE);        linePaint.setStrokeWidth(statsBorderWidth / 2);        starMeter.setLinePaint(linePaint);    }    private void initializeDataFields() {        scoreBox = new GameActivityDataBox();        //initialize Score        float frameLeft = getHorizPixelLocation(STATS_BOX_LEFT);        float frameRight = getHorizPixelLocation(STATS_BOX_LEFT) + ((getHorizPixelLocation(STATS_BOX_RIGHT) - getHorizPixelLocation(STATS_BOX_LEFT)) / 2) - getHorizPixelLocation(SCORE_BOX_PADDING);        float frameTop = getVertPixelLocation(SCORE_BOX_TOP);        float frameBottom = getVertPixelLocation(SCORE_BOX_BOTTOM);        RectF scoreRectF = new RectF(frameLeft, frameTop, frameRight, frameBottom);        scoreBox.setBoxRectF(scoreRectF);        Paint paintFill = new Paint();        paintFill.setStyle(Paint.Style.FILL);        Shader shader = new LinearGradient(0, 0, 0, 50, App.getContext().getResources().getColor(R.color.gameBackground1),                App.getContext().getResources().getColor(R.color.gameBackground2), Shader.TileMode.MIRROR);        paintFill.setShader(shader);        scoreBox.setBoxPaintFill(paintFill);        Paint paintBorder = new Paint();        paintBorder.setStyle(Paint.Style.STROKE);        paintBorder.setColor(Color.BLUE);        paintBorder.setStrokeWidth(App.getContext().getResources().getDimensionPixelSize(R.dimen.statsBorderWidth));        scoreBox.setBoxPaintBorder(paintBorder);        Paint labelPaint = new Paint();        labelPaint.setTextSize(App.getContext().getResources().getDimensionPixelSize(R.dimen.statsLabelFontSize));        labelPaint.setTextAlign(Paint.Align.CENTER);        scoreBox.setLabelPaint(labelPaint);        activityHelper.setMainGameFontToViews(labelPaint);        float labelLeft = frameLeft + (frameRight - frameLeft) / 2;        scoreBox.setLabelLeft(labelLeft);        float labelTop = frameTop + (frameBottom - frameTop) * .35f;        scoreBox.setLabelTop(labelTop);        scoreBox.setValueLeft(labelLeft);        float valueTop = frameTop + (frameBottom - frameTop) * .80f;        scoreBox.setValueTop(valueTop);        Paint valuePaint = new Paint();        valuePaint.setTextSize(App.getContext().getResources().getDimensionPixelSize(R.dimen.statsNumberFontSize));        valuePaint.setTextAlign(Paint.Align.CENTER);        scoreBox.setValuePaint(valuePaint);        activityHelper.setMainGameFontToViews(valuePaint);        ScoreType scoreType = gameDataHolder.getLevelInfo().getScoreType();        String scoreBoxText;        if (scoreType.equals(ScoreType.TILE_SEQUENCE_LENGTH)) {            scoreBoxText = String.format(scoreType.getGameActivityLabel(), gameDataHolder.getLevelInfo().getScoreTypeValue());        } else {            scoreBoxText = scoreType.getGameActivityLabel();        }        scoreBox.setLabelText(scoreBoxText);        //initialize game limit        gameLimitBox = new GameActivityDataBox();        frameRight = getHorizPixelLocation(STATS_BOX_RIGHT);        frameLeft = getHorizPixelLocation(STATS_BOX_RIGHT) - ((getHorizPixelLocation(STATS_BOX_RIGHT) - getHorizPixelLocation(STATS_BOX_LEFT)) / 2) + getHorizPixelLocation(SCORE_BOX_PADDING);        gameLimitBox.setBoxRectF(new RectF(frameLeft, frameTop, frameRight, frameBottom));        gameLimitBox.setBoxPaintFill(paintFill);        gameLimitBox.setBoxPaintBorder(paintBorder);        gameLimitBox.setLabelPaint(labelPaint);        gameLimitBox.setValuePaint(valuePaint);        labelLeft = frameLeft + (frameRight - frameLeft) / 2;        gameLimitBox.setLabelLeft(labelLeft);        gameLimitBox.setLabelTop(labelTop);        gameLimitBox.setValueLeft(labelLeft);        gameLimitBox.setValueTop(valueTop);        float characterLeft = getHorizPixelLocation(CHARACTER_LEFT);        float characterTop = getVertPixelLocation(CHARACTER_TOP);        float characterBottom = getVertPixelLocation(CHARACTER_BOTTOM);        charBitmap = Character.getCharacterFromEpic(gameDataHolder.getLevelData().getEpic()).fullBodyBitmap();        float originalHeight = charBitmap.getHeight();        float scale = (characterBottom - characterTop) / originalHeight;        characterMatrix = new Matrix();        characterMatrix.postTranslate(characterLeft, characterTop);        characterMatrix.preScale(scale, scale);        characterPaint = new Paint();        characterPaint.setFilterBitmap(true);        thoughtBubble = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.thought_bubble_main_game);        float thoughtLeft = getHorizPixelLocation(THOUGHT_LEFT);        float thoughtRight = getHorizPixelLocation(THOUGHT_RIGHT);        float thoughtTop = getVertPixelLocation(THOUGHT_TOP);        float thoughtBottom = getVertPixelLocation(THOUGHT_BOTTOM);        thoughtBubbleRectF = new RectF(thoughtLeft, thoughtTop, thoughtRight, thoughtBottom);        TextPaint textPaint = new TextPaint();        textPaint.setTextSize(App.getContext().getResources().getDimensionPixelSize(R.dimen.statsLabelFontSize));        activityHelper.setMainGameFontToViews(textPaint);        thoughtWidth = getHorizPixelLocation(THOUGHT_RIGHT) - getHorizPixelLocation(THOUGHT_LEFT);        thoughtDynamicLayout = new DynamicLayout(gameDataHolder.getThoughtBubbleString(), textPaint,                (int) (thoughtWidth - getHorizPixelLocation(THOUGHT_PADDING_HORIZ)), Layout.Alignment.ALIGN_CENTER, 1, 1, false);    }    public void updateStats(List<Tile> tileList) {        LevelInfo levelInfo = gameDataHolder.getLevelInfo();        Stats stats = levelInfo.getStats();        if (ScoreType.POINTS.equals(levelInfo.getScoreType())) {            long scoreToAdd = getScoreToAdd(tileList);            stats.setScore(stats.getScore() + scoreToAdd);        } else if (ScoreType.TILE_SEQUENCE_LENGTH.equals(levelInfo.getScoreType())) {            if (tileList.size() >= levelInfo.getScoreTypeValue()) {                stats.setScore(stats.getScore() + 1);            }        }        if (levelInfo.getGameType().equals(GameType.MOVES)) {            stats.setNumOfMovesLeft(stats.getNumOfMovesLeft() - 1);        }    }    public void updateCharacterFacesCount(int numOfFacesToAdd) {        LevelInfo levelInfo = gameDataHolder.getLevelInfo();        Stats stats = levelInfo.getStats();        stats.setScore(stats.getScore() + numOfFacesToAdd);    }    public Long getScoreToAdd(List<Tile> tileList) {        int numOfTiles = tileList.size();        if (numOfTiles > GameConstants.MAX_NUM_OF_TILES_FOR_SCORE) {            numOfTiles = GameConstants.MAX_NUM_OF_TILES_FOR_SCORE;        }        return ((long) Math.pow(2, numOfTiles - 1)) * 5;    }    public void drawStats(Canvas canvas) {        //must always draw the header first for initialization purposes        drawHeader(canvas);        drawScore(canvas);        drawGameLimit(canvas);        drawStarMeter(canvas);        drawCharacter(canvas);        drawCharacterThoughtBubble(canvas);    }    private void drawCharacter(Canvas canvas) {        canvas.drawBitmap(charBitmap, characterMatrix, characterPaint);    }    private void drawCharacterThoughtBubble(Canvas canvas) {        if (gameDataHolder.isShowThoughtBubble()) {            canvas.drawBitmap(thoughtBubble, null, thoughtBubbleRectF, null);            canvas.save();            canvas.translate(getHorizPixelLocation(THOUGHT_LEFT), getVertPixelLocation(THOUGHT_TOP) + getVertPixelLocation(THOUGHT_PADDING_VERT));            thoughtDynamicLayout.draw(canvas);            canvas.restore();        }    }    private void drawHeader(Canvas canvas) {        //TODO better way to initialize        if (!gameInitialized) {            initializeDataFields();            initializeStarMeter();            gameInitialized = true;        }        LevelInfo levelInfo = gameDataHolder.getLevelInfo();        View gamePanel = gameDataHolder.getGamePanel();        canvas.drawText(levelInfo.getCalcType().toString() + " to " + levelInfo.getGridData().getNumToAddUpTo(),                gamePanel.getWidth() * 0.7f, gamePanel.getHeight() * 0.04f, titlePaint);    }    private void drawScore(Canvas canvas) {        Stats stats = gameDataHolder.getLevelInfo().getStats();        canvas.drawRoundRect(scoreBox.getBoxRectF(), 25, 25, scoreBox.getBoxPaintFill());        canvas.drawRoundRect(scoreBox.getBoxRectF(), 25, 25, scoreBox.getBoxPaintBorder());        canvas.drawText(scoreBox.getLabelText(), scoreBox.getLabelLeft(), scoreBox.getLabelTop(),                scoreBox.getLabelPaint());        String scoreToDisplay = stats.getScore().toString();        canvas.drawText(scoreToDisplay, scoreBox.getValueLeft(), scoreBox.getValueTop(),                scoreBox.getValuePaint());    }    private void drawGameLimit(Canvas canvas) {        LevelInfo levelInfo = gameDataHolder.getLevelInfo();        GameType gameType = levelInfo.getGameType();        if (!GameType.DROP.equals(gameType)) {            canvas.drawRoundRect(gameLimitBox.getBoxRectF(), 25, 25, gameLimitBox.getBoxPaintFill());            canvas.drawRoundRect(gameLimitBox.getBoxRectF(), 25, 25, gameLimitBox.getBoxPaintBorder());            canvas.drawText(gameType.getGameActivityLabel(), gameLimitBox.getLabelLeft(), gameLimitBox.getLabelTop(),                    gameLimitBox.getLabelPaint());            String gameLimitToDisplay = "";            if (GameType.MOVES.equals(gameType)) {                gameLimitToDisplay = levelInfo.getStats().getNumOfMovesLeft().toString();            } else if (GameType.TIMED.equals(gameType)) {                gameLimitToDisplay = getTimeInMinutesFormat(levelInfo.getStats().getNumOfMillisLeft());            }            canvas.drawText(gameLimitToDisplay, gameLimitBox.getValueLeft(), gameLimitBox.getValueTop(),                    gameLimitBox.getValuePaint());        }    }    private void drawStarMeter(Canvas canvas) {        canvas.drawRoundRect(starMeter.getFrameRectF(), 5, 5, starMeter.getBackgroundPaint());        canvas.drawRoundRect(starMeter.getFrameRectF(), 5, 5, starMeter.getFramePaint());        Stats stats = gameDataHolder.getLevelInfo().getStats();        Long scoreToUse = stats.getScore() <= stats.getStarsInfo().getMinForThreeStars() ?                stats.getScore() : stats.getStarsInfo().getMinForThreeStars();        RectF fillRectF = starMeter.getFillRectF();        fillRectF.right = getHorizMeterPosBasedOnScore(fillRectF.left, starMeter.getPixelPerScore(), scoreToUse);        canvas.drawRect(fillRectF, starMeter.getFillPaint());        StarsInfo starsInfo = stats.getStarsInfo();        drawStarLocationsOnMeter(canvas, starsInfo.getMinForOneStar());        drawStarLocationsOnMeter(canvas, starsInfo.getMinForTwoStars());        drawStarLocationsOnMeter(canvas, starsInfo.getMinForThreeStars());    }    private void drawStarLocationsOnMeter(Canvas canvas, Long score) {        RectF frameRectF = starMeter.getFrameRectF();        float xPosition = getHorizMeterPosBasedOnScore(starMeter.getFillRectF().left, starMeter.getPixelPerScore(), score);        canvas.drawLine(xPosition, frameRectF.top, xPosition, frameRectF.bottom, starMeter.getLinePaint());        float starLength = gameDataHolder.getGamePanel().getWidth() * 0.05f;        RectF starRectF = new RectF(xPosition - (starLength / 2), frameRectF.bottom - (starLength / 2),                xPosition + (starLength / 2), frameRectF.bottom + (starLength / 2));        canvas.drawBitmap(OtherGameImages.STAR.getBitmap(), null, starRectF, null);    }    private float getHorizMeterPosBasedOnScore(float barLeft, float pixelPerScore, long score) {        return barLeft + (pixelPerScore * score);    }    public String getTimeInMinutesFormat(Long millisUntilFinished) {        Long timeInMinutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);        Long timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);        return String.format("%d:%02d", timeInMinutes, timeInSeconds - TimeUnit.MINUTES.toSeconds(timeInMinutes));    }    public void drawFloatingPoints(Canvas canvas) {        FloatingPointsDisplay floatingPointsDisplay = gameDataHolder.getLevelInfo().getStats().getFloatingPointsDisplay();        if (floatingPointsDisplay != null) {            canvas.drawText(floatingPointsDisplay.getPoints().toString(), floatingPointsDisplay.getxPixelLoc(),                    floatingPointsDisplay.getyPixelLoc(), floatingPointPaint);//            canvas.drawText(floatingPointsDisplay.getPoints().toString(), floatingPointsDisplay.getxPixelLoc(),//                    floatingPointsDisplay.getyPixelLoc(), floatingPntBorderPaint);        }    }    private float getHorizPixelLocation(float percentage) {        return gameDataHolder.getGamePanel().getWidth() * percentage;    }    private float getVertPixelLocation(float percentage) {        return gameDataHolder.getGamePanel().getHeight() * percentage;    }}