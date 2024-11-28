package com.example.appchatchit.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatchit.R;
import com.example.appchatchit.models.Profile;
import com.example.appchatchit.resources.Enum;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewSearchFriendAdapter extends RecyclerView.Adapter<ViewSearchFriendAdapter.MyViewHolder>{

    Activity activity;
    ArrayList<Profile> arrayList;
    int layoutId;

    ViewPostAdapter.OnClickItemListener onClickItemListener;

    public interface OnClickItemListener{
        void onClickItem(int position, View v);
    }

    public void setOnClickItemListener(ViewPostAdapter.OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }
    public ViewSearchFriendAdapter(Activity activity, ArrayList<Profile> arrayList, int layoutId) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.layoutId = layoutId;
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
        Profile profile = arrayList.get(position);
        holder.name.setText(profile.getName());
        holder.address.setText(profile.getAddress());
        Enum.setCircleImageView(holder.avatar,profile.getAvatar(),activity);
        holder.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickItemListener.onClickItem(position,view);
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

        CircleImageView avatar;
        TextView name;
        TextView address;
        View.OnClickListener onClickListener;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onClickListener.onClick(view);
        }
    }
}
