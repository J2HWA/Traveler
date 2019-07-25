package com.likeonline.travelmaker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.likeonline.travelmaker.model.ReportModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.androidquery.util.AQUtility.getContext;



public class Travelyourdlg extends Activity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    public Context context1;
    private CircleImageView profile;
    private FirebaseAuth mAuth;
    private TextView name; //사용자 이름
    private TextView grade; //회원 등급
    private long kakaoEmail;
    private String uid;
    private String photo;
    String destinationUid;
    String uid1;
    String chatRoomUid;
    private Button mPositiveButton2;
    private Button mCancelButton2;

    RatingBar ratingBar;
    float totalrating; //총 유저 신뢰도 점수
    public float trustCount; //유저 평가 갯수
    private String report;
    private int user_score; //유저 활동 점수

    public static Activity Travelyourdlg;

    @Override
    public void onBackPressed() {
        myRef.child("users").child (uid).addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child ("user_acceptCP").getValue ().toString ().equals ("2")) {
                    dataSnapshot.child ("user_accept").getRef ().setValue ("3");

                }
                else
                    dataSnapshot.child ("user_accept").getRef ().setValue ("1");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        Toast.makeText(getApplication(),"여행하기를 거절 했습니다.",Toast.LENGTH_LONG).show();
        super.onBackPressed();
        finish ();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.travel_yourdialog);




        ratingBar=(RatingBar)findViewById(R.id.mypage_ratingBar);

        context1=this.getApplication();
        profile=(CircleImageView)findViewById(R.id.myPage_profileup);
        name=(TextView) findViewById(R.id.myapge_name_text);
        kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;
        grade=(TextView) findViewById(R.id.mypage_grade);

        mAuth = FirebaseAuth.getInstance();
        destinationUid =getIntent().getStringExtra("travel_id");; // 다른 액티비티의 uid 참고
        uid1=getIntent ().getStringExtra ("travel_id2");
        chatRoomUid=getIntent ().getStringExtra ("user_travel");


        FirebaseUser user = mAuth.getCurrentUser();
        if(kakaoEmail==0) {
            uid = user.getUid();
        } else {
            uid = String.valueOf(kakaoEmail);
        }
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myRef.child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener () {

            /*if(dataSnapshot.child("user_trustCount").getValue()!=null) {
                trustCount = Float.valueOf (dataSnapshot.child ("user_trustCount").getValue ().toString ());
            }*/
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                totalrating=Float.valueOf(dataSnapshot.child("user_trustScore").getValue().toString());
                if (dataSnapshot.child("user_trustCount").getValue() != null) {
                    trustCount = Float.valueOf(dataSnapshot.child("user_trustCount").getValue().toString());
                }
                if(Float.isNaN(totalrating/trustCount)){
                    ratingBar.setRating(Float.parseFloat(String.format("%.1f",totalrating/trustCount)));
                }else{
                    ratingBar.setRating(Float.parseFloat(String.format("%.1f",totalrating/trustCount)));
                }
                if(dataSnapshot.child("profileImage1").getValue().toString().replace(" ", "").equals("")){



                    String user = dataSnapshot.child("userName").getValue().toString();

                    int score = Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());
                    profile.setImageResource(R.drawable.mainicon);
                    name.setText(user);

                    if (score < 50) {
                        grade.setText("여행아싸");
                    } else if (score < 100) {
                        grade.setText("여행들러리");
                    } else if (score < 300) {
                        grade.setText("여행친구");
                    } else if (score < 1000) {
                        grade.setText("여행베프");
                    } else if (score >= 1000) {
                        grade.setText("여행인싸");
                    }
                }else {
                    photo = dataSnapshot.child("profileImage1").getValue().toString();
                    String user = dataSnapshot.child("userName").getValue().toString();

                    int score = Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());
                    Picasso.with(getApplication ()).load(photo).fit().centerInside().into(profile, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                            // Index 0 is the image view.
                        }
                    });
                    name.setText(user);

                    if (score < 50) {
                        grade.setText("여행아싸");
                    } else if (score < 100) {
                        grade.setText("여행들러리");
                    } else if (score < 300) {
                        grade.setText("여행친구");
                    } else if (score < 1000) {
                        grade.setText("여행베프");
                    } else if (score >= 1000) {
                        grade.setText("여행인싸");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //셋팅

        mPositiveButton2=(Button)findViewById(R.id.ok_button);
        mCancelButton2=(Button)findViewById(R.id.cancel_button);

        mPositiveButton2.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                database = FirebaseDatabase.getInstance();
                myRef = database.getReference();

                myRef.child ("chatrooms").child (chatRoomUid).addListenerForSingleValueEvent (new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.child ("user_travel").getRef ().setValue ("2");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                myRef.child ("users").child (destinationUid).addListenerForSingleValueEvent (new ValueEventListener () {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child ("user_accept").getValue ().toString ().equals ("1")){  //상대방의 user_accept의 값이 1일때 , 상대방의 user_accept 값을 3으로 바꿔준다
                            // dataSnapshot.child ("user_accept").getValue ().toString ().replaceAll ("1","2");
                            dataSnapshot.child ("user_accept").getRef ().setValue ("3");
                            dataSnapshot.child ("user_acceptCP").getRef ().setValue ("2");
                            user_score=Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());
                            user_score+=25;
                            Map<String, Object> taskMap1 = new HashMap<String, Object>(); //업데이트 시킬 total레이팅
                            taskMap1.put("user_score", user_score);
                            myRef.child ("users").child (destinationUid).updateChildren(taskMap1);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                myRef.child ("users").child (uid).addListenerForSingleValueEvent (new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {                  //나의 user_accept의 값이 2일때, 나의 user_accept 값을 3으로 바꿔준다.
                        if(dataSnapshot.child ("user_accept").getValue ().toString ().equals ("2")){
                            dataSnapshot.child ("user_accept").getRef ().setValue ("3");
                            dataSnapshot.child ("user_acceptCP").getRef ().setValue ("2");
                            user_score=Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());
                            user_score+=25;
                            Map<String, Object> taskMap1 = new HashMap<String, Object>(); //업데이트 시킬 total레이팅
                            taskMap1.put("user_score", user_score);
                            myRef.child ("users").child (uid).updateChildren(taskMap1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                ReportModel reportModel=new ReportModel(
                        report="여행매칭이 성사되었습니다."
                );
                myRef.child("users").child(uid).child("report").push().setValue(reportModel);
                myRef.child("users").child(destinationUid).child("report").push().setValue(reportModel);

                Toast.makeText(getApplication(),"여행하기를 수락 했습니다.",Toast.LENGTH_LONG).show();
                finish();
            }
        });
        mCancelButton2.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                myRef.child("users").child (uid).addListenerForSingleValueEvent (new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child ("user_acceptCP").getValue ().toString ().equals ("2")) {
                            dataSnapshot.child ("user_accept").getRef ().setValue ("3");

                        }
                        else
                            dataSnapshot.child ("user_accept").getRef ().setValue ("1");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });

                Toast.makeText(getApplication(),"여행하기를 거절 했습니다.",Toast.LENGTH_LONG).show();
                finish();
            }
        });

        myRef.child ("users").addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child (uid).child ("user_accept").getValue ().toString ().equals ("3")&&dataSnapshot.child (destinationUid).child ("user_accept").getValue ().toString ().equals ("3")){
                    finish ();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myRef.child ("chatrooms").addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child (chatRoomUid).child ("user_travel").getValue ().toString ().equals ("2")){
                    finish ();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


}