package com.example.appchatchit.resources;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.example.appchatchit.R;
import com.example.appchatchit.activities.ProfileActivity;
import com.example.appchatchit.models.Chat;
import com.example.appchatchit.models.Message;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Enum {
    public static final String PRE_LOGIN = "SharedPreferencesLogin";
    public static final String NULL_STRING = "NULL";
    public static final String ID_USER = "id_user";
    public static final String ID_USER_SHOW = "id_user_show";
    public static final int NULL_INT = -1;
    public static final String CHECK_LOGIN = "isLogin";

    public static final FirebaseDatabase FIREBASE_DATABASE = FirebaseDatabase.getInstance();
    public static final DatabaseReference DATABASE_REFERENCE = FIREBASE_DATABASE.getReference();

    //Tên Bảng
    public static final String ACCOUNT_TABLE = "Accounts";
    public static final String POST_TABLE = "Posts";
    public static final String PROFILE_TABLE = "Profiles";
    public static final String NOTIFICATION_TABLE = "Notifications";
    public static final String FRIEND_TABLE = "Friends";
    public static final String CHAT_TABLE = "Chats";
    public static final String MY_CHAT_TABLE = "My_Chats";
    public static final String MESSAGE_TABLE = "Messages";
    public static final String EXIST_TABLE = "Exists";

    public static final String HEART_LIST = "hearts";
    public static final String COMMENT_LIST = "comments";

    //Trạng thái bạn bè
    public static final int BAN_BE = 1;
    public static final int CHO_XAC_NHAN = 0;
    //KEY INTENT
    public static final String KEY_ROOM_CHAT = "Key_Room_Chat";
    public static final String ID_RECEIVER = "Id_Receiver";

    //Giới tính
    public static final int NAM = 0;
    public static final int NU = 1;
    public static final int BI_MAT = 2;
    //Tình trạng hôn nhân
    public static final int DOC_THAN = 0;
    public static final int DANG_HEN_HO = 1;
    public static final int DA_KET_HON = 2;
    public static final int DA_LY_HON = 3;
    //Trạng thái bài viết
    public static final int CO_ANH = 1;
    public static final int KHONG_CO_ANH = 0;

    //Hàm hiển thị loading
    public static final AlertDialog DIALOG(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        builder.setCancelable(false);
        builder.setView(layoutInflater.inflate(R.layout.loading,null));
        return  builder.create();
    }
    //Hàm hiển thị thông báo
    public static final void THONGBAO(String message, Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.notification, null);
        builder.setView(dialogView);
        TextView textView = dialogView.findViewById(R.id.text);
        textView.setText(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create();
        builder.show();
    }
    //Hàm set hình ảnh
    public static final void setCircleImageView(CircleImageView circleImageView,String imageStr,Activity activity){
        if(!imageStr.equals(NULL_STRING)){
            Glide.with(activity.getLayoutInflater().getContext()).load(imageStr).into(circleImageView);
        }else{
            circleImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.logo, activity.getTheme()));
        }
    }
    public static final void setImageView(ImageView circleImageView, String imageStr, Activity activity){
        if(!imageStr.equals(NULL_STRING)){
            Glide.with(activity.getLayoutInflater().getContext()).load(imageStr).into(circleImageView);
        }else{
            circleImageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.logo, activity.getTheme()));
        }
    }
    //Hàm lấy ngày
    public static final String getDate(){
        Date currentTime = Calendar.getInstance().getTime();
        Timestamp tsTemp = new Timestamp(currentTime.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // Chuyển đổi Timestamp thành chuỗi theo định dạng
        return dateFormat.format(tsTemp);
    }
    public static final String getTime(){
        Date currentTime = Calendar.getInstance().getTime();
        Timestamp tsTemp = new Timestamp(currentTime.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        // Chuyển đổi Timestamp thành chuỗi theo định dạng
        return dateFormat.format(tsTemp);
    }
    //Set Heart
    public static final void setColorOfHeart(boolean data, ImageView heart, Activity activity){
        if(data){
            heart.setImageDrawable(activity.getResources().getDrawable(R.drawable.heart_red, activity.getTheme()));
        }else{
            heart.setImageDrawable(activity.getResources().getDrawable(R.drawable.heart, activity.getTheme()));
        }
    }

    //Click item chuyển sang màn hình cá nhân
    public static final void setEventClickUser(Activity activity,long id){
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra(ID_USER_SHOW,id);
        activity.startActivity(intent);
    }
    public static final boolean kiemTraTonTaiChat(long id, ArrayList<Chat> arrayList){
        boolean kiemTra = false;
        for (Chat item:
             arrayList) {
            if(item.getId() == id){
                kiemTra = true;
                break;
            }
        }
        return kiemTra;
    }
    public static final void swap(long id, ArrayList<Chat> arrayList,Chat chatNew){
        for (int i = 0; i < arrayList.size(); i++) {
            if(arrayList.get(i).getId()==id){
                arrayList.set(i,chatNew);
                break;
            }
        }
    }
    public static final boolean kiemTraTonTaiMes(long id, ArrayList<Message> arrayList){
        boolean kiemTra = false;
        for (Message item:
                arrayList) {
            if(item.getId() == id){
                kiemTra = true;
                break;
            }
        }
        return kiemTra;
    }
    public static final void swapMes(long id, ArrayList<Message> arrayList,Message chatNew){
        for (int i = 0; i < arrayList.size(); i++) {
            if(arrayList.get(i).getId()==id){
                arrayList.set(i,chatNew);
                break;
            }
        }
    }

}
