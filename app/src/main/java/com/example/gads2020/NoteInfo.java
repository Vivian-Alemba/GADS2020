package com.example.gads2020;


import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("ALL")
public final class NoteInfo implements Parcelable {
    //declaring the variables for the three type fields
    private CourseInfo mCourse;
    private String mTitle;
    private String mText;
    private int mId;




    public NoteInfo( CourseInfo course, String title, String text,int id) {
        mId = id;
        //constructor for those particular fields
        mCourse = course;
        mTitle = title;
        mText = text;

    }
    public NoteInfo(CourseInfo course, String title, String text) {
        mCourse = course;
        mTitle = title;
        mText = text;
    }




    private NoteInfo(Parcel parcel) {
        //it is used to read the fields so that they can be displayed in the next activity
        mCourse=parcel.readParcelable( CourseInfo.class.getClassLoader() );
        mTitle=parcel.readString();
        mText=parcel.readString();
        //mId=parcel.readInt();


    }



    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }



//    public NoteInfo(Integer integer, Object noteCourse, Object noteTitle, Object noteText) {
//
//    }

    //getters and setters that are used to return the fields used
    public CourseInfo getCourse()
    {
        return mCourse;
    }

    public void setCourse(CourseInfo course) {

        mCourse = course;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(String title)
    {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    private String getCompareKey() {
        return mCourse.getCourseId() + "|" + mTitle + "|" + mText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoteInfo that = (NoteInfo) o;

        return getCompareKey().equals(that.getCompareKey());
    }

    @Override
    public int hashCode() {
        return getCompareKey().hashCode();
    }

    @Override
    public String toString() {
        return getCompareKey();
    }

    @Override
    //used by parcelable
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable( mCourse,0 );
        parcel.writeString( mTitle );
        parcel.writeString( mText );


    }
    public  static final Creator<NoteInfo> CREATOR=new Creator<NoteInfo>() {
        @Override
        public NoteInfo createFromParcel(Parcel source) {
            return new NoteInfo(source);
        }

        @Override
        public NoteInfo[] newArray(int size) {
            return new NoteInfo[size];
        }
    };

}
