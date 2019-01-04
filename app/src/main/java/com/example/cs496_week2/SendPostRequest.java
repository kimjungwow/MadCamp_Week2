package com.example.cs496_week2;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendPostRequest extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        String postData = "";

        HttpURLConnection httpConnection= null;
        try {


            httpConnection= (HttpURLConnection) new URL(params[0]).openConnection();
            httpConnection.setRequestMethod("POST");
//            httpURLConnection.setRequestMethod("POST");
//            httpURLConnection.setDoOutput(true);
            httpConnection.setDoOutput(true);

            DataOutputStream outputStream= new DataOutputStream(httpConnection.getOutputStream());
//            DataOutputStream outputStream= new DataOutputStream(httpURLConnection.getOutputStream());
            Log.d("DEBUG2", params[0]);
            Log.d("DEBUG2", params[1]);
            outputStream.writeBytes(params[1]);
            outputStream.flush();
            outputStream.close();

            InputStream in = httpConnection.getInputStream();
//            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char currentData = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                postData += currentData;
            }
        } catch (Exception e) {
            Log.d("LOG in Catch", "exception occurred");
            e.printStackTrace();
        } finally {
            Log.d("LOG in Finally", "exception occurred");
            if (httpConnection!= null) {
                httpConnection.disconnect();
//                httpURLConnection.disconnect();
            }
        }
        return postData;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}