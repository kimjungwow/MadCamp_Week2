package com.cs496.cs496_week2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Tab3Game extends FragmentActivity {
    String TAG = "Tab3Gamee";

    private View startBtn;
    private String id;
    private JSONObject jsonlogin, jsonlogin1, jsonlogin2;
    private EditText texthorse, texthorse1, texthorse2;
    private Socket mSocket, mSocket1, mSocket2;
    private StringBuilder sa,sb,sc;
    private int which;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab3game);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        texthorse = findViewById(R.id.texthorse);
        texthorse1 = findViewById(R.id.texthorse1);
        texthorse2 = findViewById(R.id.texthorse2);


        startBtn = findViewById(R.id.startbutton);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBtn.setVisibility(View.GONE);
                texthorse.setText("", null);
                texthorse.setVisibility(View.VISIBLE);
                texthorse1.setVisibility(View.VISIBLE);
                texthorse1.setText("", null);
                texthorse2.setVisibility(View.VISIBLE);
                texthorse2.setText("", null);


                try {
                    jsonlogin = new JSONObject();
                    jsonlogin.put("option", "game");
                    jsonlogin.put("id", id);
                    jsonlogin.put("number",1);

                    mSocket = IO.socket("http://socrip3.kaist.ac.kr:9089/");
                    mSocket.connect();
                    mSocket.on(Socket.EVENT_CONNECT, onConnect);
                    mSocket.on("serverMessage", onMessageReceived);



                    jsonlogin1 = new JSONObject();
                    jsonlogin1.put("option", "game");
                    jsonlogin1.put("id", id);
                    jsonlogin1.put("number",2);

                    mSocket1 = IO.socket("http://socrip3.kaist.ac.kr:9089/");
                    mSocket1.connect();
                    mSocket1.on(Socket.EVENT_CONNECT, onConnect);
                    mSocket1.on("serverMessage", onMessageReceived);

                    jsonlogin2 = new JSONObject();
                    jsonlogin2.put("option", "game");
                    jsonlogin2.put("id", id);
                    jsonlogin2.put("number",3);



                    mSocket2 = IO.socket("http://socrip3.kaist.ac.kr:9089/");
                    mSocket2.connect();
                    mSocket2.on(Socket.EVENT_CONNECT, onConnect);
                    mSocket2.on("serverMessage", onMessageReceived);




                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch(which) {
                case 1:
                    texthorse.setText(sa);
                    break;
                case 2:
                    texthorse1.setText(sb);
                    break;
                case 3:
                    texthorse2.setText(sc);
                    break;
            }







        }
    };



    // Socket서버에 connect 되면 발생하는 이벤트
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
                    mSocket.emit("clientMessage", jsonlogin);
                    mSocket1.emit("clientMessage", jsonlogin1);
                    mSocket2.emit("clientMessage", jsonlogin2);







        }
    };

    // 서버로부터 전달받은 'chat-message' Event 처리.
    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // 전달받은 데이터는 아래와 같이 추출할 수 있습니다.
            try {

                JSONArray a = (JSONArray) args[0];


                sa = new StringBuilder("");
                sb = new StringBuilder("");
                sc = new StringBuilder("");


                if(a.getJSONObject(0).getString("name").equals("Alpha")) {
                    for (int i = 0; i < a.length(); i++) {
                        JSONObject b = a.getJSONObject(i);
                        sa.append(b.toString());


                    }
                    which=1;
                }

                else if(a.getJSONObject(0).getString("name").equals("Bravo")) {
                    for (int i = 0; i < a.length(); i++) {
                        JSONObject b = a.getJSONObject(i);
                        sb.append(b.toString());
                    }
                    which=2;
                }
                else if(a.getJSONObject(0).getString("name").equals("Charlie")) {
                    for (int i = 0; i < a.length(); i++) {
                        JSONObject b = a.getJSONObject(i);
                        sc.append(b.toString());
                    }
                    which=3;
                }






                Message msg = handler.obtainMessage();

                handler.sendMessage(msg);








            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


}
