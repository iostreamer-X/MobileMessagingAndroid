package io.streamer.oose;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.define.Define;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;



public class Chat extends AppCompatActivity {



    private EditText messageET;
    private static ListView messagesContainer;
    private FloatingActionButton sendBtn;
    private static ChatAdapter adapter;
    String name;
    String phone;
    boolean running = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);

        initControls();

        Util.loadChatHistory(Main2Activity.phone, phone);
        new Thread(){
            @Override
            public void run() {
                while (running){
                    Util.updateChat(Main2Activity.phone, phone);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (FloatingActionButton) findViewById(R.id.chatSendButton);
        adapter = new ChatAdapter(Chat.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        sendBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FishBun.with(Chat.this).startAlbum();
                return true;
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                Util.sendMessage(Main2Activity.phone, phone, messageText);
                messageET.setText("");
            }
        });
    }

    public static void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> paths = imageData.getStringArrayListExtra(Define.INTENT_PATH);
                    for(String path:paths)
                        try {
                            String file = Base64.encodeFromFile(path);
                            Util.sendMessage(Main2Activity.phone, phone, file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    break;
                }
        }
    }
}
