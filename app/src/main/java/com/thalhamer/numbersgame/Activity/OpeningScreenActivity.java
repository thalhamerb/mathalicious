package com.thalhamer.numbersgame.Activity;import android.app.AlertDialog;import android.content.DialogInterface;import android.content.Intent;import android.graphics.Typeface;import android.media.AudioManager;import android.net.Uri;import android.os.Bundle;import android.support.v4.app.FragmentActivity;import android.util.Log;import android.view.View;import android.view.Window;import android.view.WindowManager;import android.widget.Button;import android.widget.TextView;import com.google.android.gms.appinvite.AppInviteInvitation;import com.google.common.collect.Lists;import com.thalhamer.numbersgame.R;import com.thalhamer.numbersgame.dagger.component.DaggerOpeningScreenActivityComponent;import com.thalhamer.numbersgame.dagger.component.OpeningScreenActivityComponent;import com.thalhamer.numbersgame.dagger.module.AppModule;import com.thalhamer.numbersgame.domain.PopupResult;import com.thalhamer.numbersgame.domain.SlideViewContent;import com.thalhamer.numbersgame.enums.sounds.SoundEnum;import com.thalhamer.numbersgame.services.SavedDataService;import com.thalhamer.numbersgame.services.popup.ExplanationPopupService;import java.util.List;import javax.inject.Inject;/** * opening screen activity * <p/> * Created by Brian on 10/14/2015. */public class OpeningScreenActivity extends FragmentActivity {    public static final int REQUEST_CODE = 1;    @Inject    SavedDataService savedDataService;    @Inject    ExplanationPopupService explanationPopupService;    @Override    public void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        OpeningScreenActivityComponent levelActivityComponent =                DaggerOpeningScreenActivityComponent.builder().appModule(new AppModule()).build();        levelActivityComponent.injectOpeningScreenActivity(this);        setVolumeControlStream(AudioManager.STREAM_MUSIC);        requestWindowFeature(Window.FEATURE_NO_TITLE);        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);        setContentView(R.layout.opening_screen_activity);        setButtons();    }    //TODO might want to use this method instead of onResume in LevelActivity to make code simpler    @Override    public void onWindowFocusChanged(boolean hasFocus) {        if (hasFocus && !savedDataService.containsKey("first_time_opened")) {            buildGameIntroPopupWindow();            savedDataService.saveKey("first_time_opened", 1);        }        super.onResume();    }    private void setButtons() {        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");        Button startButton = (Button) findViewById(R.id.startButton);        startButton.setOnClickListener(new View.OnClickListener() {            public void onClick(View view) {                SoundEnum.CLICK1.getMediaPlayer().start();                Intent intent = new Intent(OpeningScreenActivity.this, ChooseEpicActivity.class);                startActivity(intent);            }        });        Button introButton = (Button) findViewById(R.id.introButton);        introButton.setOnClickListener(new View.OnClickListener() {            public void onClick(View view) {                buildGameIntroPopupWindow();            }        });        TextView inviteButton = (TextView) findViewById(R.id.inviteButton);        inviteButton.setTypeface(font);        inviteButton.setOnClickListener(new View.OnClickListener() {            public void onClick(View view) {                onInviteClicked();            }        });    }    private void onInviteClicked() {        Intent intent = new AppInviteInvitation.IntentBuilder("Send App Invitation")                .setMessage("Check out this fun game app!")                .setDeepLink(Uri.parse("https://play.google.com/store/apps/details?id=com.thalhamer.numbersgame&hl=en"))                .setCustomImage(Uri.parse("https://lh3.googleusercontent.com/aGWQhZzzq82vag3gkLUWGf8xmBnrVzJtglm5hPTqXfVRdgA2C5d3KbW7OqbcW7-XBwI=h80-rw"))                .setCallToActionText("Install!")                .build();        startActivityForResult(intent, REQUEST_CODE);    }    private void buildGameIntroPopupWindow() {        List<SlideViewContent> contents = Lists.newArrayList();        SlideViewContent content1 = new SlideViewContent();        content1.setTitle("The Game");        content1.setDescription("Play with famous mathamaticians to collect stars and unlock new levels!");        content1.setImageResourceId(R.drawable.intro_game);        contents.add(content1);        SlideViewContent content2 = new SlideViewContent();        content2.setTitle("Levels");        content2.setDescription("Add or subtract numbered tiles to collect points, character faces, etc.");        content2.setImageResourceId(R.drawable.intro_levels);        contents.add(content2);        SlideViewContent content3 = new SlideViewContent();        content3.setTitle("Powers");        content3.setDescription("Use power tiles to destroy numbered tiles that are in your way!");        content3.setImageResourceId(R.drawable.explain_power);        contents.add(content3);        PopupResult popupResult = new PopupResult(this);        explanationPopupService.buildPopupWindow(popupResult, contents);    }    @Override    protected void onActivityResult(int requestCode, int resultCode, Intent data) {        super.onActivityResult(requestCode, resultCode, data);        Log.d(getLocalClassName(), "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);        if (requestCode == REQUEST_CODE) {            if (resultCode != RESULT_OK) {                Log.e(getLocalClassName(), "Unsuccessful use of app invites.");            }        }    }    @Override    public void onBackPressed() {        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);        String message = String.format("Are you sure you want to exit %s?", getString(R.string.app_name));        alertDialogBuilder                .setMessage(message)                .setCancelable(false)                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {                    public void onClick(DialogInterface dialog, int id) {                        finishAffinity();                    }                })                .setNegativeButton("No", new DialogInterface.OnClickListener() {                    public void onClick(DialogInterface dialog, int id) {                        dialog.cancel();                    }                });        AlertDialog alertDialog = alertDialogBuilder.create();        alertDialog.show();    }}