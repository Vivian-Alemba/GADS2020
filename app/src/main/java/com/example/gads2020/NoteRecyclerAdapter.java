package com.example.gads2020;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;



import static com.example.gads2020.NoteKeeperDatabaseContract.*;


public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.viewholder> {
    private final Context mContext;
    //private final List<NoteInfo> notes;
    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;
    private int mCoursePos;
    private int mNoteTitlePos;
    private int mIdPos;

    NoteRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor=cursor;
        mLayoutInflater =LayoutInflater.from( mContext );
        populateColumnPositions();
    }

    private void populateColumnPositions() {
        if(mCursor==null)
            return;
        //Get column indexes from mCursor
        mCoursePos = mCursor.getColumnIndex( CourseInfoEntry.COLUMN_COURSE_TITLE );
        mNoteTitlePos = mCursor.getColumnIndex( NoteInfoEntry.COLUMN_NOTE_TITLE );
        mIdPos = mCursor.getColumnIndex( NoteInfoEntry._ID );

    }
    void changeCursor(Cursor cursor){
        if(mCursor !=null){
            mCursor.close();
        }
        mCursor=cursor;
        populateColumnPositions();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=mLayoutInflater.inflate( R.layout.item_note_list,parent,false );

        return new viewholder( itemView );
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        mCursor.moveToPosition( position );
        String course=mCursor.getString( mCoursePos );
        String noteTitle=mCursor.getString( mNoteTitlePos );
        int id=mCursor.getInt( mIdPos );


        //NoteInfo notes=this.notes.get( position );
        holder.noteCourse.setText( course);
        holder.noteTitle.setText( noteTitle);
        holder.mId =id;


    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    final class viewholder extends RecyclerView.ViewHolder {
        final TextView noteCourse;
        final TextView noteTitle;
        final CardView card;
        int mId;



    viewholder(@NonNull View itemView) {
        super( itemView );
        noteCourse=itemView.findViewById( R.id.text_course );
        noteTitle=itemView.findViewById( R.id.text_title );
        card=itemView.findViewById( R.id.card_view );

        itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent( mContext,NoteActivity.class );
                intent.putExtra(NoteActivity.NOTE_ID, mId );
                mContext.startActivity( intent );




            }
        } );
    }
}


}
