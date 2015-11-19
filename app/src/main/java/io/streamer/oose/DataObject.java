package io.streamer.oose;

import android.graphics.drawable.Drawable;

public class DataObject {
    private String mText1;
    private String mText2;
    private Drawable mImage;

    DataObject (String text1, String text2, Drawable image ){
        mText1 = text1;
        mText2 = text2;
        mImage= image;
    }

    public String getmText1() {
        return mText1;
    }

    public void setmText1(String mText1) {
        this.mText1 = mText1;
    }

    public String getmText2() {
        return mText2;
    }

    public void setmText2(String mText2) {
        this.mText2 = mText2;
    }

    public Drawable getmImage() {
        return mImage;
    }
}