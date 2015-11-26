package com.thalhamer.numbersgame.services;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.RectF;

import com.google.common.collect.Lists;
import com.thalhamer.numbersgame.domain.DiagonalTile;
import com.thalhamer.numbersgame.domain.GameDataHolder;
import com.thalhamer.numbersgame.domain.Image;
import com.thalhamer.numbersgame.domain.Tile;
import com.thalhamer.numbersgame.enums.sounds.Direction;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * diagonal grid mapping service
 * <p/>
 * Created by Brian on 11/23/2015.
 */
public class GridMappingDiagonalService {

    @Inject
    GridService gridService;
    @Inject
    GameDataHolder gameDataHolder;

    public void performDiagonalMove(ArrayList<Tile> currentCol, GridMappingService gridMappingService) {
        for (Tile currTile : currentCol) {
            DiagonalTile diagTile = gridService.getEmptyTileBelowOrDownDiagonal(currTile);
            if (diagTile != null) {
                List<DiagonalTile> diagonalTiles = Lists.newArrayList();
                performMoveDiagonal(diagTile, currentCol, diagonalTiles, currTile, currTile, gridMappingService);
                break;
            }
        }
    }

    private void performMoveDiagonal(DiagonalTile diagTile, ArrayList<Tile> currentCol, List<DiagonalTile> diagonalTiles,
                                     Tile currTile, Tile originalTile, GridMappingService gridMappingService) {
        //recursive method
        if (diagTile != null) {
            diagonalTiles.add(diagTile);
            shiftTilesOnePlaceAfterInitialDrop(diagonalTiles, currentCol, currTile, originalTile, gridMappingService);
        }
    }

    private void shiftTilesOnePlaceAfterInitialDrop(List<DiagonalTile> diagonalTiles, ArrayList<Tile> currentCol,
                                                    Tile currTile, Tile originalTile, GridMappingService gridMappingService) {
        //move diagonal tiles
        for (int i = diagonalTiles.size() - 1; i >= 0; i--) {
            DiagonalTile diagTile = diagonalTiles.get(i);
            if (i != 0) {
                Image previousImage = diagonalTiles.get(i - 1).getTile().getImage();
                diagTile.getTile().setImage(previousImage);
            } else {
                diagTile.getTile().setImage(originalTile.getImage());
            }
        }

        //move tiles in row down one column
        for (int i = originalTile.getRowNum(); i < currentCol.size() - 1; i++) {
            Tile tile = currentCol.get(i);
            Tile nextTile = currentCol.get(i + 1);
            nextTile.setImage(tile.getImage());
        }

        //add one new image and drop one place
        Tile topTile = currentCol.get(currentCol.size() - 1);
        topTile.setImage(gridService.getRandomImage(gameDataHolder.getLevelInfo().getGridData().getGameTileList()));
        topTile.getImage().setNewImage(true);

        imageDropDiagonalAsGroup(diagonalTiles, currentCol, currTile, originalTile, gridMappingService);
    }

    private void imageDropDiagonalAsGroup(final List<DiagonalTile> diagonalTiles, final ArrayList<Tile> currentCol,
                                          final Tile currTile, final Tile originalTile, final GridMappingService gridMappingService) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(1000);

        final ArrayList<ArrayList<Tile>> grid = gameDataHolder.getLevelInfo().getGridData().getGrid();
        final float diagonalXMove = grid.get(0).get(1).getImage().getRectF().left - grid.get(0).get(0).getImage().getRectF().left;
        final float diagonalYMove = grid.get(0).get(1).getImage().getRectF().top - grid.get(0).get(0).getImage().getRectF().top;
        final float verticalYMove = currentCol.get(1).getImage().getRectF().top - currentCol.get(0).getImage().getRectF().top;

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private float previousPercentage = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float currentPercentage = (Float) animation.getAnimatedValue();
                float percentageChange = currentPercentage - previousPercentage;

                //move diagonal tiles
                for (DiagonalTile diagTile : diagonalTiles) {
                    setNewDiagonalRectFDiagonalTile(diagTile, diagonalXMove * percentageChange, diagonalYMove * percentageChange);
                }

                //move vertical tiles
                for (int i = originalTile.getRowNum(); i < currentCol.size(); i++) {
                    float extraVertShift = 0;
                    if (currentCol.get(i).getImage().isNewImage()) {
                        extraVertShift = gridMappingService.getTopOfGrid(gameDataHolder.getGamePanel());
                        currentCol.get(i).getImage().setNewImage(false);
                    }
                    RectF vertRectF = currentCol.get(i).getImage().getRectF();
                    vertRectF.top += (verticalYMove * percentageChange) + extraVertShift;
                    vertRectF.bottom += (verticalYMove * percentageChange) + extraVertShift;
                }

                previousPercentage = currentPercentage;
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            public synchronized void onAnimationEnd(Animator animation) {
                DiagonalTile diagTile = gridService.getEmptyTileBelowOrDownDiagonal(currTile);
                if (diagTile != null) {
                    performMoveDiagonal(diagTile, currentCol, diagonalTiles, currTile, originalTile, gridMappingService);
                } else {
                    gridMappingService.checkForDiagonalMove(currentCol);
                }
            }
        });
    }

    private void setNewDiagonalRectFDiagonalTile(DiagonalTile diagTile, float xAmountToMove, float yAmountToMove) {
        RectF diagRectF = diagTile.getTile().getImage().getRectF();
        diagRectF.top += yAmountToMove;
        diagRectF.bottom += yAmountToMove;

        if (diagTile.getDirection().equals(Direction.DIAGONAL_DOWN_LEFT)) {
            diagRectF.left -= xAmountToMove;
            diagRectF.right -= xAmountToMove;
        } else if (diagTile.getDirection().equals(Direction.DOWN)) {
            diagRectF.left += xAmountToMove;
            diagRectF.right += xAmountToMove;
        }

    }
}
