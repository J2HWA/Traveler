package com.likeonline.travelmaker.chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.likeonline.travelmaker.LoginActivity;
import com.likeonline.travelmaker.R;
import com.likeonline.travelmaker.TravelCustomActivity;
import com.likeonline.travelmaker.TravelDialog;
import com.likeonline.travelmaker.TravelMydlg;
import com.likeonline.travelmaker.TravelgoodActivity;
import com.likeonline.travelmaker.TravelgoodActivity2;
import com.likeonline.travelmaker.TravelreportActivity;
import com.likeonline.travelmaker.TravelreportActivity2;
import com.likeonline.travelmaker.Travelyourdlg;
import com.likeonline.travelmaker.detailProfile.YourPageActivity;
import com.likeonline.travelmaker.model.ChatModel;
import com.likeonline.travelmaker.model.NotificationModel;
import com.likeonline.travelmaker.model.ReportModel;
import com.likeonline.travelmaker.model.UserModel;
import com.likeonline.travelmaker.reportDialog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//import com.likeonline.travelmaker.fragment.PeopleFragment;

public class MessageActivity extends AppCompatActivity {
    private FirebaseRemoteConfig firebaseRemoteConfig;
    String splash_background;
    private DatabaseReference mReference; // 데이터베이스 값을 읽어오기 위한 레퍼런스 선언
    private FirebaseAuth mFirebase_Auth; // 파이어베이스 계정 값을 읽어오기 위한 선언
    private String destinationUid;
    private Button button;
    private EditText editText;
    private String uid;
    private String uid1;
    private String chatRoomUid;
    private RecyclerView recyclerView1; // 채팅방 뷰

    private String mUser_id ;






    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.HH:mm");
    private UserModel destinationUserModel;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    //final static PeopleFragment peopleFragment = new PeopleFragment();
    //private static RecyclerView recyclerView2= peopleFragment.recyclerView; //유저뷰
    int peopleCount = 0;
    ChatModel chatModel = new ChatModel();

    private long kakaoEmail;
    private com.likeonline.travelmaker.reportDialog reportDialog; //신고하기 커스텀 다이얼로그
    private TravelDialog travelDialog; //여행하기 커스텀 다이얼로그
    private RatingBar ratingBar;
    private float ratingScore; // 신뢰도 점수
    private float scoreCount; //칭찬하기 및 신고하기 카운트
    private int user_score;
    String userName;
    FirebaseDatabase database;
    DatabaseReference myRef;

    private ValueEventListener mMessageListener;
    private String report;
    private String name; //상대 유저 이름
    private String name2; //내 유저 이름

    public void setUser_trustScore(float ratingScore) { //커스텀다이얼로그 클래스에 있는 레이팅바 점수를 받아오는 함수
        this.ratingScore = ratingScore;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        final Handler timer = new Handler(); //Handler 생성





        kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;
        destinationUid = getIntent().getStringExtra("destinationUid"); //채팅을 당하는 아이디

        if(kakaoEmail==0) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            uid = String.valueOf(kakaoEmail);
        }

