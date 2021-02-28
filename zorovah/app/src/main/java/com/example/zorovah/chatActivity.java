package com.example.zorovah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.zorovah.adapters.chatAdapter;
import com.example.zorovah.models.chatModel;
import com.example.zorovah.registration.UserDetailsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatActivity extends AppCompatActivity {
   private ImageView userProfile;
   private ImageView send;
   private TextView userName,status;
   private RecyclerView recyclerView;
   private EditText messageText;
   private chatAdapter chatAdapter;
   private List<chatModel> list;
   private String myUid;
   private String hisUid;
   private String his_name="";
   private String his_url="";
    private String name="";
    private String url="";
    private ProgressBar progressBar;

    @Override
    protected void onStart() {
        super.onStart();
        checkOnlineStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String timestamp=String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOnlineStatus("online");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        userProfile=findViewById(R.id.userProfile);
        progressBar=findViewById(R.id.progressBar4);
        userName=findViewById(R.id.userName);
        status=findViewById(R.id.status);
        Toolbar toolbar=findViewById(R.id.toolbar);
        recyclerView=findViewById(R.id.chatRecylerView);
        send=findViewById(R.id.send);
        messageText=findViewById(R.id.messageText);
        myUid= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().toString();

        LinearLayoutManager chatManager=new LinearLayoutManager(this);
        chatManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(chatManager);

       hisUid=getIntent().getStringExtra("hisUid");
        FirebaseFirestore.getInstance().collection("user").document(myUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    name=task.getResult().get("username").toString();
                    url=task.getResult().get("profile_url").toString();

                }
            }
        });
        FirebaseFirestore.getInstance().collection("user").document(hisUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    his_name=task.getResult().get("username").toString();
                    his_url=task.getResult().get("profile_url").toString();
                    String OnlineStatus=task.getResult().get("onlineStatus").toString();
                    if(OnlineStatus.equals("online")){status.setText(OnlineStatus);}
                    else{
                        Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
                        calendar.setTimeInMillis(Long.parseLong(OnlineStatus));
                        String dateTime= DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();
                        status.setText("Last Seen at "+dateTime);
                    }
                    userName.setText(his_name);
                    Glide.with(chatActivity.this).load(his_url).centerCrop().placeholder(R.drawable.profile).into(userProfile);
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String message=messageText.getText().toString().trim();
                //Toast.makeText(chatActivity.this, message, Toast.LENGTH_SHORT).show();


                if(message.isEmpty()){
                    Toast.makeText(chatActivity.this, "Unable to send Empty message", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    String timestamp=String.valueOf(System.currentTimeMillis());
                    Map<String,Object> map=new HashMap<>();
                    map.put("sender", myUid);
                    map.put("receiver",hisUid);
                    map.put("message",message);
                    map.put("timestamp",timestamp);
                    map.put("isSeen",false);

                    FirebaseFirestore.getInstance().collection("chats").document(timestamp).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(chatActivity.this, "message successfully Sent", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                messageText.setText("");

                                readMessage();
                                chatlist();
                            }
                            else {
                                Toast.makeText(chatActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });

       list=new ArrayList<>();
       readMessage();

    }
    public void addchatlist(){
        Map<String,String> map=new HashMap<>();
        map.put("sender",myUid);
        map.put("receiver",hisUid);
        map.put("s_url",url);
        map.put("s_name",name);
        map.put("r_url",his_url);
        map.put("r_name",his_name);
        FirebaseFirestore.getInstance().collection("chatList").document().set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if(!task.isSuccessful()){
                   Toast.makeText(chatActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
               }
            }
        });
    }
    public void chatlist(){
        FirebaseFirestore.getInstance().collection("chatList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> list_chat = task.getResult().getDocuments();
                    boolean flag = false;
                    for (int i = 0; i < list_chat.size(); i++) {
                        if (list_chat.get(i).get("sender").equals(hisUid) && list_chat.get(i).get("receiver").equals(myUid) || list_chat.get(i).get("receiver").equals(hisUid) && list_chat.get(i).get("sender").equals(myUid)) {
                            flag = true;
                            break;
                        }
                    }
                    if(!flag){addchatlist();}
                                         }
            }
        });

    }
    public void readMessage(){
        FirebaseFirestore.getInstance().collection("chats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    list.clear();
                    List<DocumentSnapshot> chatList=task.getResult().getDocuments();
                    for(int i=0;i<chatList.size();i++){
                        if(chatList.get(i).get("sender").equals(hisUid) && chatList.get(i).get("receiver").equals(myUid) || chatList.get(i).get("sender").equals(myUid) && chatList.get(i).get("receiver").equals(hisUid))
                        {
                            list.add(new chatModel(chatList.get(i).get("sender").toString(),chatList.get(i).get("receiver").toString(),chatList.get(i).get("message").toString(),chatList.get(i).get("timestamp").toString(),(boolean)chatList.get(i).get("isSeen")));
                        }
                        chatAdapter=new chatAdapter(list);
                        recyclerView.setAdapter(chatAdapter);

                    }

                }
            }
        });
    }
    public void checkOnlineStatus(String status){
        Map<String,Object> user=new HashMap<>();
        user.put("onlineStatus",status);
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
     }
}