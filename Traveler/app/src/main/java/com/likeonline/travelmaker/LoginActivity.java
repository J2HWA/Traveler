package com.likeonline.travelmaker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.util.Arrays;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private CallbackManager callbackManager;
    private Button login; //로그인;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    private FirebaseAuth firebaseAuth; //로그인 관리
    FirebaseDatabase database;
    DatabaseReference myRef;
    private FirebaseAuth.AuthStateListener authStateListener; //로그인이 됐는지 체크
    private EditText id;
    private EditText pw;
    public AccessToken facebookId;
    private TextView idFind;
    private TextView pwFind;
    private ProgressDialog progressDialog;
    private MainActivity ma=(MainActivity) MainActivity.mainActivity;
    private String uid;
    public static String faceuid;
    FirebaseUser user;
    private SessionCallback callback;
    AQuery aQuery;
    public long kakaoEmail;
    public static Context context;
    private ProgressBar progressBar;

    private boolean saveLoginData;
    private String id1;
    private String pwd;
    private CheckBox checkBox;
    private SharedPreferences appData;

    Button signbtn;
    com.kakao.usermgmt.LoginButton kakaobtn;
    LoginButton loginButton; //페이스북

    ImageView fakefacebook;
    ImageView fakekakao;
    private Boolean faceclick=false; //페이스북 버튼 클릭
    private String check=null; //페이스북 회원가입창 띄웠을경우 디비에 값들어감
    private BackPressCloseHandler backPressCloseHandler;

    final static SnsSignUpActivity snsSignUpActivity= new SnsSignUpActivity();
    MainActivity mainActivity;
    private long backKeyPressedTime = 0;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_login);

        backPressCloseHandler = new BackPressCloseHandler(this);

        mainActivity=new MainActivity(this);
        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();

        context=this;
        progressDialog = new ProgressDialog(this);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        firebaseRemoteConfig=FirebaseRemoteConfig.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        //progressDialog = new ProgressDialog(this);
        //회원가입
        signbtn = (Button) findViewById(R.id.btn_signup);
        //로그인
        login=(Button) findViewById(R.id.btn_login);

        id=(EditText) findViewById(R.id.edit_id);
        pw=(EditText) findViewById(R.id.edit_pwd);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        if (saveLoginData) {
            id.setText(id1);
            pw.setText(pwd);
            checkBox.setChecked(saveLoginData);
        }

        //비밀번호 찾기
        pwFind=(TextView) findViewById(R.id.txt_pwFind);
        pwFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindePasswordActivty();
            }
        });

        String splash_background=firebaseRemoteConfig.getString(getString(R.string.rc_color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }

        //회원가입 액티비티 띄우는
        signbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginViewPager.class);
                startActivity(intent);
            }
        });

        //로그인 인터페이스 리스터
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //showProgress("로딩중...");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //로그인
                    user = firebaseAuth.getCurrentUser();
                    faceuid=firebaseAuth.getCurrentUser().getUid();
                    uid = user.getUid();
                    //myRef.child("users").push().setValue(faceuid);

                    save();
                    if(faceclick==false){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }
                    else{
                        if(check==null) {
                            myRef.child("users").child(faceuid).orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() == null) {
                                        startActivity(new Intent(getApplicationContext(), SnsSignUpActivity.class));

                                        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                check=dataSnapshot.child("user_facetrue").getValue().toString();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    } else {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        LoginActivity.this.finish();
                                        Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_SHORT).show();

                                    }
                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                } else {
                    LoginManager loginManager = LoginManager.getInstance();
                    loginManager.logOut();
                }
                //hideProgress();
                progressDialog.dismiss();
            }
        };
        kakaobtn = (com.kakao.usermgmt.LoginButton) findViewById(R.id.btn_kakao);

        aQuery = new AQuery(this);
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

        //KakaoSDK.init(new KakaoSDKAdapter());
        //callback = new SessionCallback();                  // 이 두개의 함수 중요함
        //Session.getCurrentSession().addCallback(callback);
        //Session.getCurrentSession().checkAndImplicitOpen();

        kakaobtn=(com.kakao.usermgmt.LoginButton) findViewById(R.id.btn_kakao);
        kakaobtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(!isConnected()){
                        Toast.makeText(LoginActivity.this,"인터넷 연결을 확인해주세요",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //if(Session.getCurrentSession().isOpened()) {
                        //requestMe();
                        kakaoEmail=0;
                        // }
                    }
                }

                if(isConnected()){
                    return false;
                }else{
                    return true;
                }
            }
        });

        fakefacebook = (ImageView) findViewById(R.id.fake_facebook);
        fakekakao = (ImageView) findViewById(R.id.fake_kakao);

        fakefacebook.setOnClickListener(this);
        fakekakao.setOnClickListener(this);

        //페이스북
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.btn_facebook);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                faceclick=true;
                handleFacebookAccessToken(loginResult.getAccessToken());
                /*facebookId=loginResult.getAccessToken();
                //Toast.makeText(LoginActivity.this, facebookId.toString(), Toast.LENGTH_SHORT).show();
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("result", object.toString());
                        startActivity(new Intent(getApplicationContext(), SnsSignUpActivity.class));
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();*/
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.e("LoginErr", error.toString());
            }
        });

        //어플 로그인
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("로그인중입니다...");
                progressDialog.show();

                if(id.getText().toString().replace(" ", "").equals("")  ){
                    Toast.makeText(LoginActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_LONG).show();
                }
                else if(pw.getText().toString().replace(" ", "").equals("") ) {
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show();
                }else if(id.getText().toString().replace(" ", "").equals("") && pw.getText().toString().replace(" ", "").equals("") ){
                    Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show();
                }else{
                    loginEvent();
                }

            }

        });

    }

    private void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        id1 = appData.getString("ID", "");
        pwd = appData.getString("PWD", "");
        // enter();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            //access token을 성공적으로 발급 받아 valid access token을 가지고 있는 상태. 일반적으로 로그인 후의 다음 activity로 이동한다.
            if(Session.getCurrentSession().isOpened()){ // 한 번더 세션을 체크해주었습니다.
                requestMe();
            }
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
                //setContentView(R.layout.activity_login); // 세션 연결이 실패했을때 로그인 화면을 다시 불러옴
            }
        }
    }

    protected void requestMe() { //유저의 정보를 받아오는 함수
        UserManagement.getInstance().requestMe(new MeResponseCallback(){
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    //redirectMainActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult)
            {
                //redirectMainActivity();
            }

            @Override
            public void onNotSignedUp() {} // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(UserProfile userProfile) {  //성공 시 userProfile 형태로 반환
                //Toast.makeText(LoginActivity.this, "성공", Toast.LENGTH_SHORT).show();
                Logger.d("UserProfile : " + userProfile);
                kakaoEmail=userProfile.getId();
                //Toast.makeText(getApplicationContext(), String.valueOf(kakaoEmail), Toast.LENGTH_SHORT).show();
                myRef.child("users").child( String.valueOf(kakaoEmail)).orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null){
                            redirectMainActivity(); // 디비에 값이 없을시 회원가입 창으로 넘김
                        }
                        else{
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            LoginActivity.this.finish();
                            Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    private void enter(){
        if(!id.getText ().equals ("") && !pw.getText ().equals ("")) {



            if (id.getText ().toString ().replace (" ", "").equals ("")) {
                Toast.makeText (LoginActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_LONG).show ();
            } else if (pw.getText ().toString ().replace (" ", "").equals ("")) {
                Toast.makeText (LoginActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show ();
            } else if (id.getText ().toString ().replace (" ", "").equals ("") && pw.getText ().toString ().replace (" ", "").equals ("")) {
                Toast.makeText (LoginActivity.this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show ();
            } else {

                progressDialog.setMessage ("로그인중입니다. 잠시 기다려 주세요...");
                progressDialog.show ();
                loginEvent ();
                Intent intent = new Intent (LoginActivity.this, MainActivity.class);
                startActivity (intent);
                LoginActivity.this.finish ();
                progressDialog.dismiss ();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fake_facebook:  //fake_facebook 내 버튼을 눌렀을 경우
                loginButton.performClick(); //performClick 클릭을 실행하게 만들어 자동으로 실행되도록 한다.
                break;
            case R.id.fake_kakao:
                kakaobtn.performClick();
                break;
        }
    }

    void loginEvent(){ //로그인 여부 판단만 해주는 메서드, 화면 넘기는거 아님
        firebaseAuth.signInWithEmailAndPassword(id.getText().toString(), pw.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            //로그인 실패한 부분
                            //Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                        // Toast.makeText(LoginActivity.this, "Travel Maker 로그인 되었습니다.", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void save() {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = appData.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        // 저장시킬 이름이 이미 존재하면 덮어씌움
        editor.putBoolean("SAVE_LOGIN_DATA", checkBox.isChecked());
        editor.putString("ID", id.getText().toString().trim());
        editor.putString("PWD", pw.getText().toString().trim());

        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();

    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
    private void FindePasswordActivty() {
        startActivity(new Intent(this, FindPassword.class));
    }

    public void showProgress(String msg){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }

        progressDialog.setMessage(msg);
        progressDialog.isShowing();
    }

    public void hideProgress(){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
    private void handleFacebookAccessToken(AccessToken token) {


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                        }else {
                            //ma.finish();


                        }


                    }
                });
    }

    //인터넷 연결상태 확인
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }

    protected void redirectMainActivity() {       //세션 연결 성공 시 SignupActivity로 넘김
        final Intent intent = new Intent(getApplicationContext(), KakaoUpActivity.class);
        intent.putExtra("kakaoid", kakaoEmail);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        //finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            LoginManager loginManager = LoginManager.getInstance();
            loginManager.logOut();
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });


        }
        this.finish();
        toast.cancel();
    }
    public void showGuide()
    { toast = Toast.makeText(this, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT); toast.show(); }
}

