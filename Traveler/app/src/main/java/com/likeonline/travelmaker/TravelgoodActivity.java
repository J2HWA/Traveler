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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.likeonline.travelmaker.chat.MessageActivity;
import com.likeonline.travelmaker.model.ReportModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TravelgoodActivity extends Activity {

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
    RatingBar ratingBar;
    RatingBar ratingBar1;
    private float ratingScore; // 신뢰도 점수


    String destinationUid;
    String chatRoomUid;
    private Button mPositiveButton2;
    private Button mCancelButton2;
    private DatabaseReference mReference; // 데이터베이스 값을 읽어오기 위한 레퍼런스 선언
    public String name1;
    private String name2; //내 유저 이름
    private Button mPositiveButton3;
    private Button mPositiveButton;
    private Button mCancelButton;
    private View.OnClickListener mPositiveListener;
    private View.OnClickListener mCancelListener;
    private String report;

    String uid1;

    float totalrating; //총 유저 신뢰도 점수
    public float trustCount; //유저 평가 갯수
    private float scoreCount; //칭찬하기 및 신고하기 카운트
    private int user_score;




    private TextView pagename;
    MessageActivity messageActivity;

    @Override
    public void onBackPressed() {
        myRef.child ("users").child (destinationUid).addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.child ("user_good").getRef ().setValue ("1");
                dataSnapshot.child ("user_goodCP").getRef ().setValue ("1");
                finish ();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myRef.child ("users").child (uid).addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.child ("user_good").getRef ().setValue ("3");
                dataSnapshot.child ("user_goodCP").getRef ().setValue ("1");
                finish ();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Toast.makeText(getApplicationContext(),"신청을 취소 했습니다..",Toast.LENGTH_LONG).show();
        super.onBackPressed();
        finish ();

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.travel_gooddialog);

        mPositiveButton=(Button)findViewById(R.id.ok_button1);
        mPositiveButton2=(Button)findViewById(R.id.ok_button);
        mCancelButton2=(Button)findViewById(R.id.cancel_button);
        mPositiveButton3=(Button)findViewById(R.id.ok_button2);



        ratingBar=(RatingBar) findViewById(R.id.dialog_ratingbar);






        ratingBar1=(RatingBar) findViewById(R.id.mypage_ratingBar);
        context1=this.getApplication();
        profile=(CircleImageView)findViewById(R.id.myPage_profileup);
        name=(TextView) findViewById(R.id.myapge_name_text);
        kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;
        grade=(TextView) findViewById(R.id.mypage_grade);
        pagename=(TextView)findViewById (R.id.pagename);

        mAuth = FirebaseAuth.getInstance();
        destinationUid =getIntent().getStringExtra("travel_id"); // 다른 액티비티의 uid 참고
        chatRoomUid=getIntent ().getStringExtra ("user_travel");

        FirebaseUser user = mAuth.getCurrentUser();
        if(kakaoEmail==0) {
            uid = user.getUid();
        } else {
            uid = String.valueOf(kakaoEmail);
        }

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingScore=rating;
            }
        });

        mReference=FirebaseDatabase.getInstance().getReference();

        mReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_score=Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                    ratingBar1.setRating(Float.parseFloat(String.format("%.1f",totalrating/trustCount)));
                }else{
                    ratingBar1.setRating(Float.parseFloat(String.format("%.1f",totalrating/trustCount)));
                }

                if(dataSnapshot.child("profileImage1").getValue().toString().replace(" ", "").equals("")){

                    String user = dataSnapshot.child("userName").getValue().toString();
                    String pageuser=dataSnapshot.child("userName").getValue().toString();

                    int score = Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());

                    profile.setImageResource(R.drawable.mainicon);
                    name.setText(user);
                    pagename.setText (pageuser);

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
                    String pageuser=dataSnapshot.child("userName").getValue().toString();

                    int score = Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());
                    Picasso.with(getApplication ()).load(photo).fit().centerInside().into(profile, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                            // Index 0 is the image view.
                        }
                    });
                    name.setText(user);
                    pagename.setText (pageuser);

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


        myRef.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child ("chatrooms").child (chatRoomUid).child ("user_good").getValue ().toString ().equals ("3")&&dataSnapshot.child ("users").child (uid).child ("user_good").getValue ().toString ().equals ("3") && dataSnapshot.child ("users").child (destinationUid).child ("user_good").getValue ().toString ().equals ("2") && dataSnapshot.child ("users").child (uid).child ("user_goodCP").getValue ().toString ().equals ("2") ) {
                    mPositiveButton2.setVisibility (View.GONE);
                    mPositiveButton3.setVisibility (View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //셋팅





        mPositiveButton2.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                myRef.child ("users").child (destinationUid).addListenerForSingleValueEvent (new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.child ("user_good").getRef ().setValue ("1");
                        dataSnapshot.child ("user_goodCP").getRef ().setValue ("1");
                        finish ();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                myRef.child ("users").child (uid).addListenerForSingleValueEvent (new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.child ("user_good").getRef ().setValue ("1");
                        dataSnapshot.child ("user_goodCP").getRef ().setValue ("2");


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                myRef.child ("chatrooms").child (chatRoomUid).addListenerForSingleValueEvent (new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child ("user_good").getValue ().toString ().equals ("2")) {
                            finish ();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }



                });




                finish ();


/*
                mPositiveButton2.setVisibility (View.GONE);
                ratingBar.setVisibility (View.VISIBLE);
                mPositiveButton.setVisibility (View.VISIBLE);*/


            }
        });
      /*  mPositiveButton3.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                myRef.addValueEventListener (new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        dataSnapshot.child ("users").child (uid).child ("user_goodCP").getRef ().setValue ("1");
                        dataSnapshot.child ("users").child (uid).child ("user_good").getRef ().setValue ("1");
                        dataSnapshot.child ("users").child (destinationUid).child ("user_goodCP").getRef ().setValue ("1");
                        dataSnapshot.child ("users").child (destinationUid).child ("user_good").getRef ().setValue ("1");
                        dataSnapshot.child ("chatrooms").child (chatRoomUid).child ("user_good").getRef ().setValue ("4");
                        finish ();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }
        });*/
        mCancelButton2.setOnClickListener (new View.OnClickListener () {

            @Override
            public void onClick(View v) {
                myRef.child ("users").child (destinationUid).addListenerForSingleValueEvent (new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.child ("user_good").getRef ().setValue ("1");
                        dataSnapshot.child ("user_goodCP").getRef ().setValue ("1");
                        finish ();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                myRef.child ("users").child (uid).addListenerForSingleValueEvent (new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.child ("user_good").getRef ().setValue ("1");
                        dataSnapshot.child ("user_goodCP").getRef ().setValue ("2");


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                myRef.child ("chatrooms").child (chatRoomUid).addListenerForSingleValueEvent (new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child ("user_good").getValue ().toString ().equals ("2")) {
                            finish ();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }



                });




                Toast.makeText(getApplicationContext(),"신청을 취소 했습니다..",Toast.LENGTH_LONG).show();
                finish ();
            }

        });

        mPositiveButton.setOnClickListener (new View.OnClickListener () {


            @Override
            public void onClick(View v) {
                myRef.child ("chatrooms").child (chatRoomUid).addListenerForSingleValueEvent (new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.child ("user_good").getRef ().setValue ("4");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
                myRef.child ("users").child (destinationUid).addListenerForSingleValueEvent (new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.child ("user_good").getRef ().setValue ("3");
                        dataSnapshot.child ("user_goodCP").getRef ().setValue ("2");

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                myRef.child ("users").child (uid).addListenerForSingleValueEvent (new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.child ("user_good").getRef ().setValue ("1");
                        dataSnapshot.child ("user_goodCP").getRef ().setValue ("1");

                        finish ();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        finish ();
                    }
                });

                mReference.child("users").child(destinationUid).addListenerForSingleValueEvent (new ValueEventListener () {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        float totalScore=Float.valueOf(dataSnapshot.child("user_trustScore").getValue().toString()); //유저 토탈 신뢰도 점수
                        totalScore+=ratingScore;
                        scoreCount=Float.valueOf(dataSnapshot.child("user_trustCount").getValue().toString());
                        scoreCount++;
                        user_score+=3;
                        final Map<String, Object> taskMap1 = new HashMap<String, Object> (); //업데이트 시킬 total레이팅
                        final Map<String, Object> taskMap2 = new HashMap<String, Object>();//업데이트 시킬 리뷰갯수
                        final Map<String, Object> taskMap3 = new HashMap<String, Object>();//업데이트 시킬 유저 활동점수
                        taskMap1.put("user_trustScore", totalScore);
                        taskMap2.put("user_trustCount",scoreCount);
                        taskMap3.put("user_score", user_score);
                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(taskMap3);
                        mReference.child("users").child(destinationUid).updateChildren(taskMap1);
                        mReference.child("users").child(destinationUid).updateChildren(taskMap2);
                        name1=dataSnapshot.child("userName").getValue().toString();

                        ReportModel reportModel=new ReportModel (
                                report=name1+" 유저를 칭찬하였습니다."
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
                        ReportModel reportModel2=new ReportModel (
                                report=name2+" 유저에게 칭찬을 받았습니다."
                        );
                        mReference.child("users").child(destinationUid).child("report").push().setValue(reportModel2);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                Toast.makeText(getApplicationContext(), "칭찬완료",Toast.LENGTH_SHORT).show();
                finish ();


            }

        });

        myRef.addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child ("chatrooms").child (chatRoomUid).child ("user_good").getValue ().toString ().equals ("3")||dataSnapshot.child ("users").child (uid).child ("user_good").getValue ().toString ().equals ("3")){
                    finish ();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        myRef.child ("users").addValueEventListener(new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child (uid).child ("user_good").getValue ().toString ().equals ("1")&&dataSnapshot.child (uid).child ("user_goodCP").getValue ().toString ().equals ("2")&&dataSnapshot.child (destinationUid).child ("user_good").getValue ().toString ().equals ("1")&&dataSnapshot.child (destinationUid).child ("user_goodCP").getValue ().toString ().equals ("1")){
                    finish ();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
