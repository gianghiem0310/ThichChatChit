package com.example.appchatchit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appchatchit.R;
import com.example.appchatchit.models.Account;
import com.example.appchatchit.models.Profile;
import com.example.appchatchit.resources.Enum;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    TextView back;
    Button btnDangKy;
    AlertDialog alertDialog;

    EditText name;
    EditText username;
    EditText password;

    ImageView showPassword;
    boolean isChecked = true;

    long idNew = -1;
    DatabaseReference databaseReference = Enum.DATABASE_REFERENCE;
    boolean checkEaqualUsername = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Ánh xạ
        back = findViewById(R.id.back);
        btnDangKy = findViewById(R.id.btnDangKy);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password_edt);
        showPassword = findViewById(R.id.show);

        alertDialog = Enum.DIALOG(this);

        //Bắt sự kiện
        setEvent();

    }
    //Bắt sự kiện
    private void setEvent(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
                //Kiểm tra rỗng các trường nhập
              if (checkEmpty()){
                  //Lấy id mới nhất
                  getIdLienTuc();
                  //Chay trình kiểm tra username đã tồn tại hay chưa!
                  equalUsername();
                  Handler handler = new Handler();
                  handler.postDelayed(new Runnable() {
                      @Override
                      public void run() {
                            //Kiêm tra xem username đã tồn tại hay chưa
                          if (checkEaqualUsername==false){
                              //Gọi hàm tạo
                              createAccount();
                          }else {
                              alertDialog.dismiss();
                              databaseReference.onDisconnect().cancel();
                              checkEaqualUsername = false;
                              Enum.THONGBAO("Username đã tồn tại!", RegisterActivity.this);
                          }
                      }
                  }, 2000);


              }else{
                  alertDialog.dismiss();
                  Enum.THONGBAO("Chưa đầy đủ dữ liệu!", RegisterActivity.this);
              }
            }
        });
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
    }


    //Kiểm tra rỗng
    private boolean checkEmpty(){
        if (name.getText().toString().trim().isEmpty()){
            return false;
        }
        if (username.getText().toString().trim().isEmpty()){
            return false;
        }
        if (password.getText().toString().trim().isEmpty()){
            return false;
        }
        return true;
    }
    //Kiểm tra trùng username
    private void equalUsername(){
        databaseReference.child(Enum.ACCOUNT_TABLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Account account = snap.getValue(Account.class);
                    if (account.getUsername().equals(username.getText().toString())){
                        checkEaqualUsername = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //Get ID Tài Khoản Mới Liên Tục
    private void getIdLienTuc(){
        databaseReference.child(Enum.ACCOUNT_TABLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idNew = snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //Hàm đẩy dữ liệu
    private void createAccount(){
        Account account = new Account(idNew,username.getText().toString(),password.getText().toString());
        databaseReference.child(Enum.ACCOUNT_TABLE).child(String.valueOf(idNew)).setValue(account).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                databaseReference.onDisconnect().cancel();
                Profile profile = new Profile(account.getId(),name.getText().toString(),Enum.NULL_STRING,Enum.NULL_STRING,Enum.NULL_STRING,Enum.NULL_STRING,false,Enum.DOC_THAN,Enum.NAM);
                databaseReference.child(Enum.PROFILE_TABLE).child(String.valueOf(profile.getIdAccount())).setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        databaseReference.onDisconnect().cancel();
                        username.setText("");
                        name.setText("");
                        password.setText("");
                        alertDialog.dismiss();
                        Enum.THONGBAO("Tạo Tài Khoản Thành Công!",RegisterActivity.this);
                    }
                });
            }
        });
    }
}