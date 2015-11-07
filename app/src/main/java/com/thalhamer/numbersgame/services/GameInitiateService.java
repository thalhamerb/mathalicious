package com.thalhamer.numbersgame.services;import android.util.Log;import com.google.common.collect.Lists;import com.thalhamer.numbersgame.domain.DropBlockData;import com.thalhamer.numbersgame.domain.GameDataHolder;import com.thalhamer.numbersgame.domain.GridData;import com.thalhamer.numbersgame.domain.LevelInfo;import com.thalhamer.numbersgame.domain.StarsInfo;import com.thalhamer.numbersgame.domain.Stats;import com.thalhamer.numbersgame.enums.CalcType;import com.thalhamer.numbersgame.enums.GameType;import com.thalhamer.numbersgame.enums.NumTile;import com.thalhamer.numbersgame.enums.OperType;import com.thalhamer.numbersgame.enums.ScoreType;import org.json.JSONArray;import org.json.JSONException;import org.json.JSONObject;import java.io.IOException;import java.util.List;import javax.inject.Inject;import javax.inject.Singleton;/** * Game initiate service * <p/> * Created by Brian on 4/12/2015. */@Singletonpublic class GameInitiateService {    @Inject    GameDataHolder gameDataHolder;    @Inject    GridService gridService;    @Inject    PowerService powerService;    @Inject    JsonService jsonService;    @Inject    StarsAndUnlockService starsAndUnlockService;    @Inject    SavedDataService savedDataService;    @Inject    GridTileDataService gridTileDataService;    public void initiateLevelData(String sectionFileName, int level) throws IOException {        try {            String levelJsonInfo = jsonService.getJsonDataFromResource(sectionFileName, "raw");            createLevelData(levelJsonInfo, level);        } catch (JSONException e) {            Log.d("InitiateLevelService", "json creating failed");            e.printStackTrace();        }    }    public void startBoard() {        LevelInfo levelInfo = gameDataHolder.getLevelInfo();        GridData gridData = levelInfo.getGridData();        if (GameType.DROP.equals(levelInfo.getGameType())) {            gridService.initialGridPopulate(levelInfo.getDropBlockData().getNumOfRowsToPopulate());        } else {            gridService.initialGridPopulate(gridData.getNumOfRows());        }        levelInfo.setPowers(powerService.getListOfPowers());    }    private void createLevelData(String sectionJsonInfo, int level) throws JSONException {        JSONArray jsonArray = new JSONArray(sectionJsonInfo);        JSONObject object = jsonArray.getJSONObject(level - 1);        Stats stats = new Stats();        String gameTypeAsString = object.getString("gameType");        if (gameTypeAsString.equals(GameType.MOVES.toString())) {            stats.setNumOfMovesLeft(object.getInt("numberOfMoves"));        } else if (gameTypeAsString.equals(GameType.TIMED.toString())) {            stats.setNumOfMillisLeft(object.getLong("numberOfSeconds") * 1000);        }        Integer[][] gridTileData = gridTileDataService.getGridTileData(object);        Integer numOfRows;        Integer numOfCols;        if (gridTileData != null) {            numOfRows = gridTileData.length;            numOfCols = gridTileData[0].length;        } else {            numOfRows = object.getInt("numOfRows");            numOfCols = object.getInt("numOfCols");        }        GridData gridData = new GridData(numOfRows, numOfCols, object.getInt("numToEquateTo"));        LevelInfo levelInfo = new LevelInfo(GameType.valueOf(gameTypeAsString), gridData,                CalcType.valueOf(object.getString("calcTypeEnum")), stats, ScoreType.valueOf(object.getString("scoreType")));        if (gridTileData != null) {            levelInfo.setGridTileData(gridTileData);        }        if (object.has("scoreTypeValue")) {            levelInfo.setScoreTypeValue(object.getLong("scoreTypeValue"));        }        gameDataHolder.setLevelInfo(levelInfo);        if (GameType.DROP.equals(levelInfo.getGameType())) {            JSONObject dropBlockJsonData = object.getJSONObject("dropBlockData");            DropBlockData dropBlockData;            if (dropBlockJsonData.has("periodBetweenFreqChanges")) {                dropBlockData = new DropBlockData(dropBlockJsonData.getInt("periodBetweenFreqChanges"),                        dropBlockJsonData.getInt("initialPeriod"), dropBlockJsonData.getDouble("periodChangeFactor"),                        dropBlockJsonData.getInt("numOfRowToPopulate"));            } else {                dropBlockData = new DropBlockData(dropBlockJsonData.getInt("numOfRowToPopulate"));            }            levelInfo.setDropBlockData(dropBlockData);        }        resolveNumTileList(object, levelInfo);        if (object.has("allowedOperations")) {            String opers = object.getString("allowedOperations");            String[] operArray = opers.split(",");            List<OperType> opersList = Lists.newArrayList();            for (String oper : operArray) {                opersList.add(OperType.valueOf(oper));            }            levelInfo.setAllowedOperations(opersList);        }        String savedDataLevelRef = savedDataService.constructLevelName(gameDataHolder.getLevelData());        StarsInfo starsInfo = starsAndUnlockService.getStarsInfo(object, savedDataLevelRef);        levelInfo.getStats().setStarsInfo(starsInfo);    }    private void resolveNumTileList(JSONObject object, LevelInfo levelInfo) throws JSONException {        List<NumTile> gameTileList = Lists.newArrayList();        if (object.has("tileNumsToUse")) {            String gameTiles = object.getString("tileNumsToUse");            String[] gameTileArray = gameTiles.split(",");            for (String tileNum : gameTileArray) {                NumTile numTile = NumTile.getGameTileFromValue(Integer.valueOf(tileNum));                gameTileList.add(numTile);            }        } else {            if (CalcType.MULTIPLY.equals(levelInfo.getCalcType())) {                int numToAddUpTo = object.getInt("numToEquateTo");                for (int i = 1; i <= 9; i++) {                    if (numToAddUpTo % i == 0) {                        gameTileList.add(NumTile.getGameTileFromValue(i));                    }                }            } else {                int maxGameTileNum = object.getInt("maxTileNum");                for (Integer gameTileValue = 1; gameTileValue <= maxGameTileNum; gameTileValue++) {                    gameTileList.add(NumTile.getGameTileFromValue(gameTileValue));                }            }        }        levelInfo.getGridData().setGameTileList(gameTileList);    }}