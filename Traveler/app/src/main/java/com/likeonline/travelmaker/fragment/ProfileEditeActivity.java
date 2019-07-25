package com.likeonline.travelmaker.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.likeonline.travelmaker.LoginActivity;
import com.likeonline.travelmaker.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditeActivity extends AppCompatActivity {

    private String uid;
    private String newName;
    private String newSNS;
    private FirebaseAuth mAuth;
    private CircleImageView profile;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Button btnchange;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    private String splash_background;
    private TextView changeName;
    private static final int PICK_FROM_ALBUM=1;
    private Uri imageURI;
    private Boolean data=false;
    private Task<Uri> image;
    private Uri downUri;
    private TextView changeSNS;
    private String photo;
    private String name;
    private String sns;
    public  boolean edit=false;
    private long kakaoEmail;
    MyPageFragment myPageFragment=new MyPageFragment();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileedit);

        firebaseRemoteConfig=FirebaseRemoteConfig.getInstance();

        kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;

        btnchange=(Button) findViewById(R.id.prfileedit_btn_change);
        changeName=(TextView) findViewById(R.id.profileeidt_name_edit);
        changeSNS=(TextView)  findViewById(R.id.profileeidt_sns_edit);

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = firebaseRemoteConfig.getString(getString(R.string.rc_color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }

        profile=(CircleImageView) findViewById(R.id.profile_profileup);


        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        //uid=user.getUid();

        if(kakaoEmail==0) {
            uid = user.getUid();
        } else {
            uid = String.valueOf(kakaoEmail);
        }

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK); //사진을 가져옴
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });

        btnchange=(Button) findViewById(R.id.prfileedit_btn_change);

        btnchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newName=changeName.getText().toString();
                newSNS=changeSNS.getText().toString();

                final Map<String, Object> taskMap1 = new HashMap<String, Object>();//프로필 이름
                final Map<String, Object> taskMap2 = new HashMap<String, Object>();// sns 주소
                final Map<String, Object> taskMap3 = new HashMap<String, Object>();//프로필 이미지
                final Map<String, Object> taskMap4 = new HashMap<String, Object>(); //유저 등급점수 0
                final Map<String, Object> taskMap5 = new HashMap<String, Object>(); //유저 등급점수 50
                final Map<String, Object> taskMap6 = new HashMap<String, Object>(); //유저 등급점수 100

                taskMap1.put("userName", newName);
                taskMap2.put("userSNS", newSNS);
                taskMap4.put("user_score", 0);
                taskMap5.put("user_score", 50);
                taskMap6.put("user_score", 100);

                myRef.child("users").child(uid).updateChildren(taskMap1);

                myRef.child("users").child(uid).updateChildren(taskMap2);

                myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("profileImage1").getValue().toString().replace(" ", "").equals("")&& changeSNS.getText().toString().replace(" ", "").equals("")){
                            myRef.child("users").child(uid).updateChildren(taskMap4);
                        }else if(!dataSnapshot.child("profileImage1").getValue().toString().replace(" ", "").equals("")&& changeSNS.getText().toString().replace(" ", "").equals("")){
                            myRef.child("users").child(uid).updateChildren(taskMap5);
                        }else if(dataSnapshot.child("profileImage1").getValue().toString().replace(" ", "").equals("")&& !changeSNS.getText().toString().replace(" ", "").equals("")){
                            myRef.child("users").child(uid).updateChildren(taskMap5);
                        }
                        else if(!dataSnapshot.child("profileImage1").getValue().toString().replace(" ", "").equals("")&& !changeSNS.getText().toString().replace(" ", "").equals("")){
                            myRef.child("users").child(uid).updateChildren(taskMap6);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                /*if(!data && changeSNS.getText().toString().replace(" ", "").equals("")){
                    myRef.child("users").child(uid).updateChildren(taskMap4);
                }else if(data && changeSNS.getText().toString().replace(" ", "").equals("")){
                    myRef.child("users").child(uid).updateChildren(taskMap5);
                }else if(!data && !changeSNS.getText().toString().replace(" ", "").equals("")){
                    myRef.child("users").child(uid).updateChildren(taskMap5);
                }else if(data && !changeSNS.getText().toString().replace(" ", "").equals("")){
                    myRef.child("users").child(uid).updateChildren(taskMap6);
                }*/



                if(!data){
                    imageURI=null;
                }else {
                    final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("userImages").child(uid);
                    profileImageRef.putFile(imageURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return profileImageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri downUri = task.getResult();
                            String imageUri1 = downUri.toString();

                            taskMap3.put("profileImage1", imageUri1);
                            myRef.child("users").child(uid).updateChildren(taskMap3);
                            //Toast.makeText(ProfileEditeActivity.this, taskMap3.toString(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }

                Toast.makeText(ProfileEditeActivity.this, "프로필이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                //myPageFragment.refresh();
                finish();
            }
        });



        myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("profileImage1").getValue().toString().replace(" ", "").equals("") && dataSnapshot.child("userSNS").getValue().toString().replace(" ", "").equals("")) {
                    photo = "";
                    sns = "";
                    profile.setImageResource(R.drawable.mainicon);
                    name = dataSnapshot.child("userName").getValue().toString();
                    changeName.setText(name);
                    changeSNS.setText(sns);

                } else if (dataSnapshot.child("userSNS").getValue().toString().replace(" ", "").equals("")) {
                    sns = "";
                    photo = dataSnapshot.child("profileImage1").getValue().toString();
                    name = dataSnapshot.child("userName").getValue().toString();

                    Picasso.with(getApplicationContext()).load(photo).fit().centerInside().into(profile, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                            // Index 0 is the image view.
                        }
                    });
                    changeName.setText(name);
                    changeSNS.setText(sns);


                } else if (dataSnapshot.child("profileImage1").getValue().toString().replace(" ", "").equals("")) {
                    photo = "";
                    name = dataSnapshot.child("userName").getValue().toString();
                    sns = dataSnapshot.child("userSNS").getValue().toString();
                    profile.setImageResource(R.drawable.mainicon);
                    changeName.setText(name);
                    changeSNS.setText(sns);

                } else {
                    photo = dataSnapshot.child("profileImage1").getValue().toString();
                    name = dataSnapshot.child("userName").getValue().toString();
                    sns = dataSnapshot.child("userSNS").getValue().toString();
                    Picasso.with(getApplicationContext()).load(photo).fit().centerInside().into(profile, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                            // Index 0 is the image view.
                        }
                    });
                    changeName.setText(name);
                    changeSNS.setText(sns);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            profile.setImageURI(data.getData());//가운데 뷰를 바꿈
            this.data=true;
            imageURI = data.getData();//이미지경로원본
        }
    }

}

