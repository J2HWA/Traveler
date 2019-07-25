package com.likeonline.travelmaker.review;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.likeonline.travelmaker.LoginActivity;
import com.likeonline.travelmaker.R;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class review_DetailActivity extends AppCompatActivity {

    private DatabaseReference mReference; // 데이터베이스 값을 읽어오기 위한 레퍼런스 선언
    private FirebaseAuth mFirebase_Auth; // 파이어베이스 계정 값을 읽어오기 위한 선언
    private String mPlace_id;
    private String mUser_id ;
    private String mReview_id ;
    private String mPlace_category;
    private TextView mReview_title;
    private TextView mReview_contents;
    private TextView mReview_writer;
    private CircleImageView mReview_profile;
    private ValueEventListener mReviewListener;
    private TextView mGrade;
    private long kakaoEmail;
    private RatingBar ratingBar;
    private float reviewCount;
    float totalrating;
    float reviewrating;
    String uid;
    String key;
    static String content1; //플레이스 리뷰 게시글 내용
    static String content2; // 나의 리뷰 게시글 내용
    static String url1;
    static String url2;
    static String title1;
    static String title2;

    String splash_background;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    String mReview_id2; //나의 리뷰에서 받아온 키값

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        mReview_title=(TextView)findViewById(R.id.reviewdetail_title);
        mReview_contents=(TextView)findViewById(R.id.reviewdetail_contents);
        mReview_writer=(TextView)findViewById(R.id.reviewdetail_writer);
        mReview_profile=(CircleImageView) findViewById(R.id.reviewdetail_profile);
        mGrade=(TextView) findViewById(R.id.review_grade);
        ratingBar=(RatingBar) findViewById(R.id.review_detail_ratingBar);
        Toolbar board_toolbar = findViewById(R.id.review_toolbar);
        setSupportActionBar(board_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.button_back);
        kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;

        if(kakaoEmail==0){
            uid = mFirebase_Auth.getInstance().getCurrentUser().getUid();
        }else{
            uid = String.valueOf(kakaoEmail);
        }


        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = firebaseRemoteConfig.getString("splash_background");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }

        //키 값을 받아줍니다.
        mPlace_id = getIntent().getStringExtra("mPlace_id");
        mPlace_category=getIntent().getStringExtra("mPlace_category");
        mReview_id=getIntent().getStringExtra("mReview_id");
        mReview_id2=getIntent().getStringExtra("mReview_id2");

        mReference = FirebaseDatabase.getInstance().getReference().child("place").child(mPlace_category).child(mPlace_id).child("review").child(mReview_id);
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ratingBar.setRating(Float.valueOf(dataSnapshot.child("rating").getValue().toString()));
                reviewrating=Float.valueOf(dataSnapshot.child("rating").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("place").child(mPlace_category).child(mPlace_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewCount=Float.valueOf(dataSnapshot.child("place_reviewCount").getValue().toString());
                totalrating=Float.valueOf(dataSnapshot.child("place_totalrating").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //메뉴 연결
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        String User_id;
        if(kakaoEmail==0){
            uid = mFirebase_Auth.getInstance().getCurrentUser().getUid();
        }else{
            uid = String.valueOf(kakaoEmail);
        }


        if(mUser_id.equals(uid)) {
            menuInflater.inflate(R.menu.menu_edit_delete, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 메뉴 클릭
                finish();
                return true;
            case R.id.action_edit: // 수정 메뉴 클릭
                //게시글 수정
                Intent intent = new Intent(getApplicationContext(), review_WritingActivity.class);
                intent.putExtra("mPlace_id", mPlace_id);
                intent.putExtra("mPlace_category", mPlace_category);
                intent.putExtra("mReview_id", mReview_id);
                intent.putExtra("mReview_id2", mReview_id2);
                startActivity(intent);
                return true;
            case R.id.action_delete: // 삭제 메뉴 클릭
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                // 제목셋팅
                alertDialogBuilder.setTitle("리뷰 삭제");
                // AlertDialog 셋팅
                alertDialogBuilder
                        .setMessage("정말로 리뷰를 삭제하시겠습니까?")
                        .setCancelable(false)
                        .setNegativeButton("예",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // 게시글 삭제
                                        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
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

                                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("review").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                for(DataSnapshot liviewSnapshot:dataSnapshot.getChildren()) {
                                                    key = liviewSnapshot.getKey();
                                                    content2=liviewSnapshot.child("content").getValue().toString();
                                                    url2=liviewSnapshot.child("user_profileUrl").getValue().toString();
                                                    title2=liviewSnapshot.child("title").getValue().toString();
                                                    if(content1.equals(content2) && url1.equals(url2) && title1.equals(title2)){
                                                        mReference.removeValue();
                                                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("review").child(key).removeValue();
                                                    }
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                        reviewCount-=1;
                                        totalrating-=reviewrating;
                                        Map<String, Object> taskMap = new HashMap<String, Object>(); //업데이트 시킬 place 리뷰갯수
                                        Map<String, Object> taskMap2 = new HashMap<String, Object>();//업데이트 시킬 토탈레이팅
                                        taskMap.put("place_reviewCount", reviewCount);
                                        taskMap2.put("place_totalrating", totalrating);
                                        FirebaseDatabase.getInstance().getReference().child("place").child(mPlace_category).child(mPlace_id).updateChildren(taskMap);
                                        FirebaseDatabase.getInstance().getReference().child("place").child(mPlace_category).child(mPlace_id).updateChildren(taskMap2);
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
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
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
                reviewModel reviewModel = dataSnapshot.getValue(reviewModel.class);
                if(reviewModel != null) {
                    int score = Integer.parseInt(dataSnapshot.child("user_grade").getValue().toString());
                    mReview_title.setText(reviewModel.getTitle());
                    mReview_contents.setText(reviewModel.getContent());
                    mReview_writer.setText(reviewModel.getUser_name());
                    mUser_id = reviewModel.getUser_id();
                    if (score < 20) {
                        mGrade.setText("여행아싸");
                    } else if (score < 40) {
                        mGrade.setText("여행들러리");
                    } else if (score < 60) {
                        mGrade.setText("여행친구");
                    } else if (score < 80) {
                        mGrade.setText("여행베프");
                    } else if (score >= 80) {
                        mGrade.setText("여행인싸");
                    }
                    if (reviewModel.getUser_profileUrl().equals("")) {
                        // 기본 이미지 값 설정
                        mReview_profile.setImageDrawable(ContextCompat.getDrawable(review_DetailActivity.this, R.drawable.mainicon));
                    } else {
                        Glide.with(review_DetailActivity.this)
                                .load(reviewModel.getUser_profileUrl())
                                .into(mReview_profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(review_DetailActivity.this, "post를 못불러옴. 왜지?", Toast.LENGTH_SHORT).show();
            }
        };
        mReference.addValueEventListener(postListener);
        mReviewListener = postListener;

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mReviewListener != null) {
            mReference.removeEventListener(mReviewListener);
        }
    }
}
