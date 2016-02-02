package com.thalhamer.numbersgame.enums;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.R;/** * Created by Brian on 5/2/2015. */public enum OperType {    ADD(R.drawable.oper_plus), SUBTRACT(R.drawable.oper_minus), MULTIPLY((R.drawable.oper_multiply));    private Bitmap bitmap;    OperType(int imageNum) {        this.bitmap = BitmapFactory.decodeResource(App.getContext().getResources(), imageNum);    }    public Bitmap getBitmap() {        return bitmap;    }}