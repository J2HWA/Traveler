package com.likeonline.travelmaker.review;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.likeonline.travelmaker.LoginActivity;
import com.likeonline.travelmaker.R;
import com.likeonline.travelmaker.place.PlaceDetailActivity;

public class my_reviewActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String uid;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RatingBar ratingBar;
    public FirebaseRecyclerAdapter<reviewModel, ReviewListAdapter.ItemViewHolder> mReviewAdapter;
    private long kakaoEmail;
    private String mPlace_id;
    private String mPlace_category;
    private String mReview_id;
    private String key;
    String splash_background;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_review);


        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = firebaseRemoteConfig.getString(getString(R.string.rc_color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }

        Toolbar my_review_toolbar =findViewById(R.id.my_review_toolbar);
        setSupportActionBar(my_review_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.button_back);

        kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;

        database=FirebaseDatabase.getInstance();
        myRef=database.getReference();

        ratingBar=(RatingBar)findViewById(R.id.reviewitem_ratingbar);

        if(kakaoEmail==0) {
            uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            uid = String.valueOf(kakaoEmail);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.myreview_list);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        ((LinearLayoutManager) mLayoutManager).setReverseLayout(true);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setAdapter(mReviewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        final DatabaseReference query =  myRef.child("users").child(uid).child("review");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot reviewsSnapshot:dataSnapshot.getChildren()){
                    key=reviewsSnapshot.getKey();
                }
                if(key==null){
                }else{
                    query.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mPlace_category=dataSnapshot.child("mPlace_category").getValue().toString();
                            mPlace_id=dataSnapshot.child("post_id").getValue().toString();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions<reviewModel> options = new FirebaseRecyclerOptions.Builder<reviewModel>()
                .setQuery(query, reviewModel.class)
                .build(); // 어떤 데이터를 어디서 가져올것이며 어떤 형태의 데이터 클래스의 결과를 반환할건지 리사이클러뷰 어댑터 옵션에 넣음

        //어댑터랑 연결해서 리니어 레이아웃에 뿌림

         mReviewAdapter = new FirebaseRecyclerAdapter<reviewModel, ReviewListAdapter.ItemViewHolder>(options) {

             @NonNull
             @Override
             public ReviewListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                 View view = LayoutInflater.from(viewGroup.getContext())
                         .inflate(R.layout.item_review, viewGroup, false);
                 return new ReviewListAdapter.ItemViewHolder(view);
             }

             @Override
             protected void onBindViewHolder(final my_reviewActivity.ReviewListAdapter.ItemViewHolder holder, int position, reviewModel model) {
                 // 포지션 값을 받아오고 그 포지션 아이템의 키값을 저장함
                 final DatabaseReference postRef = getRef(position);
                 final String review_id = postRef.getKey();

                 // 아이템 클릭 이벤트
                 holder.itemView.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         Intent intent = new Intent(getApplicationContext(), PlaceDetailActivity.class);
                         intent.putExtra("place_id", mPlace_id);
                         intent.putExtra("place_category", mPlace_category);
                         intent.putExtra("review_id", review_id);
                         startActivity(intent);

                     }
                 });

                 // 사실상 bind 함수를 여기서 다하는중
                 holder.review_title.setText(model.getTitle());
                 if (model.getUser_profileUrl() == null) {
                     holder.review_profile.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                             R.drawable.mainicon));
                 } else {
                     Glide.with(getApplicationContext())
                             .load(model.getUser_profileUrl())
                             .into(holder.review_profile);
                 }

                 holder.review_writer.setText(model.getUser_name());
                 holder.review_postingtime.setText(model.getTime());

                 assert review_id != null;
                 query.child(review_id).addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         float ratingScore = Float.valueOf(dataSnapshot.child("rating").getValue().toString());
                         holder.ratingBar.setRating(ratingScore);
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });
             }


         };

        mRecyclerView.setAdapter(mReviewAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mReviewAdapter != null){
            mReviewAdapter.startListening(); // 이거 해야 어댑터 목록 읽기 시작
        }
    }

    // 액티비티의 라이프사이클이 끝날 때 발생하는 이벤트
    @Override
    public void onStop() {
        super.onStop();
        if (mReviewAdapter != null){
            mReviewAdapter.stopListening(); // 이거 해야 어댑터 목록 안읽음
        }
    }

    public static class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ItemViewHolder> {

        my_reviewActivity review = new my_reviewActivity();
        @NonNull
        @Override
        public my_reviewActivity.ReviewListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
            // return 인자는 ViewHolder 입니다.
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
            return new my_reviewActivity.ReviewListAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            // RecyclerView의 총 개수 입니다.
            return review.mReviewAdapter.getItemCount();
        }

        public static class ItemViewHolder extends RecyclerView.ViewHolder {
            ImageView review_profile;
            TextView review_title;
            TextView review_writer;
            TextView review_postingtime;
            RatingBar ratingBar;

            ItemViewHolder(View itemView) {
                super(itemView);
                review_profile = itemView.findViewById(R.id.reviewitem_profile);
                review_title = itemView.findViewById(R.id.reviewitem_title);
                review_writer = itemView.findViewById(R.id.reviewitem_writer);
                review_postingtime = itemView.findViewById(R.id.reviewitem_time);
                ratingBar=itemView.findViewById(R.id.reviewitem_ratingbar);
            }

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

}

