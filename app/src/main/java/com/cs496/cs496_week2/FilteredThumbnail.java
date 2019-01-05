package com.cs496.cs496_week2;

import android.graphics.Bitmap;

public class FilteredThumbnail {
    private Bitmap imgBP;
    private String filterType;
    private int filterTypeIndex;
    public Bitmap getImgBP(){return imgBP;}
    public void setImgBP(Bitmap newBP){this.imgBP = newBP;}
    public String getFilterType(){return filterType;}
    public void setFilterType(String newType){this.filterType = newType;}
    public int getFilterTypeIndex(){return filterTypeIndex;}
    public void setFilterTypeIndex(int newIndex){this.filterTypeIndex = newIndex;}
}