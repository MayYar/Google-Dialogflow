package com.example.ChatwithDialogflow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ChatwithDialogflow.Adapter.MessageAdapter;
import com.example.ChatwithDialogflow.Model.Chat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ChatRoomActivity extends AppCompatActivity {

    private static final String TAG = "ChatRoomActivity";
    static final int CUSTOM_POST_REQUEST = 1;  // The request code

    ImageButton btn_input;
    EditText ed_input;
    TextView response;

    MessageAdapter messageAdapter;
    ArrayList<Chat> mchat = new ArrayList<>();

    RecyclerView recyclerView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        //define view
        btn_input = (ImageButton)findViewById(R.id.btn_input);
        ed_input = (EditText) findViewById(R.id.ed_input);
        response = (TextView) findViewById(R.id.tv_response);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);

        //set the layout of recyclerview
        recyclerView.setLayoutManager(linearLayoutManager);

        btn_input.setOnClickListener(doClick);
    }

    private Button.OnClickListener doClick = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {

            //User Input and run POST Request
            sendMessage();
        }
    };

    private void sendMessage() {
//        Intent intent = new Intent();
//        intent.putExtra("test", CUSTOM_POST_REQUEST);

        try {
//            startActivityForResult(intent, CUSTOM_POST_REQUEST);
            String userQuery = ed_input.getText().toString();
            ed_input.setText("");

            Log.d(TAG, "User Query: " + userQuery);
            mchat.add(new Chat("sender", userQuery));

            messageAdapter = new MessageAdapter(ChatRoomActivity.this, mchat);

            recyclerView.setAdapter(messageAdapter);

            //task running
            RetrieveFeedTask task=new RetrieveFeedTask();
            task.execute(userQuery);
            Log.d(TAG, "AsyncTask invoked");

        }catch (ActivityNotFoundException e){
            Toast.makeText(this, "POST Request error", Toast.LENGTH_SHORT).show();
        }
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode){
//            case CUSTOM_POST_REQUEST:
//                if (requestCode == CUSTOM_POST_REQUEST && null != data){
//
//                    String userQuery = ed_input.getText().toString();
//                    ed_input.setText(userQuery);
//                    Log.d(TAG, "User Query: " + userQuery);
//                    RetrieveFeedTask task=new RetrieveFeedTask();
//                    task.execute(userQuery);
//                }
//                break;
//        }
//    }

    // function connect with API.api and get json response
    public String GetResponse(String query) throws UnsupportedEncodingException{

        String text = "";
        BufferedReader reader;

        try{
            URL url = new URL("https://api.dialogflow.com/v1/query?v=20150910");
            URLConnection conn = url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // Parameter, including Access token
            conn.setRequestProperty("Authorization", "Bearer " + "60e9b458dc2147ef94437dfdb0c62f2a");
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonParam = new JSONObject();
            JSONArray queryArray = new JSONArray();
            queryArray.put(query);
            jsonParam.put("query", queryArray);

            jsonParam.put("lang", "en");
            //sessionId: random value
            jsonParam.put("sessionId", "1234567890");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(jsonParam.toString());
            Log.d(TAG, "Write jsonParam");

            wr.flush();

            //Get sever response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

//            Read server Response
            while ((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }

            text = sb.toString();
            Log.d(TAG, "response is " + text);
            JSONObject object = new JSONObject(text);
            JSONObject object1 = object.getJSONObject("result");
            JSONObject fulfillment = null;
            String speech = null;
            fulfillment = object1.getJSONObject("fulfillment");
            speech = fulfillment.getString("speech");

            return speech;

        }catch (Exception e){
            Log.d(TAG, "Exception occur: " + e);

        }
        finally {
            try {

            }catch (Exception ex){

            }
        }
        return null;
    }

    // Asynctask to run POST Request
    class RetrieveFeedTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... voids) {
            String s = null;
            try{
                Log.d(TAG, "doInBackground. Param: " + voids[0]);
                s = GetResponse(voids[0]);

            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            response.setText(s);
            mchat.add(new Chat("receiver", s));
            messageAdapter = new MessageAdapter(ChatRoomActivity.this, mchat);
            recyclerView.setAdapter(messageAdapter);
        }
    }

}
