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
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appchatchit.R;
import com.example.appchatchit.adapters.ViewMessageApdater;
import com.example.appchatchit.models.Chat;
import com.example.appchatchit.models.Message;
import com.example.appchatchit.models.Profile;
import com.example.appchatchit.resources.Enum;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    DatabaseReference databaseReference = Enum.DATABASE_REFERENCE;
    SharedPreferences sharedPreferences;
    private long idUser = -1;
    private long idRoomChat = -1;
    private long idReceiver = -1;

    ImageView ic_back;
    ArrayList<Message> arrayList;
    ViewMessageApdater viewMessageApdater;
    RecyclerView recyclerViewDoc;
    AlertDialog alertDialog;

    CircleImageView avt;
    TextView name_chat;
    TextView trangThai;
    EditText input_chat;
    ImageView send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        sharedPreferences = getBaseContext().getSharedPreferences(Enum.PRE_LOGIN, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getLong(Enum.ID_USER,-1);
        idRoomChat = getIntent().getLongExtra(Enum.KEY_ROOM_CHAT,Enum.NULL_INT);
        idReceiver = getIntent().getLongExtra(Enum.ID_RECEIVER,Enum.NULL_INT);
        //Ánh xạ
        ic_back = findViewById(R.id.ic_back);
        recyclerViewDoc = findViewById(R.id.recyclerViewDoc);
        avt = findViewById(R.id.avt);
        name_chat = findViewById(R.id.name_chat);
        trangThai = findViewById(R.id.trang_thai);
        input_chat = findViewById(R.id.input_chat);
        send = findViewById(R.id.send);
        alertDialog = Enum.DIALOG(this);

    }

    @Override
    protected void onResume() {
        databaseReference.child(Enum.CHAT_TABLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()>0){
                    for(DataSnapshot snap:snapshot.getChildren()){
                        Chat chat = snap.getValue(Chat.class);
                        if((chat.getIdAccount1()==idUser&&chat.getIdAccount2()==idReceiver)||(chat.getIdAccount2()==idUser&&chat.getIdAccount1()==idReceiver)){
                            idRoomChat = chat.getId();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        setData();
        setEvent();
        super.onResume();
    }

    //Set data
    private void setData(){
        arrayList = new ArrayList<>();
        viewMessageApdater = new ViewMessageApdater(R.layout.cardview_send_message,R.layout.cardview_recieve_message,arrayList,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewDoc.setLayoutManager(linearLayoutManager);
        recyclerViewDoc.setAdapter(viewMessageApdater);
        viewMessageApdater.notifyDataSetChanged();
        databaseReference.child(Enum.CHAT_TABLE).child(String.valueOf(idRoomChat)).child(Enum.MESSAGE_TABLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()>0){
                    arrayList.clear();
                    for (DataSnapshot snap:snapshot.getChildren()){
                        Message message = snap.getValue(Message.class);
                        arrayList.add(message);
                    }
                    recyclerViewDoc.scrollToPosition(arrayList.size()-1);
                    viewMessageApdater.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Set Người Nhắn
        databaseReference.child(Enum.PROFILE_TABLE).child(String.valueOf(idReceiver)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()>0){
                    Profile profile = snapshot.getValue(Profile.class);
                    Enum.setCircleImageView(avt,profile.getAvatar(),MessageActivity.this);
                    name_chat.setText(profile.getName());
                    databaseReference.child(Enum.PROFILE_TABLE).child(String.valueOf(idReceiver)).removeEventListener(this);
                }
              }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //Set sự kiện
    private void setEvent(){
        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(kiemTra()){
                    if(idRoomChat==-1){
                        createChat(input_chat.getText().toString());

                    }else{
                        sendMessage(input_chat.getText().toString(),idRoomChat);
                    }
                }
            }
        });

    }
    //Kiểm tra rỗng
    private boolean kiemTra(){
        if(!input_chat.getText().toString().trim().isEmpty()){
            return true;
        }
        return false;
    }

    //Tạo đoạn chat mới
    private long idChatNew = -1;
    ValueEventListener listenerChat;
    private void createChat(String content){
        getIdDoanChatMoi();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(idChatNew!=-1){
                    Chat chat = new Chat(idChatNew,idUser,idReceiver,content,Enum.getTime(),true,true);
                    databaseReference.child(Enum.CHAT_TABLE).removeEventListener(listenerChat);
                    idRoomChat = chat.getId();
                    databaseReference.child(Enum.CHAT_TABLE).child(String.valueOf(chat.getId())).setValue(chat);
                    databaseReference.child(Enum.MY_CHAT_TABLE).child(String.valueOf(idUser)).child(String.valueOf(chat.getId())).setValue(chat.getId());
                    databaseReference.child(Enum.MY_CHAT_TABLE).child(String.valueOf(idReceiver)).child(String.valueOf(chat.getId())).setValue(chat.getId());
                    sendMessage(input_chat.getText().toString(),idRoomChat);
                }else{
                    sendMessage(input_chat.getText().toString(),idRoomChat);
                }
            }
        }, 1000);
    }
    private void getIdDoanChatMoi(){
        listenerChat = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idChatNew = snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.child(Enum.CHAT_TABLE).addValueEventListener(listenerChat);

    }
    private long idMessage = -1;
    ValueEventListener listenerMes;
    private void sendMessage(String content,long idRoomChat){
        this.idRoomChat = idRoomChat;
        getIdMesNew(idRoomChat);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Message message = new Message(idMessage,idUser,idReceiver,content,Enum.getTime());
                databaseReference.child(Enum.CHAT_TABLE).child(String.valueOf(idRoomChat)).child("newMessage").setValue(content);
                databaseReference.child(Enum.CHAT_TABLE).child(String.valueOf(idRoomChat)).child("timeMessage").setValue(Enum.getTime());
                databaseReference.child(Enum.CHAT_TABLE).child(String.valueOf(idRoomChat)).child(Enum.MESSAGE_TABLE);
                databaseReference.child(Enum.CHAT_TABLE).child(String.valueOf(idRoomChat)).child(Enum.MESSAGE_TABLE).child(String.valueOf(message.getId())).setValue(message);
                input_chat.setText("");
                setData();
            }
        }, 1000);
    }
    private void getIdMesNew(long id){
        listenerMes = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idMessage = snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.child(Enum.CHAT_TABLE).child(String.valueOf(id)).child(Enum.MESSAGE_TABLE).addValueEventListener(listenerMes);
    }

}