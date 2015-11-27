package com.thalhamer.numbersgame.services;import android.util.Log;import com.google.common.collect.Lists;import com.thalhamer.numbersgame.domain.AddNewImagesResult;import com.thalhamer.numbersgame.domain.DiagonalTile;import com.thalhamer.numbersgame.domain.GameDataHolder;import com.thalhamer.numbersgame.domain.GridData;import com.thalhamer.numbersgame.domain.Image;import com.thalhamer.numbersgame.domain.OperationImage;import com.thalhamer.numbersgame.domain.Tile;import com.thalhamer.numbersgame.enums.CalcType;import com.thalhamer.numbersgame.enums.Character;import com.thalhamer.numbersgame.enums.GameType;import com.thalhamer.numbersgame.enums.NumTile;import com.thalhamer.numbersgame.enums.OperType;import com.thalhamer.numbersgame.enums.PowerEnum;import com.thalhamer.numbersgame.enums.ScoreType;import com.thalhamer.numbersgame.enums.SpecialTile;import com.thalhamer.numbersgame.enums.TileAttribute;import com.thalhamer.numbersgame.enums.sounds.Direction;import com.thalhamer.numbersgame.viewhelper.GameConstants;import java.util.ArrayList;import java.util.HashSet;import java.util.List;import java.util.Set;import javax.inject.Inject;import javax.inject.Singleton;/** * performs backend grid related tasks * <p/> * Created by Brian on 1/1/2015. */@Singletonpublic class GridService {    @Inject    GameDataHolder gameDataHolder;    private int characterCounter = 0;    private int numTileCounter = 0;    public ArrayList<ArrayList<Tile>> initialGridPopulate(int numOfRowsToPopulate) {        GridData gridData = gameDataHolder.getLevelInfo().getGridData();        ArrayList<ArrayList<Tile>> grid = gridData.getGrid();        initializeTiles(gridData, grid);        addTileAttributesAndSpecialTiles();        addTileImages(numOfRowsToPopulate, gridData);        return grid;    }    private void initializeTiles(GridData gridData, ArrayList<ArrayList<Tile>> grid) {        for (int colNum = 0; colNum < gridData.getNumOfColumns(); colNum++) {            grid.add(new ArrayList<Tile>());            for (int rowNum = 0; rowNum < gridData.getNumOfRows(); rowNum++) {                Tile currTile = new Tile(colNum, rowNum);                grid.get(colNum).add(currTile);            }        }    }    private void addTileAttributesAndSpecialTiles() {        Integer[][] gridTileData = gameDataHolder.getLevelInfo().getGridTileData();        if (gridTileData != null) {            GridData gridData = gameDataHolder.getLevelInfo().getGridData();            ArrayList<ArrayList<Tile>> grid = gridData.getGrid();            int maxRowNum = gridData.getNumOfRows() - 1;            for (int colNum = 0; colNum < gridData.getNumOfColumns(); colNum++) {                for (int rowNum = 0; rowNum < gridData.getNumOfRows(); rowNum++) {                    Integer gridTileDataValue = gridTileData[maxRowNum - rowNum][colNum];                    Tile currTile = grid.get(colNum).get(rowNum);                    TileAttribute tileAttribute = TileAttribute.getTileAttributeByMappedInteger(gridTileDataValue);                    if (tileAttribute != null) {                        currTile.setTileAttribute(tileAttribute);                    }                    SpecialTile specialTile = SpecialTile.getSpecialTileByMappedValue(gridTileDataValue);                    if (specialTile != null) {                        currTile.setImage(new Image(specialTile));                    }                    if (CalcType.MULT_OPER.equals(gameDataHolder.getLevelInfo().getCalcType())) {                        setTileOperationImages(currTile);                    }                }            }        }    }    private void addTileImages(int numOfRowsToPopulate, GridData gridData) {        Image currImage;        for (ArrayList<Tile> currCol : gridData.getGrid()) {            for (Tile currTile : currCol) {                if (!TileAttribute.noImageTileAttributes().contains(currTile.getTileAttribute()) && currTile.getImage() == null) {                    currImage = currTile.getRowNum() < numOfRowsToPopulate ? getRandomImage(gridData.getGameTileList()) : null;                    currTile.setImage(currImage);                }            }        }    }    public void setTouchedAttrForTiles(List<Tile> touchedTiles, boolean touchStatus) {        for (Tile tile : touchedTiles) {            tile.setTouched(touchStatus);        }    }    public void setImageDropDistances() {        GridData gridData = gameDataHolder.getLevelInfo().getGridData();        for (ArrayList<Tile> currCol : gridData.getGrid()) {            int placesToDrop = 0;            for (Tile tile : currCol) {            //starts at bottom of column                if (TileAttribute.noImageTileAttributes().contains(tile.getTileAttribute())) {                    placesToDrop = 0;                } else if (tile.getImage() == null) {                    placesToDrop++;                } else {                    tile.getImage().setPlacesToDrop(placesToDrop);                }            }        }    }    public void clearDropDistances() {        GridData gridData = gameDataHolder.getLevelInfo().getGridData();        for (ArrayList<Tile> currCol : gridData.getGrid()) {            for (Tile tile : currCol) {                if (tile.getImage() != null) {                    tile.getImage().setPlacesToDrop(0);                }            }        }    }    public void imageShift() {        GridData gridData = gameDataHolder.getLevelInfo().getGridData();        ArrayList<ArrayList<Tile>> grid = gridData.getGrid();        for (ArrayList<Tile> currentCol : grid) {            for (Tile currTile : currentCol) {                Image tileImage = currTile.getImage();                if (tileImage != null && tileImage.getPlacesToDrop() > 0) {                    Tile tileToDropImageTo = grid.get(currTile.getColNum()).get(currTile.getRowNum() - tileImage.getPlacesToDrop());                    tileToDropImageTo.setImage(currTile.getImage());                    currTile.setImage(null);                }            }        }    }    public int getNumberOfColumnsAffectedByImageShift(List<Tile> touchedTiles) {        Set<Integer> uniqueIntegers = new HashSet<>();        for (Tile tile : touchedTiles) {            uniqueIntegers.add(tile.getColNum());        }        return uniqueIntegers.size();    }    public List<Tile> getAllTilesWithNum(Tile tile) {        List<Tile> tilesWithNum = Lists.newArrayList();        if (tile.getImage() == null) {            return tilesWithNum;        }        NumTile touchedTileEnum = tile.getImage().getNumTile();        GridData gridData = gameDataHolder.getLevelInfo().getGridData();        ArrayList<ArrayList<Tile>> grid = gridData.getGrid();        for (ArrayList<Tile> currentCol : grid) {            for (Tile currTile : currentCol) {                Image tileImage = currTile.getImage();                if (tileImage != null && tileImage.getNumTile() != null &&                        touchedTileEnum.equals(tileImage.getNumTile())) {                    tilesWithNum.add(currTile);                }            }        }        return tilesWithNum;    }    public void addNewImagesToGrid(GridMappingService gridMappingService, boolean addSingleImageToEachCol, boolean dueToPower) {        GridData gridData = gameDataHolder.getLevelInfo().getGridData();        ArrayList<ArrayList<Tile>> grid = gridData.getGrid();        for (int colNum = 0; colNum < gridData.getNumOfColumns(); colNum++) {            ArrayList<Tile> currCol = grid.get(colNum);            for (int rowNum = gridData.getNumOfRows() - 1; rowNum >= 0; rowNum--) {  //start from top of column                Tile currTile = grid.get(colNum).get(rowNum);                AddNewImagesResult addNewImagesResult = new AddNewImagesResult();                addNewImagesResult.setCurrCol(currCol);                addNewImagesResult.setDueToPower(dueToPower);                addNewImagesResult.setGameTilesToUse(gridData.getGameTileList());                if (currTile.getImage() != null || TileAttribute.noImageTileAttributes().contains(currTile.getTileAttribute())) {                    int numOfImagesToAdd = getNumberOfImagesToAdd(currCol, currTile, addSingleImageToEachCol, false);                    addNewImagesResult.setFirstRowWithoutImage(currTile.getRowNum() + 1);                    addNewImagesResult.setNumOfImagesToAdd(numOfImagesToAdd);                    addNewImagesToCol(addNewImagesResult, gridMappingService);                    break;                } else if (currTile.getRowNum() == 0) {  //whole column is empty                    int numOfImagesToAdd = getNumberOfImagesToAdd(currCol, currTile, addSingleImageToEachCol, true);                    addNewImagesResult.setFirstRowWithoutImage(currTile.getRowNum());                    addNewImagesResult.setNumOfImagesToAdd(numOfImagesToAdd);                    addNewImagesToCol(addNewImagesResult, gridMappingService);                    break;                }            }        }    }    public int getNumberOfImagesToAdd(ArrayList<Tile> currCol, Tile currTile, boolean addSingleImageToEachColumn, boolean isColumnEmpty) {        if (addSingleImageToEachColumn) {            return 1;        } else {            if (isColumnEmpty) {                return currCol.size();            } else {                return currCol.size() - (currTile.getRowNum() + 1);            }        }    }    private void addNewImagesToCol(AddNewImagesResult addNewImagesResult, GridMappingService gridMappingService) {        int currRowNum = addNewImagesResult.getFirstRowWithoutImage();        List<Tile> currCol = addNewImagesResult.getCurrCol();        Integer placesToDrop = currCol.size() - currRowNum;        for (int newImagesCount = 0; newImagesCount < addNewImagesResult.getNumOfImagesToAdd(); newImagesCount++) {            Tile currTile = currCol.get(currRowNum);            Image newImage;            if (gameDataHolder.getLevelInfo().getGameType().equals(GameType.MOVES) &&                    numTileCounter == GameConstants.NUM_OF_TILES_BETWEEN_POWER_ENUMS) {                newImage = new Image(PowerEnum.CLEAR_ALL_NUM);                numTileCounter = 0;            } else {                newImage = getRandomImage(addNewImagesResult.getGameTilesToUse());                if (!addNewImagesResult.isDueToPower()) {                    numTileCounter++;                }            }            newImage.setNewImage(true);            newImage.setPlacesToDrop(placesToDrop);            gridMappingService.setNewImageRectangleForInitialDrop(currTile, newImagesCount, newImage);            currTile.setImage(newImage);            currRowNum++;        }    }    public boolean topRowHasImage() {        GridData gridData = gameDataHolder.getLevelInfo().getGridData();        int topRowNum = gridData.getNumOfRows() - 1;        ArrayList<ArrayList<Tile>> grid = gridData.getGrid();        for (int colNum = 0; colNum < gridData.getNumOfColumns(); colNum++) {            if (grid.get(colNum).get(topRowNum).getImage() != null) {                return true;            }        }        return false;    }    private void setTileOperationImages(Tile currTile) {        if (currTile.getColNum() != 0) {            OperationImage leftOpImage = new OperationImage();            leftOpImage.setOperation(getRandomOperation(gameDataHolder.getLevelInfo().getAllowedOperations()));            currTile.setLeftOperationImage(leftOpImage);        }        if (currTile.getRowNum() != (gameDataHolder.getLevelInfo().getGridData().getNumOfRows() - 1)) {            OperationImage topOpImage = new OperationImage();            topOpImage.setOperation(getRandomOperation(gameDataHolder.getLevelInfo().getAllowedOperations()));            currTile.setTopOperationImage(topOpImage);        }    }    public void nullifyTileImages(List<Tile> tilesToRemove) {        for (Tile tile : tilesToRemove) {            tile.setImage(null);        }    }    public List<Tile> getTilesWithCharactersOnBottomGridRow() {        List<Tile> tilesWithCharacter = Lists.newArrayList();        GridData gridData = gameDataHolder.getLevelInfo().getGridData();        ArrayList<ArrayList<Tile>> grid = gridData.getGrid();        for (ArrayList<Tile> currentCol : grid) {            Tile bottomRowTile = currentCol.get(0);            if (bottomRowTile.getImage() != null && bottomRowTile.getImage().getCharacter() != null) {                tilesWithCharacter.add(bottomRowTile);            }        }        return tilesWithCharacter;    }    public Image getRandomImage(List<NumTile> gameTilesToUse) {        ScoreType scoreType = gameDataHolder.getLevelInfo().getScoreType();        if (ScoreType.CHARACTER_FACES.equals(scoreType) && characterCounter == GameConstants.NUM_OF_TILES_BETWEEN_CHARACTERS) {            characterCounter = 0;            Character character = Character.getCharacterFromEpic(gameDataHolder.getLevelData().getEpic());            return new Image(character);        } else {            int randomElementLoc = (int) (Math.random() * gameTilesToUse.size());            characterCounter++;            return new Image(gameTilesToUse.get(randomElementLoc));        }    }    public OperType getRandomOperation(List<OperType> operTypes) {        int randomOper = (int) (Math.random() * operTypes.size());        return operTypes.get(randomOper);    }    public boolean allImagesDroppedInColumn(ArrayList<Tile> currentCol) {        for (Tile tile : currentCol) {            if (tile.getImage() != null && tile.getImage().getPlacesToDrop() != 0) {                Log.e("Drop", "tileNotDropped: " + tile.getColNum() + "," + tile.getRowNum());                return false;            }        }        return true;    }    public DiagonalTile getEmptyTileBelowOrDownDiagonal(Tile tile) {        GridData gridData = gameDataHolder.getLevelInfo().getGridData();        ArrayList<ArrayList<Tile>> grid = gridData.getGrid();        if (tile.getRowNum() > 0) {            Tile tileBelow = grid.get(tile.getColNum()).get(tile.getRowNum() - 1);            if (tileBelow.getImage() == null) {                return new DiagonalTile(tileBelow, Direction.DOWN);            }            if (tile.getColNum() > 0) {                Tile tileDiagonalDownLeft = grid.get(tile.getColNum() - 1).get(tile.getRowNum() - 1);                if (tileDiagonalDownLeft.getImage() == null) {                    return new DiagonalTile(tileDiagonalDownLeft, Direction.DIAGONAL_DOWN_LEFT);                }            }            if (tile.getColNum() < gameDataHolder.getLevelInfo().getGridData().getGrid().size() - 1) {                Tile tileDiagonalDownRight = grid.get(tile.getColNum() + 1).get(tile.getRowNum() - 1);                if (tileDiagonalDownRight.getImage() == null) {                    return new DiagonalTile(tileDiagonalDownRight, Direction.DIAGONAL_DOWN_RIGHT);                }            }        }        return null;    }}