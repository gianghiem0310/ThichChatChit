package com.example.appchatchit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appchatchit.R;
import com.example.appchatchit.models.Account;
import com.example.appchatchit.resources.Enum;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    TextView btnDangKy;
    EditText username;
    EditText password;
    Button btnDangNhap;
    ImageView showPassword;
    boolean isChecked = true;
    AlertDialog alertDialog;
    SharedPreferences sharedPreferences;
    DatabaseReference databaseReference = Enum.DATABASE_REFERENCE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Ánh xạ
        btnDangKy = findViewById(R.id.btnDangKy);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password_edt);
        showPassword = findViewById(R.id.show);

        sharedPreferences = getBaseContext().getSharedPreferences(Enum.PRE_LOGIN, Context.MODE_PRIVATE);

        alertDialog = Enum.DIALOG(this);
        setEvent();
    }
    //Bắt sự kiện
    private void setEvent(){
        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isChecked == true){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isChecked = false;
                }
                else{
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isChecked = true;
                }
            }
        });
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
            }
        });
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
                if(checkEmpty()){
                    databaseReference.child(Enum.ACCOUNT_TABLE).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getChildrenCount()>0){
                                for(DataSnapshot snap:snapshot.getChildren()){
                                    Account account = snap.getValue(Account.class);
                                    if (account.getUsername().equals(username.getText().toString())&&account.getPassword().equals(password.getText().toString())){
                                        databaseReference.child(Enum.ACCOUNT_TABLE).removeEventListener(this);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                databaseReference.onDisconnect().cancel();
                                                alertDialog.dismiss();
                                                sharedPreferences.edit().putLong(Enum.ID_USER,account.getId()).commit();
                                                sharedPreferences.edit().putBoolean(Enum.CHECK_LOGIN,true).commit();
                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }
                                        }, 1000);


                                    }
                                }
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        databaseReference.onDisconnect().cancel();
                                        alertDialog.dismiss();
                                        if (!LoginActivity.this.isFinishing()){
                                            Enum.THONGBAO("Tài khoản không đúng!", LoginActivity.this);
                                        }

                                    }
                                }, 2000);
                            }else{
                                databaseReference.onDisconnect().cancel();
                                alertDialog.dismiss();
                                Enum.THONGBAO("Tài khoản không đúng!", LoginActivity.this);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            databaseReference.onDisconnect().cancel();
                        }
                    });
                }else{
                    alertDialog.dismiss();
                    Enum.THONGBAO("Thiếu dữ liệu đăng nhập!", LoginActivity.this);
                }


            }
        });
    }
    //Kiểm tra rỗng
    private boolean checkEmpty(){
        if (username.getText().toString().trim().isEmpty()){
            return false;
        }
        if (password.getText().toString().trim().isEmpty()){
            return false;
        }
        return true;
    }

}