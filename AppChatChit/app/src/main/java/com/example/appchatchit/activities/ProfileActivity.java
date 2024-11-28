package com.example.appchatchit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appchatchit.R;
import com.example.appchatchit.models.Friend;
import com.example.appchatchit.models.Profile;
import com.example.appchatchit.resources.Enum;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    DatabaseReference databaseReference = Enum.DATABASE_REFERENCE;
    ImageView ic_back;
    CircleImageView avatar;
    TextView name;
    Button btn_add;
    private long idUser = -1;
    private long idUserShow = -1;
    ValueEventListener suKienGetId;
    AlertDialog alertDialog;
    long idInviteNew = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Lấy id user show
        sharedPreferences = getBaseContext().getSharedPreferences(Enum.PRE_LOGIN, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getLong(Enum.ID_USER,-1);
        idUserShow = getIntent().getLongExtra(Enum.ID_USER_SHOW,-1);
        //Ánh xạ
        ic_back = findViewById(R.id.ic_back);
        avatar = findViewById(R.id.avatar);
        name = findViewById(R.id.name);
        btn_add = findViewById(R.id.btn_add);

        alertDialog = Enum.DIALOG(this);
        setData();
        setEvent();

    }
    //Set dữ liệu
    private void setData(){
        databaseReference.child(Enum.PROFILE_TABLE).child(String.valueOf(idUserShow)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()>0){
                    Profile profile = snapshot.getValue(Profile.class);
                    name.setText(profile.getName());
                    Enum.setCircleImageView(avatar,profile.getAvatar(),ProfileActivity.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                databaseReference.onDisconnect().cancel();
            }
        });
        databaseReference.child(Enum.FRIEND_TABLE).child(String.valueOf(idUserShow)).child(String.valueOf(idUser)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Friend friend = snapshot.getValue(Friend.class);
                    if (friend.getStatus()==Enum.BAN_BE){
                        btn_add.setText("Bạn bè");
                    }else{
                        btn_add.setText("Đã Gửi");
                    }

                }else{
                    btn_add.setText("Thêm bạn");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
                invite();
            }
        });
    }

    //Hàm gửi, xóa invite
    private void invite(){
        suKienGetId = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Friend friend = snapshot.getValue(Friend.class);
                    if(friend.getStatus()==Enum.CHO_XAC_NHAN){
                        databaseReference.child(Enum.FRIEND_TABLE).child(String.valueOf(idUserShow)).child(String.valueOf(idUser)).removeValue();
                        databaseReference.child(Enum.FRIEND_TABLE).child(String.valueOf(idUserShow)).child(String.valueOf(idUser)).removeEventListener(this);
                        databaseReference.onDisconnect().cancel();
                        alertDialog.dismiss();
                    }else{
                        Enum.THONGBAO("Bạn muốn hủy kết bạn!",ProfileActivity.this);
                        databaseReference.onDisconnect().cancel();
                        alertDialog.dismiss();
                    }
                }else{
                    Friend friend = new Friend(idUser,Enum.CHO_XAC_NHAN);
                    databaseReference.child(Enum.FRIEND_TABLE).child(String.valueOf(idUserShow)).child(String.valueOf(idUser)).setValue(friend);
                    databaseReference.child(Enum.FRIEND_TABLE).child(String.valueOf(idUserShow)).child(String.valueOf(idUser)).removeEventListener(this);
                    databaseReference.onDisconnect().cancel();
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                alertDialog.dismiss();
            }
        };
        databaseReference.child(Enum.FRIEND_TABLE).child(String.valueOf(idUserShow)).child(String.valueOf(idUser)).addValueEventListener(suKienGetId);
    }
}