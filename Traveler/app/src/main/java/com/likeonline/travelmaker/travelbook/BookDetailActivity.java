package com.likeonline.travelmaker.travelbook;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.likeonline.travelmaker.R;

import java.util.ArrayList;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {
    private DatabaseReference mReference;
    private FirebaseAuth mFirebase_Auth = FirebaseAuth.getInstance(); // 파이어베이스 계정 값 초기화
    private ValueEventListener mBookListener;

    private String mBook_id;
    private String mUser_id;
    private String mYour_id;
    private String mUser_name;
    private int mUser_score;
    private int mBook_like;

    public static String mBook_schedule_list = "";

    private TextView mBook_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        String splash_background = firebaseRemoteConfig.getString("splash_background");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }

        Toolbar place_toolbar = findViewById(R.id.bookdetail_toolbar);
        setSupportActionBar(place_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.button_back);

        mBook_id = getIntent().getStringExtra("book_id");
        mYour_id = getIntent().getStringExtra("your_id");
        if(mBook_id == null) {
            mBook_title.setText("트래블 북이 없습니다.");
        }
        mUser_id = mFirebase_Auth.getCurrentUser().getUid();
        if(mYour_id != null) {
            mUser_id = mYour_id;
        }
        FirebaseDatabase.getInstance().getReference().child("users").child(mUser_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user = dataSnapshot.child("userName").getValue().toString();
                mUser_score = Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());
                mUser_name = user;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mReference = FirebaseDatabase.getInstance().getReference().child("users").child(mUser_id).child("book").child(mBook_id);

        LoadTab();

        mBookListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                BookModel bookModel = dataSnapshot.getValue(BookModel.class);
                if (bookModel != null) {
                    mBook_title.setText(bookModel.getBook_title());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        BookScheduleFragment bookSchedule = new BookScheduleFragment();
        bookSchedule.setmBook_id(mBook_id);
        bookSchedule.setmYour_id(mYour_id);

        BookAlbumFragment bookAlbumFragment= new BookAlbumFragment();
        bookAlbumFragment.setmBook_id2(mBook_id);
        bookAlbumFragment.setmYour_id(mYour_id);

        mBook_title = findViewById(R.id.bookdetail_title);
        //mBook_schedule_list = findViewById(R.id.bookdetail_schedule_text);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mBookListener != null) {
            mReference.addValueEventListener(mBookListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBookListener != null) {
            mReference.removeEventListener(mBookListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_text, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 메뉴 클릭
                finish();
                return true;
            case R.id.action_text:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                // 제목셋팅
                alertDialogBuilder.setTitle("스케쥴 목록(텍스트)");
                // AlertDialog 셋팅
                alertDialogBuilder
                        .setMessage(mBook_schedule_list)
                        .setCancelable(false)
                        .setNegativeButton("텍스트 복사",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                        ClipData clipData = ClipData.newPlainText("schedule", mBook_schedule_list); //클립보드에 ID라는 이름표로 id 값을 복사하여 저장
                                        clipboardManager.setPrimaryClip(clipData);
                                        Toast.makeText(BookDetailActivity.this, "스케쥴 목록이 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton("닫기",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                // 다이얼로그 생성
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setOnShowListener( new DialogInterface.OnShowListener() { @Override public void onShow(DialogInterface arg0) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GRAY);
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
                }
                });
                // 다이얼로그 보여주기
                alertDialog.show();
                return true;
        }
        return false;
    }

    private void LoadTab() {
        SectionsPagerAdapter SectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager ViewPager = findViewById(R.id.container);
        ViewPager.setAdapter(SectionsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);

        ViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(ViewPager));
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return BookScheduleFragment.newInstance(position);
                case 1:
                    return BookAlbumFragment.newInstance(position);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }
}
