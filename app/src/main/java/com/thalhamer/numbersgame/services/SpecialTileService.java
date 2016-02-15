package com.thalhamer.numbersgame.services;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Handler;
import android.view.animation.AccelerateInterpolator;

import com.thalhamer.numbersgame.domain.GameDataHolder;
import com.thalhamer.numbersgame.domain.Tile;
import com.thalhamer.numbersgame.enums.SpecialTile;
import com.thalhamer.numbersgame.viewhelper.MainGamePanel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * special tile service
 * <p/>
 * Created by Brian on 11/14/2015.
 */
@Singleton
public class SpecialTileService {

    public static final int OPEN_LEPRECHAUN_TIME = 300;
    public static final int KEEP_OPEN_LEPRECHAUN_TIME = 800;

    @Inject
    GameDataHolder gameDataHolder;
    @Inject
    GridMappingService gridMappingService;

    @Inject
    public SpecialTileService() {
    }

    private RectF leprechaunRectF;

    public void processSpecialTile(List<Tile> touchedTiles) {
        for (Tile tile : touchedTiles) {
            if (tile.getImage().getSpecialTile() != null && tile.getImage().getSpecialTile().equals(SpecialTile.LEPRECHAUN)) {
                SpecialTile.LEPRECHAUN.getMediaPlayer().start();
                startLeprechaunAnimation();
            }
        }
    }

    private void startLeprechaunAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(OPEN_LEPRECHAUN_TIME);
        animator.setInterpolator(new AccelerateInterpolator());
        MainGamePanel gamePanel = gameDataHolder.getGamePanel();
        float topOfGrid = gridMappingService.getTopOfGrid();
        final float vertCenter = topOfGrid + gridMappingService.getGridHeight() / 2;
        final float horizCenter = gamePanel.getWidth() / 2;
        leprechaunRectF = new RectF(horizCenter, vertCenter, horizCenter, vertCenter);

        final float halfMaxHeight = (gridMappingService.getGridHeight() * 0.7f) / 2;
        final float halfMaxWidth = (gamePanel.getWidth() * 0.7f) / 2;

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float scaleFactor = (Float) animation.getAnimatedValue();
                leprechaunRectF.left = horizCenter - (halfMaxWidth * scaleFactor);
                leprechaunRectF.right = horizCenter + (halfMaxWidth * scaleFactor);
                leprechaunRectF.top = vertCenter - (halfMaxHeight * scaleFactor);
                leprechaunRectF.bottom = vertCenter + (halfMaxHeight * scaleFactor);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                closeLeprechaunAfterTime(KEEP_OPEN_LEPRECHAUN_TIME);
            }
        });
        animator.start();
    }

    private void closeLeprechaunAfterTime(int timeInMillis) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                leprechaunRectF = null;
            }
        };
        new Handler().postDelayed(runnable, timeInMillis);
    }

    public void drawSpecialTileAnimations(Canvas canvas) {
        if (leprechaunRectF != null) {
            canvas.drawBitmap(SpecialTile.LEPRECHAUN.getBitmap(), null, leprechaunRectF, null);
        }
    }

}
