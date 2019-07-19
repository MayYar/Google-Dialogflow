package com.example.ChatwithDialogflow.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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
                    .append("----點我填願望吧----");

            holder.show_message.setText(builder);
            Linkify.addLinks(holder.show_message, Linkify.EMAIL_ADDRESSES);
            holder.show_message.setOnClickListener(doClick);
        }else if (chat.getMessage().contains("來逛逛最新資訊吧！") ){
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(chat.getMessage())
                    .append("\n")
                    .append(" ", new ImageSpan(mContext, R.drawable.notonlybus), 0)
                    .append("\n");

            holder.show_message.setText(builder);
            Linkify.addLinks(holder.show_message, Linkify.WEB_URLS);

        }else if (chat.getMessage().contains("以下為常見問題") ){
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(chat.getMessage())
                    .append("測試" ,new TextView(mContext), 0)
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

    private TextView.OnClickListener doClick = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            final EditText input = new EditText(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            builder.setTitle("許願池")
                    .setMessage("說出你的願望")
                    .setView(input)
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }
    };
}
