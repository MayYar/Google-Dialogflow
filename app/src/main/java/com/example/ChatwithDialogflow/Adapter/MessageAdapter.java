package com.example.ChatwithDialogflow.Adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ChatwithDialogflow.Model.Chat;
import com.example.ChatwithDialogflow.R;

import java.util.ArrayList;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final String TAG = "MessageAdapter";

//    public static int MSG_TYPE_RIGHT = 0;
//    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private ArrayList<Chat> mChat = new ArrayList<>();


    public MessageAdapter(Context mContext, ArrayList<Chat> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;
    }

    //從getItemViewType取得sender or receiver, create diff type holder, viewType即return value
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }

    }

    //每個holder顯示內容
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat = mChat.get(position);
        Log.d(TAG, "(" +position + ") " + chat.getMessage());
        if(chat.getMessage().equals("有什麼想要建議我們的嗎~快來說下你的願望吧！") ){

            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(chat.getMessage())
                    .append("\n")
                    .append(" ", new ImageSpan(mContext, R.drawable.fountain), 0)
                    .append("\n")
                    .append("-------");

            holder.show_message.setText(builder);
            Linkify.addLinks(holder.show_message, Linkify.EMAIL_ADDRESSES);
        }else if (chat.getMessage().contains("來逛逛最新資訊吧！") ){
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(chat.getMessage())
                    .append("\n")
                    .append(" ", new ImageSpan(mContext, R.drawable.notonlybus), 0)
                    .append("\n");

            holder.show_message.setText(builder);
            Linkify.addLinks(holder.show_message, Linkify.WEB_URLS);

        }else
            holder.show_message.setText(chat.getMessage());
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    //define item in holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
        }
    }

    //定義每個holder類型
    @Override
    public int getItemViewType(int position) {
        if(mChat.get(position).getType() == "sender")
            return 0;
        else
            return 1;
    }
}
