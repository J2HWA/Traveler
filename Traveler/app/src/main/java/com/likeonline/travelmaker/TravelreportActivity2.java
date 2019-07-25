package com.likeonline.travelmaker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class TravelreportActivity2 extends Activity {

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
    private Button mPositiveButton2;
    private Button mCancelButton2;
    String uid1;
    RatingBar ratingBar;
    String chatRoomUid;
    float totalrating; //총 유저 신뢰도 점수
    public float trustCount; //유저 평가 갯수

    @Override
    public void onBackPressed() {
        myRef.child ("chatrooms").child (chatRoomUid).addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.child ("user_report").getRef ().setValue ("3");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.child ("users").child (destinationUid).addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.child ("user_report").getRef ().setValue ("1");
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myRef.child ("users").child (uid).addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.child ("user_report").getRef ().setValue ("1");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        super.onBackPressed();
        finish ();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.travel_reportdialog2);



        ratingBar=(RatingBar) findViewById(R.id.mypage_ratingBar);
        context1=this.getApplication();
        profile=(CircleImageView)findViewById(R.id.myPage_profileup);
        name=(TextView) findViewById(R.id.myapge_name_text);
        kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;
        grade=(TextView) findViewById(R.id.mypage_grade);

        mAuth = FirebaseAuth.getInstance();
        destinationUid =getIntent().getStringExtra("travel_id");; // 다른 액티비티의 uid 참고
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

        mCancelButton2=(Button)findViewById(R.id.cancel_button);


        mCancelButton2.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                myRef.child ("chatrooms").child (chatRoomUid).addValueEventListener (new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.child ("user_report").getRef ().setValue ("3");
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                myRef.child ("users").child (destinationUid).addValueEventListener (new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.child ("user_report").getRef ().setValue ("1");
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                myRef.child ("users").child (uid).addValueEventListener (new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.child ("user_report").getRef ().setValue ("1");
                   }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                TravelreportActivity2.this.finish ();

            }
        });





    }




    }



