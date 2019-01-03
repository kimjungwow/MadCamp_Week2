package com.example.cs496_week2;
import android.graphics.Bitmap;

public class ContactModel{
    private String name, number;
    private Bitmap iconbp ;

    public Bitmap getIcon() {
        return this.iconbp ;
    }

    public void setIcon(Bitmap icon) {
        this.iconbp = icon ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}