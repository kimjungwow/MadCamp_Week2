package com.example.cs496_week2;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Downloader;

import org.json.JSONArray;
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
    private JSONObject big;

    private String urlstring = "http://socrip3.kaist.ac.kr:9080/api/contacts";
    private FloatingActionButton msgButton;
    private FloatingActionButton addButton;
    private RequestQueue mQueue;
    private URL url;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("전화번호부 fragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.tab1phonebook, container, false);
        tvData = (TextView) getActivity().findViewById(R.id.tvData);

        mQueue = Volley.newRequestQueue(getContext());







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

//                jsonParse();




//                HttpURLConnection httpURLConnection = null;
//                try{

//                    httpURLConnection = (HttpURLConnection) url.openConnection();
//
//                    httpURLConnection.setRequestMethod("GET"); // URL 요청에 대한 메소드 설정 : POST.
////                    httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
////                    httpURLConnection.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");
//                    httpURLConnection.setReadTimeout(10000);
//                    httpURLConnection.setConnectTimeout(10000);
//                    httpURLConnection.setDoOutput(true);
////                    httpURLConnection.setDoInput(true);
//
//
//                    httpURLConnection.connect();
//
//
////                    InputStream is = httpURLConnection.getInputStream(); //input스트림 개방
//
//
//
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
//                    StringBuilder builder = new StringBuilder(); //문자열을 담기 위한 객체
////                    BufferedReader reader = new BufferedReader(new InputStreamReader(is)); //문자열 셋 세팅
////                    BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8")); //문자열 셋 세팅
//
//                    String line;
//
//                    while ((line = reader.readLine()) != null) {
//                        builder.append(line+ "\n");
//                    }
//                    reader.close();
//
//                    String result = builder.toString();
////                    Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
//
//
//
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    if (httpURLConnection != null) {
//                        httpURLConnection.disconnect();
//                    }
//                }

            }

        });
        msgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contactsListView.getCheckedItemCount() != 0 && sel_pos != -1) {


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
//                if (WritePermissioncheck()) {
//                    Intent intent = new Intent(getActivity().getApplicationContext(), com.example.cs496_week2.AddContactActivity.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getContext(), "Cannot add contact.", Toast.LENGTH_SHORT).show();
//                }
                jsonParse();
            }
        });
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
    public void jsonParse() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlstring, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("TAG", response.toString());


                try {
                    JSONArray contacts = response;


                    Log.d("JsonArray",contacts.toString());
                    for(int i=0;i<contacts.length();i++){
                        JSONObject jresponse = contacts.getJSONObject(i);

                        String name = jresponse.getString("name");
                        Log.d("nickname",name);
                        String number = jresponse.getString("number");
                        if(i==0)
                        Toast.makeText(getContext(),name+number,Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });
        mQueue.add(request);

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
//                    obj.put("img", getStringFromBitmap(bp));
                    jsonArr.add(obj);


                    new SendDeviceDetails().execute("http://socrip3.kaist.ac.kr:9080/api/contacts", obj.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            phones.close();
        }

//        big.put("contacts", jsonArr);






        File firstmyfile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File myfile = new File(firstmyfile, "json_phonebook2.txt");

        try (FileWriter fileWriter = new FileWriter(myfile)) {
            String jsonstring;
            for (JSONObject s : jsonArr) {

                jsonstring = s.toString();

                fileWriter.append(jsonstring);

            }
//            jsonstring=big.toString();
//            fileWriter.append(jsonstring);

//            new SendDeviceDetails().execute("http://socrip3.kaist.ac.kr:9080/api/contacts", obj.toString());



        } catch (IOException e) {
            //Handle exception
        }

        adapter = new Tab1ContactViewAdapter(getActivity().getApplicationContext(), contactModelArrayList);
        if (!(adapter.isEmpty())) {
            LV.setAdapter(adapter);
        }
        return;
    }

    public String getStringFromBitmap(Bitmap bitmapPicture) {
        /*
         * This functions converts Bitmap picture to a string which can be
         * JSONified.
         * */
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    public Bitmap getBitmapFromString(String jsonString) {
        /*
         * This Function converts the String back to Bitmap
         * */
        byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
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