package com.thalhamer.numbersgame.Activity;import android.app.AlertDialog;import android.content.DialogInterface;import android.content.Intent;import android.media.AudioManager;import android.os.Bundle;import android.support.v4.app.FragmentActivity;import android.view.View;import android.view.Window;import android.view.WindowManager;import android.widget.Button;import com.thalhamer.numbersgame.R;import com.thalhamer.numbersgame.enums.sounds.SoundEnum;/** * opening screen activity * <p/> * Created by Brian on 10/14/2015. */public class OpeningScreenActivity extends FragmentActivity {    @Override    public void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setVolumeControlStream(AudioManager.STREAM_MUSIC);        requestWindowFeature(Window.FEATURE_NO_TITLE);        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);        setContentView(R.layout.activity_first_screen);        Button startButton = (Button) findViewById(R.id.startButton);        startButton.setOnClickListener(new View.OnClickListener() {            public void onClick(View view) {                SoundEnum.CLICK1.getMediaPlayer().start();                Intent intent = new Intent(OpeningScreenActivity.this, ChooseEpicActivity.class);                startActivity(intent);            }        });    }    @Override    public void onBackPressed() {        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);        alertDialogBuilder                .setMessage("Are you sure you want to exit Count 'em Up?")                .setCancelable(false)                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {                    public void onClick(DialogInterface dialog, int id) {                        finishAffinity();                    }                })                .setNegativeButton("No", new DialogInterface.OnClickListener() {                    public void onClick(DialogInterface dialog, int id) {                        dialog.cancel();                    }                });        AlertDialog alertDialog = alertDialogBuilder.create();        alertDialog.show();    }}