package com.likeonline.travelmaker.travelbook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.likeonline.travelmaker.LoginActivity;
import com.likeonline.travelmaker.R;
import com.likeonline.travelmaker.place.PlaceActivity;
import com.likeonline.travelmaker.place.PlaceDetailActivity;

public class BookScheduleFragment extends Fragment {

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance(); // 데이터베이스 선언 및 초기화
    private DatabaseReference mReference = mDatabase.getReference(); // 데이터베이스 값을 읽어오기 위한 레퍼런스 선언 및 초기화
    private FirebaseAuth mFirebase_Auth = FirebaseAuth.getInstance();; // 파이어베이스 계정 값 초기화
    private RecyclerView mRecyclerView;
    private RecyclerView mMiniRecyclerView;
    public FirebaseRecyclerAdapter<ScheduleModel, ScheduleListHolder.ItemViewHolder> mScheduleAdapter;
    public FirebaseRecyclerAdapter<ScheduleModel, ScheduleListHolder.ItemViewHolder> mMiniScheduleAdapter;

    private static String mBook_id;
    private String mUser_id;
    private static String mYour_id;

    private Button mBook_addBtn;
    private int mScheduleNum = 0;

    public BookScheduleFragment() {
        // Required empty public constructor
    }

    public void setmBook_id(String mBook_id) {
        this.mBook_id = mBook_id;
    }
    public void setmYour_id(String mYour_id) {
        this.mYour_id = mYour_id;
    }

