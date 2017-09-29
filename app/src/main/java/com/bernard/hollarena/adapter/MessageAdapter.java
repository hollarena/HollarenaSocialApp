package com.bernard.hollarena.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bernard.hollarena.R;
import com.bernard.hollarena.model.Messages;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    FirebaseAuth mAuth;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;


    public MessageAdapter(List<Messages> messageList){
        this.mMessageList = messageList;
    }


    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT ){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent,parent,false);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received,parent,false);
        } else{
            return null;
        }
        return new MessageViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        mAuth = FirebaseAuth.getInstance();
        String mCurrentUserId = mAuth.getCurrentUser().getUid();

        Messages messages = mMessageList.get(position);

        String fromId = messages.getFrom();


        if (fromId.equals(mCurrentUserId)){
            //my text
            return  VIEW_TYPE_MESSAGE_SENT;
        }else {
            //sender text
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public void onBindViewHolder(MessageAdapter.MessageViewHolder holder, int position) {

        Messages messages = mMessageList.get(position);

        holder.messageTextBody.setText(messages.getMessage());


    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextBody;
        public TextView timeText;
        public MessageViewHolder(View itemView) {
            super(itemView);

            messageTextBody = (TextView) itemView.findViewById(R.id.text_message_body);
        }
    }
}
