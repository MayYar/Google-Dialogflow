package com.example.ChatwithDialogflow.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.text.SpannableStringBuilder;
import android.text.style.EasyEditSpan;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ChatwithDialogflow.Model.Chat;
import com.example.ChatwithDialogflow.R;

import java.io.InputStream;
import java.util.ArrayList;

import static com.example.ChatwithDialogflow.ChatRoomActivity.wordCloudPic;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final String TAG = "MessageAdapter";

//    public static String wordCloudPic;


//    public static int MSG_TYPE_RIGHT = 0;
//    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private ArrayList<Chat> mChat = new ArrayList<>();
    private EditText ed_input;


    public MessageAdapter(Context mContext, ArrayList<Chat> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;
    }

    public MessageAdapter(Context mContext, ArrayList<Chat> mChat, EditText editText) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.ed_input = editText;
    }

    //從getItemViewType取得sender or receiver, create diff type holder, viewType即return value
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else if (viewType == 2){
            View view = LayoutInflater.from(mContext).inflate(R.layout.question_list, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else if (viewType == 3){
            View view = LayoutInflater.from(mContext).inflate(R.layout.hope_well, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else if (viewType == 4){
            View view = LayoutInflater.from(mContext).inflate(R.layout.word_cloud, parent, false);
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
        if(chat.getMessage().equals("有什麼想要建議我們的嗎~快來說下你的願望吧！") ){    //許願池

//            holder.hope_message.setVisibility(View.VISIBLE);
//            holder.btn_ok.setVisibility(View.VISIBLE);


            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(chat.getMessage())
                    .append("\n")
                    .append(" ", new ImageSpan(mContext, R.drawable.fountain), 0);

            holder.show_message.setText(builder);
//            Linkify.addLinks(holder.show_message, Linkify.EMAIL_ADDRESSES);
            holder.show_message.setOnClickListener(doClick);
        }else if (chat.getMessage().contains("來逛逛最新資訊吧！") ) {    //Forum
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(chat.getMessage())
                    .append("\n")
                    .append(" ", new ImageSpan(mContext, R.drawable.notonlybus), 0)
                    .append("\n");

            holder.show_message.setText(builder);
            Linkify.addLinks(holder.show_message, Linkify.WEB_URLS);



        }else if (chat.getMessage().contains("近期常見問題") ){  //
            holder.test1.setOnClickListener(doClick);
            holder.test2.setOnClickListener(doClick);
            holder.test3.setOnClickListener(doClick);


//
//            holder.show_message.setText(builder);
//            Linkify.addLinks(holder.show_message, Linkify.WEB_URLS);
        }else if (chat.getMessage().contains("reply文字雲") ){
//            holder.word_cloud.setImageResource(R.drawable.bus);
//             holder.word_cloud.setImageResource(R.mipmap.ic_launcher);
//            Glide.with(mContext).load(wordCloudPic).into(holder.word_cloud);



        }else
            holder.show_message.setText(chat.getMessage());
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    //define item in holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message, test1, test2, test3;
        public EditText hope_message;
        public Button btn_ok;
        public ImageView word_cloud;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            hope_message = itemView.findViewById(R.id.hope_message);
            btn_ok = itemView.findViewById(R.id.btn_ok);
            word_cloud = itemView.findViewById(R.id.show_pic);
            test1 = itemView.findViewById(R.id.test1);
            test2 = itemView.findViewById(R.id.test2);
            test3 = itemView.findViewById(R.id.test3);

        }
    }

    //定義每個holder類型
    @Override
    public int getItemViewType(int position) {
        if(mChat.get(position).getType() == "sender")
            return 0;
        else if (mChat.get(position).getType() == "question_list")
            return 2;
        else if (mChat.get(position).getType() == "hope_well")
            return 3;
        else if (mChat.get(position).getType() == "word_cloud")
            return 4;
        else
            return 1;
    }

    private TextView.OnClickListener doClick = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.test1:
                    ed_input.setText("路線規劃可變回中文站名嗎?");
                    break;
                case R.id.test2:
                    ed_input.setText("不能離線操作嗎?");
                    break;
                case R.id.test3:
                    ed_input.setText("可不可以多個機車導航");
                    break;

                case R.id.show_message:
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
                    break;

            }

        }
    };



}
