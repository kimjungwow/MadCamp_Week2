package com.example.cs496_week2;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.PermissionChecker;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.facebook.HttpMethod.POST;

public class Tab1Phonebook extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {
    TextView tvData;
    private ListView contactsListView;
    private Tab1ContactViewAdapter adapter;
    private ArrayList<ContactModel> contactModelArrayList;
    private ArrayList<JSONObject> jsonArr = new ArrayList<JSONObject>();
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
    public int sel_pos = -1;

    private FloatingActionButton msgButton;
    private FloatingActionButton addButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("전화번호부 fragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.tab1phonebook, container, false);
        tvData = (TextView) getActivity().findViewById(R.id.tvData);


        contactsListView = rootView.findViewById(R.id.contactLV);
        adapter = new Tab1ContactViewAdapter(this.getContext(), contactModelArrayList);
        contactModelArrayList = new ArrayList<>();


        msgButton = (FloatingActionButton) rootView.findViewById(R.id.messageButton);
        addButton = rootView.findViewById(R.id.addContactButton);



        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("전화번호부 fragment", "onResume()");

        if (Permissioncheck()) {
            loadContacts(contactsListView);
        }

        WritePermissioncheck();


        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (sel_pos == position) {
                    contactsListView.setAdapter(adapter);
                    sel_pos = -1;


                } else {
                    sel_pos = position;
                }

            }

        });
        msgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contactsListView.getCheckedItemCount() != 0 && sel_pos!=-1) {



                    Uri smsUri;

                    String temp = ((ContactModel) contactsListView.getItemAtPosition(sel_pos)).getNumber();
                    String phone[] = new String[1];
                    if (temp.contains("-")) {
                        String phonenumbers[] = temp.split("-");
                        StringBuilder sb = new StringBuilder("010");
                        sb.append(phonenumbers[1]);
                        sb.append(phonenumbers[2]);

                        phone[0] = sb.toString();
                    } else {
                        phone[0] = temp;
                    }


                    smsUri = Uri.parse("smsto:" + Uri.encode(TextUtils.join(",", phone)));


                    Intent intent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        intent = new Intent(Intent.ACTION_SENDTO, smsUri);
                        intent.setPackage(Telephony.Sms.getDefaultSmsPackage(getActivity().getApplicationContext()));
                    } else {
                        intent = new Intent(Intent.ACTION_VIEW, smsUri);
                    }


                    contactsListView.clearChoices();
                    startActivity(intent);

                } else {
                    String defaultApplication = Settings.Secure.getString(getContext().getContentResolver(), "sms_default_application");
                    PackageManager pm = getContext().getPackageManager();
                    Intent intent = pm.getLaunchIntentForPackage(defaultApplication);

                    startActivity(intent);

                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (WritePermissioncheck()) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), com.example.cs496_week2.AddContactActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Cannot add contact.", Toast.LENGTH_SHORT).show();
                }


            }
        });



    }
    public class JSONTask extends AsyncTask<String, String, String> {

        @Override

        protected String doInBackground(String[] urls) {

            try {

                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.

                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("user_id", "androidTest");

                jsonObject.accumulate("name", "yun");

                HttpURLConnection con = null;

                BufferedReader reader = null;

                try{

                    //URL url = new URL(“http://192.168.25.16:3000/users“);

                    URL url = new URL(urls[0]);

                    //연결을 함

                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");//POST방식으로 보냄

                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정

                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송

                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음

                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미

                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미

                    con.connect();

                    //서버로 보내기위해서 스트림 만듬

                    OutputStream outStream = con.getOutputStream();

                    //버퍼를 생성하고 넣음

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));

                    writer.write(jsonObject.toString());

                    writer.flush();

                    writer.close();//버퍼를 받아줌

                    //서버로 부터 데이터를 받음

                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";

                    while((line = reader.readLine()) != null){

                        buffer.append(line);

                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){

                    e.printStackTrace();

                } catch (IOException e) {

                    e.printStackTrace();

                } finally {

                    if(con != null){

                        con.disconnect();

                    }

                    try {

                        if(reader != null){

                            reader.close();//버퍼를 닫아줌

                        }

                    } catch (IOException e) {

                        e.printStackTrace();

                    }

                }

            } catch (Exception e) {

                e.printStackTrace();

            }

            return null;

        }

        @Override

        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            tvData.setText(result);//서버로 부터 받은 값을 출력해주는 부

        }

    }


    public int checkselfpermission(String permission) {
        return PermissionChecker.checkSelfPermission(getContext(), permission);


    }

    public boolean Permissioncheck() {
        if (checkselfpermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            loadContacts(contactsListView);
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 100);
            if (checkselfpermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                loadContacts(contactsListView);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean WritePermissioncheck() {
        if (checkselfpermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_CONTACTS}, 100);
            if (checkselfpermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }


    private void loadContacts(ListView LV) {

        StringBuilder builder = new StringBuilder();
        ContentResolver contentResolver = getActivity().getContentResolver();

        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (phones.getCount() != contactModelArrayList.size()) {
            contactModelArrayList.removeAll(contactModelArrayList);
            while (phones.moveToNext()) {
                //default photo is in res/drawable folder
                Bitmap bp = BitmapFactory.decodeResource(getContext().getResources(),
                        R.drawable.default_contact_photo);

                //get name, number, and image uri from contact info
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String image_uri = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                //if image is not default
                if (image_uri != null) {
                    try {
                        bp = MediaStore.Images.Media
                                .getBitmap(getContext().getContentResolver(),
                                        Uri.parse(image_uri));
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                ContactModel contactModel = new ContactModel();
                contactModel.setName(name);
                contactModel.setNumber(phoneNumber);
                contactModel.setIcon(bp);
                contactModelArrayList.add(contactModel);
                //Log.d("name>>", name + "  " + phoneNumber);

                //add contact information in form of JSONObject to jsonArr

                JSONObject obj = new JSONObject();
                try {

                    obj.put("name", name);
                    obj.put("number", phoneNumber);
//                    obj.put("img", bp);
                    jsonArr.add(obj);
                    new SendDeviceDetails().execute("http://socrip3.kaist.ac.kr:9080/api/contacts", obj.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
            phones.close();
        }


        File firstmyfile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File myfile = new File(firstmyfile, "json_phonebook.txt");
        try (FileWriter fileWriter = new FileWriter(myfile)) {
            String jsonstring;
            for (JSONObject s : jsonArr) {

                jsonstring = s.toString();

                fileWriter.append(jsonstring);

            }

        } catch (IOException e) {
            //Handle exception
        }

        adapter = new Tab1ContactViewAdapter(getActivity().getApplicationContext(), contactModelArrayList);
        if (!(adapter.isEmpty())) {
            LV.setAdapter(adapter);
        }
        return;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("전화번호부 fragment", "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("전화번호부 fragment", "onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("전화번호부 fragment", "onDestroy()");
    }
}