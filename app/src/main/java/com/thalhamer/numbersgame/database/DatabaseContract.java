package com.thalhamer.numbersgame.database;import android.provider.BaseColumns;/** * Created by Brian on 7/2/2015. */public final class DatabaseContract {    public DatabaseContract() {    }    public static abstract class CharacterMessages implements BaseColumns {        public static final String TABLE_NAME = "CHARACTER_MESSAGES";        public static final String COLUMN_NAME_CHARACTER = "CHARACTER";        public static final String COLUMN_NAME_MESSAGE_TYPE = "MESSAGE_TYPE";        public static final String COLUMN_NAME_MESSAGE = "MESSAGE";        public static final String COLUMN_NAME_MESSAGE_LOCATION = "MESSAGE_LOCATION";    }}