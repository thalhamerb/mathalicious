package com.thalhamer.numbersgame.enums;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.media.MediaPlayer;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.R;/** * Character * <p/> * Created by Brian on 7/3/2015. */public enum Character {    EINSTEIN("Einstein", 1, R.mipmap.einstein, R.mipmap.einstein_full_body, R.raw.einstein1),    NEWTON("Newton", 2, R.mipmap.newton, R.mipmap.einstein_full_body, R.raw.newton1),    GALILEO("Galileo", 3, R.mipmap.galileo, R.mipmap.einstein_full_body, R.raw.galileo1);    private String name;    private int epic;    private int faceResourceId;    private Bitmap faceBitmap;    private Bitmap leaningBitmap;    private int nameAsSoundResId;    private MediaPlayer mediaPlayer;    Character(String name, int epic, int faceResourceId, int fullBodyResourceId, int nameAsSoundResId) {        this.name = name;        this.epic = epic;        this.faceResourceId = faceResourceId;        this.faceBitmap = BitmapFactory.decodeResource(App.getContext().getResources(), faceResourceId);        this.leaningBitmap = BitmapFactory.decodeResource(App.getContext().getResources(), fullBodyResourceId);        this.nameAsSoundResId = nameAsSoundResId;        this.mediaPlayer = MediaPlayer.create(App.getContext(), nameAsSoundResId);    }    public static Character getCharacterFromEpic(int epic) {        if (epic == 1) {            return Character.EINSTEIN;        } else if (epic == 2) {            return Character.NEWTON;        } else if (epic == 3) {            return Character.GALILEO;        } else {            return null;        }    }    public String getName() {        return name;    }    public Bitmap getLeaningBitmap() {        return leaningBitmap;    }    public int getEpic() {        return epic;    }    public Bitmap getFaceBitmap() {        return faceBitmap;    }    public int getNameAsSoundResId() {        return nameAsSoundResId;    }    public MediaPlayer getMediaPlayer() {        return mediaPlayer;    }    public int getFaceResourceId() {        return faceResourceId;    }}