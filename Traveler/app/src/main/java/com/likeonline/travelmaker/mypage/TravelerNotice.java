package com.likeonline.travelmaker.mypage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.likeonline.travelmaker.LoginActivity;
import com.likeonline.travelmaker.R;

public class TravelerNotice extends AppCompatActivity{


    private FirebaseRemoteConfig firebaseRemoteConfig;
    private String splash_background;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        firebaseRemoteConfig=FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        splash_background = firebaseRemoteConfig.getString(getString(R.string.rc_color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }

        ImageView a = (ImageView)findViewById(R.id.a);
        a.setImageResource(R.drawable.a);
        //Glide.with(this).load(R.drawable.a).into(a);

        ImageView b = (ImageView)findViewById(R.id.b);
        b.setImageResource(R.drawable.b);
        //Glide.with(this).load(R.drawable.b).into(b);

        ImageView c = (ImageView)findViewById(R.id.c);
        c.setImageResource(R.drawable.c);
        //Glide.with(this).load(R.drawable.c).into(c);

        ImageView d = (ImageView)findViewById(R.id.d);
        d.setImageResource(R.drawable.d);
        //Glide.with(this).load(R.drawable.d).into(d);


    }
}

