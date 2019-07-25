package com.likeonline.travelmaker;

import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.likeonline.travelmaker.model.UserModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {
    String splash_background;
    private static final int PICK_FROM_ALBUM1 = 10;
    private ProgressDialog progressDialog;
    private Boolean data = false;

    CircleImageView profile1;


    Uri imageURI1;


    EditText Id; //아이디
    EditText pw1; //패스워드
    EditText pw2; //패스워드 확인
    Spinner age; //나이
    EditText sns; //sns 주소
    EditText name; //닉네임
    String sex; //성별

    Spinner job;
    Spinner religion;
    Spinner drinking;
    Spinner bloodtype;
    Spinner height;
    Spinner location;
    EditText introduce;

    RadioButton radio_m;
    RadioButton radio_w;
    Button btnSign;

    Boolean MCheck = false;
    Boolean WCheck = false;

    FirebaseAuth firebaseAuth;

    private DatabaseReference mDatabase;
    //private CustomDialog customDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        btnSign = (Button) findViewById(R.id.sign_up);
        progressDialog = new ProgressDialog(this);

        //파라미터에 리스너 등록
        //customDialog = new CustomDialog(this, positiveListener);
        //customDialog.show();


        splash_background = firebaseRemoteConfig.getString(getString(R.string.rc_color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }

        profile1 = (CircleImageView) findViewById(R.id.img_profileup1);

        Id = (EditText) findViewById(R.id.edit_id);
        pw1 = (EditText) findViewById(R.id.edit_pw1);
        pw2 = (EditText) findViewById(R.id.edit_pw2);
        age = (Spinner) findViewById(R.id.edit_age);
        sns = (EditText) findViewById(R.id.edit_sns);
        name = (EditText) findViewById(R.id.edit_name);

        radio_m = (RadioButton) findViewById(R.id.radio_m);
        radio_w = (RadioButton) findViewById(R.id.radio_w);


        job=(Spinner)findViewById (R.id.edit_job);
        religion=(Spinner)findViewById (R.id.edit_religion);
        drinking=(Spinner)findViewById (R.id.edit_drinking);
        bloodtype=(Spinner)findViewById (R.id.edit_bloodtype);
        height=(Spinner)findViewById (R.id.edit_height);
        location=(Spinner)findViewById (R.id.edit_location);
        introduce=(EditText) findViewById (R.id.edit_introduce);

        radio_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MCheck = true;
                sex = radio_m.getText().toString();
            }
        });

        radio_w.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WCheck = true;
                sex = radio_w.getText().toString();
            }
        });

        btnSign.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (Id.getText().toString().replace(" ", "").equals("") || pw1.getText().toString().replace(" ", "").equals("") || pw2.getText().toString().replace(" ", "").equals("") || age.getSelectedItem().toString().replace(" ", "").equals("")) {
                    Toast.makeText(SignUp.this, "빈칸을 작성해주세요.", Toast.LENGTH_SHORT).show();
                } else if (!radio_m.isChecked() && !radio_w.isChecked()) {
                    Toast.makeText(SignUp.this, "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else if (!pw1.getText().toString().equals(pw2.getText().toString())) {
                    Toast.makeText(SignUp.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else if (pw1.getText().toString().length() < 8 || pw2.getText().toString().length() < 8) {
                    Toast.makeText(SignUp.this, "비밀번호를 8자리 이상으로 설정해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("잠시만 기다려주세요...");
                    progressDialog.show();
                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(Id.getText().toString(), pw2.getText().toString())
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    final String uid;
                                    if (!task.getResult().getUser().getUid().isEmpty()) {
                                        //Toast.makeText(SignUp.this, "아이디가 이미 존재합니다.", Toast.LENGTH_SHORT).show();
                                    }
                                    uid = task.getResult().getUser().getUid();
                                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString()).build();

                                    task.getResult().getUser().updateProfile(userProfileChangeRequest);

                                    final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("userImages").child(uid);
                                    /*if (sns.getText().toString().replace(" ", "").equals("")) {
                                        Toast.makeText(SignUp.this, "이미지 널", Toast.LENGTH_SHORT).show();
                                    } else if (data) {
                                        Toast.makeText(SignUp.this, "이미지값 있음", Toast.LENGTH_SHORT).show();
                                    }*/

                                    if (!data && sns.getText().toString().replace(" ", "").equals("") ) {

                                        UserModel userModel = new UserModel();
                                        userModel.userid = Id.getText().toString();
                                        userModel.userName = name.getText().toString();
                                        userModel.userAge = age.getSelectedItem().toString();
                                        userModel.userSex = sex;
                                        userModel.userSNS="";
                                        userModel.profileImage1="";
                                        userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        userModel.user_score=0;
                                        userModel.user_trustScore=0;
                                        userModel.user_trustCount=0;
                                        userModel.user_accept="1";
                                        userModel.user_acceptCP="1";
                                        userModel.user_good="1";
                                        userModel.user_goodCP="1";
                                        userModel.user_report="1";

                                        userModel.user_job = job.getSelectedItem ().toString ();
                                        userModel.user_religion=religion.getSelectedItem ().toString ();
                                        userModel.user_drinking=drinking.getSelectedItem ().toString ();
                                        userModel.user_bloodtype=bloodtype.getSelectedItem ().toString ();
                                        userModel.user_height=height.getSelectedItem ().toString ();
                                        userModel.user_location=location.getSelectedItem ().toString ();

                                        if(introduce.getText().toString().replace(" ", "").equals("")){
                                            userModel.user_introduce="";
                                        }else{
                                            userModel.user_introduce=introduce.getText ().toString ();
                                        }

                                        mDatabase.child("users").child(userModel.uid).setValue(userModel);
                                        SignUp.this.finish();
                                        Toast.makeText(SignUp.this, "Traveler에 가입되셨습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                    else if(sns.getText().toString().replace(" ", "").equals("") && data){
                                        profileImageRef.putFile(imageURI1).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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


                                                if (task.isSuccessful()) {
                                                    UserModel userModel = new UserModel();
                                                    userModel.userid = Id.getText().toString();
                                                    userModel.userName = name.getText().toString();
                                                    userModel.userAge = age.getSelectedItem().toString();
                                                    userModel.userSNS = "";
                                                    userModel.userSex = sex;
                                                    userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                    userModel.profileImage1 = imageUri1;
                                                    userModel.user_score=50;
                                                    userModel.user_trustScore=0;
                                                    userModel.user_trustCount=0;
                                                    userModel.user_accept="1";
                                                    userModel.user_acceptCP="1";
                                                    userModel.user_good="1";
                                                    userModel.user_goodCP="1";
                                                    userModel.user_report="1";
                                                    userModel.user_job = job.getSelectedItem ().toString ();
                                                    userModel.user_religion=religion.getSelectedItem ().toString ();
                                                    userModel.user_drinking=drinking.getSelectedItem ().toString ();
                                                    userModel.user_bloodtype=bloodtype.getSelectedItem ().toString ();
                                                    userModel.user_height=height.getSelectedItem ().toString ();
                                                    userModel.user_location=location.getSelectedItem ().toString ();
                                                    if(introduce.getText().toString().replace(" ", "").equals("")){
                                                        userModel.user_introduce="";
                                                    }else{
                                                        userModel.user_introduce=introduce.getText ().toString ();
                                                    }

                                                    mDatabase.child("users").child(userModel.uid).setValue(userModel);
                                                    SignUp.this.finish();
                                                    Toast.makeText(SignUp.this, "Traveler에 가입되셨습니다.", Toast.LENGTH_SHORT).show();


                                                } else {
                                                    Toast.makeText(SignUp.this, "이메일이 존재하지 않거나 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }else if(!data && !sns.getText().toString().replace(" ", "").equals("")){
                                        UserModel userModel = new UserModel();
                                        userModel.userid = Id.getText().toString();
                                        userModel.userName = name.getText().toString();
                                        userModel.userAge = age.getSelectedItem().toString();
                                        userModel.userSNS=sns.getText().toString();
                                        userModel.profileImage1="";
                                        userModel.userSex = sex;
                                        userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        userModel.user_score=50;
                                        userModel.user_trustScore=0;
                                        userModel.user_trustCount=0;
                                        userModel.user_accept="1";
                                        userModel.user_acceptCP="1";
                                        userModel.user_good="1";
                                        userModel.user_goodCP="1";
                                        userModel.user_report="1";
                                        userModel.user_job = job.getSelectedItem ().toString ();
                                        userModel.user_religion=religion.getSelectedItem ().toString ();
                                        userModel.user_drinking=drinking.getSelectedItem ().toString ();
                                        userModel.user_bloodtype=bloodtype.getSelectedItem ().toString ();
                                        userModel.user_height=height.getSelectedItem ().toString ();
                                        userModel.user_location=location.getSelectedItem ().toString ();
                                        if(introduce.getText().toString().replace(" ", "").equals("")){
                                            userModel.user_introduce="";
                                        }else{
                                            userModel.user_introduce=introduce.getText ().toString ();
                                        }

                                        mDatabase.child("users").child(userModel.uid).setValue(userModel);
                                        SignUp.this.finish();
                                        Toast.makeText(SignUp.this, "Traveler에 가입되셨습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                    else if (data && !sns.getText().toString().replace(" ", "").equals(""))
                                    {
                                        profileImageRef.putFile(imageURI1).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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


                                                if (task.isSuccessful()) {
                                                    UserModel userModel = new UserModel();
                                                    userModel.userid = Id.getText().toString();
                                                    userModel.userName = name.getText().toString();
                                                    userModel.userAge = age.getSelectedItem().toString();
                                                    userModel.userSNS = sns.getText().toString();
                                                    userModel.userSex = sex;
                                                    userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                    userModel.profileImage1 = imageUri1;
                                                    userModel.user_score=100;
                                                    userModel.user_trustScore=0;
                                                    userModel.user_trustCount=0;
                                                    userModel.user_accept="1";
                                                    userModel.user_acceptCP="1";
                                                    userModel.user_good="1";
                                                    userModel.user_goodCP="1";
                                                    userModel.user_report="1";
                                                    userModel.user_job = job.getSelectedItem ().toString ();
                                                    userModel.user_religion=religion.getSelectedItem ().toString ();
                                                    userModel.user_drinking=drinking.getSelectedItem ().toString ();
                                                    userModel.user_bloodtype=bloodtype.getSelectedItem ().toString ();
                                                    userModel.user_height=height.getSelectedItem ().toString ();
                                                    userModel.user_location=location.getSelectedItem ().toString ();
                                                    if(introduce.getText().toString().replace(" ", "").equals("")){
                                                        userModel.user_introduce="";
                                                    }else{
                                                        userModel.user_introduce=introduce.getText ().toString ();
                                                    }

                                                    mDatabase.child("users").child(userModel.uid).setValue(userModel);
                                                    SignUp.this.finish();
                                                    Toast.makeText(SignUp.this, "Traveler에 가입되셨습니다.", Toast.LENGTH_SHORT).show();


                                                } else {
                                                    Toast.makeText(SignUp.this, "이메일이 존재하지 않거나 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                progressDialog.dismiss();
                                            }
                                        });
                                    } //else
                                }
                            });

                }
            }
        });
        profile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK); //사진을 가져옴
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM1);
            }
        });


    }

    private View.OnClickListener positiveListener = new View.OnClickListener() {
        public void onClick(View v) {
            //Toast.makeText(getApplicationContext(), "확인버튼이 눌렸습니다.",Toast.LENGTH_SHORT).show();
            //customDialog.dismiss();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_FROM_ALBUM1 && resultCode == RESULT_OK) {
            profile1.setImageURI(data.getData());//가운데 뷰를 바꿈
            this.data = true;
            imageURI1 = data.getData();//이미지경로원본
        }
    }
}