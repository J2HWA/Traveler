package com.likeonline.travelmaker.place;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.likeonline.travelmaker.LoginActivity;
import com.likeonline.travelmaker.R;
import com.likeonline.travelmaker.fragment.PlaceDetail_content;
import com.likeonline.travelmaker.fragment.PlaceDetail_review;
import com.likeonline.travelmaker.mypage.Link_Model;
import com.likeonline.travelmaker.mypage.RecentTravel_Model;
import com.likeonline.travelmaker.travelbook.BookDetailActivity;
import com.likeonline.travelmaker.travelbook.BookModel;
import com.likeonline.travelmaker.travelbook.ScheduleModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PlaceDetailActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ImageView placeimg;
    private ImageButton mPlace_select;
    private TextView textTitle;
    private TextView textExplain;
    private TextView textToolbar;
    private String mPlace_id;
    private String place_id=null;
    private String mPlace_category;
    private String mReview_id;
    PlaceDetail_content ContentFragment;
    PlaceDetail_review ReviewFragment;
    String splash_background;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    private Integer click; //조회수;
    private String mPlace_imgUrl; // 여행지의 이미지 url
    private String mPlace_title; // 여행지의 이름
    private String mPlace_tag; // 여행지의 태그
    private Integer mPlace_views; // 여행지의 조회수
    private float mPlace_totalrating; //여행지 점수
    private float mPlace_reviewCount; //리뷰 갯수
    private String place_category; //디비에 들어갈 카테고리명
    private String post_id;
    private String uid ;
    private long kakaoEmail;
    private String mBook_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detailinfo);

        Toolbar board_toolbar = findViewById(R.id.placedetail_toolbar);
        setSupportActionBar(board_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.button_back);
        kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;

        if(kakaoEmail==0) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            uid = String.valueOf(kakaoEmail);
        }

        PlaceDetail_content detail = new PlaceDetail_content();
        PlaceDetail_review review=new PlaceDetail_review();

        ContentFragment=detail;
        ReviewFragment =review;
        mPlace_id = getIntent().getStringExtra("place_id");
        mPlace_category = getIntent().getStringExtra("place_category");
        mReview_id = getIntent().getStringExtra("review_id");
        mBook_id = getIntent().getStringExtra("book_id");

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = firebaseRemoteConfig.getString("splash_background");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }

        review.setmPlace_id(mPlace_id);
        detail.setmPlace_id(mPlace_id);
        review.setmPlace_category(mPlace_category);
        detail.setmPlace_category(mPlace_category);
        review.setmReviewid(mReview_id);

        TabLayout tabs=(TabLayout)findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("상세내용"));
        tabs.addTab(tabs.newTab().setText("리뷰"));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position=tab.getPosition();
                Fragment selected=null;
                if(position==0)
                { selected=ContentFragment;}
                else if(position==1)
                { selected=ReviewFragment;}
                getSupportFragmentManager().beginTransaction().replace(R.id.tablayout, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.tablayout, ContentFragment).commit();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        placeimg=(ImageView) findViewById(R.id.placedetail_image);
        mPlace_select = findViewById(R.id.placedetail_select);
        textTitle=(TextView) findViewById(R.id.place_detail_title);
        textExplain=(TextView) findViewById(R.id.place_detail_content);
        textToolbar=(TextView) findViewById(R.id.placedetail_title);
        textExplain=(TextView) findViewById(R.id.place_detail_content);
        myRef.child("place").child(mPlace_category).child(mPlace_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String  area= dataSnapshot.child("place_area").getValue().toString();
                String  address= dataSnapshot.child("place_address").getValue().toString();
                String  title= dataSnapshot.child("place_title").getValue().toString();
                String  tel= dataSnapshot.child("place_tel").getValue().toString();
                String photo=dataSnapshot.child("place_imgUrl").getValue().toString();
                String sub=dataSnapshot.child("place_sub").getValue().toString();

                Picasso.with(getApplicationContext()).load(photo).fit().centerCrop().into(placeimg, new Callback.EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        // Index 0 is the image view.
                    }
                });
                textTitle.setText(title);
                textToolbar.setText(area);
                textExplain.setText(sub);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // 트래블북 추가 이벤트
        mPlace_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBook_id != null) {
                    myRef.child("place").child(mPlace_category).child(mPlace_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String tag = dataSnapshot.child("place_tag").getValue().toString();
                            String title = dataSnapshot.child("place_title").getValue().toString();
                            String photo = dataSnapshot.child("place_imgUrl").getValue().toString();
                            float totalrating = Float.valueOf(dataSnapshot.child("place_totalrating").getValue().toString());
                            float reviewCount = Float.valueOf(dataSnapshot.child("place_reviewCount").getValue().toString());

                            ScheduleModel model = new ScheduleModel(
                                    mPlace_id,
                                    mPlace_category,
                                    title,
                                    tag,
                                    photo,
                                    totalrating,
                                    reviewCount);
                            myRef.child("users").child(uid).child("book").child(mBook_id).child("schedule").push().setValue(model);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Toast.makeText(PlaceDetailActivity.this, "트래블 북에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PlaceDetailActivity.this);
                    // 제목셋팅
                    alertDialogBuilder.setTitle("트래블 북을 찾을 수 없음");
                    // AlertDialog 셋팅
                    alertDialogBuilder
                            .setMessage("트래블 북을 찾을 수 없습니다. 만들어보실래요?")
                            .setCancelable(false)
                            .setNegativeButton("예",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(getApplicationContext(), BookDetailActivity.class);
                                        }
                                    })
                            .setPositiveButton("아니요",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // 다이얼로그를 취소한다
                                            dialog.cancel();
                                        }
                                    });
                    // 다이얼로그 생성
                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setOnShowListener( new DialogInterface.OnShowListener() { @Override public void onShow(DialogInterface arg0) {
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GRAY);
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                    }
                    });
                    // 다이얼로그 보여주기
                    alertDialog.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_link, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 메뉴 클릭
                finish();
                return true;
            case R.id.action_like:
                myRef.child("place").child(mPlace_category).child(mPlace_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String tag= dataSnapshot.child("place_tag").getValue().toString();
                        String title= dataSnapshot.child("place_title").getValue().toString();
                        String photo=dataSnapshot.child("place_imgUrl").getValue().toString();
                        Integer click=Integer.parseInt(dataSnapshot.child("place_views").getValue().toString());
                        float totalrating=Float.valueOf(dataSnapshot.child("place_totalrating").getValue().toString());
                        float reviewCount=Float.valueOf(dataSnapshot.child("place_reviewCount").getValue().toString());

                        Link_Model model = new  Link_Model(
                                mPlace_imgUrl=photo,
                                mPlace_title =title,
                                mPlace_tag =tag,
                                click,
                                mPlace_totalrating=totalrating,
                                mPlace_reviewCount=reviewCount,
                                place_category=mPlace_category,
                                post_id=mPlace_id
                        );

                        if(place_id==null || place_id!=mPlace_id){
                            myRef.child("users").child(uid).child("link").child(mPlace_id).setValue(model);// 노드 생성 및 데이터 입력
                            place_id=mPlace_id;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Toast.makeText(this, "마이페이지 링크목록에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }
}