package com.thalhamer.numbersgame.Fragment;import android.os.Bundle;import android.support.v4.app.Fragment;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.GridLayout;import android.widget.ImageButton;import android.widget.ImageView;import android.widget.RelativeLayout;import android.widget.TextView;import com.google.common.collect.Lists;import com.thalhamer.numbersgame.Activity.ChooseLevelActivity;import com.thalhamer.numbersgame.Factory.App;import com.thalhamer.numbersgame.Factory.GameUnlockDataHolder;import com.thalhamer.numbersgame.R;import com.thalhamer.numbersgame.domain.GameUnlock;import com.thalhamer.numbersgame.domain.LevelData;import com.thalhamer.numbersgame.services.SavedDataService;import java.util.ArrayList;import java.util.List;/** * section fragment * <p/> * Created by Brian on 8/30/2015. */public class LevelFragment extends Fragment {    private static SavedDataService savedDataService;    private static ChooseLevelActivity chooseLevelActivity;    public static LevelFragment newInstance(Integer epic, Integer section, SavedDataService savedDataService,                                            ChooseLevelActivity chooseLevelActivity) {        LevelFragment fragment = new LevelFragment();        Bundle bdl = new Bundle();        bdl.putInt("section", section);        bdl.putInt("epic", epic);        fragment.setArguments(bdl);        LevelFragment.savedDataService = savedDataService;        LevelFragment.chooseLevelActivity = chooseLevelActivity;        return fragment;    }    @Override    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {        Bundle bundle = getArguments();        View view = inflater.inflate(R.layout.fragment_choose_level, container, false);        int section = bundle.getInt("section");        TextView title = (TextView) view.findViewById(R.id.chooseLevelTitle);        title.setText("Section " + section);        int epic = bundle.getInt("epic");        String sectionName = savedDataService.constructSectionName(epic, section);        RelativeLayout buttonLayout = (RelativeLayout) view.findViewById(R.id.level_button_layout);        RelativeLayout lockLayout = (RelativeLayout) view.findViewById(R.id.level_lock_layout);        if (savedDataService.containsKey(sectionName)) {            buttonLayout.setVisibility(View.VISIBLE);            lockLayout.setVisibility(View.GONE);            ArrayList<LevelData> levelDataList = getLevelInfoForSection(bundle.getInt("epic"), section);            setLevelLocks(view, levelDataList);            TextView starsTotal = (TextView) view.findViewById(R.id.stars_level);            Integer numOfStars = getTotalNumOfStarsForSection(levelDataList);            Integer totalNumOfStars = levelDataList.size() * 3;            starsTotal.setText("Total: " + numOfStars + "/" + totalNumOfStars);        } else {            buttonLayout.setVisibility(View.GONE);            lockLayout.setVisibility(View.VISIBLE);            TextView numOfStars = (TextView) view.findViewById(R.id.numOfStars);            List<GameUnlock> gameUnlockList = GameUnlockDataHolder.getInstance().getGameUnlockInfo();            for (GameUnlock gameUnlock : gameUnlockList) {                if (sectionName.equals(gameUnlock.getSectionName())) {                    numOfStars.setText(gameUnlock.getNumOfStars().toString());                    break;                }            }        }        return view;    }    private int getTotalNumOfStarsForSection(ArrayList<LevelData> levelDataList) {        int total = 0;        for (LevelData levelData : levelDataList) {            if (levelData.getNumOfStars() != -1) {                total += levelData.getNumOfStars();            }        }        return total;    }    private ArrayList<LevelData> getLevelInfoForSection(Integer epic, Integer section) {        ArrayList<LevelData> levelDataList = Lists.newArrayList();        for (Integer i = 1; i < 10; i++) {            String levelName = savedDataService.constructLevelName(epic, section, i);            if (i == 1 && !savedDataService.containsKey(levelName)) {                savedDataService.saveKey(levelName, 0);            }            LevelData levelData = new LevelData();            levelData.setEpic(epic);            levelData.setSection(section);            levelData.setLevel(i);            levelData.setLevelName(levelName);            Integer numOfStars = savedDataService.getIntKeyValue(levelName, -1);            levelData.setNumOfStars(numOfStars);            levelDataList.add(levelData);        }        return levelDataList;    }    private void setLevelLocks(View view, ArrayList<LevelData> levelDataList) {        for (int i = 0; i < levelDataList.size(); i++) {            LevelData levelData = levelDataList.get(i);            int resID = getResources().getIdentifier("imageButton" + (i + 1), "id", App.getContext().getPackageName());            ImageButton button = (ImageButton) view.findViewById(resID);            if (levelData.getNumOfStars() != -1) {                button.setClickable(true);                button.setTag(levelData.getSection() + "-" + levelData.getLevel());                button.setOnClickListener(chooseLevelActivity);                int starResID = getResources().getIdentifier("starGrid" + (i + 1), "id", App.getContext().getPackageName());                GridLayout starGridLayout = (GridLayout) view.findViewById(starResID);                for (int j = 0; j < 3; j++) {                    if (j >= levelData.getNumOfStars()) {                        starGridLayout.addView(createStarImageView(j, false));                    } else {                        starGridLayout.addView(createStarImageView(j, true));                    }                }            } else {                button.setClickable(false);                button.setBackgroundResource(R.mipmap.lock);            }        }    }    public ImageView createStarImageView(int column, boolean fullStar) {        int starWidth = App.getContext().getResources().getDimensionPixelSize(R.dimen.level_star_width);        ImageView oImageView = new ImageView(App.getContext());        if (fullStar) {            oImageView.setImageResource(R.mipmap.star);        } else {            oImageView.setImageResource(R.mipmap.star_empty_blk);        }        GridLayout.LayoutParams param = new GridLayout.LayoutParams();        param.height = starWidth;        param.width = starWidth;        param.columnSpec = GridLayout.spec(column);        param.rowSpec = GridLayout.spec(0);        oImageView.setLayoutParams(param);        return oImageView;    }}