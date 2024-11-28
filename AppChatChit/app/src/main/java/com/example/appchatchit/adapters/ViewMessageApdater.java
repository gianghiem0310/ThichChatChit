package com.example.appchatchit.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatchit.R;
import com.example.appchatchit.models.Message;
import com.example.appchatchit.models.Profile;
import com.example.appchatchit.resources.Enum;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewMessageApdater extends RecyclerView.Adapter<ViewMessageApdater.MyViewHolder>{
    private int layoutIdSent;
    private int layoutIdReceive;
    private ArrayList<Message> arrayList;
    private Activity activity;
    DatabaseReference databaseReference = Enum.DATABASE_REFERENCE;
    private long idUser = -1;
    SharedPreferences sharedPreferences;

    public ViewMessageApdater(int layoutIdSent, int layoutIdReceive, ArrayList<Message> arrayList, Activity activity) {
        this.layoutIdSent = layoutIdSent;
        this.layoutIdReceive = layoutIdReceive;
        this.arrayList = arrayList;
        this.activity = activity;
        sharedPreferences = this.activity.getSharedPreferences(Enum.PRE_LOGIN, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getLong(Enum.ID_USER,-1);
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
        Message message = arrayList.get(position);
        holder.content.setText(message.getContent());
        if(idUser == message.getIdReceiver()){
            databaseReference.child(Enum.PROFILE_TABLE).child(String.valueOf(message.getIdReceiver())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getChildrenCount()>0){
                        Profile profile = snapshot.getValue(Profile.class);
                        Enum.setCircleImageView(holder.avt,profile.getAvatar(),activity);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = arrayList.get(position);
        if(message.getIdSender() == idUser){
            return layoutIdSent;
        }
        return layoutIdReceive;
    }

    protected class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avt;
        TextView content;
        TextView time;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avt = itemView.findViewById(R.id.avt);
            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
        }
    }
}
