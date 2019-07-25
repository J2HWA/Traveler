package com.likeonline.travelmaker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.likeonline.travelmaker.R;
import com.likeonline.travelmaker.place.PlaceActivity;
import com.likeonline.travelmaker.review.reviewModel;
import com.likeonline.travelmaker.review.review_DetailActivity;
import com.likeonline.travelmaker.review.review_WritingActivity;

public class PlaceDetail_review extends Fragment {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Button writebtn;
    private String mPlace_id;
    private String mPlace_category;
    private String mReview_id;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RatingBar ratingBar;
    // 어댑터 선언
    public FirebaseRecyclerAdapter<reviewModel, ReviewListAdapter.ItemViewHolder> mReviewAdapter;

    public void setmPlace_id(String mPlace_id) {
        this.mPlace_id = mPlace_id;
    }

    public void setmPlace_category(String mPlace_category) {
        this.mPlace_category = mPlace_category;
    }
    public void setmReviewid(String mReview_id) {
        this.mReview_id = mReview_id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View View=(ViewGroup)inflater.inflate(R.layout.fragment_place_detail_review, container, false);

        database=FirebaseDatabase.getInstance();
        myRef=database.getReference();
        writebtn=View.findViewById(R.id.review_btn);
        ratingBar=View.findViewById(R.id.reviewitem_ratingbar);

        writebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent=new Intent(getActivity(), review_WritingActivity.class);
                intent.putExtra("mPlace_id", mPlace_id);
                intent.putExtra("mPlace_category", mPlace_category);
                intent.putExtra("mReview_id2", mReview_id);
                startActivity(intent);
            }
        });


        mRecyclerView = (RecyclerView) View.findViewById(R.id.review_postlist);
        mLayoutManager = new LinearLayoutManager(getActivity());
        ((LinearLayoutManager) mLayoutManager).setReverseLayout(true);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setAdapter(mReviewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        final DatabaseReference query =  myRef.child("place").child(mPlace_category).child(mPlace_id).child("review");
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
            protected void onBindViewHolder(final PlaceDetail_review.ReviewListAdapter.ItemViewHolder holder, int position, reviewModel model) {
                // 포지션 값을 받아오고 그 포지션 아이템의 키값을 저장함
                final DatabaseReference postRef = getRef(position);
                final String review_id = postRef.getKey();

                // 아이템 클릭 이벤트
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), review_DetailActivity.class);
                        intent.putExtra("mPlace_id", mPlace_id);
                        intent.putExtra("mPlace_category", mPlace_category);
                        intent.putExtra("mReview_id", review_id);
                        intent.putExtra("mReview_id2", mReview_id);
                        startActivity(intent);

                    }
                });

                // 사실상 bind 함수를 여기서 다하는중
                holder.review_title.setText(model.getTitle());
                if (model.getUser_profileUrl() == null) {
                    holder.review_profile.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                            R.drawable.mainicon));
                } else {
                    Glide.with(getActivity())
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

        return View;
    }
    // 액티비티의 라이프사이클이 시작될 때 발생하는 이벤트

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

        PlaceDetail_review review = new PlaceDetail_review();
        PlaceActivity.FoodFragment foodFragment;
        PlaceActivity.TravelFragment travelFragment;
        PlaceActivity.HotelFragment hotelFragment;
        float reviewCount;
        PlaceActivity placeActivity;

        @NonNull
        @Override
        public PlaceDetail_review.ReviewListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
            // return 인자는 ViewHolder 입니다.
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
            return new PlaceDetail_review.ReviewListAdapter.ItemViewHolder(view);
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
}
