package com.example.gads2020;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;



@SuppressWarnings("deprecation")
public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.viewholder> {
    private final Context mContext;
    private final List<NoteInfo> notes;
    private final LayoutInflater mLayoutInflater;

    public NoteRecyclerAdapter(Context context, List<NoteInfo> notes) {
        mContext = context;
        this.notes = notes;
        mLayoutInflater =LayoutInflater.from( mContext );
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=mLayoutInflater.inflate( R.layout.item_note_list,parent,false );

        return new viewholder( itemView );
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        NoteInfo notes=this.notes.get( position );
        holder.noteCourse.setText( notes.getCourse().getTitle() );
        holder.noteTitle.setText( notes.getTitle() );
        holder.mId =notes.getId();


    }

    @Override
    public int getItemCount() {
        return this.notes.size();
    }

    final

public class viewholder extends RecyclerView.ViewHolder {
        public final TextView noteCourse;
        public final TextView noteTitle;
        public final CardView card;
        public int mId;



    public viewholder(@NonNull View itemView) {
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
