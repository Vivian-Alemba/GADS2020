package com.example.gads2020;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {
    private NoteRecyclerAdapter mNoteRecyclerAdapter;
    //List<NoteInfo> note;
    //private ArrayAdapter<NoteInfo> mNoteInfoArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_note_list );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        FloatingActionButton fab = findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent( NoteListActivity.this,NoteActivity.class );
                startActivity( intent );
            }
        } );


        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNoteRecyclerAdapter.notifyDataSetChanged();
        //mNoteInfoArrayAdapter.notifyDataSetChanged();
    }

    private void initializeDisplayContent() {
//       final ListView notesList = findViewById( R.id.list_note );
//        note=DataManager.getInstance().getNotes();
//        mNoteInfoArrayAdapter = new ArrayAdapter<>( this,android.R.layout.simple_list_item_1,note );
//        notesList.setAdapter( mNoteInfoArrayAdapter );
//
//        notesList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent=new Intent( NoteListActivity.this,NoteActivity.class );
//                NoteInfo note=(NoteInfo) notesList.getItemAtPosition( position );
//                intent.putExtra( NoteActivity.NOTE_POSITION,position );
//
//                startActivity( intent );
//            }
//        } );

        final RecyclerView recyclernotes=(RecyclerView)findViewById( R.id.list_notes );
        final LinearLayoutManager layoutManager=new LinearLayoutManager( this );
        recyclernotes.setLayoutManager( layoutManager );

        List<NoteInfo> notes=DataManager.getInstance().getNotes();
        mNoteRecyclerAdapter = new NoteRecyclerAdapter( this,null );
        recyclernotes.setAdapter( mNoteRecyclerAdapter );
    }

}