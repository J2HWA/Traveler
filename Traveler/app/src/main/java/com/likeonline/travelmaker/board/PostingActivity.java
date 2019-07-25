package com.likeonline.travelmaker.board;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.kakao.auth.authorization.AuthorizationResult;
import com.likeonline.travelmaker.LoginActivity;
import com.likeonline.travelmaker.R;
import com.likeonline.travelmaker.model.ReportModel;
import com.likeonline.travelmaker.travelbook.TravelBookViewActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostingActivity extends AppCompatActivity {
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReference = mDatabase.getReference(); // 데이터베이스 사용을 위한 선언
    private FirebaseAuth mFirebase_Auth;
    private ValueEventListener mPostListener;

    String splash_background;
    private FirebaseRemoteConfig firebaseRemoteConfig;

    private Toolbar mToolbar;
    // 게시날짜를 받아오기 위한 포멧
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat mDateFormat = new SimpleDateFormat("MM/dd");

    private boolean mPost_traveling;
    private boolean mPost_carbool;
    private boolean mPost_meetbool;

    private String mPost_id;
    private String mUser_id;
    private String mBoard_id;
    private String mUser_name;
    private String mUser_profileUrl;
    private EditText mPost_title;
    private EditText mPost_content;
    private boolean mPost_bookbool;
    private String mTravelbook_id;
    private String mTravelbook_title;
    private String mPost_str_traveling;
    private boolean mPost_chatbool;
    private String mPost_tag;
    //private String mPost_views;
    private String mPost_PostingTime;
    private CircleImageView mUser_profile;
    private long kakaoEmail;
    private int user_score=10;
    private String report; //게시글 이력
    private TextView mPost_travelbook_title;
    private ImageButton mPost_travelbook_addbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = firebaseRemoteConfig.getString("splash_background");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }

        kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;

        // 각 변수에 정보, 초기값을 담음
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            mPost_id = intent.getExtras().getString("Post_id");
        }

        mBoard_id = null; // 보드 아이디 구현해야함
        if(kakaoEmail==0) {
            mUser_id = mFirebase_Auth.getInstance().getCurrentUser().getUid();
        } else {
            mUser_id= String.valueOf(kakaoEmail);
        }
        mReference.child("users").child(mUser_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String photo=dataSnapshot.child("profileImage1").getValue().toString();
                String user=dataSnapshot.child("userName").getValue().toString();
                user_score=Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());
                /*Picasso.with(getApplicationContext()).load(photo).fit().centerInside().into(mUser_profile, new Callback.EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        // Index 0 is the image view.
                    }
                });*/
                mUser_name = user;
                mUser_profileUrl = photo;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mPost_title = (EditText) findViewById(R.id.post_title);
        mPost_content = (EditText) findViewById(R.id.post_contents);
        mPost_travelbook_title = findViewById(R.id.post_travelbook_title);
        mPost_travelbook_addbtn = findViewById(R.id.post_addtravelbook);
        mPost_bookbool = false; // 아직 추가할 트래블 북이 없음
        mTravelbook_id = "empty"; // 기본적으로 비어있으므로 empty
        mTravelbook_title = "empty";
        mPost_traveling = true;
        mPost_chatbool = true;
        mPost_carbool = false;
        mPost_meetbool = false; // 태그 관련 초기값
        mPost_tag = "";
        //mPost_views = "0"; // 임의의 데이터 값
        mPost_PostingTime = mDateFormat.format(date); // 임의의 데이터 값

        // 툴바 설정
        mToolbar = findViewById(R.id.post_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.button_back);

        //커스텀 메서드 실행
        travelingSelect(); // 여행중 계획중 클릭 이벤트
        chatSelect(); // 채팅 여부 이벤트
        tagSelect(); // 태그 클릭 이벤트
        tavelbookAdd(); // 트래블 북 추가 버튼 클릭 이벤트
    }

    //메뉴 연결
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_create, menu);
        return true;
    }

    //메뉴 선택
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mPost_traveling) {
            mPost_str_traveling = "여행중";
        }
        else {
            mPost_str_traveling = "계획중";
        }
        if (mPost_meetbool) mPost_tag += "#만나서_여행해요 ";
        if (mPost_carbool) mPost_tag += "#차타고_다니는중 ";

        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 메뉴 클릭
                finish();
                return true;
            case R.id.action_posting: // 글작성 메뉴 클릭 이때 모든 정보들이 데이터베이스로 들어가야함
                PostModel postmodel = new PostModel(
                        mUser_id,
                        mBoard_id,
                        mUser_profileUrl,
                        mPost_title.getText().toString(),
                        mPost_content.getText().toString(),
                        mPost_bookbool,
                        mTravelbook_id,
                        mTravelbook_title,
                        mPost_str_traveling,
                        mPost_chatbool,
                        mPost_tag,
                        mUser_name,
                        mPost_PostingTime);
                if (mPost_id == null) {
                    final Map<String, Object> taskMap = new HashMap<String, Object>();
                    mReference.child("post").push().setValue(postmodel); // 노드 생성 및 데이터 입력
                    user_score+=10;
                    taskMap.put("user_score", user_score);
                    mReference.child("users").child(mUser_id).updateChildren(taskMap);
                    ReportModel reportModel=new ReportModel(
                            report="게시글을 작성하였습니다."
                    );
                    mReference.child("users").child(mUser_id).child("report").push().setValue(reportModel);
                    Toast toast;
                    toast = Toast.makeText(this, "글이 게시되었습니다.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 150);
                    toast.show();
                } else {
                    mReference.child("post").child(mPost_id).setValue(postmodel);
                    Toast toast;
                    toast = Toast.makeText(this, "글이 수정되었습니다.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 150);
                    toast.show();
                }
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mPost_id != null) {
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    PostModel post = dataSnapshot.getValue(PostModel.class);
                    if (post != null) {
                        mPost_title.setText(post.getPost_title());
                        mPost_content.setText(post.getPost_contents());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(PostingActivity.this, "post를 못불러옴. 왜지?", Toast.LENGTH_SHORT).show();
                }
            };

            mReference.child("post").child(mPost_id).addValueEventListener(postListener);
            mPostListener = postListener;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPostListener != null) {
            mReference.child("post").child(mPost_id).removeEventListener(mPostListener);
        }
    }

    private void travelingSelect() {
        //여행중 계획중 선택
        final TextView post_traveling = findViewById(R.id.post_traveling);
        final TextView post_planing = findViewById(R.id.post_planing);

        // 여행중 텍스트 컬러 설정
        post_traveling.setTextColor(0xAA008577);
        post_traveling.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mPost_traveling) {
                    post_planing.setTextColor(Color.GRAY);
                }
                post_traveling.setTextColor(0xAA008577);
                mPost_traveling = true;
            }
        });

        // 계획중 텍스트 컬러 설정
        post_planing.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPost_traveling) {
                    post_traveling.setTextColor(Color.GRAY);
                }
                post_planing.setTextColor(Color.RED);
                mPost_traveling = false;
            }
        });
    }

    private void chatSelect() {
        //채팅 여부 선택
        final TextView post_yeschat = findViewById(R.id.post_yeschat);
        final TextView post_notchat = findViewById(R.id.post_notchat);

        // 여행중 텍스트 컬러 설정
        post_yeschat.setTextColor(0xAA008577);
        post_yeschat.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mPost_chatbool) {
                    post_notchat.setTextColor(Color.GRAY);
                }
                post_yeschat.setTextColor(0xAA008577);
                mPost_chatbool = true;
            }
        });

        // 계획중 텍스트 컬러 설정
        post_notchat.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPost_chatbool) {
                    post_yeschat.setTextColor(Color.GRAY);
                }
                post_notchat.setTextColor(Color.RED);
                mPost_chatbool = false;
            }
        });
    }

    private void tagSelect() {
        //태그 설정
        final TextView post_meetbool = findViewById(R.id.post_meetbool);
        final TextView post_carbool = findViewById(R.id.post_carbool);

        post_meetbool.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mPost_meetbool) {
                    post_meetbool.setTextColor(0xAA01787C);
                    mPost_meetbool = true;
                }
                else {
                    post_meetbool.setTextColor(Color.GRAY);
                    mPost_meetbool = false;
                }
            }
        });

        post_carbool.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mPost_carbool) {
                    post_carbool.setTextColor(0xAA01787C);
                    mPost_carbool = true;
                }
                else {
                    post_carbool.setTextColor(Color.GRAY);
                    mPost_carbool = false;
                }
            }
        });
    }

    private void tavelbookAdd()  {
        mPost_travelbook_addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TravelBookViewActivity.class);
                startActivityForResult(intent, 1000);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1000:
                    mTravelbook_id = data.getStringExtra("book_id");
                    mTravelbook_title = data.getStringExtra("book_title");
                    if (!mTravelbook_id.equals("empty")) {
                        mPost_travelbook_title.setText(mTravelbook_title);
                        mPost_travelbook_addbtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_travelbook_normal));
                    }
                    break;
            }
        }
    }
}
