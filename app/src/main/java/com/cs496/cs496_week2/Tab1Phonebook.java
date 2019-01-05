package com.cs496.cs496_week2;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.PermissionChecker;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.facebook.AccessTokenManager.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;

public class Tab1Phonebook extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    private ListView contactsListView;
    private Tab1ContactViewAdapter adapter;
    private ArrayList<ContactModel> contactModelArrayList;
    private ArrayList<JSONObject> jsonArr = new ArrayList<JSONObject>();
    private ArrayList<String> xjsonArr = new ArrayList<String>();
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
    public int sel_pos = -1;
    private JSONObject big;


    private String urlstring = "http://socrip3.kaist.ac.kr:9080/api/contacts";
    private FloatingActionButton msgButton;
    private FloatingActionButton addButton;
    private RequestQueue mQueue;
    private URL url;
    private CallbackManager callbackManager;
    ShareDialog shareDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("전화번호부 fragment", "onCreateView()");

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        View rootView = inflater.inflate(R.layout.tab1phonebook, container, false);
        shareDialog = new ShareDialog(getActivity());


        //Need to "GET"
        mQueue = Volley.newRequestQueue(getContext());
        LoginButton facebookLoginButton = (LoginButton) getActivity().findViewById(R.id.facebook_login_button);


        contactsListView = rootView.findViewById(R.id.contactLV);
        adapter = new Tab1ContactViewAdapter(this.getContext(), contactModelArrayList);
        contactModelArrayList = new ArrayList<>();


        msgButton = (FloatingActionButton) rootView.findViewById(R.id.messageButton);
        addButton = rootView.findViewById(R.id.addContactButton);
        facebookLoginButton = rootView.findViewById(R.id.facebook_login_button);

        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.i(TAG, "User ID: " + loginResult.getAccessToken().getUserId());
                Log.i(TAG, "Auth Token: " + loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                // App code
                Log.w(TAG, "Cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.e(TAG, "Error", exception);
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("전화번호부 fragment", "onResume()");


        AppEventsLogger.activateApp(getContext());

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
                if (Permissioncheck()) {
                    loadContacts(contactsListView);
                }

//                if (contactsListView.getCheckedItemCount() != 0 && sel_pos != -1) {
//
//
//                    Uri smsUri;
//
//                    String temp = ((ContactModel) contactsListView.getItemAtPosition(sel_pos)).getNumber();
//                    String phone[] = new String[1];
//                    if (temp.contains("-")) {
//                        String phonenumbers[] = temp.split("-");
//                        StringBuilder sb = new StringBuilder("010");
//                        sb.append(phonenumbers[1]);
//                        sb.append(phonenumbers[2]);
//
//                        phone[0] = sb.toString();
//                    } else {
//                        phone[0] = temp;
//                    }
//
//
//                    smsUri = Uri.parse("smsto:" + Uri.encode(TextUtils.join(",", phone)));
//
//
//                    Intent intent;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        intent = new Intent(Intent.ACTION_SENDTO, smsUri);
//                        intent.setPackage(Telephony.Sms.getDefaultSmsPackage(getActivity().getApplicationContext()));
//                    } else {
//                        intent = new Intent(Intent.ACTION_VIEW, smsUri);
//                    }
//                    contactsListView.clearChoices();
//                    startActivity(intent);
//
//                } else {
//                    String defaultApplication = Settings.Secure.getString(getContext().getContentResolver(), "sms_default_application");
//                    PackageManager pm = getContext().getPackageManager();
//                    Intent intent = pm.getLaunchIntentForPackage(defaultApplication);
//
//                    startActivity(intent);
//
//                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlstring, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("TAG", response.toString());
                        Intent intent;
                        intent = new Intent(ContactsContract.Intents.Insert.ACTION);


                        try {
                            JSONArray contacts = response;


                            Log.d("JsonArray", contacts.toString());
                            for (int i = 0; i < contacts.length(); i++) {
                                JSONObject jresponse = contacts.getJSONObject(i);

                                String name = jresponse.getString("name");
                                Log.d("nickname", name);
                                String number = jresponse.getString("number");
//                                if(i==0)

//                                    Toast.makeText(getContext(),name+number,Toast.LENGTH_LONG).show();

                                if (!xjsonArr.contains(name)) {
                                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                                intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
                                intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
                                startActivity(intent);
                            }

                        }
                    } catch(
                    JSONException e)

                    {
                        e.printStackTrace();
                    }


                }
            },new Response.ErrorListener()

            {
                @Override
                public void onErrorResponse (VolleyError error){
                error.printStackTrace();

            }
            });
                mQueue.add(request);


//                if (WritePermissioncheck()) {
//                    Intent intent = new Intent(getActivity().getApplicationContext(), com.example.cs496_week2.AddContactActivity.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getContext(), "Cannot add contact.", Toast.LENGTH_SHORT).show();
//                }
//                jsonParse();
        }
    });
}

    public int checkselfpermission(String permission) {
        return PermissionChecker.checkSelfPermission(getContext(), permission);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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


                    Log.d("JsonArray", contacts.toString());
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject jresponse = contacts.getJSONObject(i);

                        String name = jresponse.getString("name");
                        Log.d("nickname", name);
                        String number = jresponse.getString("number");
                        if (i == 0)
                            Toast.makeText(getContext(), name + number, Toast.LENGTH_LONG).show();
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
                        xjsonArr.add(name);
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
        AppEventsLogger.deactivateApp(getContext());
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