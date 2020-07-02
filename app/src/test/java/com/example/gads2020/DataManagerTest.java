package com.example.gads2020;

import android.provider.ContactsContract;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;




@SuppressWarnings("ALL")
public class DataManagerTest {
    static DataManager sDataManager;
    @BeforeClass
    public static void classSetUp(){
        sDataManager=DataManager.getInstance();
    }
    @Before
    public  void setUp(){

        sDataManager.getNotes().clear();
        sDataManager.initializeExampleNotes();

    }
    @Test
    public void createNewNote(){

        final CourseInfo course= sDataManager.getCourse( "Android" );
        final String noteTitle="Test not title";
        final String noteText="This is the body text of my text note";

        int noteIndex=sDataManager.createNewNote();
        NoteInfo newNote=sDataManager.getNotes().get( noteIndex);
        newNote.setCourse( course );
        newNote.setTitle( noteTitle );
        newNote.setText( noteText );

        NoteInfo compareNote=sDataManager.getNotes().get( noteIndex );
         assertEquals( compareNote.getCourse(),course );
         assertEquals( compareNote.getTitle(),noteTitle );
         assertEquals( compareNote.getText(),noteText );




    }
    @Test
   public void findSimilarNotes(){

       final CourseInfo course= sDataManager.getCourse( "Android" );
       final String noteTitle="Test not title";
       final String noteText1="This is the body text of my text note";
       final String noteText2="This is the body text of my second text note";


       int noteIndex1=sDataManager.createNewNote();
       NoteInfo newNote1=sDataManager.getNotes().get(noteIndex1);
       newNote1.setCourse( course );
       newNote1.setTitle( noteTitle );
       newNote1.setText( noteText1 );


       int noteIndex2=sDataManager.createNewNote();
       NoteInfo newNote2=sDataManager.getNotes().get( noteIndex2 );
       newNote2.setCourse( course );
       newNote2.setTitle( noteTitle );
       newNote2.setText( noteText2 );

       int foundIndex1=sDataManager.findNote( newNote1 );
       assertNotEquals( noteIndex1,foundIndex1 );

       int foundIndex2=sDataManager.findNote( newNote2 );
       assertNotEquals( noteIndex2,foundIndex2 );

   }
   //Test-driven implementation
    //writing tests before the actual method

    @Test
    public void createNewNoteOneStepCreation(){
        final CourseInfo course=sDataManager.getCourse( "android_async" );
        final String noteTitle="Test note title";
        final String noteText="This is the body of my test note";

        int noteIndex=sDataManager.createNewNote(course,noteTitle,noteText);

        NoteInfo compareNotes=sDataManager.getNotes().get( noteIndex );
        assertNotEquals( course,compareNotes.getCourse() );
        assertEquals( noteTitle,compareNotes.getTitle() );
        assertEquals( noteText,compareNotes.getText() );



    }

}