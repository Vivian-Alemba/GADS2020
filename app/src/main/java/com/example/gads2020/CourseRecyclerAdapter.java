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

import com.google.android.material.snackbar.Snackbar;

import java.util.List;


@SuppressWarnings("deprecation")
public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.viewholder> {
    private final Context mContext;
    private final List<CourseInfo> mCourses;
    private final LayoutInflater mLayoutInflater;

    public CourseRecyclerAdapter(Context context, List<CourseInfo> mCourses) {
        mContext = context;
        this.mCourses = mCourses;
        mLayoutInflater =LayoutInflater.from( mContext );
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=mLayoutInflater.inflate( R.layout.item_course_list,parent,false );

        return new viewholder( itemView );
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        CourseInfo course=this.mCourses.get( position );
        holder.noteCourse.setText(course.getTitle() );
        holder.mId=position;


    }

    @Override
    public int getItemCount() {
        return this.mCourses.size();
    }

    final

public class viewholder extends RecyclerView.ViewHolder {
        public final TextView noteCourse;
        public final CardView card;
        public int mId;




    public viewholder(@NonNull View itemView) {
        super( itemView );
        noteCourse=itemView.findViewById( R.id.text_course );
        card=itemView.findViewById( R.id.card_view );

        itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make( v,mCourses.get(mId).getTitle(),Snackbar.LENGTH_LONG ).show();
            }
        } );
    }
}


}
