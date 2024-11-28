package com.example.appchatchit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.appchatchit.R;
import com.example.appchatchit.adapters.ViewInviteAdapter;
import com.example.appchatchit.models.Friend;
import com.example.appchatchit.models.Profile;
import com.example.appchatchit.resources.Enum;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InviteActivity extends AppCompatActivity {
    //Can thiet
    DatabaseReference databaseReference = Enum.DATABASE_REFERENCE;
    SharedPreferences sharedPreferences;
    private long idUser = -1;

    ImageView ic_back;
    RecyclerView recyclerView;
    ViewInviteAdapter viewInviteAdapter;
    ArrayList<Profile> arrayList;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        sharedPreferences = getBaseContext().getSharedPreferences(Enum.PRE_LOGIN, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getLong(Enum.ID_USER,-1);
        alertDialog = Enum.DIALOG(this);
        //Ánh xạ
        ic_back = findViewById(R.id.ic_back);
        recyclerView = findViewById(R.id.recyclerViewDoc);
        setData();
        setEvent();
    }
    //Set dữ liệu
    private void setData(){
        arrayList = new ArrayList<>();
        viewInviteAdapter = new ViewInviteAdapter(this,R.layout.card_view_invite,arrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(viewInviteAdapter);
        alertDialog.show();
        databaseReference.child(Enum.FRIEND_TABLE).child(String.valueOf(idUser)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                if(snapshot.getChildrenCount()>0){
                    for (DataSnapshot snap:snapshot.getChildren()){
                        Friend friend = snap.getValue(Friend.class);
                        if(friend.getStatus()==Enum.CHO_XAC_NHAN) {
                            databaseReference.child(Enum.PROFILE_TABLE).child(String.valueOf(friend.getId())).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Profile profile = snapshot.getValue(Profile.class);
                                    arrayList.add(profile);
                                    viewInviteAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                    alertDialog.dismiss();
                    databaseReference.child(Enum.FRIEND_TABLE).child(String.valueOf(idUser)).removeEventListener(this);
                    viewInviteAdapter.notifyDataSetChanged();
                }else{
                    databaseReference.child(Enum.FRIEND_TABLE).child(String.valueOf(idUser)).removeEventListener(this);
                    viewInviteAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                alertDialog.dismiss();
                databaseReference.onDisconnect().cancel();
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
        viewInviteAdapter.setOnClickItemListener(new ViewInviteAdapter.OnClickItemListener() {
            @Override
            public void onClickItem(int position, View v) {
                Enum.setEventClickUser(InviteActivity.this,arrayList.get(position).getIdAccount());
            }

            @Override
            public void onClickXacNhan(int position, View v) {
                alertDialog.show();
                databaseReference.child(Enum.FRIEND_TABLE).child(String.valueOf(idUser)).child(String.valueOf(arrayList.get(position).getIdAccount())).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Friend friend = snapshot.getValue(Friend.class);
                        if(friend.getStatus()==Enum.CHO_XAC_NHAN){
                            databaseReference.child(Enum.FRIEND_TABLE).child(String.valueOf(idUser)).child(String.valueOf(arrayList.get(position).getIdAccount())).child("status").setValue(Enum.BAN_BE);
                            Friend friend1 = new Friend(idUser,Enum.BAN_BE);
                            databaseReference.child(Enum.FRIEND_TABLE).child(String.valueOf(arrayList.get(position).getIdAccount())).child(String.valueOf(idUser)).setValue(friend1);
                            databaseReference.child(Enum.FRIEND_TABLE).child(String.valueOf(idUser)).child(String.valueOf(arrayList.get(position).getIdAccount())).removeEventListener(this);
                            alertDialog.dismiss();
                            Enum.THONGBAO("Có thêm bạn mới rồi!",InviteActivity.this);
                            arrayList.remove(position);
                            viewInviteAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        alertDialog.dismiss();
                        Enum.THONGBAO("Có lỗi!",InviteActivity.this);
                        databaseReference.child(Enum.FRIEND_TABLE).child(String.valueOf(idUser)).child(String.valueOf(arrayList.get(position).getIdAccount())).removeEventListener(this);
                    }
                });
            }

            @Override
            public void onClickHuy(int position, View v) {

            }
        });
    }
}