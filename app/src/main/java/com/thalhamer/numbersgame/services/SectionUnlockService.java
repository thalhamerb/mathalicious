package com.thalhamer.numbersgame.services;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.Lists;
import com.thalhamer.numbersgame.Factory.App;
import com.thalhamer.numbersgame.R;
import com.thalhamer.numbersgame.database.DatabaseHelper;
import com.thalhamer.numbersgame.domain.LevelData;
import com.thalhamer.numbersgame.domain.Power;
import com.thalhamer.numbersgame.domain.SectionUnlock;
import com.thalhamer.numbersgame.enums.PowerEnum;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * blah
 * <p/>
 * Created by Brian on 12/20/2015.
 */
@Singleton
public class SectionUnlockService {

    DatabaseHelper databaseHelper = new DatabaseHelper(App.getContext());

    @Inject
    SavedDataService savedDataService;

    @Inject
    public SectionUnlockService() {
    }

    private SectionUnlock getNextSectionGameUnlock() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "select * from section_unlock where unlocked = 0 order by stars asc";
        Cursor c = db.rawQuery(query, null);
        SectionUnlock sectionUnlock = mapFirstResultToSectionUnlockObject(c);
        c.close();
        db.close();
        return sectionUnlock;
    }

    private SectionUnlock mapFirstResultToSectionUnlockObject(Cursor c) {
        if (c.moveToFirst()) {
            SectionUnlock sectionUnlock = new SectionUnlock();
            sectionUnlock.setEpic(c.getInt(c.getColumnIndex("epic")));
            sectionUnlock.setSection(c.getInt(c.getColumnIndex("section")));
            sectionUnlock.setNumOfStars(c.getInt(c.getColumnIndex("stars")));
            sectionUnlock.setUnlocked(c.getInt(c.getColumnIndex("unlocked")));

            List<Power> powers = Lists.newArrayList();
            String powersAsString = c.getString(c.getColumnIndex("powers"));
            if (powersAsString != null) {
                String[] powersStringArray = powersAsString.split(",");
                for (String powerAsString : powersStringArray) {
                    String[] powerValues = powerAsString.split("-");
                    PowerEnum powerEnum = PowerEnum.valueOf(powerValues[0]);
                    int quantity = Integer.parseInt(powerValues[1]);
                    powers.add(new Power(powerEnum, quantity));
                }
                sectionUnlock.setPowers(powers);
            }

            return sectionUnlock;
        }

        return null;
    }

    public SectionUnlock getSectionUnlock(Integer epic, Integer section) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "select * from section_unlock where epic=" + epic + " and section=" + section;
        Cursor c = db.rawQuery(query, null);
        SectionUnlock sectionUnlock = mapFirstResultToSectionUnlockObject(c);
        c.close();
        db.close();
        return sectionUnlock;
    }

    private boolean updateSectionToUnlocked(Integer epic, Integer section) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put("unlocked", 1);
        int numOfRowsUpdated = db.update("section_unlock", args, "epic=? and section=?", new String[]{epic.toString(), section.toString()});
        db.close();
        return numOfRowsUpdated == 1;
    }

    public SectionUnlock checkAndPerformSectionUnlock() {
        SectionUnlock sectionUnlock = getNextSectionGameUnlock();
        if (sectionUnlock != null) {
            Integer numOfStarsGoal = sectionUnlock.getNumOfStars();
            Integer numOfStarsHave = savedDataService.getIntKeyValue(App.getContext().getString(R.string.total_stars_key), 0);
            if (numOfStarsHave >= numOfStarsGoal) {
                //save new power quantities
                for (Power power : sectionUnlock.getPowers()) {
                    String powerAsString = power.getPowerEnum().toString();
                    Integer powerQuantity = savedDataService.getIntKeyValue(powerAsString, 0);
                    Integer quantityToAdd = power.getQuantity();
                    savedDataService.saveKey(powerAsString, powerQuantity + quantityToAdd);
                }

                updateSectionToUnlocked(sectionUnlock.getEpic(), sectionUnlock.getSection());
                return sectionUnlock;
            }
        }

        return null;
    }

    public boolean isSectionUnlocked(int epic, int section) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "select * from section_unlock where epic=" + epic + " and section=" + section;
        Cursor c = db.rawQuery(query, null);
        SectionUnlock sectionUnlock = mapFirstResultToSectionUnlockObject(c);
        c.close();
        db.close();
        return sectionUnlock.getUnlocked() == 1;
    }

    public boolean isSectionUnlocked(LevelData levelData) {
        return isSectionUnlocked(levelData.getEpic(), levelData.getSection());
    }
}
