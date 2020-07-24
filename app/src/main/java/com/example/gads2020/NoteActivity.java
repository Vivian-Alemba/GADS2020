package com.example.gads2020;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;


import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import static com.example.gads2020.NoteKeeperDatabaseContract.*;
import static com.example.gads2020.NoteKeeperDatabaseContract.NoteInfoEntry;
import static com.example.gads2020.NoteKeeperProviderContract.*;

@SuppressWarnings("ALL")
public class NoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_NOTES = 0;
    public static final int LOADER_COURSES=1;
    private final String TAG=getClass().getSimpleName();
    public static final String NOTE_ID ="NOTE_POSITION";
    public static final int ID_NOT_SET = -1;
    private NoteInfo mNote =new NoteInfo(DataManager.getInstance().getCourses().get(0), "", "");

    private Spinner mSpinner;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private Button btnClear;
    private boolean mIsNewNote;
    private int mNoteId;
    private boolean mIsCancelling;

    private NoteActivityViewModel mViewModel;
    private NoteKeeperOpenHelper mNoteKeeperOpenHelper;
    private Cursor mNoteCursor;
    private int mCourseidPos;
    private int mNoteTitlePos;
    private int mNoteTextPos;


//    public static final String ORIGINAL_NOTE_COURSE_ID = "ORIGINAL_NOTE_COURSE_ID";
//    public static final String ORIGINAL_NOTE_TITLE = "ORIGINAL_NOTE_TITLE";
//    public static final String ORIGINAL_NOTE_TEXT = "ORIGINAL_NOTE_TEXT";

    private String mOriginalNoteCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;
    private SimpleCursorAdapter mCursorAdapter;
    private boolean mCoursesQueryFinished;
    private boolean mNotesQueryFinished;

    @Override
    protected void onDestroy() {
        mNoteKeeperOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_note );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );



        mNoteKeeperOpenHelper = new NoteKeeperOpenHelper( this );

        //Reference to getting viewModelProvider
       ViewModelProvider viewModelProvider=new ViewModelProvider( getViewModelStore(),
               ViewModelProvider.AndroidViewModelFactory.getInstance( getApplication() ) );
        mViewModel=viewModelProvider.get( NoteActivityViewModel.class );


//        if(mViewModel.mIsNewlyCreated && savedInstanceState != null)
//            mViewModel.restoreState( savedInstanceState );


//        mViewModel.mIsNewlyCreated=false;

        mSpinner=findViewById( R.id.spinner_show );




      //List<CourseInfo> courses=DataManager.getInstance().getCourses();
