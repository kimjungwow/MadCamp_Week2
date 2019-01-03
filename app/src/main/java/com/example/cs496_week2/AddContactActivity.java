package com.example.cs496_week2;


import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;



public class AddContactActivity extends FragmentActivity {
    private EditText tvName;
    private EditText tvNumber;
    private View closeBtn;
    private View cancelBtn;
    private View addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcontact);

        tvName = findViewById(R.id.nameInput);
        tvNumber = findViewById(R.id.numberInput);
        closeBtn = findViewById(R.id.popupCloseBtn);
        cancelBtn = findViewById(R.id.popupCancelBtn);
        addBtn = findViewById(R.id.popupAddBtn);

        tvName.setText("", null);
        tvNumber.setText("", null);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closePopUp();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closePopUp();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = tvName.getText().toString();
                String number = tvNumber.getText().toString();
                if (name != "" && number != "") {
                    Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                    intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                    intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Please type name and number.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        tvName.setText("", null);
        tvNumber.setText("", null);
    }

    public void closePopUp(){
        onBackPressed();
    }
}