package com.thalhamer.numbersgame.Factory;import android.app.Application;import android.content.Context;import android.graphics.Point;import android.view.Display;import android.view.WindowManager;import com.google.android.gms.ads.InterstitialAd;import com.thalhamer.numbersgame.dagger.component.LevelActivityComponent;/** * App * * Created by Brian on 2/7/2015. */public class App extends Application {    private static Context mContext;    private static boolean connectedToPurchasingNetwork = false;    private static InterstitialAd mInterstitialAd;    private static Point screenDimensions;    private static LevelActivityComponent levelActivityComponent;    @Override    public void onCreate() {        super.onCreate();        mContext = this;        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);        Display display = wm.getDefaultDisplay();        Point screenDimensions = new Point();        display.getSize(screenDimensions);        this.screenDimensions = screenDimensions;    }    public static Context getContext() {        return mContext;    }    public static LevelActivityComponent getLevelActivityComponent() {        return levelActivityComponent;    }    public static void setLevelActivityComponent(LevelActivityComponent levelActivityComponent) {        App.levelActivityComponent = levelActivityComponent;    }    public static boolean isConnectedToPurchasingNetwork() {        return connectedToPurchasingNetwork;    }    public static void setConnectedToPurchasingNetwork(boolean connectedToPurchasingNetwork) {        App.connectedToPurchasingNetwork = connectedToPurchasingNetwork;    }    public static InterstitialAd getmInterstitialAd() {        return mInterstitialAd;    }    public static void setmInterstitialAd(InterstitialAd mInterstitialAd) {        App.mInterstitialAd = mInterstitialAd;    }    public static int getDisplayWidth() {        return screenDimensions.x;    }    public static int getDisplayHeight() {        return screenDimensions.y;    }}