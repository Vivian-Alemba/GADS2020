package com.example.gads2020;

import android.net.Uri;
import android.provider.BaseColumns;

@SuppressWarnings("ALL")
public final class NoteKeeperDatabaseContract {


    private NoteKeeperDatabaseContract(){}

    public static final class CourseInfoEntry implements BaseColumns {




        public static final String TABLE_NAME="course_info";
        public static final String COLUMN_COURSE_ID="course_id";
        public static final String COLUMN_COURSE_TITLE="course_title";

        //CREATE INDEX course_info_index1 ON course_info(course_title)
        public static final String INDEX1=TABLE_NAME + "_index ";
        public static final String SQL_CREATE_INDEX1=" CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME +
                "( " + COLUMN_COURSE_TITLE + " )";


        public static final String getQName(String columnName){
            return TABLE_NAME + "." + columnName;
        }

//        CREATE TABLE course_info(course_id,course_title)
        public static final String SQL_CREATE_TABLE=
        "CREATE TABLE " + TABLE_NAME + "( " +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_COURSE_ID +" TEXT UNIQUE NOT NULL, " +
                COLUMN_COURSE_TITLE + " TEXT NOT NULL)";




    }

    public static final class NoteInfoEntry implements BaseColumns{


        public static String TABLE_NAME="note_info";
        public static String COLUMN_NOTE_TITLE="note_title";
        public static String COLUMN_NOTE_TEXT="note_text";
        public static String COLUMN_COURSE_ID="course_id";

        //CREATE INDEX note_info_index1 ON note_info(course_title)
        public static final String INDEX1=TABLE_NAME + "_index ";
        public static final String SQL_CREATE_INDEX1=" CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME +
                "( " + COLUMN_NOTE_TITLE + " )";

        public static final String getQName(String columnName){
            return TABLE_NAME + "." + columnName;
        }

        //CREATE TABLE note_info(course_id,note_title,note_text)
        public static final String SQL_CREATE_TABLE= "CREATE TABLE " + TABLE_NAME + "( " +
                _ID + " INTEGER PRIMARY KEY, "+
                COLUMN_COURSE_ID + " TEXT NOT NULL, " +
                COLUMN_NOTE_TITLE + " TEXT NOT NULL, " +
                COLUMN_NOTE_TEXT +" TEXT)";
    }
}
