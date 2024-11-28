package com.example.appchatchit.fragments.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatchit.R;
import com.example.appchatchit.adapters.ViewPostAdapter;
import com.example.appchatchit.adapters.ViewSearchFriendAdapter;
import com.example.appchatchit.models.Profile;
import com.example.appchatchit.resources.Enum;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFriendFragment extends AbstractFragment{

    SharedPreferences sharedPreferences;
    private long idUser = -1;
    DatabaseReference databaseReference = Enum.DATABASE_REFERENCE;

    RecyclerView recyclerView;
    ArrayList<Profile> arrayList;
    ViewSearchFriendAdapter viewSearchFriendAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        view = getLayoutInflater().inflate(R.layout.search_friend_fragment, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewDoc);
        setData();
        setEvent();
        return view;
    }
    //Set dữ liệu
    private void setData(){
        arrayList = new ArrayList<>();
        viewSearchFriendAdapter = new ViewSearchFriendAdapter(getActivity(),arrayList,R.layout.card_view_search_friend);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(viewSearchFriendAdapter);
        viewSearchFriendAdapter.notifyDataSetChanged();
        databaseReference.child(Enum.PROFILE_TABLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()>0){
                    arrayList.clear();
                    for (DataSnapshot snap :snapshot.getChildren()){
                        Profile profile = snap.getValue(Profile.class);
                        arrayList.add(profile);
                    }
                    viewSearchFriendAdapter.notifyDataSetChanged();
                    databaseReference.child(Enum.PROFILE_TABLE).removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                databaseReference.onDisconnect().cancel();
            }
        });
    }
    //Set event
    private void setEvent(){
        viewSearchFriendAdapter.setOnClickItemListener(new ViewPostAdapter.OnClickItemListener() {
            @Override
            public void onClickItem(int position, View v) {
                Enum.setEventClickUser(getActivity(),arrayList.get(position).getIdAccount());
            }
        });
    }
}
