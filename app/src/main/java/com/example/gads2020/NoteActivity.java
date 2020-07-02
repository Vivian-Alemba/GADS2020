package com.example.gads2020;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import static com.example.gads2020.NoteKeeperDatabaseContract.NoteInfoEntry;

@SuppressWarnings("ALL")
public class NoteActivity extends AppCompatActivity {
    private final String TAG=getClass().getSimpleName();
    public static final String NOTE_ID ="NOTE_POSITION";
    public static final int ID_NOT_SET = -1;
    private NoteInfo mNoteInfo;

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


    public static final String ORIGINAL_NOTE_COURSE_ID = "ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "ORIGINAL_NOTE_TEXT";

    private String mOriginalNoteCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;

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
       // ViewModelProvider viewModelProvider=new ViewModelProvider( getViewModelStore(),ViewModelProvider.AndroidViewModelFactory.getInstance( getApplication() ) );
       // mViewModel=viewModelProvider.get( NoteActivityViewModel.class );


        //if(mViewModel.mIsNewlyCreated && savedInstanceState != null)
           // mViewModel.restoreState( savedInstanceState );


        //mViewModel.mIsNewlyCreated=false;

        mSpinner=findViewById( R.id.spinner_show );
        mTextNoteTitle =findViewById( R.id.edit_notes );
        mTextNoteText =findViewById( R.id.edit_content );



        List<CourseInfo> courses=DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapter=new ArrayAdapter<>( this,android.R.layout.simple_spinner_item,courses );
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        mSpinner.setAdapter( adapter );




        intentDisplayValues();

        if(savedInstanceState == null){
           saveOriginalValues();
        }else {
            restoreOriginalValues(savedInstanceState);
        }




        if(!mIsNewNote)
            //displayNote();
            loadNoteData();

        Log.d(TAG,"onCreate");


    }

    private void restoreOriginalValues(Bundle savedInstanceState) {
        mOriginalNoteCourseId=savedInstanceState.getString( ORIGINAL_NOTE_COURSE_ID );
        mOriginalNoteTitle=savedInstanceState.getString( ORIGINAL_NOTE_TITLE );
        mOriginalNoteText=savedInstanceState.getString( ORIGINAL_NOTE_TEXT );

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
        mOriginalNoteCourseId = mNoteInfo.getCourse().getCourseId();
        mOriginalNoteTitle = mNoteInfo.getTitle();
        mOriginalNoteText = mNoteInfo.getText();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling) {
            Log.i(TAG,"cancelling a note at position"+ mNoteId );
            if (mIsNewNote) {
                DataManager.getInstance().removeNote( mNoteId );
            }else {
                storePreviousNoteValues();
            }
        }else{
                saveNote();
            }
        Log.d(TAG,"onPause");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState( outState );
        //verify the bundle being passed is not null
     if(outState !=null)
  mViewModel.saveState(outState);
        outState.putString( ORIGINAL_NOTE_COURSE_ID,mOriginalNoteCourseId );
        outState.putString( ORIGINAL_NOTE_TITLE,mOriginalNoteTitle );
        outState.putString( ORIGINAL_NOTE_TEXT,mOriginalNoteText );
    }

    private void storePreviousNoteValues() {
        CourseInfo course=DataManager.getInstance().getCourse(mOriginalNoteCourseId );
        mNoteInfo.setCourse( course );
        mNoteInfo.setTitle( mOriginalNoteTitle );
        mNoteInfo.setText( mOriginalNoteText);
    }

    private void saveNote() {
        mNoteInfo.setCourse( (CourseInfo)mSpinner.getSelectedItem() );
        mNoteInfo.setTitle( mTextNoteTitle.getText().toString() );
        mNoteInfo.setText( mTextNoteText.getText().toString() );

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
       mNoteInfo=DataManager.getInstance().getNotes().get( mNoteId );

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


        List<CourseInfo>courseInfoList=DataManager.getInstance().getCourses();
        CourseInfo course=DataManager.getInstance().getCourse(courseId);

        int courseIndex=courseInfoList.indexOf(course);
        mSpinner.setSelection( courseIndex );


        mTextNoteTitle.setText( noteTitle );
        mTextNoteText.setText( noteText);


    }

    private void intentDisplayValues() {
        Intent intent=getIntent();
//        mNoteInfo= intent.getParcelableExtra( NOTE_POSITION );
        mNoteId = intent.getIntExtra( NOTE_ID, ID_NOT_SET );

        mIsNewNote = mNoteId == ID_NOT_SET;
        if(mIsNewNote) {

            createNewNote();
        }
        //mNoteInfo=DataManager.getInstance().getNotes().get( mNoteId );
        Log.i( TAG,"mNoteId" + mNoteId );




    }

    private void createNewNote() {
        DataManager dm=DataManager.getInstance();
        mNoteId = dm.createNewNote();
        mNoteInfo=dm.getNotes().get( mNoteId );

    }

}
