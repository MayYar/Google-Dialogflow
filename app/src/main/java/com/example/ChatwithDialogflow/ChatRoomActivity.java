package com.example.ChatwithDialogflow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ChatwithDialogflow.Adapter.MessageAdapter;
import com.example.ChatwithDialogflow.Model.Chat;
import com.example.ChatwithDialogflow.View.Thermometer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.language.v1beta2.CloudNaturalLanguage;
import com.google.api.services.language.v1beta2.CloudNaturalLanguageRequestInitializer;
import com.google.api.services.language.v1beta2.model.AnnotateTextRequest;
import com.google.api.services.language.v1beta2.model.AnnotateTextResponse;
import com.google.api.services.language.v1beta2.model.Document;
import com.google.api.services.language.v1beta2.model.Entity;
import com.google.api.services.language.v1beta2.model.Features;
import com.google.api.services.language.v1beta2.model.Token;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoomActivity extends AppCompatActivity {

    private static final String TAG = "ChatRoomActivity";
    static final int CUSTOM_POST_REQUEST = 1;  // The request code

    static float sentimentScore = 0;


    ImageButton btn_input;
    EditText ed_input;
//    TextView response;
    Button report, question, hope, notonlybus, wordcloud;

    private Thermometer thermometer;

    private int temperature = 0;

    ImageView iv_wordcloud;

    private DatabaseReference mDatabase;
    String userQuery;
    ArrayList<String> entities;

    private StorageReference mStorageRef;
    MessageAdapter messageAdapter;
    ArrayList<Chat> mchat = new ArrayList<>();
    public static int action = 0;

    RecyclerView recyclerView;
    Intent intent;
    public static String wordCloudPic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        //define view
        btn_input = (ImageButton)findViewById(R.id.btn_input);
        ed_input = (EditText) findViewById(R.id.ed_input);
//        response = (TextView) findViewById(R.id.tv_response);
        report = (Button)findViewById(R.id.report);
        question = (Button)findViewById(R.id.question);
        hope = (Button)findViewById(R.id.hope);
        notonlybus = (Button)findViewById(R.id.not_only_bus);
        wordcloud = (Button)findViewById(R.id.word_cloud);
        iv_wordcloud = (ImageView)findViewById(R.id.show_pic);
//        thermometer = (Thermometer) findViewById(R.id.thermometer);
//        thermometer.setCurrentTemp(temperature);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);

        //set the layout of recyclerview
        recyclerView.setLayoutManager(linearLayoutManager);

        btn_input.setOnClickListener(doClick);
        report.setOnClickListener(doClick);
        hope.setOnClickListener(doClick);
        notonlybus.setOnClickListener(doClick);
        question.setOnClickListener(doClick);
        wordcloud.setOnClickListener(doClick);
    }

    private Button.OnClickListener doClick = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.btn_input:
                    action = 0;
                    //User Input and run POST Request
                    sendMessage();
                    break;
                case R.id.report:
                    action = 1;
                    sendMessage();
                    break;
                case R.id.hope:
                    action = 2;
                    sendMessage();
                    break;
                case R.id.not_only_bus:
                    action = 3;
                    sendMessage();
                    break;
                case R.id.question:
                    action = 4;
                    sendMessage();
                    break;
                case R.id.word_cloud:
                    action = 5;
                    sendMessage();
                    break;
            }

        }
    };



    private void sendMessage() {
//        Intent intent = new Intent();
//        intent.putExtra("test", CUSTOM_POST_REQUEST);
        userQuery = ed_input.getText().toString();

        try {
            if(action == 1){
                userQuery = "立即回報";
                mchat.add(new Chat("sender", userQuery));
            }else if(action == 2) {
                userQuery = "許願池";
                mchat.add(new Chat("sender", userQuery));
            }else if(action == 3) {
                userQuery = "不。只。是。等。公。車";
                mchat.add(new Chat("sender", userQuery));
            }else if(action == 4) {
                userQuery = "常見問題";
                mchat.add(new Chat("sender", userQuery));
            }else if(action == 5) {
                userQuery = "最近搞什麼";

//                File localFile = File.createTempFile("images", "png");
//                Log.d(TAG, "download" + localFile.getAbsolutePath());
                mStorageRef.child("test1.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, uri.toString());
                        wordCloudPic = uri.toString();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

                mchat.add(new Chat("sender", userQuery));
            }else{
                mchat.add(new Chat("sender", userQuery));
            }
            Log.d(TAG, "User Query: " + userQuery);

            messageAdapter = new MessageAdapter(ChatRoomActivity.this, mchat);
            recyclerView.setAdapter(messageAdapter);

            //task running
            RetrieveFeedTask task=new RetrieveFeedTask();
            task.execute(userQuery);
            Log.d(TAG, "AsyncTask invoked");
            ed_input.setText("");


        }catch (ActivityNotFoundException e){
            Toast.makeText(this, "POST Request error", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
        }
    }


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



    private String NLPprocessing(){

//        需要HTTP傳輸和JSON工廠這兩個參數, 通過分配CloudNaturalLanguageRequestInitializer實例給它，可以強制它將API金鑰添加到所有請求中
        final CloudNaturalLanguage naturalLanguageService = new CloudNaturalLanguage.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                .setCloudNaturalLanguageRequestInitializer(new CloudNaturalLanguageRequestInitializer(Constants.CLOUD_API_KEY)).build();

//        要使用API​​分析的所有文本必須放在Document物件內
        String transcript = userQuery;
        Document document = new Document();
        document.setType("PLAIN_TEXT");
        document.setLanguage("zh-Hant");
        document.setContent(transcript);

        Features features = new Features();
        features.setExtractEntities(true);  //提取實體
        features.setExtractDocumentSentiment(true); //情緒分析
        features.setExtractSyntax(true);
//        features.setExtractEntitySentiment(true);

        final AnnotateTextRequest request = new AnnotateTextRequest();
        request.setDocument(document);
        request.setFeatures(features);

        AnnotateTextResponse response = null;
        try {
            response = naturalLanguageService.documents().annotateText(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

        final List<Entity> entityList = response.getEntities();

        entities = new ArrayList<>();
        for (Entity entity : entityList) {
            entities.add(entity.getName());
        }
        Log.d(TAG, "Entity = " + entities);
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        Log.d(TAG, "xxxx");

        final DatabaseReference databaseReference = mDatabase.child("entities");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(String entity:entities){
                    int size = (int)dataSnapshot.getChildrenCount();
//                System.out.println(size);
                    Map<String,Object> childUpdates = new HashMap<>();
                    childUpdates.put(String.valueOf(size), entity);
                    databaseReference.updateChildren(childUpdates);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        Map<String,Object> childUpdates = new HashMap<>();
//        childUpdates.put("1", entities);
//        databaseReference.updateChildren(childUpdates);

//        mDatabase.child("entities").setValue(entities);

        final float sentiment = response.getDocumentSentiment().getScore();
        sentimentScore = response.getDocumentSentiment().getScore();
        final List<Token> tokenList = response.getTokens();



        return String.valueOf(sentimentScore);

//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                //Instantiates a client
//
//                AnnotateTextResponse response = null;
//                try {
//                    response = naturalLanguageService.documents().annotateText(request).execute();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                final List<Entity> entityList = response.getEntities();
//                final float sentiment = response.getDocumentSentiment().getScore();
//                sentimentScore = response.getDocumentSentiment().getScore();
////                final List<Sentence> sentenceList = response.getSentences();
//                final List<Token> tokenList = response.getTokens();

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String entities = "";
//                        for (Entity entity : entityList) {
//                            entities += "\n" + entity.getName().toUpperCase();
//                        }
//
//                        String tokens = "";
//                        for (Token token: tokenList) {
//                            tokens += "\n" + token.getLemma();
//                        }
//
//                        AlertDialog dialog = new AlertDialog.Builder(ChatRoomActivity.this)
//                                .setTitle("Sentiment: " + sentiment)
//                                .setMessage("This message talks about: " + entities + " tokens = " + tokens)
//                                .setNeutralButton("OK", null)
//                                .create();
//                        dialog.show();
//                    }
//                });

//            }
//        });

    }



    // Asynctask to run POST Request
    class RetrieveFeedTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... voids) {
            String s = null;
            try{
                Log.d(TAG, "doInBackground. Param: " + voids[0]);
                s = GetResponse(voids[0]);
                sentimentScore = Float.valueOf(NLPprocessing());

            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            response.setText(s);
//            Linkify.addLinks(response, Linkify.EMAIL_ADDRESSES);
            System.out.println("sentimentScore: " + sentimentScore);
            if(sentimentScore > 0.5){
//                System.out.println("好正面: " + s);
                thermometer.setCurrentTemp(temperature);
                mchat.add(new Chat("receiver", Constants.POSITiVE_REPLY));
                messageAdapter = new MessageAdapter(ChatRoomActivity.this, mchat);
                recyclerView.setAdapter(messageAdapter);
            }else if(sentimentScore < -0.5) {
//                System.out.println("好負面: " + s);
                thermometer.setCurrentTemp(temperature);
                mchat.add(new Chat("receiver", Constants.NEGATIVE_REPLY));
                messageAdapter = new MessageAdapter(ChatRoomActivity.this, mchat);
                recyclerView.setAdapter(messageAdapter);
            }else{
                if(userQuery == "常見問題") {
                    mchat.add(new Chat("question_list", s));
                    messageAdapter = new MessageAdapter(ChatRoomActivity.this, mchat, ed_input);
                    recyclerView.setAdapter(messageAdapter);
                }else if(userQuery == "許願池") {
                    mchat.add(new Chat("hope_well", s));
                    messageAdapter = new MessageAdapter(ChatRoomActivity.this, mchat);
                    recyclerView.setAdapter(messageAdapter);
                }else if(userQuery == "最近搞什麼") {
                    mchat.add(new Chat("word_cloud", "reply文字雲"));
                    messageAdapter = new MessageAdapter(ChatRoomActivity.this, mchat);
                    recyclerView.setAdapter(messageAdapter);
                }else {
                    mchat.add(new Chat("receiver", s));
                    messageAdapter = new MessageAdapter(ChatRoomActivity.this, mchat);
                    recyclerView.setAdapter(messageAdapter);
                }
            }
            if(sentimentScore > 0)
                temperature = temperature + 1;
            else
                temperature = temperature - 1;


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            AlertDialog alertDialog = dialogBuilder.create();
            dialogBuilder.setCancelable(true);

            alertDialog.show();
            alertDialog.getWindow().setBackgroundDrawable(null);
//            alertDialog.getWindow().setLayout(300, 400);

//            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//            Log.d(TAG, lp)
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            Log.e("Width", "" + width);
            Log.e("height", "" + height);

            alertDialog.getWindow().setLayout(width/3, height/2);


            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.thermometer, null);

            alertDialog.getWindow().setContentView(dialogView);
            thermometer = (Thermometer) dialogView.findViewById(R.id.thermometer);
            thermometer.setCurrentTemp(temperature);
//            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
