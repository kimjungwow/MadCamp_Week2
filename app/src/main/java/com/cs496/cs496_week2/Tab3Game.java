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
    private String id, sel_name;
    private int balance;
    private JSONObject jsonlogin, jsonbet;
    private Socket mSocket;
    private JSONArray gotjsonarray;
    private HashMap<String, View> horsemap;
    private HashMap<String, Number> horsex;


    private int betmoney;
    private float ax, ay, bx, by, cx, cy, dx, dy, ex, ey;
    private View Horse1, Horse2, Horse3, Horse4, Horse5;
    private String betarray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab3game);


        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        balance = intent.getIntExtra("balance", 100000);

        try {
            jsonlogin = new JSONObject();
            jsonlogin.put("option", "game");
            mSocket = IO.socket("http://socrip3.kaist.ac.kr:9089/");
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on("HorseInfo", new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    try {
                        JSONArray m = new JSONArray(args[0].toString());
                        betarray = args[0].toString();
                        Message msg = new Message();
                        msg.obj = m;
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            mSocket.on("FirstHorseInfo", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    betarray=args[0].toString();
                }
            });
            mSocket.connect();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        horsemap = new HashMap<>();
        horsex = new HashMap<>();
        Horse1 = (ImageView) findViewById(R.id.horse1);
        Horse2 = (ImageView) findViewById(R.id.horse2);
        Horse3 = (ImageView) findViewById(R.id.horse3);
        Horse4 = (ImageView) findViewById(R.id.horse4);
        Horse5 = (ImageView) findViewById(R.id.horse5);
        horsegone();

        Horse1.setY(-500);
        Horse2.setY(-300);
        Horse3.setY(-100);
        Horse4.setY(100);
        Horse5.setY(300);

        ((ImageView) Horse1).setImageResource(R.drawable.runninghorse);
        final AnimationDrawable runningHorse = (AnimationDrawable) ((ImageView) Horse1).getDrawable();
        runningHorse.start();
        horsemap.put("Alpha", Horse1);
        horsex.put("Alpha", 10);


        ((ImageView) Horse2).setImageResource(R.drawable.runninghorsered);
        final AnimationDrawable runningHorse2 = (AnimationDrawable) ((ImageView) Horse2).getDrawable();
        runningHorse2.start();
        horsemap.put("Bravo", Horse2);
        horsex.put("Bravo", 210);


        ((ImageView) Horse3).setImageResource(R.drawable.runninghorsegreen);
        final AnimationDrawable runningHorse3 = (AnimationDrawable) ((ImageView) Horse3).getDrawable();
        runningHorse3.start();
        horsemap.put("Charlie", Horse3);
        horsex.put("Charlie", 410);


        ((ImageView) Horse4).setImageResource(R.drawable.runninghorseyellow);
        final AnimationDrawable runningHorse4 = (AnimationDrawable) ((ImageView) Horse4).getDrawable();
        runningHorse4.start();
        horsemap.put("Delta", Horse4);
        horsex.put("Delta", 610);


        ((ImageView) Horse5).setImageResource(R.drawable.runninghorseblue);
        final AnimationDrawable runningHorse5 = (AnimationDrawable) ((ImageView) Horse5).getDrawable();
        runningHorse5.start();
        horsemap.put("Echo", Horse5);
        horsex.put("Echo", 810);


        betBtn = findViewById(R.id.betbutton);
        startBtn = findViewById(R.id.startbutton);

        startBtn.setEnabled(false);

        betBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent betintent = new Intent(getApplicationContext(), Tab3Bet.class);
                betintent.putExtra("id", id);
                betintent.putExtra("horsearray", betarray);
                betintent.putExtra("balance", balance);
                startActivityForResult(betintent, 1);

            }
        });


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                startBtn.setVisibility(View.GONE);
                betBtn.setVisibility(View.GONE);



                try {
                    jsonbet = new JSONObject();
                    jsonbet.put("option", "bet");
                    jsonbet.put("id", id);
                    jsonbet.put("betmoney", betmoney);
                    jsonbet.put("name", sel_name);
                    mSocket.emit("gameMessage", jsonbet);


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
                sel_name = data.getStringExtra("name");
                startBtn.setEnabled(true);
            }
        }
    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                horsevisible();
                JSONArray t = (JSONArray) msg.obj;
                for (int i = 0; i < t.length(); i++) {
                    JSONObject ob = t.getJSONObject(i);
                    float xx = (float) ob.getInt("location");
                    if (xx>500){
                        horsemap.get(ob.getString("name")).setX(3 * 500);
                    } else {

                    horsemap.get(ob.getString("name")).setX(3 * ((float) ob.getInt("location")));
                    }
                }
            } catch (JSONException e) {
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