    public static BookScheduleFragment newInstance(int sectionNumber) {
        BookScheduleFragment fragment = new BookScheduleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BookDetailActivity.mBook_schedule_list = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootview = inflater.inflate(R.layout.fragment_book_schedule, container, false);

        mBook_addBtn = rootview.findViewById(R.id.schedule_add);
        long kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;
        if(kakaoEmail==0) {
            mUser_id = mFirebase_Auth.getInstance().getCurrentUser().getUid();
        } else {
            mUser_id= String.valueOf(kakaoEmail);
        }


        final DatabaseReference query;
        FirebaseRecyclerOptions<ScheduleModel> options = null;

        if (mYour_id != null) {
            if(!mYour_id.equals(mUser_id)) {
                mBook_addBtn.setVisibility(View.GONE);
                query = mReference.child("users").child(mYour_id).child("book").child(mBook_id).child("schedule");
                options = new FirebaseRecyclerOptions.Builder<ScheduleModel>()
                        .setQuery(query, ScheduleModel.class)
                        .build(); // 어떤 데이터를 어디서 가져올것이며 어떤 형태의 데이터 클래스의 결과를 반환할건지 리사이클러뷰 어댑터 옵션에 넣음
            } else {
                query = mReference.child("users").child(mYour_id).child("book").child(mBook_id).child("schedule");
                options = new FirebaseRecyclerOptions.Builder<ScheduleModel>()
                        .setQuery(query, ScheduleModel.class)
                        .build(); // 어떤 데이터를 어디서 가져올것이며 어떤 형태의 데이터 클래스의 결과를 반환할건지 리사이클러뷰 어댑터 옵션에 넣음
            }
        } else {
            query = mReference.child("users").child(mUser_id).child("book").child(mBook_id).child("schedule");
            options = new FirebaseRecyclerOptions.Builder<ScheduleModel>()
                    .setQuery(query, ScheduleModel.class)
                    .build(); // 어떤 데이터를 어디서 가져올것이며 어떤 형태의 데이터 클래스의 결과를 반환할건지 리사이클러뷰 어댑터 옵션에 넣음
        }

        mBook_addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlaceActivity.class);
                intent.putExtra("book_id", mBook_id);
                startActivity(intent);
            }
        });

        mMiniRecyclerView = (RecyclerView) rootview.findViewById(R.id.schedule_miniList);
        LinearLayoutManager HorizonlayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mMiniRecyclerView.setLayoutManager(HorizonlayoutManager);
        mMiniRecyclerView.scrollToPosition(0);
        mMiniRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mMiniScheduleAdapter = new FirebaseRecyclerAdapter<ScheduleModel, ScheduleListHolder.ItemViewHolder>(options) {

            @NonNull
            @Override
            public ScheduleListHolder.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_schedule_mini, viewGroup, false);
                return new ScheduleListHolder.ItemViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(final ScheduleListHolder.ItemViewHolder holder, final int position, ScheduleModel model) {
                final DatabaseReference scheduleRef = getRef(position);
                final String schedule_id = scheduleRef.getKey();

                Glide.with(holder.itemView.getContext())
                        .load(model.getSchedule_imgUrl())
                        .apply(new RequestOptions())
                        .into(holder.schedule_mini_image);

                mScheduleNum++;
                BookDetailActivity.mBook_schedule_list += mScheduleNum + ". " +  model.getSchedule_title() + "\n";
            }
        };

        mMiniRecyclerView.setAdapter(mMiniScheduleAdapter);

        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.schedule_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mScheduleAdapter = new FirebaseRecyclerAdapter<ScheduleModel, ScheduleListHolder.ItemViewHolder>(options) {

            @NonNull
            @Override
            public ScheduleListHolder.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_schedule, viewGroup, false);
                return new ScheduleListHolder.ItemViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(final ScheduleListHolder.ItemViewHolder holder, final int position, final ScheduleModel model) {
                final DatabaseReference scheduleRef = getRef(position);
                final String schedule_id = scheduleRef.getKey();

                float totalrating = model.getSchedule_totalrating();
                float reviewCount = model.getSchedule_reviewCount();

                // 아이템 클릭 이벤트
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), PlaceDetailActivity.class);
                        intent.putExtra("place_id", model.getPlace_id().toString());
                        intent.putExtra("place_category", model.getSchedule_category().toString());
                        startActivity(intent);
                    }
                });

                if (mYour_id != null) {
                    if (!mYour_id.equals(mUser_id)) {
                        mBook_addBtn.setVisibility(View.GONE);
                        holder.schedule_delete.setVisibility(View.GONE);
                    }
                }

                holder.schedule_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        // 제목셋팅
                        alertDialogBuilder.setTitle("스케쥴 삭제 확인");
                        // AlertDialog 셋팅
                        alertDialogBuilder
                                .setMessage("스케쥴을 삭제하시겠습니까?")
                                .setCancelable(false)
                                .setNegativeButton("예",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                mReference.child("users").child(mUser_id).child("book").child(mBook_id).child("schedule").child(schedule_id).removeValue();
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
                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
                        }
                        });
                        // 다이얼로그 보여주기
                        alertDialog.show();
                    }
                });

                Glide.with(holder.itemView.getContext())
                        .load(model.getSchedule_imgUrl())
                        .apply(new RequestOptions())
                        .into(holder.schedule_image);
                holder.schedule_title.setText(model.getSchedule_title());
                holder.schedule_rating.setRating(totalrating / reviewCount);
                if (Float.isNaN(totalrating / reviewCount)) {
                    holder.schedule_score.setText("0");
                } else {
                    holder.schedule_score.setText(String.valueOf(String.format("%.1f", totalrating / reviewCount)));
                }
                holder.schedule_tag.setText(model.getSchedule_tag());
            }
        };

        mRecyclerView.setAdapter(mScheduleAdapter);

        return rootview;
    }

    // 라이프사이클이 시작될 때 발생하는 이벤트
    @Override
    public void onStart() {
        super.onStart();
        if (mScheduleAdapter != null){
            mScheduleAdapter.startListening(); // 이거 해야 어댑터 목록 읽기 시작
        }
        if (mMiniScheduleAdapter != null){
            mMiniScheduleAdapter.startListening(); // 이거 해야 어댑터 목록 읽기 시작
        }
    }

    // 라이프사이클이 끝날 때 발생하는 이벤트
    @Override
    public void onStop() {
        super.onStop();
        if (mScheduleAdapter != null){
            mScheduleAdapter.stopListening(); // 이거 해야 어댑터 목록 안읽음
        }
        if (mMiniScheduleAdapter != null){
            mMiniScheduleAdapter.stopListening(); // 이거 해야 어댑터 목록 안읽음
        }
    }

    public static class ScheduleListHolder extends RecyclerView.ViewHolder {

        public ScheduleListHolder(@NonNull View itemView) {
            super(itemView);
        }

        public static class ItemViewHolder extends RecyclerView.ViewHolder {
            ImageView schedule_image;
            TextView schedule_title;
            TextView schedule_tag;
            RatingBar schedule_rating;
            TextView schedule_score;
            ImageButton schedule_delete;

            ImageButton schedule_mini_image;

            ItemViewHolder(View itemView) {
                super(itemView);
                schedule_image = itemView.findViewById(R.id.schedule_image);
                schedule_title = itemView.findViewById(R.id.schedule_name);
                schedule_tag = itemView.findViewById(R.id.schedule_tag);
                schedule_rating = itemView.findViewById(R.id.schedule_rating);
                schedule_score = itemView.findViewById(R.id.schedule_score);
                schedule_delete = itemView.findViewById(R.id.schedule_delete);
                schedule_mini_image = itemView.findViewById(R.id.schedule_mini_image);
            }
        }
    }
}
