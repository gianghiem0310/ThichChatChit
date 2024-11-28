package com.example.appchatchit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.appchatchit.activities.HomeActivity;
import com.example.appchatchit.activities.LoginActivity;
import com.example.appchatchit.resources.Enum;

public class LoadingActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        sharedPreferences = getSharedPreferences("SharedPreferencesLogin", Context.MODE_PRIVATE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sharedPreferences.getBoolean(Enum.CHECK_LOGIN,false)==false){
                    Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(LoadingActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }, 1000);

    }
}