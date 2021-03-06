package com.example.gads2020;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.example.gads2020.NoteKeeperDatabaseContract.*;
import static com.example.gads2020.NoteKeeperProviderContract.*;

@SuppressWarnings("deprecation")
public class NoteKeeperProvider extends ContentProvider {
    private NoteKeeperOpenHelper mNoteKeeperOpenHelper;

    private static UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH );

    public static final int COURSES = 0;

    public static final int NOTES = 1;

    public static final int NOTES_EXPANDED = 2;

    static {
        sUriMatcher.addURI( AUTHORITY, Courses.PATH_COURSES, COURSES );
        sUriMatcher.addURI( AUTHORITY, Notes.PATH_NOTES, NOTES );
        sUriMatcher.addURI( AUTHORITY,Notes.PATH_EXPANDED , NOTES_EXPANDED );
    }


    public NoteKeeperProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException( "Not yet implemented" );
    }

    @Override
    public String getType(Uri uri) {

        throw new UnsupportedOperationException( "Not yet implemented" );
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        throw new UnsupportedOperationException( "Not yet implemented" );
    }

    @Override
    public boolean onCreate() {

        mNoteKeeperOpenHelper=new NoteKeeperOpenHelper( getContext() );
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor=null;
        SQLiteDatabase db=mNoteKeeperOpenHelper.getReadableDatabase();


        int uriMatch=sUriMatcher.match( uri );
        switch (uriMatch){
            case COURSES:
                cursor = db.query( CourseInfoEntry.TABLE_NAME,projection,selection,selectionArgs,
                        null,null,sortOrder );
                break;
            case NOTES:
                cursor = db.query( NoteInfoEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,
                        sortOrder);
                break;
            case NOTES_EXPANDED:
                cursor = noteExpandedQuery(db,projection,selection,selectionArgs,sortOrder);

               break;

        }

        return cursor;
    }

    private Cursor noteExpandedQuery(SQLiteDatabase db, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        String[]columns =new String[projection.length];
        for(int i=0; i < projection.length; i++){
            columns[i]= projection[i].equals( BaseColumns._ID ) ||
                    projection[i].equals( CourseIdColumns.COLUMN_COURSE_ID ) ?
                    NoteInfoEntry.getQName( projection[i]) : projection [i];
        }

        String tablesWithJoin= NoteInfoEntry.TABLE_NAME + " JOIN "+
                CourseInfoEntry.TABLE_NAME + " ON " +
                NoteInfoEntry.getQName( NoteInfoEntry.COLUMN_COURSE_ID ) + " = "+
                CourseInfoEntry.getQName( CourseInfoEntry.COLUMN_COURSE_ID );

        return db.query( tablesWithJoin,columns,selection,selectionArgs,null,null,sortOrder );


    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException( "Not yet implemented" );
    }
}
