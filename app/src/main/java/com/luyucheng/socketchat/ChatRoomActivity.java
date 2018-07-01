package com.luyucheng.socketchat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int UPDATE = 1;
    private Socket socket = null;
    private EditText edit;
    private Button send;
    private RecyclerView msg_recyclerView;
    private MsgAdapter adapter;
    private List<Msg> msgs = new ArrayList<Msg>();//存储消息容器
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //更新消息列表
                case UPDATE: {
                    Bundle bundle = msg.getData();
                    String msg_get = bundle.getString("msg");
                    Msg msg1 = new Msg(msg_get);
                    msgs.add(msg1);
                    adapter.notifyDataSetChanged();
                }
                break;
            }
        }
    };
    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        name=getInfo();
        Log.d("Luyucheng", name);

        //从服务器读数进程
        new Thread(new Runnable() {
            private String msg_get = "";

            @Override
            public void run() {
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress("118.24.85.15", 12345));

                    //input
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    while ((msg_get = br.readLine()) != null) {
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        Log.d("Luyucheng", msg_get);
                        bundle.putString("msg", msg_get);
                        message.setData(bundle);
                        message.what = UPDATE;
                        handler.sendMessage(message);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        edit = findViewById(R.id.input_text);
        send = findViewById(R.id.send);
        send.setOnClickListener(this);

        msg_recyclerView = findViewById(R.id.msg_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        msg_recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MsgAdapter(msgs);
        msg_recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send: {
                String s = name + ":" + edit.getText().toString();
                edit.setText("");
                new Thread(new SocketThread(s)).start();
            }
            break;
        }
    }
    private String getInfo()
    {
        TelephonyManager mTm = (TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
        String mtype = android.os.Build.MODEL; // 手机型号
        return mtype;
    }


//    @Override
//    public void onBackPressed() {
//        try {
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
//            bw.write("-用户退出-");
//            bw.newLine();
//            bw.flush();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        super.onBackPressed();
//    }

    //back事件

    //向服务器发数据进程
    class SocketThread extends Thread {
        private String msg;
        //Socket socket;

        public SocketThread(String m) {
            msg = m;
        }

        @Override
        public void run() {
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                bw.write(msg);
                bw.newLine();
                bw.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}