//        ArrayAdapter<CourseInfo> adapter=new ArrayAdapter<>( this,android.R.layout.simple_spinner_item,courses );
//        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        //working with simpleCursorAdapter to populate course selection spinner from the list of courses contained in the cursor
       // String[]from={CourseInfoEntry.COLUMN_COURSE_TITLE};

        mCursorAdapter = new SimpleCursorAdapter( this,android.R.layout.simple_spinner_item,null,
                new String[]{CourseInfoEntry.COLUMN_COURSE_TITLE},new int[]{android.R.id.text1},0 );
        mCursorAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        mSpinner.setAdapter( mCursorAdapter );

   getLoaderManager().initLoader(LOADER_COURSES,null,this  );
         // loadCourseData();



         readDisplayValues();
        if(savedInstanceState == null){
           saveOriginalValues();
        }else {
            restoreOriginalValues(savedInstanceState);
        }

        mTextNoteTitle =findViewById( R.id.edit_notes );
        mTextNoteText =findViewById( R.id.edit_content );




        if(!mIsNewNote)
            //displayNote();
            //loadNoteData();
           getLoaderManager().initLoader( LOADER_NOTES,null,  this );

        Log.d(TAG,"onCreate");
    }

    //getting cursor to load our course from the database
    private void loadCourseData() {
        SQLiteDatabase db=mNoteKeeperOpenHelper.getReadableDatabase();
        String[] courseColumn={
                CourseInfoEntry.COLUMN_COURSE_TITLE,
                CourseInfoEntry.COLUMN_COURSE_ID,
                CourseInfoEntry._ID
        };
        Cursor cursor=db.query( CourseInfoEntry.TABLE_NAME,courseColumn,null,null,null,null,
                CourseInfoEntry.COLUMN_COURSE_TITLE );
        mCursorAdapter.changeCursor( cursor );

    }

    private void restoreOriginalValues(Bundle savedInstanceState) {
        mOriginalNoteCourseId=savedInstanceState.getString(mViewModel.ORIGINAL_NOTE_COURSE_ID );
        mOriginalNoteTitle=savedInstanceState.getString( mViewModel.ORIGINAL_NOTE_TITLE);
        mOriginalNoteText=savedInstanceState.getString( mViewModel.ORIGINAL_NOTE_TEXT );

    }

    private void loadNoteData() {
        SQLiteDatabase db=mNoteKeeperOpenHelper.getReadableDatabase();

        //implementing selection criteria
        String courseId="android_intents";
        String titleStart="dynamic";

        String selection= NoteInfoEntry._ID + " = ?";
              //  " AND " + NoteInfoEntry.COLUMN_NOTE_TITLE + " LIKE ?";

        //String[] selectionArgs={courseId,titleStart + "%"};
        String[]selectionArgs={Integer.toString(mNoteId  )};

        String[] noteColumn={
                NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry.COLUMN_NOTE_TEXT,
                NoteInfoEntry.COLUMN_COURSE_ID
        };

        mNoteCursor = db.query( NoteInfoEntry.TABLE_NAME,noteColumn,selection,selectionArgs,null,null,null );
        mCourseidPos = mNoteCursor.getColumnIndex( NoteInfoEntry.COLUMN_COURSE_ID );
        mNoteTitlePos = mNoteCursor.getColumnIndex( NoteInfoEntry.COLUMN_NOTE_TITLE );
        mNoteTextPos = mNoteCursor.getColumnIndex( NoteInfoEntry.COLUMN_NOTE_TEXT );

        mNoteCursor.moveToNext();
        displayNote();


    }


    private void saveOriginalValues() {
        if(mIsNewNote)
            return;
      mOriginalNoteCourseId = mNote.getCourse().getCourseId();
      mOriginalNoteTitle= mNote.getTitle();
      mOriginalNoteText  = mNote.getText();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling) {
            Log.i(TAG,"cancelling a note at position"+ mNoteId );
            if (mIsNewNote) {
//                DataManager.getInstance().removeNote( mNoteId );
                deleteNoteFromDatabase();
            }else {
                storePreviousNoteValues();
            }

        }else{
                saveNote();
            }
        Log.d(TAG,"onPause");
    }

    private void deleteNoteFromDatabase() {
        //selection criteria
      final  String selection=NoteInfoEntry._ID + "=?";
       final String []selectionArgs={Integer.toString( mNoteId )};

        AsyncTask task=new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                //database connection
                SQLiteDatabase db=mNoteKeeperOpenHelper.getWritableDatabase();
                db.delete( NoteInfoEntry.TABLE_NAME,selection,selectionArgs );
                return null;
            }
        };
        task.execute(  );



    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState( outState );
        //verify the bundle being passed is not null
     if(outState !=null)
         mViewModel.saveState(outState);
        outState.putString( mViewModel.ORIGINAL_NOTE_COURSE_ID,mOriginalNoteCourseId );
        outState.putString( mViewModel.ORIGINAL_NOTE_TITLE,mOriginalNoteTitle );
        outState.putString( mViewModel.ORIGINAL_NOTE_TEXT,mOriginalNoteText );
    }

    private void storePreviousNoteValues() {
        CourseInfo course=DataManager.getInstance().getCourse(mOriginalNoteCourseId );
        mNote.setCourse( course );
        mNote.setTitle( mOriginalNoteTitle );
        mNote.setText( mOriginalNoteText);
    }

    private void saveNote() {
        //mNote.setCourse( (CourseInfo) mSpinner.getSelectedItem() );
//        mNote.setTitle( mTextNoteTitle.getText().toString() );
//        mNote.setText( mTextNoteText.getText().toString() );
        String courseId=selectedCourseId();
        String noteTitle=mTextNoteTitle.getText().toString();
        String noteText=mTextNoteText.getText().toString();
        saveNoteToDatabase( courseId,noteTitle,noteText );


    }

    private String selectedCourseId() {
        int selectedPosition=mSpinner.getSelectedItemPosition();
        Cursor cursor=mCursorAdapter.getCursor();
        cursor.moveToPosition( selectedPosition );

        int courseIdPos=cursor.getColumnIndex( CourseInfoEntry.COLUMN_COURSE_ID );
        String courseId=cursor.getString( courseIdPos );


        return courseId;
    }

    private void saveNoteToDatabase(String courseId,String noteTitle,String noteText){
        //choose the criteria of selection where the id=?
        String selection=NoteInfoEntry._ID + "=?";
        String[]selectionArgs={Integer.toString( mNoteId )};



        //identifies the columns in the values
        ContentValues values=new ContentValues( );
        values.put( NoteInfoEntry.COLUMN_COURSE_ID,courseId );
        values.put( NoteInfoEntry.COLUMN_NOTE_TITLE,noteTitle );
        values.put( NoteInfoEntry.COLUMN_NOTE_TEXT,noteText );

        //connection to the database
        SQLiteDatabase db=mNoteKeeperOpenHelper.getWritableDatabase();
        db.update( NoteInfoEntry.TABLE_NAME,values,selection,selectionArgs );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.note,menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemClicked=item.getItemId();
        if(itemClicked==R.id.action_send_email){
            sendEmail();
        }else if(itemClicked==R.id.action_cancel){
            mIsCancelling = true;
            finish();

        }else if(itemClicked==R.id.action_next){
            moveNext();
        }

        return super.onOptionsItemSelected( item );
    }
    //implementing the next disable menu item


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem( R.id.action_next );
        int lastNoteIndex=DataManager.getInstance().getNotes().size()-1;
        item.setEnabled( mNoteId <lastNoteIndex );
        return super.onPrepareOptionsMenu( menu );
    }

    private void moveNext() {
        saveNote();
        ++mNoteId;
       mNote=DataManager.getInstance().getNotes().get( mNoteId );

       saveOriginalValues();
       displayNote();
       invalidateOptionsMenu();

    }

    private void sendEmail() {
        CourseInfo courseInfo=(CourseInfo)mSpinner.getSelectedItem();
        String subject= mTextNoteText.getText().toString();
        String text="Check out what i learned in Teams mTextNoteTitle \n\n" + courseInfo.getTitle() + "\n\n" + mTextNoteText.getText();
        Intent intent=new Intent( Intent.ACTION_SEND );
        intent.setType( "message/rfc2822" );
        intent.putExtra( Intent.EXTRA_SUBJECT,subject );
        intent.putExtra( Intent.EXTRA_TEXT,text );
        startActivity( intent );

    }

    private void displayNote() {
//        reference to our cursor
        String courseId=mNoteCursor.getString( mCourseidPos );
        String noteTitle=mNoteCursor.getString( mNoteTitlePos );
        String noteText=mNoteCursor.getString( mNoteTextPos );


//        List<CourseInfo>courseInfoList=DataManager.getInstance().getCourses();
//        CourseInfo course=DataManager.getInstance().getCourse(courseId);

        int courseIndex=getIndexOfCourseId(courseId);
        mSpinner.setSelection( courseIndex );
        mTextNoteTitle.setText( noteTitle );
        mTextNoteText.setText( noteText);


    }

    private int getIndexOfCourseId(String courseId) {
        Cursor cursor=mCursorAdapter.getCursor();
        int courseIdPos=cursor.getColumnIndex( CourseInfoEntry.COLUMN_COURSE_ID );
        int courseRowIndex=0;

        boolean more=cursor.moveToFirst();
        while(more){
            String cursorCourseId=cursor.getString( courseIdPos );
            if(courseId.equals( (cursorCourseId) ))
                break;
            courseRowIndex++;
            more=cursor.moveToNext();
        }
       return courseRowIndex;
    }

    private void readDisplayValues() {
        Intent intent = getIntent();
//        mNoteInfo= intent.getParcelableExtra( NOTE_POSITION );
        mNoteId = intent.getIntExtra( NOTE_ID, ID_NOT_SET );
        mIsNewNote = mNoteId == ID_NOT_SET;
        if (mIsNewNote)
            createNewNote();
//        else{
//        mNote=DataManager.getInstance().getNotes().get( mNoteId );
//    }
        Log.i( TAG,"mNoteId" + mNoteId );




    }


    private void createNewNote() {
//        DataManager dm=DataManager.getInstance();
//        mNoteId = dm.createNewNote();
      // mNote=dm.getNotes().get( mNoteId );
        //getting columns from the user
        ContentValues values=new ContentValues(  );
        values.put(NoteInfoEntry.COLUMN_COURSE_ID,"");
        values.put(NoteInfoEntry.COLUMN_NOTE_TITLE,"");
        values.put( NoteInfoEntry.COLUMN_NOTE_TEXT,"" );

        //database connection
        SQLiteDatabase db=mNoteKeeperOpenHelper.getWritableDatabase();
       mNoteId=(int) db.insert( NoteInfoEntry.TABLE_NAME,null,values );

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader=null;
        if(id==LOADER_NOTES)
            cursorLoader=createLoaderNotes();
        else if(id==LOADER_COURSES)
            cursorLoader = createLoaderCourses();

        return cursorLoader;
    }

    private CursorLoader createLoaderCourses() {

       mCoursesQueryFinished = false;
       Uri uri= Courses.CONTENT_URI;
        String[] courseColumn={
                Courses.COLUMN_COURSE_TITLE,
                Courses.COLUMN_COURSE_ID,
                Courses._ID
        };
        return new CursorLoader( this,uri,courseColumn,null,null,Courses.COLUMN_COURSE_TITLE );

//        return new CursorLoader( this ){
//            @Override
//            public Cursor loadInBackground() {
//
//                SQLiteDatabase db=mNoteKeeperOpenHelper.getReadableDatabase();
//
//                String[] courseColumn={
//                        CourseInfoEntry.COLUMN_COURSE_TITLE,
//                        CourseInfoEntry.COLUMN_COURSE_ID,
//                        CourseInfoEntry._ID
//                };
//                return  db.query( CourseInfoEntry.TABLE_NAME,courseColumn,null,null,null,null,
//                        CourseInfoEntry.COLUMN_COURSE_TITLE );
//            }
//        };
    }

    private CursorLoader createLoaderNotes() {
        mNotesQueryFinished = false;
        return new CursorLoader( this ){
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db=mNoteKeeperOpenHelper.getReadableDatabase();

                //implementing selection criteria
                String courseId="android_intents";
                String titleStart="dynamic";

                String selection= NoteInfoEntry._ID + " = ?";
                //  " AND " + NoteInfoEntry.COLUMN_NOTE_TITLE + " LIKE ?";

                //String[] selectionArgs={courseId,titleStart + "%"};
                String[]selectionArgs={Integer.toString(mNoteId  )};

                String[] noteColumn={
                        NoteInfoEntry.COLUMN_NOTE_TITLE,
                        NoteInfoEntry.COLUMN_NOTE_TEXT,
                        NoteInfoEntry.COLUMN_COURSE_ID
                };

                return db.query( NoteInfoEntry.TABLE_NAME,noteColumn,selection,selectionArgs,null,null,null );
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId()==LOADER_NOTES)
            loadFinishedNotes(data);
        else if(loader.getId()==LOADER_COURSES){
            mCursorAdapter.changeCursor( data);
            mCoursesQueryFinished=true;
            displayNoteWhenQueryFinished();

        }

    }



    private void loadFinishedNotes(Cursor cursor) {
        mNoteCursor=cursor;
        mCourseidPos = mNoteCursor.getColumnIndex( NoteInfoEntry.COLUMN_COURSE_ID );
        mNoteTitlePos = mNoteCursor.getColumnIndex( NoteInfoEntry.COLUMN_NOTE_TITLE );
        mNoteTextPos = mNoteCursor.getColumnIndex( NoteInfoEntry.COLUMN_NOTE_TEXT );

        mNoteCursor.moveToNext();
        mNotesQueryFinished=true;
        displayNoteWhenQueryFinished();

    }

    private void displayNoteWhenQueryFinished() {
        if(mNotesQueryFinished && mCoursesQueryFinished)
            displayNote();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_NOTES) {
            if (mNoteCursor != null)
                mNoteCursor.close();
        } else if (loader.getId() == LOADER_COURSES)
            mCursorAdapter.changeCursor( null );
    }


}
