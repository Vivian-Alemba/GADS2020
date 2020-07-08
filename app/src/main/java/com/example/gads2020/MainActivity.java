package com.example.gads2020;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;



import android.view.MenuItem;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;


import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static com.example.gads2020.NoteKeeperDatabaseContract.*;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_NOTES = 0;
    private NoteRecyclerAdapter mNoteRecyclerAdapter;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    private RecyclerView mRecyclesitem;
    private LinearLayoutManager mLayoutManager;
    private CourseRecyclerAdapter mCourseRecyclerAdapter;
    private GridLayoutManager mCoursesLayoutManager;
    private NoteKeeperOpenHelper mNoteKeeperOpenHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        mNoteKeeperOpenHelper = new NoteKeeperOpenHelper( this );


        FloatingActionButton fab = findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( MainActivity.this, NoteActivity.class );
                startActivity( intent );
            }
        } );

        //Setting our values to default
        PreferenceManager.setDefaultValues( this, R.xml.pref_data_sync, false );
        //PreferenceManager.setDefaultValues( this,R.xml.root_preferences,false );
        PreferenceManager.setDefaultValues( this, R.xml.pref_general, false );
        PreferenceManager.setDefaultValues( this, R.xml.pref_notification, false );


        mDrawer = findViewById( R.id.drawer_layout );

        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer( GravityCompat.START );
            }
        } );


        mNavigationView = findViewById( R.id.nav_view );
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        mNavigationView.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_courses) {
                    displayCourses();

                } else if (id == R.id.nav_notes) {
                    displayNotes();

                } else if (id == R.id.nav_send) {
                    Toast.makeText( MainActivity.this, "send me the notes ", Toast.LENGTH_SHORT ).show();

                } else if (id == R.id.nav_share) {
                    handleShare();

                    //Toast.makeText( MainActivity.this, "share with me", Toast.LENGTH_SHORT ).show();


                }

                mDrawer = findViewById( R.id.drawer_layout );
                mDrawer.closeDrawer( GravityCompat.START );
                return true;
            }
        } );

        //This has the hurmburger that allows you to click so that you can view the item thats on the navigation menu
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed( drawerView );
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened( drawerView );
            }
        };
        mDrawer.addDrawerListener( actionBarDrawerToggle );
        //mDrawer.setDrawerListener( actionBarDrawerToggle );
        actionBarDrawerToggle.syncState();


        initializeDisplayContent();


    }

    @Override
    protected void onDestroy() {
        mNoteKeeperOpenHelper.close();
        super.onDestroy();

    }

    private void handleShare() {
        View view = findViewById( R.id.list_items );
        Snackbar.make( view, "share to -" + PreferenceManager.getDefaultSharedPreferences( this ).getString( "user_favorite_social", "" ),
                Snackbar.LENGTH_LONG ).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //loadNotes();
        getLoaderManager().restartLoader( LOADER_NOTES, null, this );
        // mNoteRecyclerAdapter.notifyDataSetChanged();
        //mNoteInfoArrayAdapter.notifyDataSetChanged();
        updateNavHeader();
    }

    private void loadNotes() {
        SQLiteDatabase db = mNoteKeeperOpenHelper.getReadableDatabase();


        String[] noteColumns = {
                NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry.COLUMN_COURSE_ID,
                NoteInfoEntry._ID
        };

        String noteOrderBy = NoteInfoEntry.COLUMN_COURSE_ID + " ," + NoteInfoEntry.COLUMN_NOTE_TITLE;
        Cursor noteCursor = db.query( NoteInfoEntry.TABLE_NAME, noteColumns, null, null,
                null, null, noteOrderBy );
        mNoteRecyclerAdapter.changeCursor( noteCursor );
    }


    private void updateNavHeader() {
        View headerView = mNavigationView.getHeaderView( 0 );
        TextView textUserName = (TextView) headerView.findViewById( R.id.text_name );
        TextView textEmailAddress = (TextView) headerView.findViewById( R.id.text_email );

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
        String userName = pref.getString( "user_display_name", "" );
        String userEmail = pref.getString( "user_email_address", "" );


        textUserName.setText( userName );
        textEmailAddress.setText( userEmail );

    }

    private void initializeDisplayContent() {
        DataManager.loadFromDatabase( mNoteKeeperOpenHelper );


        mRecyclesitem = findViewById( R.id.list_items );
        mLayoutManager = new LinearLayoutManager( this );
        mCoursesLayoutManager = new GridLayoutManager( this, 2 );


        //List<NoteInfo> notes = DataManager.getInstance().getNotes();
        mNoteRecyclerAdapter = new NoteRecyclerAdapter( this, null );


        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        mCourseRecyclerAdapter = new CourseRecyclerAdapter( this, courses );

        displayNotes();
    }

    private void displayNotes() {
        mRecyclesitem.setLayoutManager( mLayoutManager );
        mRecyclesitem.setAdapter( mNoteRecyclerAdapter );

        //SQLiteDatabase database=mNoteKeeperOpenHelper.getReadableDatabase();

        selectNavigationMenuItem( R.id.nav_notes );
    }

    private void selectNavigationMenuItem(int id) {
        Menu menu = mNavigationView.getMenu();
        menu.findItem( id ).setChecked( true );
    }

    public void displayCourses() {
        mRecyclesitem.setLayoutManager( mCoursesLayoutManager );
        mRecyclesitem.setAdapter( mCourseRecyclerAdapter );
        selectNavigationMenuItem( R.id.nav_courses );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity( new Intent( MainActivity.this, SettingsActivity.class ) );
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen( GravityCompat.START )) {
            mDrawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        android.content.CursorLoader cursorLoader = null;
        if (id == LOADER_NOTES)
            cursorLoader = new CursorLoader( this ) {
                @Override
                public Cursor loadInBackground() {
                    SQLiteDatabase db = mNoteKeeperOpenHelper.getReadableDatabase();


                   final String[] noteColumns = {
                           NoteInfoEntry.getQName( NoteInfoEntry._ID),
                            NoteInfoEntry.COLUMN_NOTE_TITLE,
                           // NoteInfoEntry.getQName( NoteInfoEntry.COLUMN_COURSE_ID),
                           CourseInfoEntry.COLUMN_COURSE_TITLE
                    };

                   final String noteOrderBy = CourseInfoEntry.COLUMN_COURSE_TITLE + " ," + NoteInfoEntry.COLUMN_NOTE_TITLE;

                    //joining course-info and note-info table
                    //note_info JOIN course_info on note_info.course_id=course_info.course_id
                    String tablesWithJoin=NoteInfoEntry.TABLE_NAME + " JOIN " +
                            CourseInfoEntry.TABLE_NAME + " ON " +
                            NoteInfoEntry.getQName( NoteInfoEntry.COLUMN_COURSE_ID )+ " = " +
                            CourseInfoEntry.getQName( CourseInfoEntry.COLUMN_COURSE_ID );
//

                    return db.query( tablesWithJoin, noteColumns, null, null,
                            null, null, noteOrderBy );

                }
            };
        return cursorLoader;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId()==LOADER_NOTES)
            mNoteRecyclerAdapter.changeCursor( data );


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        if(loader.getId()==LOADER_NOTES){
           mNoteRecyclerAdapter.changeCursor( null );

        }


    }
}

