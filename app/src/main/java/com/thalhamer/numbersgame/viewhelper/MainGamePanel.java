/** * */package com.thalhamer.numbersgame.viewhelper;import android.content.Context;import android.util.AttributeSet;import android.util.Log;import android.view.MotionEvent;import android.view.SurfaceHolder;import android.view.SurfaceView;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.domain.GameDataHolder;import com.thalhamer.numbersgame.domain.GridData;import com.thalhamer.numbersgame.services.GameInitiateService;import com.thalhamer.numbersgame.services.GridMappingService;import com.thalhamer.numbersgame.services.MotionEventService;import com.thalhamer.numbersgame.services.PowerService;import com.thalhamer.numbersgame.services.SavedDataService;import java.io.IOException;import javax.inject.Singleton;import dagger.ObjectGraph;@Singletonpublic class MainGamePanel extends SurfaceView implements SurfaceHolder.Callback {    private ObjectGraph objectGraph;    private volatile MainThread thread;    private MotionEventService motionEventService;    private GridMappingService gridMappingService;    private GameInitiateService gameInitiateService;    private GameDataHolder gameDataHolder;    private SavedDataService savedDataService;    private PowerService powerService;    public MainGamePanel(Context context, AttributeSet attrs) throws IOException {        super(context, attrs);        objectGraph = App.getGameActivityObjectGraph();        init();        // adding the callback (this) to the surface holder to intercept events        getHolder().addCallback(this);        // make the GamePanel focusable so it can handle events        setFocusable(true);    }    private void init() throws IOException {        this.gameInitiateService = objectGraph.get(GameInitiateService.class);        this.motionEventService = objectGraph.get(MotionEventService.class);        this.gridMappingService = objectGraph.get(GridMappingService.class);        this.gameDataHolder = objectGraph.get(GameDataHolder.class);        this.gameDataHolder.setGamePanel(this);        this.savedDataService = objectGraph.get(SavedDataService.class);        this.powerService = objectGraph.get(PowerService.class);    }    @Override    public void surfaceCreated(SurfaceHolder holder) {        Log.d("MainGamePanel", "Surface is being created");        thread = new MainThread(this, objectGraph);        gameDataHolder.setMainThread(thread);        // at this point the surface is created and we can safely start the game loop        thread.setRunning(true);        thread.start();        TouchStateHolder.setTouchState(GridData.TouchState.ENABLED);    }    @Override    public boolean onTouchEvent(MotionEvent event) {        if (TouchStateHolder.getTouchState() != GridData.TouchState.DISABLED) {            return motionEventService.handleEvent(event);        } else {            return false;        }    }    @Override    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {    }    @Override    public void surfaceDestroyed(SurfaceHolder holder) {        Log.d("MainGamePanel", "Surface is being destroyed");        boolean retry = true;        thread.setRunning(false);        while (retry) {            try {                thread.join();                thread = null;                retry = false;            } catch (InterruptedException e) {                Log.d("destroying surface fail", e.getMessage());            }        }    }}