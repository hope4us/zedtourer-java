package com.appbuildersworld.zedtourerjava.models;
import android.graphics.drawable.Drawable;

public class People {

    public int image;
    public Drawable imageDrw;
    public String name;
    public String email;
    public boolean section = false;

    public People() {
    }

    public People(String name, String email, boolean section) {
        this.name = name;
        this.email = email;
        this.section = section;
    }

}
