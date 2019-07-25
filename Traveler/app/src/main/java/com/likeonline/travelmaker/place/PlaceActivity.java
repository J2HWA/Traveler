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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.likeonline.travelmaker.LoginActivity;
import com.likeonline.travelmaker.R;
import com.likeonline.travelmaker.mypage.RecentTravel_Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlaceActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    String splash_background;
    private FirebaseRemoteConfig firebaseRemoteConfig;

    private static String mBook_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        //툴바 세팅
        Toolbar place_toolbar = findViewById(R.id.place_toolbar);
        setSupportActionBar(place_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.button_back);

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = firebaseRemoteConfig.getString("splash_background");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }

        mBook_id = getIntent().getStringExtra("book_id");

        // 탭을 생성하는 메서드입니다.
        LoadTab();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        /*MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_create, menu);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 메뉴 클릭
                if (mBook_id != null) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    // 제목셋팅
                    alertDialogBuilder.setTitle("스케쥴 작성 종료");
                    // AlertDialog 셋팅
                    alertDialogBuilder
                            .setMessage("추가한 스케쥴을 트래블 북에 저장하시겠습니까?")
                            .setCancelable(false)
                            .setNegativeButton("예",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            finish();

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
                } else {
                    finish();
                }
                return true;
            case R.id.action_posting:
                Intent intent = new Intent(this, PlaceCreateActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    private void LoadTab() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    public static class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.ItemViewHolder> {

        @NonNull
        @Override
        public PlaceListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
            // return 인자는 ViewHolder 입니다.
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
            return new PlaceListAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PlaceListAdapter.ItemViewHolder holder, final int position) {
            // bind를 여기서 해주세요.
            /*holder.place_imgUrl.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.baseline_account_circle_black_18dp));
            holder.place_name.setText(model.getPlace_title());
            holder.place_rating.setRating(model.getPlace_rating());
            holder.place_tag.setText(model.getPlace_tag());
            holder.place_views.setText(model.getPlace_views());*/
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        // RecyclerView의 핵심인 ViewHolder 입니다.
        // 여기서 subView를 setting 해줍니다.
        public static class ItemViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView place_name;
            RatingBar place_rating;
            TextView place_tag;
            TextView place_views;
            TextView PlaceRating;

            ItemViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.place_dogimage);
                place_name = itemView.findViewById(R.id.place_name);
                place_rating = itemView.findViewById(R.id.place_rating);
                PlaceRating=itemView.findViewById(R.id.place_score);
                place_tag = itemView.findViewById(R.id.place_tag);
                place_views = itemView.findViewById(R.id.place_views);
            }

        }
    }

    public static class FoodFragment extends Fragment {
        private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance(); // 데이터베이스 선언 및 초기화
        private DatabaseReference mReference = mDatabase.getReference(); // 데이터베이스 값을 읽어오기 위한 레퍼런스 선언 및 초기화
        private FirebaseRecyclerAdapter<PlaceModel, PlaceListAdapter.ItemViewHolder> mPlaceAdapter;
        private final List<PlaceModel> mPlaceModels = new ArrayList<>();
        private RecyclerView mPlace_list;
        private String mPlace_root = "place";
        private String mPlace_category = "food";
        float totalrating;
        public float reviewCount; //리뷰 갯수
        private Integer click; //조회수;
        private String mplace_imgUrl; // 여행지의 이미지 url
        private String mplace_title; // 여행지의 이름
        private String mplace_tag; // 여행지의 태그
        private Integer mplace_views; // 여행지의 조회수
        private float mplace_totalrating; //여행지 점수
        private float mplace_reviewCount; //리뷰 갯수
        private long kakaoEmail;
        private String uid;
        private String mPlace_id=null;
        private String place_category; //디비에 들어갈 카테고리명
        private String post_id;
        private float reviewModelRating;

        public FoodFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_place_food, container, false);

            kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;

            if(kakaoEmail==0){
                uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
            }
            else{
                uid=String.valueOf(kakaoEmail);
            }

            mPlace_list = rootView.findViewById(R.id.food_list);

            //어댑터랑 연결해서 리니어 레이아웃에 뿌림
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setStackFromEnd(true);
            mPlace_list.setLayoutManager(layoutManager);

            final Query query = mReference.child(mPlace_root).child(mPlace_category);
            FirebaseRecyclerOptions<PlaceModel> options = new FirebaseRecyclerOptions.Builder<PlaceModel>()
                    .setQuery(query, PlaceModel.class)
                    .build(); // 어떤 데이터를 어디서 가져올것이며 어떤 형태의 데이터 클래스의 결과를 반환할건지 리사이클러뷰 어댑터 옵션에 넣음

            FirebaseDatabase.getInstance().getReference().child(mPlace_root).child(mPlace_category).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mPlaceModels.clear();

                    for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        PlaceModel placeModel = snapshot.getValue(PlaceModel.class);
                        mPlaceModels.add(placeModel);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mPlaceAdapter = new FirebaseRecyclerAdapter<PlaceModel, PlaceListAdapter.ItemViewHolder>(options) {

                @NonNull
                @Override
                public PlaceListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.item_place, viewGroup, false);
                    return new PlaceListAdapter.ItemViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(PlaceListAdapter.ItemViewHolder holder, final int position, PlaceModel model) {
                    // 포지션 값을 받아오고 그 포지션 아이템의 키값을 저장함
                    final DatabaseReference placeRef = getRef(position);
                    final String place_id = placeRef.getKey();

                    click=model.getPlace_views();

                    totalrating =model.getPlace_totalrating();
                    reviewCount=model.getPlace_reviewCount();

                    if(Float.isNaN(reviewModelRating)){
                        reviewModelRating=0;
                    }else{
                        reviewModelRating=totalrating;
                    }


                    Glide.with(holder.itemView.getContext())
                            .load(mPlaceModels.get(position).getPlace_imgUrl())
                            .apply(new RequestOptions())
                            .into(holder.imageView);
                    holder.place_name.setText(mPlaceModels.get(position).getPlace_title());
                    if(Float.isNaN(totalrating/reviewCount)){
                        holder.PlaceRating.setText("0");
                    }else{
                        holder.PlaceRating.setText(String.valueOf(String.format("%.1f",totalrating/reviewCount)));
                    }
                    holder.place_rating.setRating(Float.parseFloat(String.format("%.1f", totalrating/reviewCount)));
                    holder.place_tag.setText(mPlaceModels.get(position).getPlace_tag());
                    holder.place_views.setText(click.toString());

                    // 아이템 클릭 이벤트
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ((DatabaseReference) query).child(place_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    click=Integer.parseInt(dataSnapshot.child("place_views").getValue().toString());
                                    click++;
                                    final Map<String, Object> taskMap1 = new HashMap<String, Object>(); //업데이트 시킬 조회수
                                    taskMap1.put("place_views", click);
                                    ((DatabaseReference) query).child(place_id).updateChildren(taskMap1);
                                    reviewCount=Integer.parseInt(dataSnapshot.child("place_reviewCount").getValue().toString());
                                    reviewModelRating=Float.valueOf(dataSnapshot.child("place_totalrating").getValue().toString());

                                    RecentTravel_Model model = new RecentTravel_Model(
                                            mplace_imgUrl=mPlaceModels.get(position).getPlace_imgUrl(),
                                            mplace_title =mPlaceModels.get(position).getPlace_title(),
                                            mplace_tag =mPlaceModels.get(position).getPlace_tag(),
                                            click,
                                            mplace_totalrating=reviewModelRating,
                                            mplace_reviewCount=reviewCount,
                                            place_category=mPlace_category,
                                            post_id=place_id
                                    );

                                    if(mPlace_id==null || mPlace_id!=place_id){
                                        mReference.child("users").child(uid).child("place").child(place_id).setValue(model);// 노드 생성 및 데이터 입력
                                        mPlace_id=place_id;
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            Intent intent = new Intent(getActivity(), PlaceDetailActivity.class);
                            intent.putExtra("place_id", place_id);
                            intent.putExtra("place_category", mPlace_category);
                            intent.putExtra("book_id", mBook_id);
                            startActivity(intent);
                        }
                    });


                }
            };

            mPlace_list.setAdapter(mPlaceAdapter);

            return rootView;
        }

        // 라이프사이클이 시작될 때 발생하는 이벤트
        @Override
        public void onStart() {
            super.onStart();
            if (mPlaceAdapter != null){
                mPlaceAdapter.startListening(); // 이거 해야 어댑터 목록 읽기 시작
            }
        }

        // 라이프사이클이 끝날 때 발생하는 이벤트
        @Override
        public void onStop() {
            super.onStop();
            if (mPlaceAdapter != null){
                mPlaceAdapter.stopListening(); // 이거 해야 어댑터 목록 안읽음
            }
        }

        public static FoodFragment newInstance(int sectionNumber) {
            FoodFragment fragment = new FoodFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }
    }

    public static class TravelFragment extends Fragment {
        private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance(); // 데이터베이스 선언 및 초기화
        private DatabaseReference mReference = mDatabase.getReference(); // 데이터베이스 값을 읽어오기 위한 레퍼런스 선언 및 초기화
        private FirebaseRecyclerAdapter<PlaceModel, PlaceListAdapter.ItemViewHolder> mPlaceAdapter;
        private final List<PlaceModel> mPlaceModels = new ArrayList<>();
        private RecyclerView mPlace_list;
        private String mPlace_root = "place";
        private String mPlace_category = "travel";
        float reviewCount;
        float totalrating;
        Integer click;
        private String mplace_imgUrl; // 여행지의 이미지 url
        private String mplace_title; // 여행지의 이름
        private String mplace_tag; // 여행지의 태그
        private Integer mplace_views; // 여행지의 조회수
        private float mplace_totalrating; //여행지 점수
        private float mplace_reviewCount; //리뷰 갯수
        private long kakaoEmail;
        private String uid;
        private String mPlace_id=null;
        private String place_category;
        private String post_id;
        private float reviewModelRating;

        public void setReviewCount(float count){
            this.reviewCount=count;
        }
        public TravelFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_place_travel, container, false);

            kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;

            if(kakaoEmail==0){
                uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
            }
            else{
                uid=String.valueOf(kakaoEmail);
            }

            mPlace_list = rootView.findViewById(R.id.travel_list);

            //어댑터랑 연결해서 리니어 레이아웃에 뿌림
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setStackFromEnd(true);
            layoutManager.setReverseLayout(true);
            mPlace_list.setLayoutManager(layoutManager);

            final Query query = mReference.child(mPlace_root).child(mPlace_category);
            FirebaseRecyclerOptions<PlaceModel> options = new FirebaseRecyclerOptions.Builder<PlaceModel>()
                    .setQuery(query, PlaceModel.class)
                    .build(); // 어떤 데이터를 어디서 가져올것이며 어떤 형태의 데이터 클래스의 결과를 반환할건지 리사이클러뷰 어댑터 옵션에 넣음

            FirebaseDatabase.getInstance().getReference().child(mPlace_root).child(mPlace_category).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mPlaceModels.clear();

                    for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        PlaceModel placeModel=snapshot.getValue(PlaceModel.class);
                        mPlaceModels.add(placeModel);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mPlaceAdapter = new FirebaseRecyclerAdapter<PlaceModel, PlaceListAdapter.ItemViewHolder>(options) {
                @NonNull
                @Override
                public PlaceListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.item_place, viewGroup, false);
                    return new PlaceListAdapter.ItemViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(PlaceListAdapter.ItemViewHolder holder, final int position, PlaceModel model) {
                    // 포지션 값을 받아오고 그 포지션 아이템의 키값을 저장함
                    final DatabaseReference placeRef = getRef(position);
                    final String place_id = placeRef.getKey();

                    click=model.getPlace_views();

                    totalrating =model.getPlace_totalrating();
                    reviewCount=model.getPlace_reviewCount();

                    if(Float.isNaN(reviewModelRating)){
                        reviewModelRating=0;
                    }else{
                        reviewModelRating=totalrating;
                    }


                    click=model.getPlace_views();
                    click++;
                    Glide.with(holder.itemView.getContext())
                            .load(mPlaceModels.get(position).getPlace_imgUrl())
                            .apply(new RequestOptions())
                            .into(holder.imageView);
                    holder.place_name.setText(mPlaceModels.get(position).getPlace_title());
                    holder.place_rating.setRating(totalrating/reviewCount);
                    if(Float.isNaN(totalrating/reviewCount)){
                        holder.PlaceRating.setText("0");
                    }else{
                        holder.PlaceRating.setText(String.valueOf(String.format("%.1f",totalrating/reviewCount)));
                    }
                    holder.place_tag.setText(mPlaceModels.get(position).getPlace_tag());
                    holder.place_views.setText(click.toString());

                    // 아이템 클릭 이벤트
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ((DatabaseReference) query).child(place_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    click=Integer.parseInt(dataSnapshot.child("place_views").getValue().toString());
                                    click++;
                                    final Map<String, Object> taskMap1 = new HashMap<String, Object>(); //업데이트 시킬 조회수
                                    taskMap1.put("place_views", click);
                                    ((DatabaseReference) query).child(place_id).updateChildren(taskMap1);

                                    reviewCount=Integer.parseInt(dataSnapshot.child("place_reviewCount").getValue().toString());
                                    reviewModelRating=Integer.parseInt(dataSnapshot.child("place_totalrating").getValue().toString());

                                    RecentTravel_Model model = new RecentTravel_Model(
                                            mplace_imgUrl=mPlaceModels.get(position).getPlace_imgUrl(),
                                            mplace_title =mPlaceModels.get(position).getPlace_title(),
                                            mplace_tag =mPlaceModels.get(position).getPlace_tag(),
                                            click,
                                            mplace_totalrating=reviewModelRating,
                                            mplace_reviewCount=reviewCount,
                                            place_category=mPlace_category,
                                            post_id=place_id
                                    );

                                    if(mPlace_id==null || mPlace_id!=place_id){
                                        mReference.child("users").child(uid).child("place").child(place_id).setValue(model);// 노드 생성 및 데이터 입력
                                        mPlace_id=place_id;
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            Intent intent = new Intent(getActivity(), PlaceDetailActivity.class);
                            intent.putExtra("place_id", place_id);
                            intent.putExtra("place_category", mPlace_category);
                            intent.putExtra("book_id", mBook_id);
                            startActivity(intent);
                        }
                    });

                }
            };

            mPlace_list.setAdapter(mPlaceAdapter);

            return rootView;
        }

        // 라이프사이클이 시작될 때 발생하는 이벤트
        @Override
        public void onStart() {
            super.onStart();
            if (mPlaceAdapter != null){
                mPlaceAdapter.startListening(); // 이거 해야 어댑터 목록 읽기 시작
            }
        }

        // 라이프사이클이 끝날 때 발생하는 이벤트
        @Override
        public void onStop() {
            super.onStop();
            if (mPlaceAdapter != null){
                mPlaceAdapter.stopListening(); // 이거 해야 어댑터 목록 안읽음
            }
        }

        public static TravelFragment newInstance(int sectionNumber) {
            TravelFragment fragment = new TravelFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }
    }

    public static class HotelFragment extends Fragment {
        private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance(); // 데이터베이스 선언 및 초기화
        private DatabaseReference mReference = mDatabase.getReference(); // 데이터베이스 값을 읽어오기 위한 레퍼런스 선언 및 초기화
        private FirebaseRecyclerAdapter<PlaceModel, PlaceListAdapter.ItemViewHolder> mPlaceAdapter;
        private final List<PlaceModel> mPlaceModels = new ArrayList<>();
        private RecyclerView mPlace_list;
        private String mPlace_root = "place";
        private String mPlace_category = "hotel";
        float reviewCount;
        float totalrating;
        Integer click;
        private String mplace_imgUrl; // 여행지의 이미지 url
        private String mplace_title; // 여행지의 이름
        private String mplace_tag; // 여행지의 태그
        private Integer mplace_views; // 여행지의 조회수
        private float mplace_totalrating; //여행지 점수
        private float mplace_reviewCount; //리뷰 갯수
        private long kakaoEmail;
        private String uid;
        private String place_category;
        private String mPlace_id=null;
        private String post_id;
        private float reviewModelRating;


        public void setReviewCount(float count){
            this.reviewCount=count;
        }

        public HotelFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_place_hotel, container, false);

            kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;

            if(kakaoEmail==0){
                uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
            }
            else{
                uid=String.valueOf(kakaoEmail);
            }

            mPlace_list = rootView.findViewById(R.id.hotel_list);

            //어댑터랑 연결해서 리니어 레이아웃에 뿌림
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setStackFromEnd(true);
            mPlace_list.setLayoutManager(layoutManager);

            final Query query = mReference.child(mPlace_root).child(mPlace_category);
            FirebaseRecyclerOptions<PlaceModel> options = new FirebaseRecyclerOptions.Builder<PlaceModel>()
                    .setQuery(query, PlaceModel.class)
                    .build(); // 어떤 데이터를 어디서 가져올것이며 어떤 형태의 데이터 클래스의 결과를 반환할건지 리사이클러뷰 어댑터 옵션에 넣음

            FirebaseDatabase.getInstance().getReference().child(mPlace_root).child(mPlace_category).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mPlaceModels.clear();

                    for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        PlaceModel placeModel=snapshot.getValue(PlaceModel.class);
                        mPlaceModels.add(placeModel);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mPlaceAdapter = new FirebaseRecyclerAdapter<PlaceModel, PlaceListAdapter.ItemViewHolder>(options) {
                @NonNull
                @Override
                public PlaceListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.item_place, viewGroup, false);
                    return new PlaceListAdapter.ItemViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(PlaceListAdapter.ItemViewHolder holder, final int position, final PlaceModel model) {
                    // 포지션 값을 받아오고 그 포지션 아이템의 키값을 저장함
                    final DatabaseReference placeRef = getRef(position);
                    final String place_id = placeRef.getKey();

                    click=model.getPlace_views();

                    totalrating =model.getPlace_totalrating();
                    reviewCount=model.getPlace_reviewCount();


                    if(Float.isNaN(reviewModelRating)){
                        reviewModelRating=0;
                    }else{
                        reviewModelRating=totalrating;
                    }

                    Glide.with(holder.itemView.getContext())
                            .load(mPlaceModels.get(position).getPlace_imgUrl())
                            .apply(new RequestOptions())
                            .into(holder.imageView);
                    holder.place_name.setText(mPlaceModels.get(position).getPlace_title());
                    holder.place_rating.setRating(totalrating/reviewCount);
                    if(Float.isNaN(totalrating/reviewCount)){
                        holder.PlaceRating.setText("0");
                    }else{
                        holder.PlaceRating.setText(String.valueOf(String.format("%.1f",totalrating/reviewCount)));
                    }
                    holder.place_tag.setText(mPlaceModels.get(position).getPlace_tag());
                    holder.place_views.setText(click.toString());

                    // 아이템 클릭 이벤트
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((DatabaseReference) query).child(place_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    click=Integer.parseInt(dataSnapshot.child("place_views").getValue().toString());
                                    click++;
                                    final Map<String, Object> taskMap1 = new HashMap<String, Object>(); //업데이트 시킬 조회수
                                    taskMap1.put("place_views", click);
                                    ((DatabaseReference) query).child(place_id).updateChildren(taskMap1);

                                    reviewCount=Integer.parseInt(dataSnapshot.child("place_reviewCount").getValue().toString());
                                    reviewModelRating=Integer.parseInt(dataSnapshot.child("place_totalrating").getValue().toString());

                                    RecentTravel_Model model = new RecentTravel_Model(
                                            mplace_imgUrl=mPlaceModels.get(position).getPlace_imgUrl(),
                                            mplace_title =mPlaceModels.get(position).getPlace_title(),
                                            mplace_tag =mPlaceModels.get(position).getPlace_tag(),
                                            click,
                                            mplace_totalrating=reviewModelRating,
                                            mplace_reviewCount=reviewCount,
                                            place_category=mPlace_category,
                                            post_id=place_id
                                    );

                                    if(mPlace_id==null || mPlace_id!=place_id){
                                        mReference.child("users").child(uid).child("place").child(place_id).setValue(model);// 노드 생성 및 데이터 입력
                                        mPlace_id=place_id;
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            Intent intent = new Intent(getActivity(), PlaceDetailActivity.class);
                            intent.putExtra("place_id", place_id);
                            intent.putExtra("place_category", mPlace_category);
                            intent.putExtra("book_id", mBook_id);
                            startActivity(intent);
                        }
                    });

                }
            };

            mPlace_list.setAdapter(mPlaceAdapter);

            return rootView;
        }

        // 라이프사이클이 시작될 때 발생하는 이벤트
        @Override
        public void onStart() {
            super.onStart();
            if (mPlaceAdapter != null){
                mPlaceAdapter.startListening(); // 이거 해야 어댑터 목록 읽기 시작
            }
        }

        // 라이프사이클이 끝날 때 발생하는 이벤트
        @Override
        public void onStop() {
            super.onStop();
            if (mPlaceAdapter != null){
                mPlaceAdapter.stopListening(); // 이거 해야 어댑터 목록 안읽음
            }
        }

        public static HotelFragment newInstance(int sectionNumber) {
            HotelFragment fragment = new HotelFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }
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
                    return FoodFragment.newInstance(position);
                case 1:
                    return TravelFragment.newInstance(position);
                case 2:
                    return HotelFragment.newInstance(position);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
