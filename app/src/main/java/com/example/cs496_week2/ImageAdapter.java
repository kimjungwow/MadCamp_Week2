package com.example.cs496_week2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<String> imgPaths;
    LayoutInflater inf;
    public ImageAdapter(Context context, int layout, ArrayList<String> imgs) {
        this.context=context;
        this.layout=layout;
        this.imgPaths=imgs;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        //이미지셋에 있는 아이템의 수를 반환함(그리드뷰는 아이템의 수에 해당하는 행렬을 준비함)
        return imgPaths.size();
    }

    @Override
    public String getItem(int position) {
        return imgPaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)
            convertView = inf.inflate(layout, null);
        ImageView iv = (ImageView)convertView.findViewById(R.id.imageView1);
        Bitmap img = BitmapFactory.decodeFile(getItem(position));
        double width = img.getWidth();
        double height = img.getHeight();
        int desiredWidth = 120;
        int desiredHeight = 120;
        if (width / height >= 1.2 && width/height <=0.8) {
            desiredHeight = (int) (height * desiredWidth / width);
        }
        img = Bitmap.createScaledBitmap(img, desiredWidth, desiredHeight, true);
        iv.setImageBitmap(img);

        return convertView;
    }



}