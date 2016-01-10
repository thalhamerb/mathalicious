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
        gameUnlocksInserts(db);
    }

    private static void gameUnlocksInserts(SQLiteDatabase db) {
        SeedDataHelper.performGameUnlocksInserts(db, 1, 1, 0, null, 1);
        SeedDataHelper.performGameUnlocksInserts(db, 1, 2, 18, "CLEAR_ONE_ENUM-3", 0);
        SeedDataHelper.performGameUnlocksInserts(db, 1, 3, 40, "CLEAR_ONE_ENUM-1,CLEAR_ALL_NUM-3", 0);
        SeedDataHelper.performGameUnlocksInserts(db, 2, 1, 65, "CLEAR_ONE_ENUM-1,CLEAR_ALL_NUM-3", 0);
        SeedDataHelper.performGameUnlocksInserts(db, 2, 2, 95, "CLEAR_ONE_ENUM-1,CLEAR_ALL_NUM-3", 0);
        SeedDataHelper.performGameUnlocksInserts(db, 2, 3, 120, "CLEAR_ONE_ENUM-1,CLEAR_ALL_NUM-3", 0);
        SeedDataHelper.performGameUnlocksInserts(db, 3, 1, 135, "CLEAR_ONE_ENUM-1,CLEAR_ALL_NUM-3", 0);
        SeedDataHelper.performGameUnlocksInserts(db, 3, 2, 150, "CLEAR_ONE_ENUM-1,CLEAR_ALL_NUM-3", 0);
        SeedDataHelper.performGameUnlocksInserts(db, 3, 3, 190, "CLEAR_ONE_ENUM-1,CLEAR_ALL_NUM-3", 0);
    }


}
