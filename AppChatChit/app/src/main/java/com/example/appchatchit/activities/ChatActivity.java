package com.example.appchatchit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.appchatchit.R;
import com.example.appchatchit.adapters.ViewChatAdapter;
import com.example.appchatchit.models.Chat;
import com.example.appchatchit.models.Profile;
import com.example.appchatchit.resources.Enum;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    DatabaseReference databaseReference = Enum.DATABASE_REFERENCE;
    SharedPreferences sharedPreferences;
    private long idUser = -1;

    RecyclerView recyclerView;
    ArrayList<Chat> arrayList;
    ViewChatAdapter viewChatAdapter;
    ImageView ic_back;
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sharedPreferences = getBaseContext().getSharedPreferences(Enum.PRE_LOGIN, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getLong(Enum.ID_USER,-1);
        alertDialog = Enum.DIALOG(this);
        //Ánh xạ
        recyclerView = findViewById(R.id.recyclerViewDoc);
        ic_back = findViewById(R.id.ic_back);

        setData();
        setEvent();
    }
    //Set dữ liệu
    private void setData(){
        arrayList = new ArrayList<>();
        viewChatAdapter = new ViewChatAdapter(this,R.layout.card_view_chat,arrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(viewChatAdapter);
        viewChatAdapter.notifyDataSetChanged();

        databaseReference.child(Enum.MY_CHAT_TABLE).child(String.valueOf(idUser)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()>0){
                    arrayList.clear();
                    for (DataSnapshot snap: snapshot.getChildren()){
                        long idChat = snap.getValue(Long.class);
                        databaseReference.child(Enum.CHAT_TABLE).child(String.valueOf(idChat)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Chat chat = snapshot.getValue(Chat.class);
                                if(!Enum.kiemTraTonTaiChat(chat.getId(),arrayList)){
                                    arrayList.add(chat);
                                }else{
                                    Enum.swap(chat.getId(),arrayList,chat);
                                }
                                viewChatAdapter.notifyDataSetChanged();
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    viewChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setEvent(){
        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        viewChatAdapter.setOnClickItemListener(new ViewChatAdapter.OnClickItemListener() {
            @Override
            public void onClickItem(int position, View v) {
                Intent intent = new Intent(ChatActivity.this,MessageActivity.class);
                intent.putExtra(Enum.KEY_ROOM_CHAT,arrayList.get(position).getId());
                if(idUser!=arrayList.get(position).getIdAccount1()){
                    intent.putExtra(Enum.ID_RECEIVER,arrayList.get(position).getIdAccount1());

                }else{
                    intent.putExtra(Enum.ID_RECEIVER,arrayList.get(position).getIdAccount2());
                }

                startActivity(intent);
            }

            @Override
            public void onChat(int position, View v) {

            }
        });
    }
}