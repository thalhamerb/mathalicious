package com.thalhamer.numbersgame.Activity;import android.content.Intent;import android.media.AudioManager;import android.os.Bundle;import android.support.annotation.NonNull;import android.support.v4.app.Fragment;import android.support.v4.app.FragmentActivity;import android.support.v4.view.ViewPager;import android.util.Log;import android.view.View;import android.view.Window;import android.view.WindowManager;import android.widget.ImageView;import android.widget.TextView;import com.google.android.gms.ads.AdView;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.Fragment.LevelFragment;import com.thalhamer.numbersgame.R;import com.thalhamer.numbersgame.dagger.component.ChooseLevelActivityComponent;import com.thalhamer.numbersgame.dagger.component.DaggerChooseLevelActivityComponent;import com.thalhamer.numbersgame.dagger.module.AppModule;import com.thalhamer.numbersgame.domain.GridData;import com.thalhamer.numbersgame.domain.LevelData;import com.thalhamer.numbersgame.domain.PopupResult;import com.thalhamer.numbersgame.enums.Character;import com.thalhamer.numbersgame.enums.PowerEnum;import com.thalhamer.numbersgame.enums.sounds.SoundEnum;import com.thalhamer.numbersgame.services.AdvertisementService;import com.thalhamer.numbersgame.services.InAppPurchaseService;import com.thalhamer.numbersgame.services.PowerService;import com.thalhamer.numbersgame.services.SavedDataService;import com.thalhamer.numbersgame.services.SectionUnlockService;import com.thalhamer.numbersgame.services.popup.IAPPopupService;import com.thalhamer.numbersgame.services.popup.LevelInfoPopupService;import com.thalhamer.numbersgame.util.IabHelper;import com.thalhamer.numbersgame.viewhelper.ActivityHelper;import com.thalhamer.numbersgame.viewhelper.TouchStateHolder;import java.util.ArrayList;import java.util.List;import javax.inject.Inject;public class ChooseLevelActivity extends FragmentActivity implements View.OnClickListener {    @Inject    SavedDataService savedDataService;    @Inject    LevelInfoPopupService levelInfoPopupService;    @Inject    ActivityHelper activityHelper;    @Inject    AdvertisementService advertisementService;    @Inject    SectionUnlockService sectionUnlockService;    @Inject    IAPPopupService iapPopupService;    @Inject    InAppPurchaseService inAppPurchaseService;    @Inject    PowerService powerService;    private Integer epic;    private Integer section;    private IabHelper mHelper;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        ChooseLevelActivityComponent chooseLevelActivityComponent =                DaggerChooseLevelActivityComponent.builder().appModule(new AppModule()).build();        chooseLevelActivityComponent.injectChooseLevelActivity(this);        setVolumeControlStream(AudioManager.STREAM_MUSIC);        Intent intent = getIntent();        epic = intent.getIntExtra("epic", -1);        section = intent.getIntExtra("section", -1);        requestWindowFeature(Window.FEATURE_NO_TITLE);        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);        setContentView(R.layout.choose_level_activity);        setViewElements();        mHelper = inAppPurchaseService.getInAppPurchaseHelper(this);    }    private void setViewElements() {        View activity_view = findViewById(android.R.id.content);        ImageView character = (ImageView) activity_view.findViewById(R.id.character);        character.setImageResource(Character.getCharacterFromEpic(epic).getFaceResourceId());        List<Fragment> fList = getFragments();        final ViewPager pager = (ViewPager) findViewById(R.id.levelViewPager);        final TextView leftArrow = (TextView) findViewById(R.id.leftArrow);        final TextView rightArrow = (TextView) findViewById(R.id.rightArrow);        activityHelper.setViewPager(this, fList, pager, leftArrow, rightArrow);        pager.setCurrentItem(section - 1);        AdView mAdView = (AdView) findViewById(R.id.adView);        advertisementService.initAdBanner(mAdView);        powerService.setPowerViewElements(findViewById(android.R.id.content));    }    @NonNull    private List<Fragment> getFragments() {        LevelFragment.savedDataService = savedDataService;        LevelFragment.chooseLevelActivity = this;        LevelFragment.sectionUnlockService = sectionUnlockService;        List<Fragment> fList = new ArrayList<>();        for (int section = 1; section <= 3; section++) {            fList.add(LevelFragment.newInstance(epic, section));        }        return fList;    }    //NOTE: NEED THIS METHOD FOR EVERY ACTIVITY YOU WANT TO HANDLE IN APP BILLING    @Override    protected void onActivityResult(int requestCode, int resultCode, Intent data) {        Log.d(getLocalClassName(), "onActivityResult(" + requestCode + "," + resultCode + "," + data);        // Pass on the activity result to the helper for handling        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {            // not handled, so handle it ourselves (here's where you'd            // perform any handling of activity results not related to in-app            // billing...            super.onActivityResult(requestCode, resultCode, data);        } else {            Log.d(getLocalClassName(), "onActivityResult handled by IABUtil.");        }    }    @Override    protected void onStop() {        super.onStop();    }    @Override    public void onClick(View v) {        SoundEnum.CLICK1.getMediaPlayer().start();        String tag = (String) v.getTag();        String[] splitTag = tag.split("-");        LevelData levelData = new LevelData(epic, Integer.parseInt(splitTag[0]), Integer.parseInt(splitTag[1]));        PopupResult popupResult = new PopupResult(ChooseLevelActivity.this);        levelInfoPopupService.buildPopupWindow(popupResult, levelData, true, false);        TouchStateHolder.setTouchState(GridData.TouchState.DISABLED);    }    public void powerClick(View v) {        SoundEnum.CLICK1.getMediaPlayer().start();        String purchaseSKU = getResources().getResourceEntryName(v.getId());        PowerEnum powerEnum = PowerEnum.getPowerEnumByPurchaseSKU(purchaseSKU);        PopupResult popupResult = new PopupResult(this);        popupResult.setmHelper(mHelper);        iapPopupService.buildPopupWindow(popupResult, powerEnum, inAppPurchaseService);    }    @Override    public void onBackPressed() {        Intent intent = new Intent(App.getContext(), ChooseEpicActivity.class);        intent.putExtra("epic", epic);        startActivity(intent);    }}