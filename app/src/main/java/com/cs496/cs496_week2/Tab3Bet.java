package com.cs496.cs496_week2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class Tab3Bet extends FragmentActivity {
    private String id;
    private View betBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab3bet);

        id = getIntent().getStringExtra("id");

        betBtn = findViewById(R.id.betbutton);

        betBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent resultintent = new Intent();
                //resultintent.putExtra("bet", 숫자);
                resultintent.putExtra("bet",50000);
                setResult(RESULT_OK, resultintent);
                finish();
            }
        });




    }
}
