package com.example.gads2020;

import android.net.Uri;
import android.provider.BaseColumns;

@SuppressWarnings("ALL")
final class NoteKeeperProviderContract {
    private NoteKeeperProviderContract(){} //making sure our class stays private

    static final String AUTHORITY ="com.example.gads2020.provider";
    //AUTHORITY_URI:content://com.example.gads2020.provider
    private static final Uri AUTHORITY_URI =Uri.parse( " content://" + AUTHORITY );
    protected interface CourseIdColumns {
       public static final String COLUMN_COURSE_ID="course_id";
    }
    protected  interface CourseColumns {

     public static final  String COLUMN_COURSE_TITLE="course_title";
    }
    protected interface NotesColumns {

     public static final  String COLUMN_NOTE_TITLE="note_title";
     public static final   String COLUMN_NOTE_TEXT="note_text";


}
  public static final class  Courses implements BaseColumns, CourseColumns, CourseIdColumns {

           public static final String PATH_COURSES="courses";
           public static final Uri CONTENT_URI=Uri.withAppendedPath( AUTHORITY_URI, PATH_COURSES );
    }

    public static final class Notes implements BaseColumns, NotesColumns, CourseIdColumns, CourseColumns {

              public  static final String PATH_NOTES="notes";
              public static final Uri CONTENT_URI=Uri.withAppendedPath( AUTHORITY_URI, PATH_NOTES);
              public static final String PATH_EXPANDED="notes_expanded";
              public static final Uri CONTENT_EXPANDED_URI=Uri.withAppendedPath( AUTHORITY_URI,PATH_EXPANDED );
    }
}
