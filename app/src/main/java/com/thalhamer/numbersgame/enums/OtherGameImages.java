package com.thalhamer.numbersgame.enums;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.R;/** * Other game images * <p/> * Created by Brian on 8/2/2015. */public enum OtherGameImages {    STAR(R.drawable.star);    private Bitmap bitmap;    private int resourceId;    OtherGameImages(int resourceId) {        this.resourceId = resourceId;        this.bitmap = BitmapFactory.decodeResource(App.getContext().getResources(), resourceId);    }    public int getResourceId() {        return resourceId;    }    public Bitmap getBitmap() {        return bitmap;    }}