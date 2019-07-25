package com.likeonline.travelmaker.review;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.likeonline.travelmaker.LoginActivity;
import com.likeonline.travelmaker.R;
import com.likeonline.travelmaker.fragment.PlaceDetail_review;
import com.likeonline.travelmaker.model.ReportModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class review_WritingActivity extends AppCompatActivity {
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
    private String mReview_id;
    private String mPlace_id;
    private String mPlace_category;
    private String mUser_id;
    private String mUser_name;
    private String mUser_profileUrl;
    private int mUser_grade;
    private EditText mPost_title;
    private EditText mPost_content;
    //private String mPost_views;
    private String mPost_PostingTime;
    private long kakaoEmail;
    private RatingBar ratingBar;
    private float ratingScore;
    PlaceDetail_review detail_review;
    private float reviewCount; //리뷰갯수
    int user_score;
    private String key; //유저테이블 리뷰 키
    private String key2; //유저테이블 플레이스 키
    private String key3; //업데이트할때 쓰일 유저 테이블 키
    private String title; //여행지 타이틀
    private String report; //이력 메시지
    private String mReview_id2;// 나의 리뷰 목록에서 받아온 리뷰 아이디

    static String content1; //플레이스 리뷰 게시글 내용
    static String content2; // 나의 리뷰 게시글 내용
    static String url1;
    static String url2;
    static String title1;
    static String title2;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        ratingBar=(RatingBar)findViewById(R.id.review_write_ratingbar);
        kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;
        reviewCount=0;

        // 각 변수에 정보, 초기값을 담음
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            mPlace_id = intent.getExtras().getString("mPlace_id");
            mPlace_category = intent.getExtras().getString("mPlace_category");
            mReview_id=intent.getExtras().getString("mReview_id");
            mReview_id2=intent.getExtras().getString("mReview_id2");
        }


        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = firebaseRemoteConfig.getString("splash_background");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }
        //detail_review.setmReviewid(mReview_id);


        if(kakaoEmail==0) {
            mUser_id = mFirebase_Auth.getInstance().getCurrentUser().getUid();
        } else {
            mUser_id= String.valueOf(kakaoEmail);
        }
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingScore=rating;
            }
        });
        mReference.child("users").child(mUser_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String photo=dataSnapshot.child("profileImage1").getValue().toString();
                mUser_grade = Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());
                String user=dataSnapshot.child("userName").getValue().toString();
                user_score=Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());
                /*Picasso.with(getApplicationContext()).load(photo).fit().centerInside().into(mUser_profile, new Callback.EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        // Index 0 is the image view.
                    }
                });*/
                mUser_profileUrl=photo;
                mUser_name=user;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mPost_title = (EditText) findViewById(R.id.review_title);
        mPost_content = (EditText) findViewById(R.id.review_contents);
        mPost_PostingTime = mDateFormat.format(date); // 임의의 데이터 값

        // 툴바 설정
        mToolbar = findViewById(R.id.review_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.button_back);

    }

    //메뉴 연결
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_create_white, menu);
        return true;
    }

    //메뉴 선택
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 메뉴 클릭
                finish();
                return true;
            case R.id.action_posting: // 글작성 메뉴 클릭 이때 모든 정보들이 데이터베이스로 들어가야함
                if(mPost_content.getText().toString().replace(" ", "").equals("")|| mPost_title.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(this, "빈칸을 작성해주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    final reviewModel reviewModel = new reviewModel(
                            mUser_id,
                            mPlace_id,
                            mPost_PostingTime,
                            mPost_title.getText().toString(),
                            mPost_content.getText().toString(),
                            mUser_name,
                            mUser_profileUrl,
                            mUser_grade,
                            ratingScore,
                            mPlace_category,
                            mReview_id);

                    if (mReview_id == null) {

                        mReference.child("place").child(mPlace_category).child(mPlace_id).child("review").push().setValue(reviewModel); // 노드 생성 및 데이터 입력
                        mReference.child("users").child(mUser_id).child("review").push().setValue(reviewModel); // 노드 생성 및 데이터 입력
                        mReference.child("place").child(mPlace_category).child(mPlace_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                title=dataSnapshot.child("place_title").getValue().toString();
                                ReportModel reportModel=new ReportModel(
                                        report=title+" 리뷰를 작성하였습니다."
                                );
                                mReference.child("users").child(mUser_id).child("report").push().setValue(reportModel);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Toast toast;
                        toast = Toast.makeText(this, "리뷰가 등록되었습니다.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 150);
                        toast.show();
                    } else {
                        mReference.child("place").child(mPlace_category).child(mPlace_id).child("review").child(mReview_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                content1 = dataSnapshot.child("content").getValue().toString();
                                url1=dataSnapshot.child("user_profileUrl").getValue().toString();
                                title1=dataSnapshot.child("title").getValue().toString();


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        FirebaseDatabase.getInstance().getReference().child("users").child(mUser_id).child("review").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for(DataSnapshot liviewSnapshot:dataSnapshot.getChildren()) {
                                    key = liviewSnapshot.getKey();
                                    content2=liviewSnapshot.child("content").getValue().toString();
                                    url2=liviewSnapshot.child("user_profileUrl").getValue().toString();
                                    title2=liviewSnapshot.child("title").getValue().toString();
                                    if(content1.equals(content2) && url1.equals(url2) && title1.equals(title2)){
                                        mReference.child("place").child(mPlace_category).child(mPlace_id).child("review").child(mReview_id).setValue(reviewModel);
                                        mReference.child("users").child(mUser_id).child("review").child(key).setValue(reviewModel); // 노드 생성 및 데이터 입력
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        Toast toast;
                        toast = Toast.makeText(this, "리뷰가 수정되었습니다.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 150);
                        toast.show();
                    }
                    //detail_review.setmReviewid(mReview_id);
                    user_score += 10;
                    final Map<String, Object> taskMap3 = new HashMap<String, Object>();
                    taskMap3.put("user_score", user_score);
                    FirebaseDatabase.getInstance().getReference().child("users").child(mUser_id).updateChildren(taskMap3);
                    mReference.child("place").child(mPlace_category).child(mPlace_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            float totalrating = Float.valueOf(dataSnapshot.child("place_totalrating").getValue().toString());
                            totalrating += ratingScore;
                            reviewCount = Float.valueOf(dataSnapshot.child("place_reviewCount").getValue().toString());
                            reviewCount++;
                            final Map<String, Object> taskMap1 = new HashMap<String, Object>(); //업데이트 시킬 total레이팅
                            final Map<String, Object> taskMap2 = new HashMap<String, Object>();//업데이트 시킬 리뷰갯수
                            taskMap1.put("place_totalrating", totalrating);
                            taskMap2.put("place_reviewCount", reviewCount);
                            mReference.child("place").child(mPlace_category).child(mPlace_id).updateChildren(taskMap1);
                            mReference.child("place").child(mPlace_category).child(mPlace_id).updateChildren(taskMap2);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    mReference.child("users").child(mUser_id).child("place").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot placeSnapshot:dataSnapshot.getChildren()) {
                                key2 = placeSnapshot.getKey();
                            }
                                mReference.child("users").child(mUser_id).child("place").child(mPlace_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        float totalrating = Float.valueOf(dataSnapshot.child("place_totalrating").getValue().toString());
                                        totalrating += ratingScore;
                                        reviewCount = Float.valueOf(dataSnapshot.child("place_reviewCount").getValue().toString());
                                        reviewCount++;
                                        final Map<String, Object> taskMap1 = new HashMap<String, Object>(); //업데이트 시킬 total레이팅
                                        final Map<String, Object> taskMap2 = new HashMap<String, Object>();//업데이트 시킬 리뷰갯수
                                        taskMap1.put("place_totalrating", totalrating);
                                        taskMap2.put("place_reviewCount", reviewCount);
                                        mReference.child("users").child(mUser_id).child("place").child(mPlace_id).updateChildren(taskMap1);
                                        mReference.child("users").child(mUser_id).child("place").child(mPlace_id).updateChildren(taskMap2);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    FirebaseDatabase.getInstance().getReference().child("users").child(mUser_id).child("link").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot linkSnapshot:dataSnapshot.getChildren()) {
                                key3 = linkSnapshot.getKey();
                            }
                            if(key3==null){

                            }
                            else {
                                FirebaseDatabase.getInstance().getReference().child("users").child(mUser_id).child("link").child(mPlace_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        float totalrating = Float.valueOf(dataSnapshot.child("place_totalrating").getValue().toString());
                                        totalrating += ratingScore;
                                        reviewCount = Float.valueOf(dataSnapshot.child("place_reviewCount").getValue().toString());
                                        reviewCount++;
                                        final Map<String, Object> taskMap1 = new HashMap<String, Object>(); //업데이트 시킬 total레이팅
                                        final Map<String, Object> taskMap2 = new HashMap<String, Object>();//업데이트 시킬 리뷰갯수
                                        taskMap1.put("place_totalrating", totalrating);
                                        taskMap2.put("place_reviewCount", reviewCount);
                                        mReference.child("users").child(mUser_id).child("link").child(mPlace_id).updateChildren(taskMap1);
                                        mReference.child("users").child(mUser_id).child("link").child(mPlace_id).updateChildren(taskMap2);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    finish();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mReview_id != null) {
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    reviewModel reviewModel = dataSnapshot.getValue(com.likeonline.travelmaker.review.reviewModel.class);
                    if (reviewModel != null) {
                        mPost_title.setText(reviewModel.getTitle());
                        mPost_content.setText(reviewModel.getContent());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(review_WritingActivity.this, "post를 못불러옴. 왜지?", Toast.LENGTH_SHORT).show();
                }
            };

            mReference.child("place").child(mPlace_category).child(mPlace_id).child("review").child(mReview_id).addValueEventListener(postListener);
            mPostListener = postListener;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPostListener != null) {
            mReference.child("place").child(mPlace_category).child(mPlace_id).child("review").removeEventListener(mPostListener);
        }
    }

}
