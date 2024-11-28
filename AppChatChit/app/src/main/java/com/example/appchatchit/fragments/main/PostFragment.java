package com.example.appchatchit.fragments.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatchit.R;
import com.example.appchatchit.adapters.ViewPostAdapter;
import com.example.appchatchit.models.Post;
import com.example.appchatchit.resources.Enum;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostFragment extends AbstractFragment{
    ArrayList<Post> arrayList;
    ViewPostAdapter viewPostAdapter;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    private long idUser = -1;
    DatabaseReference databaseReference = Enum.DATABASE_REFERENCE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        view = getLayoutInflater().inflate(R.layout.post_fragment, container, false);
        //Ánh xạ
        sharedPreferences = getActivity().getSharedPreferences(Enum.PRE_LOGIN, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getLong(Enum.ID_USER,-1);

        recyclerView = view.findViewById(R.id.recyclerViewDoc);
        setData();
        setEvent();
        return view;
    }
    //Set Dữ Liệu
    private void setData(){
        arrayList = new ArrayList<>();
        viewPostAdapter = new ViewPostAdapter(R.layout.card_view_post,R.layout.card_view_post_no_img,getActivity(),arrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(viewPostAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        databaseReference.child(Enum.POST_TABLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                if(snapshot.getChildrenCount()>0){
                    for(DataSnapshot snap:snapshot.getChildren()){
                        Post post = snap.getValue(Post.class);
                        arrayList.add(post);
                    }
                    viewPostAdapter.notifyDataSetChanged();
                }else{
                    viewPostAdapter.notifyDataSetChanged();
                    databaseReference.onDisconnect().cancel();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                databaseReference.onDisconnect().cancel();
            }
        });




    }
    //Bắt sự kiện
    private void setEvent(){
        viewPostAdapter.setOnClickItemListener(new ViewPostAdapter.OnClickItemListener() {
            @Override
            public void onClickItem(int position, View v) {
                Post post = arrayList.get(position);
                databaseReference.child(Enum.POST_TABLE).child(String.valueOf(post.getId())).child(Enum.HEART_LIST).child(String.valueOf(idUser)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            databaseReference.child(Enum.POST_TABLE).child(String.valueOf(post.getId())).child(Enum.HEART_LIST).child(String.valueOf(idUser)).removeValue();
                            databaseReference.child(Enum.POST_TABLE).child(String.valueOf(post.getId())).child(Enum.HEART_LIST).child(String.valueOf(idUser)).removeEventListener(this);
                            databaseReference.onDisconnect().cancel();
                        }else{
                            databaseReference.child(Enum.POST_TABLE).child(String.valueOf(post.getId())).child(Enum.HEART_LIST).child(String.valueOf(idUser)).setValue(idUser);
                            databaseReference.child(Enum.POST_TABLE).child(String.valueOf(post.getId())).child(Enum.HEART_LIST).child(String.valueOf(idUser)).removeEventListener(this);
                            databaseReference.onDisconnect().cancel();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        databaseReference.onDisconnect().cancel();
                    }
                });

            }
        });
    }


}
