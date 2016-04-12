package com.thalhamer.numbersgame.Activity;import android.content.Intent;import android.database.sqlite.SQLiteDatabase;import android.media.AudioManager;import android.os.Bundle;import android.support.v4.app.Fragment;import android.support.v4.app.FragmentActivity;import android.support.v4.view.ViewPager;import android.util.Log;import android.view.View;import android.view.Window;import android.view.WindowManager;import android.widget.TextView;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.Fragment.EpicFragment;import com.thalhamer.numbersgame.R;import com.thalhamer.numbersgame.dagger.component.ChooseEpicActivityComponent;import com.thalhamer.numbersgame.dagger.component.DaggerChooseEpicActivityComponent;import com.thalhamer.numbersgame.dagger.module.AppModule;import com.thalhamer.numbersgame.database.DatabaseHelper;import com.thalhamer.numbersgame.domain.ViewPagerResult;import com.thalhamer.numbersgame.enums.Character;import com.thalhamer.numbersgame.enums.PowerEnum;import com.thalhamer.numbersgame.services.AdvertisementService;import com.thalhamer.numbersgame.services.SavedDataService;import com.thalhamer.numbersgame.services.SectionUnlockService;import com.thalhamer.numbersgame.viewhelper.ActivityHelper;import java.util.ArrayList;import java.util.List;import javax.inject.Inject;/** * choose epic activity * <p/> * Created by Brian on 5/5/2015. */public class ChooseEpicActivity extends FragmentActivity implements View.OnClickListener {    @Inject    SavedDataService savedDataService;    @Inject    ActivityHelper activityHelper;    @Inject    AdvertisementService advertisementService;    @Inject    SectionUnlockService sectionUnlockService;    @Override    public void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        ChooseEpicActivityComponent chooseEpicActivityComponent =                DaggerChooseEpicActivityComponent.builder().appModule(new AppModule()).build();        chooseEpicActivityComponent.injectEpicActivity(this);//        createMockData();        setVolumeControlStream(AudioManager.STREAM_MUSIC);        requestWindowFeature(Window.FEATURE_NO_TITLE);        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);        setContentView(R.layout.choose_epic_activity);        TextView textView = (TextView) findViewById(R.id.stars_epic);        int totalStarCount = savedDataService.getIntKeyValue(getString(R.string.total_stars_key), 0);        textView.setText(String.format("Total: %d", totalStarCount));        final List<Fragment> fragments = getFragments();        final ViewPager pager = (ViewPager) findViewById(R.id.epicViewPager);        setViewPager(fragments, pager);        Intent intent = getIntent();        if (intent.hasExtra("epic")) {            pager.setCurrentItem(intent.getIntExtra("epic", 1) - 1);        }        advertisementService.requestNewInterstitialAd();    }    private void createMockData() {        DatabaseHelper dtb = new DatabaseHelper(App.getContext());        SQLiteDatabase db = dtb.getWritableDatabase();        dtb.updateDatabaseForTesting(db);//        db.execSQL("drop table score_result");//        db.execSQL("create table score_result (_id integer primary key, level text, score integer, " +//                "stars_earned integer, sent_for_analysis integer, player_name text)");        savedDataService.saveKey(PowerEnum.PAUSE_TIME.toString(), 2);//        savedDataService.saveKey(PowerEnum.CLEAR_ONE_ENUM.toString(), 6);//        savedDataService.saveKey(PowerEnum.CLEAR_ALL_NUM.toString(), 0);//        savedDataService.removeKey(CalcType.ADD.toString());        for (int k = 1; k <= 3; k++) {            for (int j = 1; j <= 3; j++) {                for (int i = 1; i <= 9; i++) {                    savedDataService.saveKey(savedDataService.constructLevelName(k, j, i), 0);                }            }        }//        savedDataService.saveKey(getString(R.string.total_stars_key), 5);    }    private void setViewPager(List<Fragment> fList, ViewPager pager) {        final TextView leftArrow = (TextView) findViewById(R.id.leftArrow);        final TextView rightArrow = (TextView) findViewById(R.id.rightArrow);        ViewPagerResult viewPagerResult = new ViewPagerResult();        viewPagerResult.setActivity(this);        viewPagerResult.setFragments(fList);        viewPagerResult.setPager(pager);        viewPagerResult.setLeftArrow(leftArrow);        viewPagerResult.setRightArrow(rightArrow);        activityHelper.setViewPager(viewPagerResult);    }    private List<Fragment> getFragments() {        EpicFragment.chooseEpicActivity = this;        EpicFragment.savedDataService = savedDataService;        EpicFragment.sectionUnlockService = sectionUnlockService;        List<Fragment> fList = new ArrayList<>();        fList.add(EpicFragment.newInstance(Character.EINSTEIN));        fList.add(EpicFragment.newInstance(Character.NEWTON));//        fList.add(EpicFragment.newInstance(Character.GALILEO));        return fList;    }    @Override    public void onClick(View v) {        int epic = Integer.parseInt((String) v.getTag(R.string.chosen_epic));        Character character = Character.getCharacterFromEpic(epic);        character.getMediaPlayer().start();        Intent intent = new Intent(this, ChooseLevelActivity.class);        intent.putExtra(getString(R.string.epic), epic);        startActivity(intent);    }    @Override    public void onBackPressed() {        Intent intent = new Intent(App.getContext(), OpeningScreenActivity.class);        startActivity(intent);    }    @Override    protected void onDestroy() {        Log.d("ChooseLevelActivity", "Activity is being destroyed");        super.onDestroy();    }}