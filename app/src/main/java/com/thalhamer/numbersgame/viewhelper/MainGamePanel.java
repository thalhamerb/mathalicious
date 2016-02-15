/** * */package com.thalhamer.numbersgame.viewhelper;import android.content.Context;import android.util.AttributeSet;import android.util.Log;import android.view.MotionEvent;import android.view.SurfaceHolder;import android.view.SurfaceView;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.domain.GameDataHolder;import com.thalhamer.numbersgame.domain.GridData;import com.thalhamer.numbersgame.services.MotionEventService;import java.io.IOException;import javax.inject.Inject;import javax.inject.Singleton;@Singletonpublic class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback {    private volatile MainThread thread;    @Inject    MotionEventService motionEventService;    @Inject    GameDataHolder gameDataHolder;    public MainGamePanel(Context context, AttributeSet attrs) throws IOException {        super(context, attrs);        App.getLevelActivityComponent().injectMainGamePanel(this);        this.gameDataHolder.setGamePanel(this);        // adding the callback (this) to the surface holder to intercept events        getHolder().addCallback(this);        // make the GamePanel focusable so it can handle events        setFocusable(true);    }    @Override    public void surfaceCreated(SurfaceHolder holder) {        Log.d("MainGamePanel", "Surface is being created");        thread = new MainThread(this);        gameDataHolder.setMainThread(thread);        // at this point the surface is created and we can safely start the game loop        thread.setRunning(true);        thread.start();        TouchStateHolder.setTouchState(GridData.TouchState.ENABLED);    }    @Override    public boolean onTouchEvent(MotionEvent event) {        if (TouchStateHolder.getTouchState() != GridData.TouchState.DISABLED) {            return motionEventService.handleEvent(event);        } else {            return false;        }    }    @Override    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {    }    @Override    public void surfaceDestroyed(SurfaceHolder holder) {        Log.d("MainGamePanel", "Surface is being destroyed");        boolean retry = true;        thread.setRunning(false);        while (retry) {            try {                thread.join();                thread = null;                retry = false;            } catch (InterruptedException e) {                Log.d("destroying surface fail", e.getMessage());            }        }    }}