        Toolbar board_toolbar = findViewById(R.id.messageActivity_toolbar);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.samdan_mint);
        board_toolbar.setOverflowIcon(drawable);
        setSupportActionBar(board_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.button_back);

        mReference=FirebaseDatabase.getInstance().getReference();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();



        myRef.child ("users").child (uid).addValueEventListener (new ValueEventListener () {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child ("user_accept").getValue ().toString ().equals ("2")){  //나의 user_accept의 값이 2일때 Travelyourdlg 를 띄어줌

                    Intent intent = new Intent(getApplication (), Travelyourdlg.class);
                    intent.putExtra ("travel_id2", uid);
                    intent.putExtra ("travel_id",destinationUid);
                    intent.putExtra ("user_travel",chatRoomUid);

                    startActivity(intent);
                   /* timer.postDelayed(new Runnable(){ //2초후 쓰레드를 생성하는 postDelayed 메소드

                        public void run(){

//intent 생성

                            Intent intent = new Intent(MessageActivity.this, Travelyourdlg.class);

                            intent.putExtra ("travel_id2", uid);
                            intent.putExtra ("travel_id",destinationUid);

                            startActivity(intent); //다음 액티비티 이동


                        }

                    }, 2000); //2000은 2초를 의미한다.*/
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.child ("users").addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child (uid).child ("user_report").getValue ().toString ().equals ("2") ){



                    Intent intent = new Intent(getApplication(), TravelreportActivity.class);
                    intent.putExtra ("travel_id2", uid);
                    intent.putExtra ("travel_id",destinationUid);
                    intent.putExtra ("user_travel",chatRoomUid);

                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
     /*   myRef.child ("users").addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child (destinationUid).child ("user_report").getValue ().toString ().equals ("3") ){



                    Intent intent = new Intent(getApplication(), TravelreportActivity2.class);
                    intent.putExtra ("travel_id2", uid);
                    intent.putExtra ("travel_id",destinationUid);
                    intent.putExtra ("user_travel",chatRoomUid);

                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/
        myRef.child ("users").addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child (uid).child ("user_good").getValue ().toString ().equals ("3") && dataSnapshot.child (uid).child ("user_goodCP").getValue ().toString ().equals ("1") && dataSnapshot.child (destinationUid).child ("user_good").getValue ().toString ().equals ("1") && dataSnapshot.child (destinationUid).child ("user_goodCP").getValue ().toString ().equals ("1") ){
                    Intent intent = new Intent(getApplicationContext (), TravelgoodActivity2.class);
                    intent.putExtra ("travel_id2", uid);
                    intent.putExtra ("travel_id",destinationUid);
                    intent.putExtra ("user_travel",chatRoomUid);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.child ("users").addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child (uid).child ("user_good").getValue ().toString ().equals ("2") && dataSnapshot.child (destinationUid).child ("user_good").getValue ().toString ().equals ("3") && dataSnapshot.child (uid).child ("user_goodCP").getValue ().toString ().equals ("1") ){
                    Intent intent = new Intent(getApplication(), TravelgoodActivity.class);
                    intent.putExtra ("travel_id2", uid);
                    intent.putExtra ("travel_id",destinationUid);
                    intent.putExtra ("user_travel",chatRoomUid);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        recyclerView1 = (RecyclerView) findViewById(R.id.messageActivity_recyclerview);

        //uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); //채팅을 요구하는 아이디 즉 단말기에 로그인된 UID



        mReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_score=Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = firebaseRemoteConfig.getString("splash_background");
        button = (Button) findViewById(R.id.messageActivity_button);
        editText = (EditText) findViewById(R.id.messageActivity_editText);




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chatModel.users.put(uid, true);
                chatModel.users.put(destinationUid, true);

                if (chatRoomUid == null) {
                    button.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });
                } else {
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = uid;
                    comment.message = editText.getText().toString();
                    comment.timestamp = ServerValue.TIMESTAMP;
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            sendGcm();
                            editText.setText("");
                        }
                    });
                }

            }
        });
        checkChatRoom();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }
    }


    //메뉴 연결
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();




        myRef.child ("chatrooms").addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child (chatRoomUid).getValue ()==null){

                }

                else if (dataSnapshot.child (chatRoomUid).child ("user_travel").getValue ().toString ().equals ("2")) {
                    menu.getItem (1).setEnabled (true);
                    menu.getItem (2).setEnabled (true);
                    menu.getItem (0).setEnabled (false);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child ("chatrooms").child (chatRoomUid).getValue ()==null){

                }
                else if (dataSnapshot.child ("chatrooms").child (chatRoomUid).child ("user_report").getValue ().toString ().equals ("2") && dataSnapshot.child ("users").child (uid).child ("user_report").getValue ().toString ().equals ("3")) {
                    menu.getItem (2).setEnabled (false);
                }
                if(dataSnapshot.child ("chatrooms").child (chatRoomUid).getValue ()==null){

                }
                else if (dataSnapshot.child ("chatrooms").child (chatRoomUid).child ("user_report").getValue ().toString ().equals ("3")) {
                    menu.getItem (2).setEnabled (false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myRef.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child ("chatrooms").child (chatRoomUid).getValue ()==null){

                }
                else if (dataSnapshot.child ("chatrooms").child (chatRoomUid).child ("user_good").getValue ().toString ().equals ("2") && dataSnapshot.child ("users").child (uid).child ("user_good").getValue ().toString ().equals ("3")) {
                    menu.getItem (1).setEnabled (false);
                }
                if(dataSnapshot.child ("chatrooms").child (chatRoomUid).getValue ()==null){

                }
                else if (dataSnapshot.child ("chatrooms").child (chatRoomUid).child ("user_good").getValue ().toString ().equals ("3")) {
                    menu.getItem (1).setEnabled (false);
                }
                else if (dataSnapshot.child ("chatrooms").child (chatRoomUid).child ("user_good").getValue ().toString ().equals ("4")) {
                    menu.getItem (1).setEnabled (false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        MenuInflater menuInflater = getMenuInflater ();
        menuInflater.inflate (R.menu.menu_usertrust_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 메뉴 클릭
                finish();
                return true;
            case R.id.action_travel: // 여행하기 메뉴 클릭
                Intent intent = new Intent(getApplicationContext(), TravelMydlg.class);
                intent.putExtra ("travel_id", destinationUid);

                startActivity (intent);
                return true;
            case R.id.action_good: // 칭찬하기 메뉴 클릭
                //파라미터에 리스너 등록
                Intent intent1 = new Intent(getApplicationContext(), TravelCustomActivity.class);
                intent1.putExtra ("travel_id", destinationUid);
                intent1.putExtra ("travel_id2", uid);
                intent1.putExtra ("user_travel", chatRoomUid);

                startActivity (intent1);
                return true;
            case R.id.action_fire:
                Intent intent2 = new Intent(getApplicationContext(), reportDialog.class);
                intent2.putExtra ("travel_id", destinationUid);
                intent2.putExtra ("travel_id2", uid);
                intent2.putExtra ("user_travel", chatRoomUid);

                startActivity (intent2);
                return true;
            case R.id.action_exit:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("채팅방");
                builder.setMessage("나가시겠습니까?");
                builder.setPositiveButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"취소되었습니다.",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.setNegativeButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                new Handler().postDelayed(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {

                                        myRef.addListenerForSingleValueEvent (new ValueEventListener () {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                myRef.child("chatrooms").child(chatRoomUid).removeValue();

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                        // FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).removeValue();
                                    }
                                }, 500);// 0.5초 정도 딜레이를 준 후 시작

                                Toast.makeText(getApplicationContext(), "채팅방을 나갔습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.show();
                return true;
        }
        return false;
    }


    private View.OnClickListener positiveListener1 = new View.OnClickListener() { //신고하기 커스텀 다이얼로그
        public void onClick(View v) {

            mReference.child ("chatrooms").child (chatRoomUid).addListenerForSingleValueEvent (new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.child ("user_report").getRef ().setValue ("2");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mReference.child ("users").child (uid).addListenerForSingleValueEvent (new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.child ("user_report").getRef ().setValue ("3");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mReference.child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.child ("user_report").getRef ().setValue ("2");

                    float totalScore=Float.valueOf(dataSnapshot.child("user_trustScore").getValue().toString()); //유저 토탈 신뢰도 점수
                    name=dataSnapshot.child("userName").getValue().toString(); //상대 유저 이름
                    totalScore-=1;
                    scoreCount=Float.valueOf(dataSnapshot.child("user_trustCount").getValue().toString());
                    scoreCount++;
                    user_score-=10;
                    final Map<String, Object> taskMap1 = new HashMap<String, Object>(); //업데이트 시킬 total레이팅
                    final Map<String, Object> taskMap2 = new HashMap<String, Object>();//업데이트 시킬 리뷰갯수
                    taskMap1.put("user_trustScore", totalScore);
                    taskMap2.put("user_trustCount",scoreCount);
                    final Map<String, Object> taskMap3 = new HashMap<String, Object>();//업데이트 시킬 유저 활동점수
                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(taskMap3);
                    mReference.child("users").child(destinationUid).updateChildren(taskMap1);
                    mReference.child("users").child(destinationUid).updateChildren(taskMap2);
                    ReportModel reportModel=new ReportModel(
                            report=name+" 유저를 신고하였습니다."
                    );
                    mReference.child("users").child(uid).child("report").push().setValue(reportModel);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name2=dataSnapshot.child("userName").getValue().toString();
                    ReportModel reportModel2=new ReportModel(
                            report=name2+" 유저에게 신고를 받았습니다."
                    );
                    mReference.child("users").child(destinationUid).child("report").push().setValue(reportModel2);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            Toast.makeText(getApplicationContext(), "신고처리가 완료되었습니다.",Toast.LENGTH_SHORT).show();

        }
    };


    private View.OnClickListener positiveListener2 = new View.OnClickListener() { //여행하기 커스텀 다이얼로그
        public void onClick(View v) {

            chatModel.users.put(uid, true);
            chatModel.users.put(destinationUid, true);

            if (chatRoomUid == null) {
                button.setEnabled(false);
                FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        checkChatRoom();
                    }
                });
            } else {
                ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = uid;
                comment.message = "여행신청을 수락하시겠습니까? ";
                comment.timestamp = ServerValue.TIMESTAMP;
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendGcm();
                        editText.setText("");
                    }
                });
            }

            checkChatRoom();
            Toast.makeText(getApplicationContext(), "여행신청이 완료되었습니다.",Toast.LENGTH_SHORT).show();
            travelDialog.dismiss();
        }
    };

    private View.OnClickListener cancelListener2 = new View.OnClickListener() { //여행하기 커스텀 다이얼로그
        public void onClick(View v) {
            travelDialog.dismiss();
        }
    };

    void sendGcm() {
        Gson gson = new Gson();
        if(kakaoEmail==0){
            userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }else{
            FirebaseDatabase.getInstance().getReference().child("users").child(String.valueOf(kakaoEmail)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userName=dataSnapshot.child("userName").getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = destinationUserModel.pushToken;
        notificationModel.notification.title = userName;
        notificationModel.notification.text = editText.getText().toString();
        notificationModel.data.title = userName;
        notificationModel.data.text = editText.getText().toString();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationModel));

        Request request = new Request.Builder()
                .header("Content-Type","application/json")
                .addHeader("Authorization","key=AAAAB11Bl9s:APA91bEPmcUpm28pmsl6eDMwVUdyfyvUd-88TC2k3hU1XKQzOPhaCJhgHCFBjjMTkz2A8C2Ih8wG1g6-z5N1GAvuGc9WkDK0ZTBu8FtaG6YLJDqSQLJO-44M6S9BciS3bVqEF9NEAS6h")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    void checkChatRoom() { //아이디 중복체크
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    ChatModel newRoom = new ChatModel();
                    newRoom.users.put(uid, true);
                    newRoom.users.put(destinationUid, true);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(newRoom).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });
                    return;
                }

                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    if (chatModel.users.containsKey(destinationUid) && chatModel.users.size() == 2) {
                        chatRoomUid = item.getKey();
                        button.setEnabled(true);
                        recyclerView1.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        recyclerView1.setAdapter(new RecycelrViewAdapter());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    class RecycelrViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<ChatModel.Comment> comments;

        public RecycelrViewAdapter() {
            comments = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    destinationUserModel = dataSnapshot.getValue(UserModel.class);
                    getMessageList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        void getMessageList() {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments");
            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    comments.clear();
                    Map<String, Object> readUsersMap = new HashMap<String, Object>();

                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        String key = item.getKey();
                        ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                        ChatModel.Comment comment_modify  = item.getValue(ChatModel.Comment.class);
                        comment_modify.readUsers.put(uid, true);

                        readUsersMap.put(key, comment_modify);
                        comments.add(comment_origin);
                    }
                    if(comments.size() == 0){return; }
                    if(!comments.get(comments.size()-1).readUsers.containsKey(uid)){
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments")
                                .updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                notifyDataSetChanged();
                                recyclerView1.scrollToPosition(comments.size() - 1);
                            }
                        });
                    }
                    notifyDataSetChanged();
                    recyclerView1.scrollToPosition(comments.size() - 1);

                    //메세지가 갱신
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder messageViewHolder = ((MessageViewHolder) holder);

            //내가보낸 메세지
            if (comments.get(position).uid.equals(uid)) {
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                messageViewHolder.textView_message.setTextSize(20);
                setReadCounter(position, messageViewHolder.textView_readCounter_left);


                //상대방이 보낸 메세지
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(destinationUserModel.profileImage1)
                        .apply(new RequestOptions().circleCrop())
                        .into(messageViewHolder.imageView_profile);
                messageViewHolder.textview_name.setText(destinationUserModel.userName);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(20);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                setReadCounter(position, messageViewHolder.getTextView_readCounter_right);
            }
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHolder.textView_timestamp.setText(time);
        }
        void setReadCounter(final int position, final TextView textView){
            if (peopleCount == 0) {
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Boolean> users = (Map<String, Boolean>) dataSnapshot.getValue();
                        peopleCount = users.size();
                        int count = peopleCount - comments.get(position).readUsers.size();
                        if (count > 0) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(count));
                        } else {
                            textView.setVisibility(View.INVISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }else{
                int count = peopleCount - comments.get(position).readUsers.size();
                if (count > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(count));
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }

            }
        }
        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public TextView textview_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;
            public TextView textView_readCounter_left;
            public TextView getTextView_readCounter_right;

            public MessageViewHolder(View view) {
                super(view);
                textView_message = (TextView) view.findViewById(R.id.messageItem_textView_message);
                textview_name = (TextView) view.findViewById(R.id.messageItem_textView_name);
                imageView_profile = (ImageView) view.findViewById(R.id.messageaItem_imageView_profile);
                linearLayout_main = (LinearLayout) view.findViewById(R.id.messageItem_linearlayout_main);
                linearLayout_destination = (LinearLayout) view.findViewById(R.id.messageItem_linearlayout_destination);
                textView_timestamp = (TextView) view.findViewById(R.id.messageItem_textview_timeStamp);
                textView_readCounter_left=(TextView) view.findViewById(R.id.messageitem_textview_readCounter_left);
                getTextView_readCounter_right=(TextView) view.findViewById(R.id.messageitem_textview_readCounter_right);

                imageView_profile.setOnClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), YourPageActivity.class);
                        intent.putExtra ("post_id", destinationUid);
                        startActivity(intent);

                        finish ();;

                    }
                });
            }
        }
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(valueEventListener != null){
            databaseReference.removeEventListener(valueEventListener);
        }

        //RecyclerView.Adapter adapter = recyclerView2.getAdapter();
        //adapter.notifyDataSetChanged();
        //recyclerView2.setAdapter(adapter);
        //recyclerView2.invalidate();


        finish();

        overridePendingTransition(R.anim.fromleft, R.anim.toright);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(chatRoomUid!=null) {
            ValueEventListener messageListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).addValueEventListener(messageListener);
            mMessageListener = messageListener;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mMessageListener != null) {
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).removeEventListener(mMessageListener);
        }

    }
}
