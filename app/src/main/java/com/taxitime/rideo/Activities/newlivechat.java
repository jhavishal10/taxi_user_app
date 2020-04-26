package com.taxitime.rideo.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.taxitime.rideo.API.RetrofitClient;
import com.taxitime.rideo.Models.Chat;
import com.taxitime.rideo.Models.ChatResponse;
import com.taxitime.rideo.Models.Errorresponse;
import com.taxitime.rideo.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class newlivechat extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Chat> chatList;
    private ProgressDialog mProg;
    private Intent mainIntent;
    private String name, img, uid, pid ,booking_id;
    private int lastsize ;
    private EditText chatedittext;
    private ImageView sendbtn;
    private ImageView btnchatsend;
    private  ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newlivechat);
        mainIntent = getIntent();
        chatedittext = findViewById(R.id.chataddtext);
        btnchatsend = findViewById(R.id.btnchatsend);
        recyclerView = findViewById(R.id.chatrecycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProg = new ProgressDialog(this);
        mProg.setTitle(R.string.app_name);
        mProg.setMessage("Loading...");
        mProg.show();
        name = mainIntent.getStringExtra("name");
        uid = mainIntent.getStringExtra("uid");
        pid = mainIntent.getStringExtra("pid");
        booking_id = mainIntent.getStringExtra("booking_id");
        //img = mainIntent.getStringExtra("img");

        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Call<ChatResponse> call = RetrofitClient.getInstance().getApi().getchat(booking_id);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                chatList = response.body().getChat();
                chatadapter adaper = new chatadapter(newlivechat.this, chatList);
                recyclerView.setAdapter(adaper);
                mProg.dismiss();
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                mProg.dismiss();
                finish();
            }
        });
        btnchatsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chatedittext.getText().toString().trim().equals("")||chatedittext.getText().toString().equals(" ")){

                }else{
                    Call<Errorresponse> call = RetrofitClient.getInstance().getApi().addchat(booking_id, uid, pid, chatedittext.getText().toString().trim(), "up");
                    call.enqueue(new Callback<Errorresponse>() {
                        @Override
                        public void onResponse(Call<Errorresponse> call, Response<Errorresponse> response) {
                            if(!response.body().isError()){
                                chatedittext.setText("");
                                recyclerView.scrollToPosition(lastsize - 1);
                               refreshchat();
                            }
                        }

                        @Override
                        public void onFailure(Call<Errorresponse> call, Throwable t) {

                        }
                    });
                }
            }
        });
        chatedittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.scrollToPosition(lastsize - 1);
            }
        });
        Handler handleCheckStatus = new Handler();
        //check status every 5 sec
        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshchat();
                handleCheckStatus.postDelayed(this, 5000);
            }
        }, 5000);



    }



    private void refreshchat() {

        Call<ChatResponse> call = RetrofitClient.getInstance().getApi().getchat(booking_id);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                chatList = response.body().getChat();
                if(lastsize != chatList.size()){
                    chatadapter adaper = new chatadapter(newlivechat.this, chatList);
                    recyclerView.setAdapter(adaper);
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                mProg.dismiss();
                finish();
            }
        });
    }

    private class chatadapter extends RecyclerView.Adapter<chatadapter.chatholder>{
        private Context mCtx;
        private List<Chat> chats;

        public chatadapter(Context mCtx, List<Chat> chats) {
            this.mCtx = mCtx;
            this.chats = chats;
        }

        @NonNull
        @Override
        public chatholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mCtx).inflate(R.layout.recycle_chat_live,viewGroup,false);
            return new chatadapter.chatholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull chatholder holder, int i) {
            Chat chat = chats.get(i);
            String type = chat.getType();
            if(type.equals("up")){
                holder.receiverlayout.setVisibility(View.VISIBLE);
                holder.chatmessage.setText(chat.getMessage());
            }else{
                holder.senderlayout.setVisibility(View.VISIBLE);
                holder.chatusermessage.setText(chat.getMessage());
                holder.chatusername.setText(name);
            }


        }

        @Override
        public int getItemCount() {
            lastsize= chats.size();
            return  chats.size();
        }

        class chatholder extends RecyclerView.ViewHolder{
            ImageView chatuserimg;
            TextView chatusername, chatusermessage, chatmessage;
            LinearLayout senderlayout, receiverlayout;

            public chatholder(@NonNull View itemView) {
                super(itemView);
                chatuserimg = itemView.findViewById(R.id.chatuserimg);
                chatusername = itemView.findViewById(R.id.chatusername);
                chatusermessage = itemView.findViewById(R.id.chatusermessage);
                chatmessage= itemView.findViewById(R.id.chatmessage);
                senderlayout = itemView.findViewById(R.id.senderlayout);
                receiverlayout = itemView.findViewById(R.id.receiverlayout);
            }
        }
    }
}
