package com.likeonline.travelmaker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.likeonline.travelmaker.area.FragmentReplaceable;
import com.likeonline.travelmaker.area.GangwondoFragment;
import com.likeonline.travelmaker.area.HomeFragment;
import com.likeonline.travelmaker.area.JejudoFragment;
import com.likeonline.travelmaker.area.PaldoFragment;
import com.likeonline.travelmaker.area.SeoulFragment;
import com.likeonline.travelmaker.area.SudoFragment;
import com.likeonline.travelmaker.fragment.ChatFragment;
import com.likeonline.travelmaker.fragment.MyPageFragment;
import com.likeonline.travelmaker.travelbook.TravelbookFragment;
//import com.likeonline.travelmaker.fragment.PeopleFragment;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements FragmentReplaceable {
    private FirebaseRemoteConfig firebaseRemoteConfig;
    String splash_background;
    private Fragment paldoFragment;
    private Fragment sudoFragment;
    private Fragment gangwondoFragment;
    private Fragment jejudoFragment;
    private Fragment seoulFragment;
    public static Activity mainActivity;
    private long kakaoEmail;
    public Boolean backcheck=false;
    private long backKeyPressedTime = 0;
    private Toast toast;

    LoginActivity loginActivity;
    private BackPressCloseHandler backPressCloseHandler;
    public MainActivity(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }
    public MainActivity( ) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backPressCloseHandler = new BackPressCloseHandler(this);
        kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;

        mainActivity= MainActivity.this;

        paldoFragment = new PaldoFragment();
        sudoFragment = new SudoFragment();
        gangwondoFragment = new GangwondoFragment();
        jejudoFragment = new JejudoFragment();
        seoulFragment = new SeoulFragment();

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();

        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Successful";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                        }

                    }
                });

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background=firebaseRemoteConfig.getString("splash_background");
        getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.mainactivity_buttomNavigationView);

        getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new HomeFragment()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new HomeFragment()).commit();
                        return true;
                    case R.id.action_chat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new ChatFragment()).commit();
                        return true;
                    case R.id.action_bucket:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new TravelbookFragment()).commit();
                        return true;
                    case R.id.action_my:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new MyPageFragment()).commit();
                        return true;
                }
                return false;
            }
        });

        passPushTokenToServer();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //backPressCloseHandler.onBackPressed();
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            this.finish();
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
        //activity.finish();
        toast.cancel();

    }

    public void showGuide()
    { toast = Toast.makeText(this, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT); toast.show(); }


    void passPushTokenToServer(){
        String uid;
        if(kakaoEmail==0) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            uid = String.valueOf(kakaoEmail);
        }
        String token= FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> map=new HashMap<>();
        map.put("pushToken", token);

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
    }

    @Override
    public void replaceFragment(int fragmentId) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        /*
         *
         * R.id.container(activity_main.xml)에 띄우겠다.
         * 파라미터로 오는 fragmentId에 따라 다음에 보여질 Fragment를 설정한다.
         */
        if ( fragmentId == 1 ) {
            transaction.replace(R.id.container, paldoFragment);
        } else if ( fragmentId == 2 ) {
            transaction.replace(R.id.container, sudoFragment);
        }
        else if ( fragmentId == 3 ) {
            transaction.replace(R.id.container, gangwondoFragment);
        }
        else if ( fragmentId == 10 ) {
            transaction.replace(R.id.container, jejudoFragment);
        }
        else if ( fragmentId == 11 ) {
            transaction.replace(R.id.container, seoulFragment);
        }
        /**
         * Fragment의 변경사항을 반영시킨다.
         */

        transaction.addToBackStack(null);
        transaction.commit();

    }
}
