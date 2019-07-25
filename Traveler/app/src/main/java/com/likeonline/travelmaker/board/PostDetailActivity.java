package com.likeonline.travelmaker.board;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.likeonline.travelmaker.LoginActivity;
import com.likeonline.travelmaker.R;
import com.likeonline.travelmaker.chat.MessageActivity;
import com.likeonline.travelmaker.detailProfile.YourPageActivity;
import com.likeonline.travelmaker.model.ChatModel;
import com.likeonline.travelmaker.model.UserModel;
import com.likeonline.travelmaker.travelbook.BookDetailActivity;
import com.likeonline.travelmaker.travelbook.BookModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity {
    private final String TAG = "PostDetailActivity";

    // DB관련 선언 및 초기화
    private DatabaseReference mReference; // 데이터베이스 값을 읽어오기 위한 레퍼런스 선언
    private FirebaseAuth mFirebase_Auth; // 파이어베이스 계정 값을 읽어오기 위한 선언
    private ChatModel chatModel=new ChatModel();
    private ValueEventListener mPostListener;

    private String mPost_id;
    private String mUser_id ;
    private String current_user_id; //로그인된 유저 아이디
    private TextView mPost_title;
    private TextView mPost_contents;
    private TextView mPost_writer;
    private TextView mPost_tag;
    private CircleImageView mPost_profile;
    private ImageView mPost_chat;
    private ImageView mPost_travelBook;
    private ImageView mPost_bookbackground;
    private TextView mPost_travelBook_title;
    private ImageView mPost_traveling;
    private TextView mPost_userRating;
    private RatingBar ratingBar;
    private long kakaoEmail;
    float totalrating; //총 유저 신뢰도 점수
    public float trustCount; //유저 평가 갯수
    int user_score;
    private String mBook_id;
    private TextView mUser_grade;

    String splash_background;
    private FirebaseRemoteConfig firebaseRemoteConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        mPost_userRating=(TextView) findViewById(R.id.post_detali_score);
        ratingBar=(RatingBar) findViewById(R.id.post_ratingBar);

        Toolbar board_toolbar = findViewById(R.id.postdetail_toolbar);
        board_toolbar.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.samdan_mint));
        setSupportActionBar(board_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.button_back);

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = firebaseRemoteConfig.getString("splash_background");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }

        kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;
        if(kakaoEmail==0){
            current_user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
        }else{
            current_user_id=String.valueOf(kakaoEmail);
        }


        // post 키 값을 받아줍니다.
        mPost_id = getIntent().getStringExtra("post_id");
        if (mPost_id == null) {
            throw new IllegalArgumentException("post 키값이 왜 없누 시발");
        }

        FirebaseDatabase.getInstance().getReference().child("users").child(current_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_score=Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mReference = FirebaseDatabase.getInstance().getReference().child("post").child(mPost_id);

        mPost_title = findViewById(R.id.postdetail_title);
        mPost_contents = findViewById(R.id.postdetail_contents);
        mPost_writer = findViewById(R.id.postdetail_writer);
        mPost_tag = findViewById(R.id.postdetail_tag);
        mPost_profile = findViewById(R.id.postdetail_profile);
        mPost_chat = findViewById(R.id.postdetail_chat);
        mPost_travelBook = findViewById(R.id.postdetail_travelbook);
        mPost_bookbackground = findViewById(R.id.postdetail_bookbackground);
        mPost_travelBook_title = findViewById(R.id.postdetail_travelbook_title);
        mPost_traveling = findViewById(R.id.postdetail_traveling);
        mUser_grade = findViewById (R.id.mypage_grade2);
        //대화하기

        mPost_travelBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BookDetailActivity.class);
                intent.putExtra("your_id", mUser_id);
                intent.putExtra("book_id", mBook_id);
                startActivity(intent);
            }
        });
    }

    //메뉴 연결
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        String User_id;

        if(kakaoEmail==0){
            User_id = mFirebase_Auth.getInstance().getCurrentUser().getUid();
        }else{
            User_id =String.valueOf(kakaoEmail);

        }


        if(mUser_id.equals(User_id)) {
            menuInflater.inflate(R.menu.menu_edit_delete, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 메뉴 클릭
                Log.v(TAG, "뒤로가기 메뉴가 선택됨");
                finish();
                return true;
            case R.id.action_edit: // 수정 메뉴 클릭
                Log.v(TAG, "수정 메뉴가 선택됨");
                //게시글 수정
                Intent intent = new Intent(getApplicationContext(), PostingActivity.class);
                intent.putExtra("Post_id", mPost_id);
                startActivity(intent);
                return true;
            case R.id.action_delete: // 삭제 메뉴 클릭
                Log.v(TAG, "삭제 메뉴가 선택됨");
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                // 제목셋팅
                alertDialogBuilder.setTitle("게시글 삭제");
                // AlertDialog 셋팅
                alertDialogBuilder
                        .setMessage("정말로 게시글을 삭제하시겠습니까?")
                        .setCancelable(false)
                        .setNegativeButton("예",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // 게시글 삭제
                                        mReference.removeValue();
                                        user_score-=10;
                                        final Map<String, Object> taskMap = new HashMap<String, Object>();
                                        taskMap.put("user_score", user_score);
                                        FirebaseDatabase.getInstance().getReference().child("users").child(current_user_id).updateChildren(taskMap);
                                        finish();
                                    }
                                })
                        .setPositiveButton("아니요",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // 다이얼로그를 취소한다
                                        dialog.cancel();
                                    }
                                });
                // 다이얼로그 생성
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setOnShowListener( new DialogInterface.OnShowListener() { @Override public void onShow(DialogInterface arg0) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GRAY);
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                }
                });
                // 다이얼로그 보여주기
                alertDialog.show();
                return true;

        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PostModel post = dataSnapshot.getValue(PostModel.class);
                if(post != null) {
                    mPost_title.setText(post.getPost_title());
                    mPost_contents.setText(post.getPost_contents());
                    mPost_writer.setText(post.getUser_name());
                    mPost_tag.setText(post.getPost_tag());
                    mUser_id = post.getUser_id();
                    FirebaseDatabase.getInstance().getReference().child("users").child(mUser_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            totalrating=Float.valueOf(dataSnapshot.child("user_trustScore").getValue().toString());
                            if(dataSnapshot.child("user_trustCount").getValue()!=null) {
                                trustCount = Float.valueOf (dataSnapshot.child ("user_trustCount").getValue ().toString ());
                            }
                            if(Float.isNaN(totalrating/trustCount)){
                                mPost_userRating.setText("0");
                                ratingBar.setRating(Float.parseFloat(String.format("%.1f",totalrating/trustCount)));
                            }else{
                                mPost_userRating.setText(String.valueOf(String.format("%.1f",totalrating/trustCount)));
                                ratingBar.setRating(totalrating/trustCount);
                            }

                            int score = Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());

                            if (score < 50) {
                                mUser_grade.setText("여행아싸");
                            } else if (score < 100) {
                                mUser_grade.setText("여행들러리");
                            } else if (score < 300) {
                                mUser_grade.setText("여행친구");
                            } else if (score < 1000) {
                                mUser_grade.setText("여행베프");
                            } else if (score >= 1000) {
                                mUser_grade.setText("여행인싸");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    if (post.getUser_profileUrl().equals("")) {
                        // 기본 이미지 값 설정
                        mPost_profile.setImageDrawable(ContextCompat.getDrawable(PostDetailActivity.this, R.drawable.baseline_account_circle_black_18dp));
                    } else {
                        Glide.with(PostDetailActivity.this)
                                .load(post.getUser_profileUrl())
                                .into(mPost_profile);
                    }
                    // 채팅 표시 여부
                    if (!post.isPost_chatbool()) {
                        mPost_chat.setVisibility(View.INVISIBLE);
                    }

                    // 이곳에 트래블 북 이미지 넣는 코드를 구현할 것
                    if (post.getPost_traveling().equals("여행중")) {
                        mPost_traveling.setImageDrawable(ContextCompat.getDrawable(PostDetailActivity.this, R.drawable.traveling));
                    } else {
                        mPost_traveling.setImageDrawable(ContextCompat.getDrawable(PostDetailActivity.this, R.drawable.traveling_yet));
                    }

                    // 트래블 북 관련 코드
                    if(post.getTravelbook_id().equals("empty")) {
                        mPost_travelBook.setVisibility(View.GONE);
                        mPost_travelBook_title.setVisibility(View.GONE);
                        mPost_bookbackground.setVisibility(View.GONE);
                    } else {
                        mBook_id = post.getTravelbook_id();
                        mPost_travelBook_title.setText(post.getUser_name() + "님의 " + post.getTravelbook_title());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PostDetailActivity.this, "post를 못불러옴. 왜지?", Toast.LENGTH_SHORT).show();
            }
        };
        mReference.addValueEventListener(postListener);
        mPostListener = postListener;

        mPost_profile.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), YourPageActivity.class);
                intent.putExtra ("post_id", mUser_id);
                startActivity(intent);

                finish ();;

            }
        });
        mPost_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(PostDetailActivity.this, mUser_id, Toast.LENGTH_SHORT).show();
                UserModel userModel = new UserModel();
                String myUID;
                if(kakaoEmail==0){
                    myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                }else{
                    myUID = String.valueOf(kakaoEmail);
                }

                chatModel.users.put(myUID, true);
                chatModel.users.put(mUser_id, true);
                chatModel.user_good="1";
                chatModel.user_report="1";
                chatModel.user_travel="1";

                FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel);

                Intent intent = new Intent(v.getContext(), MessageActivity.class);
                intent.putExtra("destinationUid", mUser_id);
                ActivityOptions activityOptions = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fromright, R.anim.toleft);
                    startActivity(intent,activityOptions.toBundle());
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPostListener != null) {
            mReference.removeEventListener(mPostListener);
        }
    }
}