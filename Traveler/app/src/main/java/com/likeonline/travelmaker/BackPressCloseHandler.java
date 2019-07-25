package com.likeonline.travelmaker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import static com.facebook.FacebookSdk.getApplicationContext;

public class BackPressCloseHandler extends AppCompatActivity {
    private long backKeyPressedTime = 0;
    private Toast toast;
    private Activity activity;
    private Context context;

    public BackPressCloseHandler(Activity context)
    { this.activity = context; }
    public void onBackPressed(){
    if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
        backKeyPressedTime = System.currentTimeMillis();
        showGuide();
        return;
    }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Toast.makeText(activity, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.finish();
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
    { toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT); toast.show(); }
}
