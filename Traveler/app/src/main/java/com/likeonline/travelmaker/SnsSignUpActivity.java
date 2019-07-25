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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.likeonline.travelmaker.model.UserModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class SnsSignUpActivity extends AppCompatActivity {
    String splash_background;
    private static final int PICK_FROM_ALBUM1 = 10;
    private ProgressDialog progressDialog;
    private Boolean data = false;
    public Boolean backclick=false; //뒤로가기 버튼 눌림
    // private CustomDialog customDialog;


    private String uid;
    CircleImageView profile1;
    Uri imageURI1;
    Spinner age; //나이
    EditText sns; //sns 주소
    EditText name; //닉네임
    String sex; //성별
    RadioButton radio_m;
    RadioButton radio_w;
    Button btnSign;
    Boolean MCheck = false;
    Boolean WCheck = false;

    Spinner job;
    Spinner religion;
    Spinner drinking;
    Spinner bloodtype;
    Spinner height;
    Spinner location;
    EditText introduce;


    FirebaseAuth mAuth;

    FirebaseDatabase database;
    DatabaseReference myRef;

    final static LoginActivity loginActivity= new LoginActivity();
    public AccessToken faceid=loginActivity.facebookId;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebooksignup);

        //파라미터에 리스너 등록
        //customDialog = new CustomDialog(this, positiveListener);
        //customDialog.show();

        //Toast.makeText(this, loginActivity.faceuid, Toast.LENGTH_SHORT).show();

        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        UserModel userModel = new UserModel();
        userModel.facetrue="true";
        FirebaseDatabase.getInstance().getReference().child("users").child("user_facetrue").setValue(userModel.facetrue);
        mAuth=FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        // uid=user.getUid();
        progressDialog = new ProgressDialog(this);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        btnSign = (Button) findViewById(R.id.sign_up);
        splash_background = firebaseRemoteConfig.getString(getString(R.string.rc_color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }

        profile1 = (CircleImageView) findViewById(R.id.img_profileup1);

        name = (EditText) findViewById(R.id.edit_name);
        sns=(EditText) findViewById(R.id.edit_sns);
        age=(Spinner) findViewById(R.id.edit_age);
        radio_m=(RadioButton) findViewById(R.id.radio_m);
        radio_w=(RadioButton) findViewById(R.id.radio_w);

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

                if(name.getText().toString().replace(" ", "").equals("") ||age.getSelectedItem ().toString().replace(" ", "").equals("") ) {
                    Toast.makeText(SnsSignUpActivity.this, "빈칸을 작성해주세요.", Toast.LENGTH_SHORT).show();}//파이어베이스 연동 회원가입
                else {

                    progressDialog.setMessage("잠시만 기다려주세요...");
                    progressDialog.show();
                    final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("userImages").child(uid);

                    if(!data && sns.getText().toString().replace(" ", "").equals("") ){
                        UserModel userModel = new UserModel();
                        userModel.userName = name.getText().toString();
                        userModel.userAge = age.getSelectedItem ().toString();
                        userModel.userSex = sex;
                        userModel.userSNS="";
                        userModel.profileImage1="";
                        userModel.uid = loginActivity.faceuid;
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
                        myRef.child("users").child(userModel.uid).setValue(userModel);
                        SnsSignUpActivity.this.finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        Toast.makeText(SnsSignUpActivity.this, "Traveler에 가입되셨습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else if(!data && !sns.getText().toString().replace(" ", "").equals("")){
                        UserModel userModel = new UserModel();
                        userModel.userName = name.getText().toString();
                        userModel.userAge = age.getSelectedItem ().toString();
                        userModel.userSNS = sns.getText().toString();
                        userModel.profileImage1="";
                        userModel.userSex = sex;
                        userModel.user_score=50;
                        userModel.user_trustScore=0;
                        userModel.user_trustCount=0;
                        userModel.uid = loginActivity.faceuid;

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

                        myRef.child("users").child(userModel.uid).setValue(userModel);
                        SnsSignUpActivity.this.finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        Toast.makeText(SnsSignUpActivity.this, "Traveler에 가입되셨습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else if(data && sns.getText().toString().replace(" ", "").equals("") ){
                        profileImageRef.putFile(imageURI1).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if(!task.isSuccessful()) {
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
                                    userModel.userName = name.getText().toString();
                                    userModel.userAge = age.getSelectedItem ().toString();
                                    userModel.userSex = sex;
                                    userModel.userSNS="";
                                    userModel.user_score=50;
                                    userModel.user_trustScore=0;
                                    userModel.uid = loginActivity.faceuid;
                                    userModel.user_trustCount=0;
                                    userModel.profileImage1=imageUri1;

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

                                    myRef.child("users").child(userModel.uid).setValue(userModel);
                                    SnsSignUpActivity.this.finish();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    Toast.makeText(SnsSignUpActivity.this, "Traveler에 가입되셨습니다.", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(SnsSignUpActivity.this, "이메일이 존재하지 않거나 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                progressDialog.dismiss();
                            }

                        });
                    }
                    else {

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
                                    userModel.userName = name.getText().toString();
                                    userModel.userAge = age.getSelectedItem ().toString();
                                    userModel.userSNS = sns.getText().toString();
                                    userModel.userSex = sex;
                                    userModel.user_score=100;
                                    userModel.user_trustScore=0;
                                    userModel.user_trustCount=0;
                                    userModel.uid = loginActivity.faceuid;
                                    userModel.profileImage1 = imageUri1;

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

                                    myRef.child("users").child(userModel.uid).setValue(userModel);
                                    SnsSignUpActivity.this.finish();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    Toast.makeText(SnsSignUpActivity.this, "Traveler에 가입되셨습니다.", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(SnsSignUpActivity.this, "이메일이 존재하지 않거나 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                progressDialog.dismiss();
                            }

                        });
                    }


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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_FROM_ALBUM1 && resultCode == RESULT_OK) {
            profile1.setImageURI(data.getData());//가운데 뷰를 바꿈
            this.data=true;
            imageURI1 = data.getData();//이미지경로원본
        }

    }

    private View.OnClickListener positiveListener = new View.OnClickListener() {
        public void onClick(View v) {
            //Toast.makeText(getApplicationContext(), "확인버튼이 눌렸습니다.",Toast.LENGTH_SHORT).show();
            // customDialog.dismiss();
        }
    };
}