package com.thalhamer.numbersgame.database.version;

import android.database.sqlite.SQLiteDatabase;

import com.thalhamer.numbersgame.database.SeedDataHelper;

/**
 * Created by Brian on 12/19/2015.
 */
public class Version1 {

    public static void executeUpdate(SQLiteDatabase db) {
        db.execSQL("create table score_result (_id integer primary key, level text, score integer, " +
                "stars_earned integer, sent_for_analysis integer, player_name text)");

        db.execSQL("create table section_unlock (_id integer primary key, epic integer, section integer, " +
                "stars integer, powers text, unlocked integer default 0)");
        sectionUnlockInserts(db);
    }

    private static void sectionUnlockInserts(SQLiteDatabase db) {
        SeedDataHelper.performSectionUnlockInserts(db, 1, 1, 0, null, 1);
        SeedDataHelper.performSectionUnlockInserts(db, 1, 2, 18, "CLEAR_ONE_NUM-3", 0);
        SeedDataHelper.performSectionUnlockInserts(db, 1, 3, 40, "CLEAR_ONE_NUM-1,CLEAR_ALL_NUM-3", 0);
        SeedDataHelper.performSectionUnlockInserts(db, 2, 1, 62, "CLEAR_ONE_NUM-1,CLEAR_ALL_NUM-3", 0);
        SeedDataHelper.performSectionUnlockInserts(db, 2, 2, 88, "CLEAR_ONE_NUM-1,CLEAR_ALL_NUM-3", 0);
        SeedDataHelper.performSectionUnlockInserts(db, 2, 3, 106, "CLEAR_ONE_NUM-1,CLEAR_ALL_NUM-3", 0);
        SeedDataHelper.performSectionUnlockInserts(db, 3, 1, 130, "CLEAR_ONE_NUM-1,CLEAR_ALL_NUM-3", 0);
        SeedDataHelper.performSectionUnlockInserts(db, 3, 2, 148, "CLEAR_ONE_NUM-1,CLEAR_ALL_NUM-3", 0);
        SeedDataHelper.performSectionUnlockInserts(db, 3, 3, 172, "CLEAR_ONE_NUM-1,CLEAR_ALL_NUM-3", 0);
    }


}
