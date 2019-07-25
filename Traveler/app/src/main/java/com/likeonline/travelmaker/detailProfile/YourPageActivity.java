package com.likeonline.travelmaker.detailProfile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.likeonline.travelmaker.LoginActivity;
import com.likeonline.travelmaker.R;
import com.likeonline.travelmaker.model.UserModel;
import com.likeonline.travelmaker.travelbook.BookDetailActivity;
import com.likeonline.travelmaker.travelbook.BookModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class YourPageActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter<BookModel, BookListHolder.ItemViewHolder> mBookAdapter;
    private String mBook_id;

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    public Context context1;
    private CircleImageView profile;
    private FirebaseAuth mAuth;
    private TextView mYour_title;
    private TextView name; //사용자 이름
    private TextView grade; //회원 등급
    private long kakaoEmail;
    private String uid;
    private String photo;
    private ArrayList<String> destinationUsers = new ArrayList<>();
    private List<UserModel> userModels = new ArrayList<>();
    String destinationUid;
    RatingBar ratingBar;
    float totalrating; //총 유저 신뢰도 점수
    public float trustCount; //유저 평가 갯수
    private TextView myjob;
    private TextView myreligion;
    private TextView mydrinking;
    private TextView mybloodtype;
    private TextView myheight;
    private TextView mylocation;
    String splash_background;
    private Button gradeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yourpage);

        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = firebaseRemoteConfig.getString(getString(R.string.rc_color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }

        Toolbar place_toolbar = findViewById(R.id.yourpage_toolbar);
        setSupportActionBar(place_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.button_back);

        mYour_title = findViewById(R.id.yourpage_title);
        ratingBar=(RatingBar)findViewById(R.id.mypage_ratingBar);
        gradeBtn=(Button) findViewById(R.id.youpage_btn_gradecareer);
        context1=this.getApplication();
        profile=(CircleImageView)findViewById(R.id.myPage_profileup);
        name=(TextView) findViewById(R.id.myapge_name_text);
        kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;
        grade=(TextView) findViewById(R.id.mypage_grade);
        mAuth = FirebaseAuth.getInstance();

        myjob=(TextView)findViewById (R.id.mypage_job);
        myreligion=(TextView)findViewById (R.id.mypage_religion);
        mydrinking=(TextView)findViewById (R.id.mypage_drinking);
        mybloodtype=(TextView)findViewById (R.id.mypage_bloodtype);
        myheight=(TextView)findViewById (R.id.mypage_height);
        mylocation=(TextView)findViewById (R.id.mypage_location);

        destinationUid =getIntent().getStringExtra("post_id");; // 다른 액티비티의 uid 참고
       // Bundle bundle = getArguments();

        FirebaseUser user = mAuth.getCurrentUser();
        if(kakaoEmail==0) {
            uid = user.getUid();
        } else {
            uid = String.valueOf(kakaoEmail);
        }
        gradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), you_gradeReportActivity.class);
                intent.putExtra("destinationUid", destinationUid);
                startActivity(intent);
            }
        });
        //  FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {

        myRef.child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener () {

            /*if(dataSnapshot.child("user_trustCount").getValue()!=null) {
                trustCount = Float.valueOf (dataSnapshot.child ("user_trustCount").getValue ().toString ());
            }*/
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
                    String job =dataSnapshot.child ("user_job").getValue().toString ();
                    String religion=dataSnapshot.child ("user_religion").getValue().toString ();
                    String drinking=dataSnapshot.child ("user_drinking").getValue().toString ();
                    String bloodtype=dataSnapshot.child ("user_bloodtype").getValue().toString ();
                    String height=dataSnapshot.child ("user_height").getValue().toString ();
                    String location=dataSnapshot.child ("user_location").getValue().toString ();


                    int score = Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());
                    profile.setImageResource(R.drawable.mainicon);
                    mYour_title.setText(user + "의 프로필");
                    name.setText(user);
                    myjob.setText(job);
                    myreligion.setText (religion);
                    mydrinking.setText (drinking);
                    mybloodtype.setText (bloodtype);
                    myheight.setText (height);
                    mylocation.setText (location);



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
                    String job =dataSnapshot.child ("user_job").getValue ().toString ();
                    String religion=dataSnapshot.child ("user_religion").getValue ().toString ();
                    String drinking=dataSnapshot.child ("user_drinking").getValue ().toString ();
                    String bloodtype=dataSnapshot.child ("user_bloodtype").getValue ().toString ();
                    String height=dataSnapshot.child ("user_height").getValue ().toString ();
                    String location=dataSnapshot.child ("user_location").getValue ().toString ();

                    int score = Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());
                    Picasso.with(getApplication ()).load(photo).fit().centerInside().into(profile, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                            // Index 0 is the image view.
                        }
                    });
                    mYour_title.setText(user + "의 프로필");
                    name.setText(user);
                    myjob.setText(job);
                    myreligion.setText (religion);
                    mydrinking.setText (drinking);
                    mybloodtype.setText (bloodtype);
                    myheight.setText (height);
                    mylocation.setText (location);

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

        init();
    }
    private void init() {

        mRecyclerView = findViewById(R.id.detail_profile_listview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        final DatabaseReference query =  myRef.child("users").child(destinationUid).child("book");
        final FirebaseRecyclerOptions<BookModel> options = new FirebaseRecyclerOptions.Builder<BookModel>()
                .setQuery(query, BookModel.class)
                .build(); // 어떤 데이터를 어디서 가져올것이며 어떤 형태의 데이터 클래스의 결과를 반환할건지 리사이클러뷰 어댑터 옵션에 넣음

        //어댑터랑 연결해서 그리드 레이아웃에 뿌림
        mBookAdapter = new FirebaseRecyclerAdapter<BookModel, BookListHolder.ItemViewHolder>(options) {

            @NonNull
            @Override
            public BookListHolder.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_book, viewGroup, false);
                return new BookListHolder.ItemViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(BookListHolder.ItemViewHolder holder, int position, final BookModel model) {
                // 포지션 값을 받아오고 그 포지션 아이템의 키값을 저장함
                final DatabaseReference postRef = getRef(position);
                mBook_id = postRef.getKey();

                // 아이템 클릭 이벤트
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (model.isBook_private()){
                            Toast.makeText(YourPageActivity.this, "해당 트래블북은 비공개로 설정되어있습니다", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), BookDetailActivity.class);
                            intent.putExtra("book_id", mBook_id);
                            intent.putExtra("your_id", destinationUid);
                            startActivityForResult(intent, 0);
                        }
                    }
                });

                if (model.isBook_private()){
                    holder.book_image.setImageDrawable(ContextCompat.getDrawable(YourPageActivity.this, R.drawable.icon_travelbook_normal_protect));
                }

                // 사실상 bind 함수를 여기서 다하는중
                holder.book_title.setText(model.getBook_title());
                /*if (model.getBook_category().equals("계획중")) {

                } else if (model.getBook_category().equals("여행중")) {

                } else if (model.getBook_category().equals("다녀옴")) {

                }*/
            }
        };


        mRecyclerView.setAdapter(mBookAdapter);

        com.likeonline.travelmaker.detailProfile.ProfileDeco decoration = new com.likeonline.travelmaker.detailProfile.ProfileDeco ();
        mRecyclerView.addItemDecoration(decoration);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mBookAdapter != null){
            mBookAdapter.startListening(); // 이거 해야 어댑터 목록 읽기 시작
        }
    }

    // 액티비티의 라이프사이클이 끝날 때 발생하는 이벤트
    @Override
    public void onStop() {
        super.onStop();
        if (mBookAdapter != null){
            mBookAdapter.stopListening(); // 이거 해야 어댑터 목록 안읽음
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 메뉴 클릭
                finish();
                return true;
        }
        return false;
    }

    public static class BookListHolder extends RecyclerView.ViewHolder {

        public BookListHolder(@NonNull View itemView) {
            super(itemView);
        }

        public static class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView book_title;
            ImageView book_image;

            ItemViewHolder(View itemView) {
                super(itemView);
                book_title = itemView.findViewById(R.id.book_title);
                book_image = itemView.findViewById(R.id.book_image);
            }
        }
    }
}
