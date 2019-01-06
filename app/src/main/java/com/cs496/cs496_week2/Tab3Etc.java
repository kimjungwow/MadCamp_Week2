package com.cs496.cs496_week2;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class Tab3Etc extends Fragment {
    String TAG = "Tab3ETC";


    private Socket mSocket;
    private EditText textid, textpw;
    private View signinBtn, signupBtn;
    private String id, password;
    private JSONObject jsonlogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.tab3etc, container, false);

        textid = rootView.findViewById(R.id.textid);
        textpw = rootView.findViewById(R.id.textpw);

        textid.setText("", null);
        textpw.setText("", null);

        signinBtn = rootView.findViewById(R.id.signin);


        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = textid.getText().toString();
                password = textpw.getText().toString();

                try {
                    jsonlogin = new JSONObject();
                    jsonlogin.put("option", "signin");
                    jsonlogin.put("id", id);
                    jsonlogin.put("password", password);
                    textid.setText("", null);
                    textpw.setText("", null);

                    mSocket = IO.socket("http://socrip3.kaist.ac.kr:9089/");
//                    mSocket = IO.socket("http://socrip3.kaist.ac.kr:9080/");
                    mSocket.connect();
                    mSocket.on(Socket.EVENT_CONNECT, onConnect);
                    mSocket.on("serverMessage", onMessageReceived);


                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        signupBtn = rootView.findViewById(R.id.signup);


        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = textid.getText().toString();
                password = textpw.getText().toString();

                try {
                    jsonlogin = new JSONObject();
                    jsonlogin.put("option", "signup");
                    jsonlogin.put("id", id);
                    jsonlogin.put("password", password);
                    textid.setText("", null);
                    textpw.setText("", null);

                    mSocket = IO.socket("http://socrip3.kaist.ac.kr:9089/");
//                    mSocket = IO.socket("http://socrip3.kaist.ac.kr:9080/");
                    mSocket.connect();
                    mSocket.on(Socket.EVENT_CONNECT, onConnect);
                    mSocket.on("serverMessage", onMessageReceived);


                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        textid.setText("", null);
        textpw.setText("", null);
    }

    // Socket서버에 connect 되면 발생하는 이벤트
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.emit("clientMessage", jsonlogin);
        }
    };

    // 서버로부터 전달받은 'chat-message' Event 처리.
    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // 전달받은 데이터는 아래와 같이 추출할 수 있습니다.
            try {
                JSONObject receivedData = (JSONObject) args[0];
//                Log.d(TAG, receivedData.getString("id"));
                Log.d(TAG, receivedData.getString("result"));
                if (receivedData.getString("result").equals("2")) {
                    Log.d(TAG, "Login Success");
                    Intent intent = new Intent(getContext(), Tab3Game.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), receivedData.getString("alert"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


}
