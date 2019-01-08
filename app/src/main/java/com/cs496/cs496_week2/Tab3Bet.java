package com.cs496.cs496_week2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Tab3Bet extends FragmentActivity {
    private String id, betarray;
    private int balance;
    private int  sel_pos=-1;
    private View betBtn;
    private TextView balanceView;
    private EditText betView;
    private ListView horseListView;
    private Tab3HorseViewAdapter adapter;
    private ArrayList<HorseModel> horseModelArrayList;
    private Socket mSocket;
    private JSONObject jsonlogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab3bet);


        id = getIntent().getStringExtra("id");
        balance = getIntent().getIntExtra("balance", 200000);
        balanceView = (TextView) findViewById(R.id.balanceedit);
        betView = (EditText) findViewById(R.id.betedit);


        balanceView.setText(Integer.toString(balance));


        betarray = getIntent().getStringExtra("horsearray");
        horseListView = findViewById(R.id.horseLV);

        adapter = new Tab3HorseViewAdapter(getApplicationContext(), horseModelArrayList);
        horseModelArrayList = new ArrayList<>();

        betBtn = findViewById(R.id.betbutton);

        betBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (betView.getText() == null) {
                    Toast.makeText(getApplicationContext(), "Please bet money!", Toast.LENGTH_LONG).show();
                } else {
                    int bettingmoney = Integer.parseInt(betView.getText().toString());
                    if (bettingmoney > balance) {
                        Toast.makeText(getApplicationContext(), "Please bet less than your balance!", Toast.LENGTH_LONG).show();
                    } else {
                        if(horseListView.getCheckedItemCount()!=0 && sel_pos!=-1) {

                            Intent resultintent = new Intent();
                            //resultintent.putExtra("bet", 숫자);
                            resultintent.putExtra("name", horseModelArrayList.get(sel_pos).getName());
                            resultintent.putExtra("bet", bettingmoney);
                            setResult(RESULT_OK, resultintent);
                            finish();
                        }
                    }
                }
            }
        });

        try {
            jsonlogin = new JSONObject();
            jsonlogin.put("option", "game");
//            jsonlogin.put("id", id);
//            jsonlogin.put("number", 1);

            mSocket = IO.socket("http://socrip3.kaist.ac.kr:9089/");
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);


            mSocket.on("FirstHorseInfo", new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    try {

                        JSONArray m = new JSONArray(args[0].toString());


                        //                        betarray = args[0].toString();
//                        JSONArray m = new JSONArray(betarray);
                        horseModelArrayList.removeAll(horseModelArrayList);
                        for (int i = 0; i < m.length(); i++) {
                            JSONObject k = m.getJSONObject(i);
                            HorseModel horseModel = new HorseModel();
                            horseModel.setName(k.getString("name"));
                            horseModel.setDividendRate(k.getDouble("dividendRate"));

                            horseModel.setFallOff(k.getDouble("fallOff"));

                            horseModel.setMaxSpeed(k.getInt("maxSpeed"));
                            horseModel.setSpeed(k.getDouble("speed"));
                            horseModel.setAcceleration(k.getDouble("acceleration"));
                            horseModelArrayList.add(horseModel);


                        }


                        Message msg = new Message();

//                        msg.obj=m;
                        handler.sendMessage(msg);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        horseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (sel_pos == position) {
                    horseListView.setAdapter(adapter);
                    sel_pos = -1;
                } else {
                    sel_pos = position;
                }

            }

        });
    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
//            try {
            adapter = new Tab3HorseViewAdapter(getApplicationContext(), horseModelArrayList);
            if (!(adapter.isEmpty())) {
                horseListView.setAdapter(adapter);
            }
//            } catch (JSONException e){}

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
