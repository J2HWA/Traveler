package com.likeonline.travelmaker.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.likeonline.travelmaker.LoginActivity;
import com.likeonline.travelmaker.R;
import com.likeonline.travelmaker.model.UserModel;
import com.likeonline.travelmaker.mypage.LinkActivity;
import com.likeonline.travelmaker.mypage.RecentTravel;
import com.likeonline.travelmaker.mypage.TravelerNotice;
import com.likeonline.travelmaker.mypage.gradeReportActivity;
import com.likeonline.travelmaker.review.my_reviewActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyPageFragment extends Fragment {
    private String uid;
    private FirebaseAuth mAuth;
    private Bitmap bitmap;
    private CircleImageView profile;
    private Button btnOut; //회원탈퇴
    private Button btnNotice; //트래블러 안내(공지사항)
    public Context context1;
    private Button btnlogOut; //로그아웃
    private Button btnChange; //프로필 수정
    private TextView name; //사용자 이름
    FirebaseDatabase database;
    DatabaseReference myRef;
    private StorageReference profileImageRef;
    private TextView grade; //회원 등급
    private long kakaoEmail;
    private TextView sns;
    private String photo;
    public FragmentTransaction t;
    public static ProfileEditeActivity profileEditeActivity=new ProfileEditeActivity();;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    String check;
    RatingBar ratingBar;
    float totalrating; //총 유저 신뢰도 점수
    public float trustCount; //유저 평가 갯수
    private ValueEventListener mMypageListener;
    private ImageButton myReviewButton;
    private ImageButton mRecentTravel;
    private ImageButton mGradeReport;
    private ImageButton mLinkButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup View=(ViewGroup)inflater.inflate(R.layout.fragment_mypage, container, false);

        fragmentManager = getFragmentManager();
        transaction=fragmentManager.beginTransaction();

        ratingBar=(RatingBar) View.findViewById(R.id.mypage_ratingBar);
        context1=container.getContext();
        profile=(CircleImageView) View.findViewById(R.id.myPage_profileup);
        name=(TextView) View.findViewById(R.id.myapge_name_text);
        mAuth = FirebaseAuth.getInstance();
        kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;
        grade=(TextView) View.findViewById(R.id.mypage_grade);

        myReviewButton=(ImageButton) View.findViewById(R.id.maypage_reviewbtn);

        myReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent= new Intent(getApplicationContext(), my_reviewActivity.class);
                startActivity(intent);
            }
        });

        mRecentTravel=(ImageButton) View.findViewById(R.id.mypage_recent_travel);
        mRecentTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent= new Intent(getApplicationContext(), RecentTravel.class);
                startActivity(intent);
            }
        });

        mGradeReport=(ImageButton) View.findViewById(R.id.mypage_grade_report);
        mGradeReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent= new Intent(getApplicationContext(), gradeReportActivity.class);
                startActivity(intent);
            }
        });

        mLinkButton=(ImageButton) View.findViewById(R.id.mypage_link_btn);
        mLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent= new Intent(getApplicationContext(), LinkActivity.class);
                startActivity(intent);
            }
        });

        Bundle bundle = getArguments();
        FirebaseUser user = mAuth.getCurrentUser();
        if(kakaoEmail==0) {
            uid = user.getUid();
        } else {
            uid = String.valueOf(kakaoEmail);
        }

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();


        myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    int score = 0;
                    if(dataSnapshot.child("user_score").getValue() != null) {
                        score = Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());
                    }
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
                    Picasso.with(getActivity()).load(photo).fit().centerInside().into(profile, new Callback.EmptyCallback() {
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




        btnOut=(Button) View.findViewById(R.id.maypage_btn_delete);
        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Travler");
                builder.setMessage("정말로 떠나실 건가요?ㅠㅠ");
                builder.setPositiveButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"취소되었습니다.",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.setNegativeButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                                    profileImageRef = FirebaseStorage.getInstance().getReference().child("userImages").child(uid);
                                    profileImageRef.delete();
                                    myRef.child("users").child(uid).removeValue();
                                    Intent intent = new Intent(context1, LoginActivity.class);
                                    getActivity().finish();
                                    startActivity(intent);
                                    Toast.makeText(context1, "회원탈퇴가 정상적으로 처리되었습니다.", Toast.LENGTH_SHORT).show();
                                }else {
                                    FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                LoginManager loginManager = LoginManager.getInstance();
                                                loginManager.logOut();
                                                profileImageRef = FirebaseStorage.getInstance().getReference().child("userImages").child(uid);
                                                profileImageRef.delete();
                                                myRef.child("users").child(uid).removeValue();
                                                Toast.makeText(context1, "회원탈퇴가 정상적으로 처리되었습니다.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(context1, LoginActivity.class);
                                                getActivity().finish();
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(context1, task.getException().toString(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                builder.show();

            }
        });

        btnlogOut=(Button) View.findViewById(R.id.maypage_btn_out);
        btnlogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Travler");
                builder.setMessage("로그아웃 하시겠습니까?");
                builder.setPositiveButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"취소되었습니다.",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.setNegativeButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                LoginManager.getInstance().logOut();
                                Toast.makeText(context1, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context1, LoginActivity.class);
                                getActivity().finish();
                                LoginManager loginManager = LoginManager.getInstance();
                                loginManager.logOut();
                                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                                    @Override
                                    public void onCompleteLogout() {
                                        Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                startActivity(intent);
                            }
                        });
                builder.show();
            }
        });

        btnChange=(Button) View.findViewById(R.id.maypage_btn_edit);

        //fragmentManager = getFragmentManager();

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
              Intent intent=new Intent(context1, ProfileEditeActivity.class);
              startActivity(intent);
            }
        });

        btnNotice=(Button) View.findViewById(R.id.maypage_btn_notice);

        btnNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent=new Intent(context1, TravelerNotice.class);
                startActivity(intent);
            }
        });

        return View;
    }

    @Override
    public void onStart() {
        super.onStart();

        ValueEventListener mypageListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel usermodel = dataSnapshot.getValue(UserModel.class);
                if (usermodel != null) {
                    int score = Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());
                    name.setText(usermodel.userName);
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

                    photo = dataSnapshot.child("profileImage1").getValue().toString();
                    if(photo.replace(" ", "").equals("")){
                        Picasso.with(getActivity()).load(R.drawable.mainicon).fit().centerInside().into(profile, new Callback.EmptyCallback() {
                            @Override
                            public void onSuccess() {
                                // Index 0 is the image view.
                            }
                        });
                    }else{
                        Picasso.with(getActivity()).load(photo).fit().centerInside().into(profile, new Callback.EmptyCallback() {
                            @Override
                            public void onSuccess() {
                                // Index 0 is the image view.
                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        myRef.child("users").child(uid).addValueEventListener(mypageListener);
        mMypageListener = mypageListener;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMypageListener != null) {
            myRef.child("users").child(uid).removeEventListener(mMypageListener);
        }
    }


    /*public void refresh(){
        transaction.setReorderingAllowed(false);
        transaction.detach(this).attach(this).commitAllowingStateLoss();
    }*/

}
