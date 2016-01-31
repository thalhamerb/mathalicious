package com.thalhamer.numbersgame.database.version;

import android.database.sqlite.SQLiteDatabase;

import com.thalhamer.numbersgame.database.SeedDataHelper;

/**
 * version2
 * <p/>
 * Created by Brian on 1/25/2016.
 */
public class Version2 {

    private static void sectionUnlockInserts(SQLiteDatabase db) {
        SeedDataHelper.performSectionUnlockInserts(db, 3, 1, 130, "CLEAR_ONE_NUM-1,CLEAR_ALL_NUM-3", 0);
        SeedDataHelper.performSectionUnlockInserts(db, 3, 2, 148, "CLEAR_ONE_NUM-1,CLEAR_ALL_NUM-3", 0);
        SeedDataHelper.performSectionUnlockInserts(db, 3, 3, 172, "CLEAR_ONE_NUM-1,CLEAR_ALL_NUM-3", 0);
    }

}
