package com.cs496.cs496_week2;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;


public class Tab2Images extends Fragment {
    EditText editText;
    GridView gridview;
    ImageAdapter adapter;
    boolean writePermission;
    private String userId;
    private RequestQueue mQueue;

    private ArrayList<String> imagePaths;
    private ArrayList<String> imageHashs;
    // public static HashMap<String, String> imageMap;
    // public static ArrayList<ImageScheme> imageInfos;

    final int REQ_CODE_SELECT_IMAGE = 100;

    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2images, container, false);
        gridview = (GridView) rootView.findViewById(R.id.gridView);
        userId = ((MainActivity) getActivity()).getjson;
        mQueue = Volley.newRequestQueue(getContext());

        //imageMap = new HashMap<>();
        imagePaths = new ArrayList<>();
        imageHashs = new ArrayList<>();
        //imageInfos = new ArrayList<>();

        return rootView;
    }


    @Override
    public void onResume(){
        super.onResume();
        if(ReadPermissioncheck()) loadPictures();

        writePermission = WritePermissioncheck();
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ReadPermissioncheck()) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), ZoomActivity.class);
                    intent.putExtra("imagePath", adapter.getItem(position));
                    intent.putExtra("writePermission", writePermission);
                    startActivity(intent);
                }
            }
        });
        clientToServer();
    }

    protected ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String PathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);
            Log.i("IMGPATH", PathOfImage);
            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }

    public void loadPictures(){
        imagePaths.clear();
        imagePaths = getImagesPath(this.getActivity());

        imageHashs.clear();
        for (String imgPath : imagePaths) {
            String imgHash = MD5_Hash(getBase64String(BitmapFactory.decodeFile(imgPath)));

            //if(!imageHashs.contains(imgHash)) {
                imageHashs.add(imgHash);
            //}
        }
        adapter = new ImageAdapter(getActivity().getApplicationContext(), R.layout.row, imagePaths);
        gridview.setAdapter(adapter);
    }

    public void clientToServer() {
        String urlstring = "http://socrip3.kaist.ac.kr:9980/api/galleries";
        Log.i("CLIENTTOSERVER","Started");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlstring + "/fbid/" + userId, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.i("CLIENTTOSERVER","ONRESPONSE!");
                if(response.length() > imagePaths.size()) {
                    try {
                        Log.i("CLIENTTOSERVER","server more");
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject imgInfo = response.getJSONObject(i);

                            String imgHash = imgInfo.getString("imageHash");

                            if(!imageHashs.contains(imgHash)) {
                                int idx = imageHashs.indexOf(imgHash);
                                sendImageInfos(imageHashs.get(idx),imagePaths.get(idx));

                                //SEND WITH
                                //imageHash: imageHashs.get(idx);
                                //imageUrl: imagePaths.get(idx);
                                //fbid: userId;
                                //IMAGE: getBase64String(BitmapFactory.decodeFile(imgPath))
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Log.i("CLIENTTOSERVER","client more");
                        ArrayList<String> resHashes = new ArrayList<>();
                        for(int j = 0; j < response.length(); j++) {
                            JSONObject imgInfo = response.getJSONObject(j);
                            resHashes.add(imgInfo.getString("imageHash"));
                        }
                        for(int i = 0; i < imageHashs.size(); i++) {
                            if (!resHashes.contains(imageHashs.get(i))) {
                                sendImageInfos(imageHashs.get(i),imagePaths.get(i));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    public void sendImageInfos(String hash, String url) {
        JSONArray jsonArr = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        try {
                jsonObj.put("fbid", userId);
                jsonObj.put("imageHash", hash);
                jsonObj.put("imageUrl", url);
                jsonObj.put("imageFile", getBase64String(BitmapFactory.decodeFile(url)));

                new SendDeviceDetails().execute("http://socrip3.kaist.ac.kr:9980/api/galleries", jsonObj.toString());
            } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int checkselfpermission(String permission) {
        return PermissionChecker.checkSelfPermission(getContext(), permission);
    }

    public boolean ReadPermissioncheck() {
        if (checkselfpermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {


            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            if (checkselfpermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }


    public boolean WritePermissioncheck() {
        if (checkselfpermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            if (checkselfpermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static String MD5_Hash(String s) {
        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(s.getBytes(),0,s.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }

    public String getBase64String(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

}