package com.bernard.hollarena.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bernard.hollarena.Chat;
import com.bernard.hollarena.R;
import com.bernard.hollarena.model.UserDetails;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<String> list;

    public UserAdapter(List<String> result) {
        this.list = result;


    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.layout_all_users,parent,false));
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.textname.setText(list.get(position));
        final int pos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                Intent intent = new Intent(c, Chat.class);
//                UserDetails.chatWith = list.get(pos);

                c.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textname;
        public UserViewHolder(View itemView) {
            super(itemView);
            textname = (TextView) itemView.findViewById(R.id.all_user_name);
        }
    }


}
