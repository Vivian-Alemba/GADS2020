package com.example.gads2020;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class NoteKeeperOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Notekeeper.db";
    //public static final int DATABASE_VERSION=1;


    NoteKeeperOpenHelper(@Nullable Context context) {
        super( context, DATABASE_NAME,null, 1 );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( NoteKeeperDatabaseContract.CourseInfoEntry.SQL_CREATE_TABLE );
        db.execSQL( NoteKeeperDatabaseContract.NoteInfoEntry.SQL_CREATE_TABLE );

        DatabaseDataWorker worker=new DatabaseDataWorker( db );
        worker.insertCourses();
        worker.insertSampleNotes();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }
}
