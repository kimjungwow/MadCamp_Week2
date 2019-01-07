package com.cs496.cs496_week2;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import net.alhazmy13.imagefilter.ImageFilter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ZoomActivity extends FragmentActivity {
    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;
    Bitmap mainImage;

    ImageView zoomview;
    RecyclerView recyclerView;
    boolean writePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);

        // Hook up clicks on the thumbnail views.
        Intent intent = getIntent();
        writePermission = intent.getBooleanExtra("writePermission", false);
        zoomview = findViewById(R.id.expanded_image);
        recyclerView = findViewById(R.id.filterThumbnails);

        //Save button gets current image in zoomview and save it to gallery
        final View savebutton = findViewById(R.id.saveButton);
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (writePermission){
                    BitmapDrawable filteredDrawable = (BitmapDrawable) zoomview.getDrawable();
                    Bitmap newBP = filteredDrawable.getBitmap();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String title = sdf.format(new Date());
                    MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), newBP ,title, "description");
                    Toast.makeText(getApplicationContext(), "Image saved",Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(getApplicationContext(), "Cannot save image.", Toast.LENGTH_SHORT).show();
                }
            }

        });

        //go back to tab2
        final View thumb1View = findViewById(R.id.thumb_button_1);
        thumb1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed(); // Go back to previous fragment!
            }
        });


        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }

    @Override
    public void onResume(){
        super.onResume();
        final String imgPath = getIntent().getStringExtra("imagePath");
        if (imgPath != ""){
            Glide.with(getApplicationContext()).load(imgPath).into(zoomview);
            mainImage = BitmapFactory.decodeFile(imgPath);
//            zoomview.setImageBitmap(mainImage);
            LoadFilterThumbnails();
        }
        else {
            Toast.makeText(getApplicationContext(), "Cannot load image",Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }

    private void LoadFilterThumbnails(){
        // array for filter type names and thumbnails
        String[] filterTypes = {"ORIGINAL", "GRAY", "BLUR", "OIL", "NEON", "BLOCK", "OLD", "SHARPEN", "LOMO","HDR", "SOFTGLOW"};
        ArrayList<FilteredThumbnail> thumbnails = new ArrayList<>();

        //resize mainImage to smaller thumbnails
        int desiredWidth = 100;
        int desiredHeight = mainImage.getHeight() * 100 / mainImage.getWidth();
        Bitmap thumbImage = Bitmap.createScaledBitmap(mainImage, desiredWidth, desiredHeight, false);

        //make thumbnails and add it to thumnails ArrayList
        FilteredThumbnail original = new FilteredThumbnail();
        original.setFilterType("ORIGINAL");
        original.setImgBP(thumbImage);
        original.setFilterTypeIndex(0);
        thumbnails.add(original);
        for (int index = 1; index < filterTypes.length; index++){
            //filter images by type
            Bitmap filteredImg = ApplyFilterByIndex(thumbImage, index);
            //and save images and corresponding filters to thumbnails array
            FilteredThumbnail thumbnail = new FilteredThumbnail();
            thumbnail.setFilterTypeIndex(index);
            thumbnail.setFilterType(filterTypes[index]);
            thumbnail.setImgBP(filteredImg);
            thumbnails.add(thumbnail);
        }

        // Create an adapter and set onClickListener
        FilterThumbnailAdapter adapter = new FilterThumbnailAdapter(getApplicationContext(), thumbnails, new FilterThumbnailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FilteredThumbnail item) {
                LoadPicture(item.getFilterTypeIndex());
            }
        });

        //use adapter to put images in recyclerview
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void LoadPicture(int index){
        if (index == 0) {
            Glide.with(getApplicationContext()).load(mainImage).into(zoomview);
            //zoomview.setImageBitmap(mainImage);
        }
        else {
            Glide.with(getApplicationContext()).load(ApplyFilterByIndex(mainImage, index)).into(zoomview);
            // zoomview.setImageBitmap(ApplyFilterByIndex(mainImage, index));
        }
        return;
    }

    private Bitmap ApplyFilterByIndex(Bitmap bitmap, int value){
        int dstHeight = 400;
        int dstWidth = bitmap.getWidth() * dstHeight / bitmap.getHeight();
        bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
        // "OLD", "SHARPEN", "LOMO","HDR"
        switch (value) {
            case 1:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.GRAY);
            case 2:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.AVERAGE_BLUR, 9);
            case 3:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.OIL,10);
            case 4:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.NEON,200, 50, 100);
            case 5:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.BLOCK);
            case 6:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.OLD);
            case 7:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.SHARPEN);
            case 8:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.LOMO);
            case 9:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.HDR);
            case 10:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.SOFT_GLOW);
            default:
                return bitmap;
        }
    }
}