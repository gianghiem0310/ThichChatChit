package com.example.appchatchit.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.example.appchatchit.R;
import com.example.appchatchit.fragments.main.AbstractFragment;
import com.example.appchatchit.fragments.main.FriendFragment;
import com.example.appchatchit.fragments.main.PostFragment;
import com.example.appchatchit.fragments.main.SearchFriendFragment;
import com.example.appchatchit.models.Friend;
import com.example.appchatchit.models.Post;
import com.example.appchatchit.models.Profile;
import com.example.appchatchit.resources.Enum;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private int MY_REQUEST_CODE = 1;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    BottomNavigationView bottomNavigationView;
    private AbstractFragment fragment;
    private FragmentTransaction transaction;
    public static final int POST = 10;
    public static final int SEARCH_FRIEND = 11;
    public static final int FRIEND = 12;
    private int screenID = POST;
    Toolbar toolbar;

    private NavigationView navigationView;
    SharedPreferences sharedPreferences;

    CircleImageView avatar;
    TextView name;
    TextView email;
    private long idUser = -1;
    DatabaseReference databaseReference = Enum.DATABASE_REFERENCE;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    ImageView backDialog;
    TextView btnNewPost;
    ImageView img_post;
    EditText content;
    private Uri mUri;
    private long idNew = -1;
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_official);
        sharedPreferences = getBaseContext().getSharedPreferences(Enum.PRE_LOGIN, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getLong(Enum.ID_USER,-1);
        alertDialog = Enum.DIALOG(this);
        //Ánh xạ
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);


        //Gắn view drawer
        this.setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        avatar = headerView.findViewById(R.id.avatar);
        name = headerView.findViewById(R.id.name);
        email = headerView.findViewById(R.id.email);


        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // Xử lý sự kiện khi người dùng chọn một mục trong Navigation Drawer
                        switch (menuItem.getItemId()) {
                            case R.id.info:
                                // Xử lý khi chọn Home
                                break;
                            case R.id.chat:
                                //Xử lý khi ấn bạn bè
                                Intent intent = new Intent(HomeActivity.this,ChatActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.setting:
                                // Xử lý khi chọn Gallery
                                break;
                            case R.id.dangXuat:
                                // Xử lý khi chọn Settings
                                sharedPreferences.edit().remove(Enum.ID_USER).commit();
                                sharedPreferences.edit().remove(Enum.CHECK_LOGIN).commit();
                                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                finish();
                                break;
                        }

                        // Đóng Navigation Drawer khi đã xử lý xong sự kiện
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
        setFragment();
        setData();
        setEvent();
    }

    @Override
    protected void onResume() {
        if(idUser==-1){
            finish();
        }
        super.onResume();
    }

    long number = 0;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem invite = menu.findItem(R.id.action_invite);
        updateNotificationCount(invite,10);
        View actionView = invite.getActionView();
        databaseReference.child(Enum.FRIEND_TABLE).child(String.valueOf(idUser)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                number = 0;
                for(DataSnapshot snap:snapshot.getChildren()){
                    Friend friend = snap.getValue(Friend.class);
                    if(friend.getStatus()==Enum.CHO_XAC_NHAN){
                        number += 1;
                    }
                }
                updateNotificationCount(invite,number);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if (actionView != null) {
            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionsItemSelected(invite);
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Viết chức năng khi click item
        switch (item.getItemId()){
            case R.id.action_settings:
                dialogNewPost();
                break;
            case R.id.action_invite:
                //Sự kiện bạn bè mời
                Intent intent = new Intent(HomeActivity.this, InviteActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void updateNotificationCount(MenuItem menuItem, long count) {
        FrameLayout rootView = (FrameLayout) menuItem.getActionView();
        TextView notificationCount = rootView.findViewById(R.id.notification_count);
        notificationCount.setText(String.valueOf(count));
        if(count>0){
            notificationCount.setVisibility(View.VISIBLE); // Hiển thị con số
        }else{
            notificationCount.setVisibility(View.INVISIBLE); // Tắt Hiển thị con số
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    //Bắt Sự Kiện
    private void setEvent(){

    }
    //Mở cửa sổ đăng bài viết
    private void dialogNewPost(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.dialog_new_post, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        //Ánh xạ
        backDialog = dialogView.findViewById(R.id.ic_back);
        btnNewPost = dialogView.findViewById(R.id.btnNewPost);
        img_post = dialogView.findViewById(R.id.img_post);
        content = dialogView.findViewById(R.id.content);
        //Bắt sự kiện
        backDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUri = null;
                dialog.dismiss();
            }
        });
        img_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
            }
        });
        btnNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
                if(checkEmpty()){
                    getIdLienTuc();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Kiêm tra xem username đã tồn tại hay chưa
                            switch (checkTypePost()){
                                case Enum.CO_ANH:
                                    pushPostCoAnh(dialog);
                                    break;
                                case Enum.KHONG_CO_ANH:
                                    pushPost(dialog);
                                    break;
                            }
                        }
                    }, 2000);
                }else{
                    alertDialog.dismiss();
                    Enum.THONGBAO("Chọn ảnh hoặc ghi caption để đăng bài!",HomeActivity.this);
                }
            }
        });
        dialog.show();
    }
    //Set dữ liệu
    private void setData(){
        databaseReference.child(Enum.PROFILE_TABLE).child(String.valueOf(idUser)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()>0){
                    Profile profile = snapshot.getValue(Profile.class);
                    name.setText(profile.getName());
                    email.setText(profile.getEmail());
                    Enum.setCircleImageView(avatar,profile.getAvatar(),HomeActivity.this);
                    databaseReference.child(Enum.PROFILE_TABLE).child(String.valueOf(idUser)).removeEventListener(this);
                    databaseReference.onDisconnect().cancel();
                }else{
                    databaseReference.child(Enum.PROFILE_TABLE).child(String.valueOf(idUser)).removeEventListener(this);
                    databaseReference.onDisconnect().cancel();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                databaseReference.onDisconnect().cancel();
            }
        });
    }
    private void setFragment(){
        replacePage();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.btn_post:
                        screenID = POST;
                        replacePage();
                        break;
                    case R.id.btn_search_friend:
                        screenID = SEARCH_FRIEND;
                        replacePage();
                        break;
                    case R.id.btn_friend:
                        screenID = FRIEND;
                        replacePage();
                        break;
                }
                return true;
            }
        });


    }

    public void replacePage(){
        if(getSupportFragmentManager().findFragmentByTag(screenID+"")!=null){
            fragment = (AbstractFragment) getSupportFragmentManager().findFragmentByTag(screenID+"");
        }else{
            if(screenID==POST){
                fragment = new PostFragment();
            }
            if(screenID==SEARCH_FRIEND){
                fragment = new SearchFriendFragment();
            }
            if(screenID==FRIEND){
                fragment = new FriendFragment();
            }

        }
        if(fragment!=null){
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_setting,fragment,screenID+"");
            if(getSupportFragmentManager().findFragmentByTag(screenID+"")==null){
                transaction.addToBackStack(screenID+"");
            }
            transaction.commit();
        }
    }

    //Check rỗng
    private boolean checkEmpty(){
        if (content.getText().toString().trim().isEmpty()&&mUri==null){
            return false;
        }
        return true;
    }
    private int checkTypePost(){
        if(mUri == null){
            return Enum.KHONG_CO_ANH;
        }
        return Enum.CO_ANH;
    }
    //Lấy ID Mới Liên Tục
    private void getIdLienTuc(){
        databaseReference.child(Enum.POST_TABLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idNew = snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                databaseReference.onDisconnect().cancel();
            }
        });
    }
    //Đẩy Post lên trước
    private void pushPost(AlertDialog dialog){
        Post post = new Post(idNew,idUser,Enum.NULL_STRING,content.getText().toString(),Enum.KHONG_CO_ANH,Enum.getDate());
        databaseReference.child(Enum.POST_TABLE).child(String.valueOf(post.getId())).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                alertDialog.dismiss();
                dialog.dismiss();
                Enum.THONGBAO("Đã đăng!",HomeActivity.this);
                databaseReference.onDisconnect().cancel();
            }
        });
    }
    private void pushPostCoAnh(AlertDialog dialog){
        StorageReference fileRef = storageReference.child(String.valueOf(idUser)).child(System.currentTimeMillis()+"."+getFileExtension(mUri));
        fileRef.putFile(mUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Post post = new Post(idNew,idUser,uri.toString(),content.getText().toString(),Enum.CO_ANH,Enum.getDate());
                        databaseReference.child(Enum.POST_TABLE).child(String.valueOf(post.getId())).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                alertDialog.dismiss();
                                dialog.dismiss();
                                mUri = null;
                                Enum.THONGBAO("Đã đăng!",HomeActivity.this);
                                databaseReference.onDisconnect().cancel();
                            }
                        });



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        alertDialog.dismiss();
                        dialog.dismiss();
                        Enum.THONGBAO("Không thể up ảnh!",HomeActivity.this);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                alertDialog.dismiss();
                dialog.dismiss();
                Enum.THONGBAO("Không thể up ảnh!",HomeActivity.this);
            }
        });


    }

    private void onClickRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permission, MY_REQUEST_CODE);
            }
        } else {
            if (this.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                String[] permission = {Manifest.permission.READ_MEDIA_IMAGES};
                requestPermissions(permission, MY_REQUEST_CODE);
            }
        }
    }
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Chọn hình ảnh"));
    }
    private String getFileExtension(Uri mUri){
        ContentResolver cr = getBaseContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data == null) {
                            return;
                        }
                        //Du anh lieu tu gallery
                        Uri uri = data.getData();
                        mUri = uri;
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), uri);
                            img_post.setImageBitmap(bitmap);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
    );

}