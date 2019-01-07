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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Tab3Game extends FragmentActivity {
    String TAG = "Tab3Gamee";

    private View startBtn, betBtn;
    private String id;
    private JSONObject jsonlogin;
    private Socket mSocket;
    private JSONArray gotjsonarray;
    private HashMap<String, View> horsemap;
    private HashMap<String, Number> horsex;


    private int betmoney;
    private float ax, ay, bx, by, cx, cy, dx, dy, ex, ey;
    private View Horse1, Horse2, Horse3, Horse4, Horse5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab3game);
        horsemap = new HashMap<>();
        horsex= new HashMap<>();
        Horse1 = (ImageView) findViewById(R.id.horse1);
        Horse2 = (ImageView) findViewById(R.id.horse2);
        Horse3 = (ImageView) findViewById(R.id.horse3);
        Horse4 = (ImageView) findViewById(R.id.horse4);
        Horse5 = (ImageView) findViewById(R.id.horse5);
        horsegone();



        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        betBtn = findViewById(R.id.betbutton);
        betBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent betintent = new Intent(getApplicationContext(), Tab3Bet.class);
                betintent.putExtra("id", id);
                startActivityForResult(betintent, 1);

            }
        });

        startBtn = findViewById(R.id.startbutton);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((ImageView) Horse1).setImageResource(R.drawable.runninghorse);
                final AnimationDrawable runningHorse = (AnimationDrawable) ((ImageView) Horse1).getDrawable();
                runningHorse.start();
                horsemap.put("Alpha", Horse1);
                horsex.put("Alpha", 10);
                Horse1.setY(-500);


                ((ImageView) Horse2).setImageResource(R.drawable.runninghorsered);
                final AnimationDrawable runningHorse2 = (AnimationDrawable) ((ImageView) Horse2).getDrawable();
                runningHorse2.start();
                horsemap.put("Bravo", Horse2);
                horsex.put("Bravo", 210);
                Horse2.setY(-300);


                ((ImageView) Horse3).setImageResource(R.drawable.runninghorsegreen);
                final AnimationDrawable runningHorse3 = (AnimationDrawable) ((ImageView) Horse3).getDrawable();
                runningHorse3.start();
                horsemap.put("Charlie", Horse3);
                horsex.put("Charlie", 410);
                Horse3.setY(-100);


                ((ImageView) Horse4).setImageResource(R.drawable.runninghorseyellow);
                final AnimationDrawable runningHorse4 = (AnimationDrawable) ((ImageView) Horse4).getDrawable();
                runningHorse4.start();
                horsemap.put("Delta", Horse4);
                horsex.put("Delta", 610);
                Horse4.setY(100);

                ((ImageView) Horse5).setImageResource(R.drawable.runninghorseblue);
                final AnimationDrawable runningHorse5 = (AnimationDrawable) ((ImageView) Horse5).getDrawable();
                runningHorse5.start();
                horsemap.put("Echo", Horse5);
                horsex.put("Echo", 810);
                Horse5.setY(300);

                startBtn.setVisibility(View.GONE);


                try {
                    jsonlogin = new JSONObject();
                    jsonlogin.put("option", "game");
                    jsonlogin.put("id", id);
                    jsonlogin.put("number", 1);

                    mSocket = IO.socket("http://socrip3.kaist.ac.kr:9089/");
                    mSocket.connect();
                    mSocket.on(Socket.EVENT_CONNECT, onConnect);


                    mSocket.on("HorseInfo", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {

                            try{

                                JSONArray m = new JSONArray(args[0].toString());


                            Message msg = new Message();

                            msg.obj=m;
                            handler.sendMessage(msg);


                            }catch (JSONException e) {e.printStackTrace();}

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {


                betmoney = data.getIntExtra("bet", 50000);

            }
        }

    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
                    try {
                        horsevisible();
                        betBtn.setVisibility(View.GONE);

                        JSONArray t = (JSONArray) msg.obj;
                        for (int i = 0; i < t.length(); i++) {
                            JSONObject ob = t.getJSONObject(i);
                            horsemap.get(ob.getString("name")).setX(3*((float)ob.getInt("location")));

                        }
                    } catch (JSONException e){}

        }
    };


    // Socket서버에 connect 되면 발생하는 이벤트
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.emit("clientMessage", jsonlogin);
        }
    };

    private void horsevisible() {
        Horse1.setVisibility(View.VISIBLE);
        Horse2.setVisibility(View.VISIBLE);
        Horse3.setVisibility(View.VISIBLE);
        Horse4.setVisibility(View.VISIBLE);
        Horse5.setVisibility(View.VISIBLE);
    }

    private void horsegone() {
        Horse1.setVisibility(View.GONE);
        Horse2.setVisibility(View.GONE);
        Horse3.setVisibility(View.GONE);
        Horse4.setVisibility(View.GONE);
        Horse5.setVisibility(View.GONE);
    }

}
