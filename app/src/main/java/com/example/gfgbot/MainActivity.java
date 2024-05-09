package com.example.gfgbot;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView chatsRV;
    private ImageButton sendMsgIB;
    private EditText userMsgEdt;
    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";
     private RequestQueue mRequestQueue;
     private ArrayList<MessageModal> messageModalArrayList;
    private MessageRVAdapter messageRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         chatsRV = findViewById(R.id.idRVChats);
        sendMsgIB = findViewById(R.id.idIBSend);
        userMsgEdt = findViewById(R.id.idEdtMessage);
         mRequestQueue = Volley.newRequestQueue(MainActivity.this);
        mRequestQueue.getCache().clear();
         messageModalArrayList = new ArrayList<>();
         sendMsgIB.setOnClickListener(v -> {
             if (userMsgEdt.getText().toString().isEmpty()) {
                //if the edit text is empty display a toast message.
                Toast.makeText(MainActivity.this, "Please enter your message..", Toast.LENGTH_SHORT).show();
                return;
            }
            sendMessage(userMsgEdt.getText().toString());
             userMsgEdt.setText("");

        });

         messageRVAdapter = new MessageRVAdapter(messageModalArrayList, this);
         LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
         chatsRV.setLayoutManager(linearLayoutManager);
         chatsRV.setAdapter(messageRVAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sendMessage(String userMsg) {
        //below line is to pass message to our array list which is entered by the user.
        messageModalArrayList.add(new MessageModal(userMsg, USER_KEY));
        messageRVAdapter.notifyDataSetChanged();

         RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
         JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://api.brainshop.ai/get?bid=181442&key=y5DjBMqUFGxSp3U3&uid=uid&msg=" + userMsg, null, response -> {
             try {
                  String botResponse = response.getString("cnt");
                 messageModalArrayList.add(new MessageModal(botResponse, BOT_KEY));
                  messageRVAdapter.notifyDataSetChanged();
             } catch (JSONException e) {
                 e.printStackTrace();
                  messageModalArrayList.add(new MessageModal("No response", BOT_KEY));
                 messageRVAdapter.notifyDataSetChanged();

             }

         }, error -> {
              messageModalArrayList.add(new MessageModal("Sorry no response found", BOT_KEY));
             Toast.makeText(MainActivity.this, "No response from the bot..", Toast.LENGTH_SHORT).show();
         });
         queue.add(jsonObjectRequest);


    }
}