package com.example.appchatchit.fragments.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatchit.R;
import com.example.appchatchit.activities.MessageActivity;
import com.example.appchatchit.adapters.ViewFriendAdapter;
import com.example.appchatchit.models.Chat;
import com.example.appchatchit.models.Friend;
import com.example.appchatchit.models.Profile;
import com.example.appchatchit.resources.Enum;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendFragment extends AbstractFragment{
    //Can thiet
    DatabaseReference databaseReference = Enum.DATABASE_REFERENCE;
    SharedPreferences sharedPreferences;
    private long idUser = -1;

    RecyclerView recyclerView;
    ArrayList<Profile> arrayList;
    ViewFriendAdapter viewFriendAdapter;
    AlertDialog alertDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        view = getLayoutInflater().inflate(R.layout.friend_fragment, container, false);
        sharedPreferences = getActivity().getSharedPreferences(Enum.PRE_LOGIN, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getLong(Enum.ID_USER,-1);
        alertDialog = Enum.DIALOG(getActivity());
        //Ánh xạ
        recyclerView = view.findViewById(R.id.recyclerViewDoc);

        return view;
    }

    @Override
    public void onResume() {
        setData();
        setEvent();
        super.onResume();
    }

    //Set dữ liệu
    private void setData(){
        arrayList = new ArrayList<>();
        viewFriendAdapter = new ViewFriendAdapter(getActivity(),R.layout.card_view_friend,arrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(viewFriendAdapter);
        alertDialog.show();
        databaseReference.child(Enum.FRIEND_TABLE).child(String.valueOf(idUser)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                if(snapshot.getChildrenCount()>0){
                    for (DataSnapshot snap:snapshot.getChildren()){
                        Friend friend = snap.getValue(Friend.class);
                        if(friend.getStatus()==Enum.BAN_BE) {
                            databaseReference.child(Enum.PROFILE_TABLE).child(String.valueOf(friend.getId())).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Profile profile = snapshot.getValue(Profile.class);
                                    arrayList.add(profile);
                                    viewFriendAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                    alertDialog.dismiss();
                    databaseReference.child(Enum.FRIEND_TABLE).child(String.valueOf(idUser)).removeEventListener(this);
                    viewFriendAdapter.notifyDataSetChanged();
                }else{
                    databaseReference.child(Enum.FRIEND_TABLE).child(String.valueOf(idUser)).removeEventListener(this);
                    viewFriendAdapter.notifyDataSetChanged();
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
        viewFriendAdapter.setOnClickItemListener(new ViewFriendAdapter.OnClickItemListener() {
            @Override
            public void onClickItem(int position, View v) {
                Enum.setEventClickUser(getActivity(),arrayList.get(position).getIdAccount());
            }

            @Override
            public void onChat(int position, View v) {
                alertDialog.show();
                Profile profile = arrayList.get(position);
                Intent intent = new Intent(getActivity(), MessageActivity.class);
                intent.putExtra(Enum.ID_RECEIVER,arrayList.get(position).getIdAccount());
                databaseReference.child(Enum.CHAT_TABLE).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getChildrenCount()>0){
                            long idRoom = -1;
                            for(DataSnapshot snap:snapshot.getChildren()){
                                Chat chat = snap.getValue(Chat.class);
                                if((chat.getIdAccount1()==idUser&&chat.getIdAccount2()==profile.getIdAccount())||(chat.getIdAccount2()==idUser&&chat.getIdAccount1()==profile.getIdAccount())){
                                    idRoom = chat.getId();
                                }
                            }
                            alertDialog.dismiss();
                            if(idRoom!=-1){
                                intent.putExtra(Enum.KEY_ROOM_CHAT,idRoom);
                            }
                            startActivity(intent);
                            databaseReference.child(Enum.CHAT_TABLE).removeEventListener(this);


                        }else{
                            //Tạo mới ở đây
                            alertDialog.dismiss();
                            databaseReference.child(Enum.CHAT_TABLE).removeEventListener(this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}
