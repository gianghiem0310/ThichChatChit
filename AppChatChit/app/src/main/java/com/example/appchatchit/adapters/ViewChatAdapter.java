package com.example.appchatchit.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatchit.R;
import com.example.appchatchit.models.Chat;
import com.example.appchatchit.models.Profile;
import com.example.appchatchit.resources.Enum;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewChatAdapter extends RecyclerView.Adapter<ViewChatAdapter.MyViewHolder>{
    Activity activity;
    int layoutId;
    ArrayList<Chat> arrayList;
    OnClickItemListener onClickItemListener;

    DatabaseReference databaseReference = Enum.DATABASE_REFERENCE;
    private long idUser = -1;
    SharedPreferences sharedPreferences;

    public interface OnClickItemListener{
        void onClickItem(int position, View v);
        void onChat(int position, View v);
    }

    public ViewChatAdapter(Activity activity, int layoutId, ArrayList<Chat> arrayList) {
        this.activity = activity;
        this.layoutId = layoutId;
        this.arrayList = arrayList;
        sharedPreferences = this.activity.getSharedPreferences(Enum.PRE_LOGIN, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getLong(Enum.ID_USER,-1);
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        CardView view = (CardView) inflater.inflate(viewType, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Chat chat = arrayList.get(position);
        if(idUser!= chat.getIdAccount1()){
            databaseReference.child(Enum.PROFILE_TABLE).child(String.valueOf(chat.getIdAccount1())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getChildrenCount()>0){
                        Profile profile = snapshot.getValue(Profile.class);
                        Enum.setCircleImageView(holder.avt_chat,profile.getAvatar(),activity);
                        holder.name_chat.setText(profile.getName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            databaseReference.child(Enum.PROFILE_TABLE).child(String.valueOf(chat.getIdAccount2())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getChildrenCount()>0){
                        Profile profile = snapshot.getValue(Profile.class);
                        Enum.setCircleImageView(holder.avt_chat,profile.getAvatar(),activity);
                        holder.name_chat.setText(profile.getName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        holder.new_chat.setText(chat.getNewMessage());
        holder.time_chat.setText(chat.getTimeMessage());
        holder.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    default:
                        onClickItemListener.onClickItem(position,view);
                        break;
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return layoutId;
    }

    protected class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View.OnClickListener onClickListener;
        CircleImageView avt_chat;
        TextView name_chat;
        TextView new_chat;
        TextView time_chat;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avt_chat = itemView.findViewById(R.id.avt_chat);
            name_chat = itemView.findViewById(R.id.name_chat);
            new_chat = itemView.findViewById(R.id.new_chat);
            time_chat = itemView.findViewById(R.id.time_chat);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onClickListener.onClick(view);
        }
    }
}
