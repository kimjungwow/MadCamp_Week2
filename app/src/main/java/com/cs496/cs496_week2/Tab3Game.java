package com.cs496.cs496_week2;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
    private JSONObject jsonlogin, jsonlogin1, jsonlogin2;
    private EditText texthorse, texthorse1, texthorse2;
    private Socket mSocket, mSocket1, mSocket2;
    private StringBuilder sa,sb,sc;
    private View Blue, Green ,Red;
    private float ax,ay,bx,by,cx,cy;
    private View Horse1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab3game);

        Blue = (ImageView)findViewById(R.id.blue);
        Green = (ImageView)findViewById(R.id.green);
        Red = (ImageView)findViewById(R.id.red);







        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        texthorse = findViewById(R.id.texthorse);
        texthorse1 = findViewById(R.id.texthorse1);
        texthorse2 = findViewById(R.id.texthorse2);


        startBtn = findViewById(R.id.startbutton);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Horse1 = (ImageView)findViewById(R.id.horse1);
                ((ImageView) Horse1).setImageResource(R.drawable.runninghorse);
                final AnimationDrawable runningHorse = (AnimationDrawable)((ImageView) Horse1).getDrawable();
                runningHorse.start();





                startBtn.setVisibility(View.GONE);
                texthorse.setText("", null);
//                texthorse.setVisibility(View.VISIBLE);
//                texthorse1.setVisibility(View.VISIBLE);
//                texthorse2.setVisibility(View.VISIBLE);
                texthorse1.setText("", null);

                texthorse2.setText("", null);


                try {
                    jsonlogin = new JSONObject();
                    jsonlogin.put("option", "game");
                    jsonlogin.put("id", id);
                    jsonlogin.put("number",1);

                    mSocket = IO.socket("http://socrip3.kaist.ac.kr:9089/");
                    mSocket.connect();
                    mSocket.on(Socket.EVENT_CONNECT, onConnect);
                    mSocket.on("Alpha", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            System.out.println("\nALPHA\n");
                            try{

                                StringBuilder sd = new StringBuilder("");


                            JSONArray a = (JSONArray) args[0];


                            for (int i = 0; i < a.length(); i++) {
                                JSONObject b = a.getJSONObject(i);
                                sd.append(b.toString());

                                if(i==2) {
                                    ax = (float) b.getInt("location");
                                }
                            }

                            Message msg = new Message();
                            msg.arg1=1;
                            msg.obj=sd;

                            handler.sendMessage(msg);

                            }
                            catch (JSONException e) {

                            }

                        }
                    });

                    mSocket.on("Bravo", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            System.out.println("\nBRAVO\n");
                            try{
//                                sa = new StringBuilder("");
                                StringBuilder sd = new StringBuilder("");
                                JSONArray a = (JSONArray) args[0];

                                for (int i = 0; i < a.length(); i++) {
                                    JSONObject b = a.getJSONObject(i);
                                    sd.append(b.toString());

                                    if(i==2) {
                                        bx = (float) b.getInt("location");
                                    }
                                }

                                Message msg = new Message();
                                msg.arg1=2;
                                msg.obj=sd;


                                handler.sendMessage(msg);

                            }
                            catch (JSONException e) {
                                System.out.println("\nBRAVO EXCEPTION\n");
                            }

                        }
                    });

                    mSocket.on("Charlie", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            System.out.println("\nCHARLIE\n");
                            try{
//                                sa = new StringBuilder("");
                                StringBuilder sd = new StringBuilder("");
                                JSONArray a = (JSONArray) args[0];
                                for (int i = 0; i < a.length(); i++) {
                                    JSONObject b = a.getJSONObject(i);
                                    sd.append(b.toString());

                                    if(i==2) {
                                        cx = (float) b.getInt("location");
                                    }
                                }

                                Message msg = new Message();
                                msg.arg1=3;
                                msg.obj=sd;


                                handler.sendMessage(msg);}
                            catch (JSONException e) {

                            }

                        }
                    });


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
            System.out.println(msg.arg1);


            switch(msg.arg1) {
                case 1:
                    Blue.setVisibility(View.VISIBLE);


                    Blue.setY(12);
                    ax = 15*ax;
                    Blue.setX(ax);

                    Horse1.setY(300);
                    Horse1.setX(ax);






                    texthorse.setText((StringBuilder)msg.obj);
                    break;

                case 2:
                    Green.setVisibility(View.VISIBLE);


                    Green.setY(92);
                    bx = 15*bx;
                    Green.setX(bx);



                    texthorse1.setText((StringBuilder)msg.obj);
                    break;
                case 3:

                    Red.setVisibility(View.VISIBLE);


                    Red.setY(172);
                    cx = 15*cx;
                    Red.setX(cx);
                    texthorse2.setText((StringBuilder)msg.obj);
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
