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
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;


public class Tab2Images extends Fragment {
    EditText editText;
    GridView gridview;
    ImageAdapter adapter;
    boolean writePermission;
    private String userId;
    private RequestQueue mQueue;

    FloatingActionButton syncButton;

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

        syncButton = rootView.findViewById(R.id.syncButton);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncWithServer();
            }
        });

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
    }

    private void notifyPhotoAdded(String photoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    protected ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String PathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);
            Log.i("IMGPATH", PathOfImage);
            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }

    public void loadPictures(){
       // Log.i("GALLERYDEBUG", "loadPictures");

        ArrayList<String> loadImgPath = getImagesPath(this.getActivity());
        adapter = new ImageAdapter(getContext(), R.layout.row, loadImgPath);
        gridview.invalidateViews();
        gridview.setAdapter(adapter);
    }

    public void getImageFile(String imageHash) {
        String urlstring = "http://socrip3.kaist.ac.kr:9980/api/galleries/image/";
        Log.i("GetImageFile", "Started");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlstring + imageHash, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String imgHash = response.getString("imageHash");
                    String imgFile = response.getString("imageFile");
                    Log.i("imgHash", imgHash);

                    byte[] decodedString = Base64.decode(imgFile, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    saveImage(decodedByte, imgHash);
                    Toast.makeText(getApplicationContext(),imgHash + " has saved", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(request);
    }

    public void syncWithServer() {
        // Toast.makeText(getApplicationContext(), "Calculating Hashes. Please Wait!",Toast.LENGTH_SHORT).show();

        imagePaths.clear();
        imagePaths = getImagesPath(this.getActivity());

        imageHashs.clear();
        for (String imgPath : imagePaths) {
           // String imgHash = MD5_Hash(getBase64String(BitmapFactory.decodeFile(imgPath)));
            String imgHash = imgPath.substring(imgPath.lastIndexOf("/")+1);
            imageHashs.add(imgHash);
        }

        String urlstring = "http://socrip3.kaist.ac.kr:9980/api/galleries";
        Log.i("SYNC","Started");
        Toast.makeText(getApplicationContext(), "Sync Started",Toast.LENGTH_SHORT).show();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlstring + "/fbid/" + userId, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.i("SYNC","ONRESPONSE! " + response.length());
                if(response.length() > imagePaths.size()) {
                    try {
                        Log.i("SYNC-ServerToClient","server more");
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject imgInfo = response.getJSONObject(i);

                            String imgHash = imgInfo.getString("imageHash");

                            if(!imageHashs.contains(imgHash)) {
                                getImageFile(imgHash);
                            }
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Server does not respond",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        return;
                    }
                } else {
                    try {
                        Log.i("SYNC-ClientToSever","client more");
                        ArrayList<String> resHashes = new ArrayList<>();
                        for(int j = 0; j < response.length(); j++) {
                            JSONObject imgInfo = response.getJSONObject(j);
                            resHashes.add(imgInfo.getString("imageHash"));
                        }
                        for(int i = 0; i < imageHashs.size(); i++) {
                            if (!resHashes.contains(imageHashs.get(i))) {
                                Log.i("****", "SENDIMAGEINFO!");
                                sendImageInfos(imageHashs.get(i),imagePaths.get(i));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Sending images failed",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Toast.makeText(getApplicationContext(), "Sync Succeed",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    public void sendImageInfos(String hash, String uri) {
        JSONObject jsonObj = new JSONObject();
        try {
                jsonObj.put("fbid", userId);
                jsonObj.put("imageHash", hash);
                jsonObj.put("imageUri", uri);
                jsonObj.put("imageFile", getBase64String(BitmapFactory.decodeFile(uri)));

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

    private void saveImage(final Bitmap bitmap, String fileName) {

        try {
            // image naming and path  to include sd card  appending name you choose for file
            final String mPath = Environment.getExternalStorageDirectory().toString() + "/Pictures/" + fileName;

            final File imageFile = new File(mPath);

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                int quality = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();
                notifyPhotoAdded(mPath);

                new CountDownTimer(1000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        // mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        loadPictures();
                    }
                }.start();

            } else {
                Dexter.withActivity(getActivity())
                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    FileOutputStream outputStream = null;
                                    try {
                                        outputStream = new FileOutputStream(imageFile);
                                        int quality = 100;
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                                        outputStream.flush();
                                        outputStream.close();

                                        notifyPhotoAdded(mPath);
                                        loadPictures();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else if (report.isAnyPermissionPermanentlyDenied()) {
                                    Toast.makeText(getContext(), "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

}