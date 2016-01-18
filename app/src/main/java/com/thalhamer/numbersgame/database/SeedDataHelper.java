package com.thalhamer.numbersgame.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Brian on 12/19/2015.
 */
public class SeedDataHelper {

    public static void performSectionUnlockInserts(SQLiteDatabase db, int epic, int section, int stars, String powers, int unlocked) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(DatabaseContract.SectionUnlock.EPIC, epic);
        insertValues.put(DatabaseContract.SectionUnlock.SECTION, section);
        insertValues.put(DatabaseContract.SectionUnlock.STARS, stars);
        insertValues.put(DatabaseContract.SectionUnlock.POWERS, powers);
        insertValues.put(DatabaseContract.SectionUnlock.UNLOCKED, unlocked);
        db.insert(DatabaseContract.SectionUnlock.TABLE_NAME, null, insertValues);
    }


}
