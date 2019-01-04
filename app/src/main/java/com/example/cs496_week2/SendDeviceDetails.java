package com.example.cs496_week2;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendDeviceDetails extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String[] params) {

        String data = "";

        HttpURLConnection httpURLConnection = null;
        try {

            Log.d("DEBUG2-0", "try to make a connection");

            httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty( "Content-Type", "application/json; charset=utf-8");

            httpURLConnection.setDoOutput(true);
            //httpURLConnection.connect();
            Log.d("DEBUG2-1", "opened connection successfully");
            OutputStream os = httpURLConnection.getOutputStream();

            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            wr.write(params[1]);
            wr.flush();
            wr.close();
            os.close();

            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                data += current;
            }
        } catch (Exception e) {
            Log.d("LOG in Catch", "exception occurred");

            e.printStackTrace();
        } finally {
            Log.d("LOG in Finally", "exception occurred");

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
    }
}