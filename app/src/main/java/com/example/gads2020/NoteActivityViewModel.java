package com.example.gads2020;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;

public class NoteActivityViewModel extends ViewModel {
    public static final String ORIGINAL_NOTE_COURSE_ID="ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE="ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT="ORIGINAL_NOTE_TEXT";


    public String mOriginalNotesCourseId;
    public String mOriginalNoteTitleId;
    public String mOriginalNoteTextId;
    public boolean mIsNewlyCreated=true;

    public void saveState(Bundle outState) {
        outState.putString( ORIGINAL_NOTE_COURSE_ID,mOriginalNotesCourseId );
        outState.putString( ORIGINAL_NOTE_TITLE,mOriginalNoteTitleId );
        outState.putString( ORIGINAL_NOTE_TEXT,mOriginalNoteTextId );

    }

    public void restoreState(Bundle inState){
        mOriginalNotesCourseId=inState.getString( ORIGINAL_NOTE_COURSE_ID );
        mOriginalNoteTitleId=inState.getString( ORIGINAL_NOTE_TITLE );
        mOriginalNoteTextId=inState.getString( ORIGINAL_NOTE_TEXT );
    }


}
