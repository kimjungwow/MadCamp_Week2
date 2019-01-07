package com.cs496.cs496_week2;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
    private JSONObject jsonlogin;
    private Socket mSocket, mSocket1, mSocket2;

    private float ax, ay, bx, by, cx, cy, dx, dy, ex, ey;
    private View Horse1, Horse2, Horse3, Horse4, Horse5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab3game);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        startBtn = findViewById(R.id.startbutton);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Horse1 = (ImageView) findViewById(R.id.horse1);
                ((ImageView) Horse1).setImageResource(R.drawable.runninghorse);
                final AnimationDrawable runningHorse = (AnimationDrawable) ((ImageView) Horse1).getDrawable();
                runningHorse.start();

                Horse2 = (ImageView) findViewById(R.id.horse2);
                ((ImageView) Horse2).setImageResource(R.drawable.runninghorsered);
                final AnimationDrawable runningHorse2 = (AnimationDrawable) ((ImageView) Horse2).getDrawable();
                runningHorse2.start();

                Horse3 = (ImageView) findViewById(R.id.horse3);
                ((ImageView) Horse3).setImageResource(R.drawable.runninghorsegreen);
                final AnimationDrawable runningHorse3 = (AnimationDrawable) ((ImageView) Horse3).getDrawable();
                runningHorse3.start();

                Horse4 = (ImageView) findViewById(R.id.horse4);
                ((ImageView) Horse4).setImageResource(R.drawable.runninghorseyellow);
                final AnimationDrawable runningHorse4 = (AnimationDrawable) ((ImageView) Horse4).getDrawable();
                runningHorse4.start();

                Horse5 = (ImageView) findViewById(R.id.horse5);
                ((ImageView) Horse5).setImageResource(R.drawable.runninghorseblue);
                final AnimationDrawable runningHorse5 = (AnimationDrawable) ((ImageView) Horse5).getDrawable();
                runningHorse5.start();


                startBtn.setVisibility(View.GONE);




                try {
                    jsonlogin = new JSONObject();
                    jsonlogin.put("option", "game");
                    jsonlogin.put("id", id);
                    jsonlogin.put("number", 1);

                    mSocket = IO.socket("http://socrip3.kaist.ac.kr:9089/");
                    mSocket.connect();
                    mSocket.on(Socket.EVENT_CONNECT, onConnect);


                    mSocket.on("Alpha", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {

                            try {
                                StringBuilder sd = new StringBuilder("");
                                JSONArray a = (JSONArray) args[0];
                                for (int i = 0; i < a.length(); i++) {
                                    JSONObject b = a.getJSONObject(i);
                                    sd.append(b.toString());
                                    if (i == 2) {
                                        ax = (float) b.getInt("location");
                                    }
                                }
                                Message msg = new Message();
                                msg.arg1 = 1;
                                msg.obj = sd;
                                handler.sendMessage(msg);
                            } catch (JSONException e) { }
                        }
                    });
                    mSocket.on("Bravo", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {

                            try {
                                StringBuilder sd = new StringBuilder("");
                                JSONArray a = (JSONArray) args[0];
                                for (int i = 0; i < a.length(); i++) {
                                    JSONObject b = a.getJSONObject(i);
                                    sd.append(b.toString());
                                    if (i == 2) {
                                        bx = (float) b.getInt("location");
                                    }
                                }
                                Message msg = new Message();
                                msg.arg1 = 2;
                                msg.obj = sd;
                                handler.sendMessage(msg);
                            } catch (JSONException e) {}
                        }
                    });
                    mSocket.on("Charlie", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {

                            try {
                                StringBuilder sd = new StringBuilder("");
                                JSONArray a = (JSONArray) args[0];
                                for (int i = 0; i < a.length(); i++) {
                                    JSONObject b = a.getJSONObject(i);
                                    sd.append(b.toString());
                                    if (i == 2) {
                                        cx = (float) b.getInt("location");
                                    }
                                }
                                Message msg = new Message();
                                msg.arg1 = 3;
                                msg.obj = sd;
                                handler.sendMessage(msg);
                            } catch (JSONException e) { }
                        }
                    });
                    mSocket.on("Delta", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {

                            try {
                                StringBuilder sd = new StringBuilder("");
                                JSONArray a = (JSONArray) args[0];
                                for (int i = 0; i < a.length(); i++) {
                                    JSONObject b = a.getJSONObject(i);
                                    sd.append(b.toString());
                                    if (i == 2) {
                                        dx = (float) b.getInt("location");
                                    }
                                }
                                Message msg = new Message();
                                msg.arg1 = 4;
                                msg.obj = sd;
                                handler.sendMessage(msg);
                            } catch (JSONException e) {}
                        }
                    });

                    mSocket.on("Echo", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {

                            try {
                                StringBuilder sd = new StringBuilder("");
                                JSONArray a = (JSONArray) args[0];
                                for (int i = 0; i < a.length(); i++) {
                                    JSONObject b = a.getJSONObject(i);
                                    sd.append(b.toString());
                                    if (i == 2) {
                                        ex = (float) b.getInt("location");
                                    }
                                }
                                Message msg = new Message();
                                msg.arg1 = 5;
                                msg.obj = sd;
                                handler.sendMessage(msg);
                            } catch (JSONException e) {}
                        }
                    });


                } catch (URISyntaxException e) { // To open socket
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            System.out.println(msg.arg1);


            switch (msg.arg1) {
                case 1:
                    ax = 3 * ax;
                    Horse1.setY(10);
                    Horse1.setX(ax);
                    break;
                case 2:
                    bx = 3 * bx;
                    Horse2.setY(210);
                    Horse2.setX(bx);
                    break;
                case 3:
                    cx = 3 * cx;
                    Horse3.setY(410);
                    Horse3.setX(cx);
                    break;
                case 4:
                    dx = 3 * dx;
                    Horse4.setY(610);
                    Horse4.setX(dx);
                    break;
                case 5:
                    ex = 3 * ex;
                    Horse5.setY(810);
                    Horse5.setX(ex);
                    break;
            }
        }
    };


    // Socket서버에 connect 되면 발생하는 이벤트
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.emit("clientMessage", jsonlogin);
        }
    };

}
