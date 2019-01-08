package com.cs496.cs496_week2;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
    private String id, sel_name, winner, timestring;
    private int balance;
    private JSONObject jsonlogin, jsonbet;
    private Socket mSocket;
    private JSONArray gotjsonarray;
    private HashMap<String, View> horsemap;
    private HashMap<String, Number> horsex;
    private HashMap<String, Number> horsewin;
    private int width, height, imageviewwidth, imageviewheight;



    private int betmoney;
    private float ax, ay, bx, by, cx, cy, dx, dy, ex, ey;
    private View Horse1, Horse2, Horse3, Horse4, Horse5;
    private TextView resttime, forresttime;
    private ImageView HorseWinImage, totoimage;
    private String betarray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab3game);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        Log.e("gggg", "width " + width);
        Log.e("gggg", "height " + height);
        imageviewwidth = width / 6;
        imageviewheight=  height/6;


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

            mSocket.on("GameResult", new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    try {
                        JSONArray m = new JSONArray(args[0].toString());
                        for(int z=0; z<m.length(); z++) {
                            JSONObject inm = m.getJSONObject(z);

                            if (inm.getString("id").equals(id)) {
                                balance = inm.getInt("balance");
                                winner = inm.getString("horse");
                                break;

                            }

                        }

//                        JSONObject m = new JSONObject(args[0].toString());



//                        balance = balance + finishbalance;




                        Message msg = new Message();

                        finishhandler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            mSocket.on("timeLeft", new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    timestring=args[0].toString();

                    Message msg = new Message();

                    timehandler.sendMessage(msg);
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
        horsewin = new HashMap<>();

        horsewin.put("Alpha", R.drawable.winblack);
        horsewin.put("Bravo", R.drawable.winred);
        horsewin.put("Charlie", R.drawable.wingreen);
        horsewin.put("Delta", R.drawable.winyellow);
        horsewin.put("Echo", R.drawable.winblue);



        Horse1 = (ImageView) findViewById(R.id.horse1);
        Horse2 = (ImageView) findViewById(R.id.horse2);
        Horse3 = (ImageView) findViewById(R.id.horse3);
        Horse4 = (ImageView) findViewById(R.id.horse4);
        Horse5 = (ImageView) findViewById(R.id.horse5);
        HorseWinImage = (ImageView) findViewById(R.id.horsewin);
        totoimage = (ImageView) findViewById(R.id.totoview);

        resttime = (TextView) findViewById(R.id.timetext);
        forresttime = (TextView) findViewById(R.id.fortimetext);






        // Change imageview size
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) Horse1.getLayoutParams();
        params.width = imageviewwidth;
        params.height = imageviewheight;
        Horse1.setLayoutParams(params);

        ViewGroup.LayoutParams params2 = (ViewGroup.LayoutParams) Horse2.getLayoutParams();
        params2.width = imageviewwidth;
        params2.height = imageviewheight;
        Horse2.setLayoutParams(params2);

        ViewGroup.LayoutParams params3 = (ViewGroup.LayoutParams) Horse3.getLayoutParams();
        params3.width = imageviewwidth;
        params3.height = imageviewheight;
        Horse3.setLayoutParams(params3);

        ViewGroup.LayoutParams params4 = (ViewGroup.LayoutParams) Horse4.getLayoutParams();
        params4.width = imageviewwidth;
        params4.height = imageviewheight;
        Horse4.setLayoutParams(params4);

        ViewGroup.LayoutParams params5 = (ViewGroup.LayoutParams) Horse5.getLayoutParams();
        params5.width = imageviewwidth;
        params5.height = imageviewheight;
        Horse5.setLayoutParams(params5);

//        ViewGroup.LayoutParams params6 = (ViewGroup.LayoutParams) HorseWinImage.getLayoutParams();
//        params6.width = imageviewwidth;
//        params6.height = imageviewheight;
//        HorseWinImage.setLayoutParams(params6);



        horsegone();
        forresttime.setVisibility(View.GONE);

        Horse1.setY(180);
        Horse2.setY(320);
        Horse3.setY(450);
        Horse4.setY(630);
        Horse5.setY(820);

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
                totoimage.setVisibility(View.GONE);
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
                forresttime.setVisibility(View.VISIBLE);



                try {
                    jsonbet = new JSONObject();
                    jsonbet.put("option", "bet");
                    jsonbet.put("id", id);
                    jsonbet.put("betmoney", betmoney);
                    jsonbet.put("sel_name", sel_name);
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
                HorseWinImage.setVisibility(View.GONE);

                betBtn.setVisibility(View.GONE);
                JSONArray t = (JSONArray) msg.obj;
                for (int i = 0; i < t.length(); i++) {
                    JSONObject ob = t.getJSONObject(i);
                    float xx = (float) ob.getInt("location");
                    if (xx>500){
                        horsemap.get(ob.getString("name")).setX(((float)(width/600)) * ((float)500));
                    } else {

                    horsemap.get(ob.getString("name")).setX(((float)(width/600)) * ((float) ob.getInt("location")));
                    }
                }
            } catch (JSONException e) {
            }

        }
    };

    final Handler finishhandler = new Handler() {
        public void handleMessage(Message msg) {


            HorseWinImage.setImageResource((int)horsewin.get(winner));


            HorseWinImage.setX(width/4);
            HorseWinImage.setY(height/8
            );
//            HorseWinImage.setX(horsemap.get(winner).getLeft());
//            HorseWinImage.setY(horsemap.get(winner).getTop());


            HorseWinImage.setVisibility(View.VISIBLE);
            horsegone();

            betBtn.setVisibility(View.VISIBLE);


        }
    };



    final Handler timehandler = new Handler() {
        public void handleMessage(Message msg) {

            resttime.setText(timestring);


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
        resttime.setVisibility(View.GONE);
        forresttime.setVisibility(View.GONE);
    }

    private void horsegone() {
        Horse1.setVisibility(View.GONE);
        Horse2.setVisibility(View.GONE);
        Horse3.setVisibility(View.GONE);
        Horse4.setVisibility(View.GONE);
        Horse5.setVisibility(View.GONE);
        resttime.setVisibility(View.VISIBLE);
        forresttime.setVisibility(View.VISIBLE);
    }

}
