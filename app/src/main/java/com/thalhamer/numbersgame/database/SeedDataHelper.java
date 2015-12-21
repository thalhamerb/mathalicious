package com.thalhamer.numbersgame.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Brian on 12/19/2015.
 */
public class SeedDataHelper {

    public static void performGameUnlocksInserts(SQLiteDatabase db, int epic, int section, int stars, String powers, int unlocked) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(DatabaseContract.GameUnlocks.EPIC, epic);
        insertValues.put(DatabaseContract.GameUnlocks.SECTION, section);
        insertValues.put(DatabaseContract.GameUnlocks.STARS, stars);
        insertValues.put(DatabaseContract.GameUnlocks.POWERS, powers);
        insertValues.put(DatabaseContract.GameUnlocks.UNLOCKED, unlocked);
        db.insert(DatabaseContract.GameUnlocks.TABLE_NAME, null, insertValues);
    }


}
