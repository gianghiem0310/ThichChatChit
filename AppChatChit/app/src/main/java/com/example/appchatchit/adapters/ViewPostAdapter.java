package com.example.appchatchit.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatchit.R;
import com.example.appchatchit.models.Post;
import com.example.appchatchit.models.Profile;
import com.example.appchatchit.resources.Enum;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ViewPostAdapter extends RecyclerView.Adapter<ViewPostAdapter.MyViewHolder>{
    DatabaseReference databaseReference = Enum.DATABASE_REFERENCE;
    int layoutId;
    int layoutIdKoAnh;
    Activity activity;
    ArrayList<Post> arrayList;
    OnClickItemListener onClickItemListener;

    public interface OnClickItemListener{
        void onClickItem(int position, View v);
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    SharedPreferences sharedPreferences;


    public ViewPostAdapter(int layoutId,int layoutIdKoAnh, Activity activity, ArrayList<Post> arrayList) {
        this.layoutIdKoAnh = layoutIdKoAnh;
        this.layoutId = layoutId;
        this.activity = activity;
        this.arrayList = arrayList;
        sharedPreferences = this.activity.getSharedPreferences(Enum.PRE_LOGIN, Context.MODE_PRIVATE);
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
        Post post = arrayList.get(position);
        holder.content.setText(post.getCaption());
        holder.date.setText(post.getDate());
        if(post.getType()==Enum.CO_ANH) {
            Enum.setImageView(holder.img_post, post.getImage(), activity);
        }
        holder.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickItemListener.onClickItem(position,view);
            }
        };

        ValueEventListener v1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()>0){
                    Profile profile = snapshot.getValue(Profile.class);
                    Enum.setCircleImageView(holder.avatar,profile.getAvatar(),activity);
                    holder.name.setText(profile.getName());
                    databaseReference.onDisconnect().cancel();
                }else{
                    databaseReference.onDisconnect().cancel();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                databaseReference.onDisconnect().cancel();
            }
        };

        databaseReference.child(Enum.PROFILE_TABLE).child(String.valueOf(post.getIdAccount())).addValueEventListener(v1);


       ValueEventListener v2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.quantityHeart.setText(snapshot.getChildrenCount()+" lượt thích");
                databaseReference.onDisconnect().cancel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                databaseReference.onDisconnect();
            }
        };
        databaseReference.child(Enum.POST_TABLE).child(String.valueOf(post.getId())).child(Enum.HEART_LIST).addValueEventListener(v2);

        databaseReference.child(Enum.POST_TABLE).child(String.valueOf(post.getId())).child(Enum.HEART_LIST).child(String.valueOf(sharedPreferences.getLong(Enum.ID_USER,-1))).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.tym.setImageDrawable(activity.getResources().getDrawable(R.drawable.heart_red, activity.getTheme()));
                }else{
                    holder.tym.setImageDrawable(activity.getResources().getDrawable(R.drawable.heart, activity.getTheme()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(arrayList.get(position).getType()==Enum.KHONG_CO_ANH){
            return layoutIdKoAnh;
        }
        return layoutId;
    }

    protected class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView avatar;
        TextView name;
        TextView date;
        ImageView img_post;
        TextView content;
        ImageView comment;
        View.OnClickListener onClickListener;
        ImageView tym;
        TextView quantityHeart;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            img_post = itemView.findViewById(R.id.img_post);
            content = itemView.findViewById(R.id.content);
            comment = itemView.findViewById(R.id.comment);
            tym = itemView.findViewById(R.id.heart);
            quantityHeart = itemView.findViewById(R.id.quantityHeart);
            tym.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onClickListener.onClick(view);
        }
    }
}
