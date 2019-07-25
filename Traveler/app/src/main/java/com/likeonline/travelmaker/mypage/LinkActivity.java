package com.likeonline.travelmaker.mypage;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.likeonline.travelmaker.LoginActivity;
import com.likeonline.travelmaker.R;
import com.likeonline.travelmaker.place.PlaceDetailActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkActivity extends AppCompatActivity {

    String splash_background;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance(); // 데이터베이스 선언 및 초기화
    private DatabaseReference mReference = mDatabase.getReference(); // 데이터베이스 값을 읽어오기 위한 레퍼런스 선언 및 초기화
    private FirebaseRecyclerAdapter<Link_Model, LinkAdapter.ItemViewHolder> mPlaceAdapter;
    private final List<Link_Model> mPlaceModels = new ArrayList<>();
    private RecyclerView mPlace_list;
    float totalrating;
    public float reviewCount; //리뷰 갯수
    private Integer click; //조회수;
    private String uid;
    private long kakaoEmail;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private String key; //플레이스 키값
    private String mPlace_id;
    private String mPlace_category;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_travel);

        kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;
        if(kakaoEmail==0) {
            uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            uid = String.valueOf(kakaoEmail);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.link_travel_recyclerview);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        ((LinearLayoutManager) mLayoutManager).setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setAdapter(mPlaceAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = firebaseRemoteConfig.getString("splash_background");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }

        //툴바 세팅
        Toolbar place_toolbar = findViewById(R.id.link_travel_toolbar);
        place_toolbar.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.samdan_mint));
        setSupportActionBar(place_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.button_back);

        final DatabaseReference query = mReference.child("users").child(uid).child("link");

        FirebaseRecyclerOptions<Link_Model> options = new FirebaseRecyclerOptions.Builder<Link_Model>()
                .setQuery(query, Link_Model.class)
                .build(); // 어떤 데이터를 어디서 가져올것이며 어떤 형태의 데이터 클래스의 결과를 반환할건지 리사이클러뷰 어댑터 옵션에 넣음

        //어댑터랑 연결해서 리니어 레이아웃에 뿌림

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("link").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPlaceModels.clear();

                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    Link_Model linkModel=snapshot.getValue(Link_Model.class);
                    mPlaceModels.add(linkModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mPlaceAdapter = new FirebaseRecyclerAdapter<Link_Model, LinkAdapter.ItemViewHolder>(options) {

            @NonNull
            @Override
            public LinkAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_place, viewGroup, false);
                return new LinkAdapter.ItemViewHolder(view);
            }


            @Override
            protected void onBindViewHolder( final LinkAdapter.ItemViewHolder holder, final int position,  Link_Model model) {
                final DatabaseReference postRef = getRef(position);
                final String travel_id = postRef.getKey();




                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot travelSnapshot:dataSnapshot.getChildren()){
                            key=travelSnapshot.getKey();
                        }
                        if(key==null){

                        }else{

                            query.child(travel_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    totalrating=Float.valueOf(dataSnapshot.child("place_totalrating").getValue().toString());
                                    reviewCount=Integer.parseInt(dataSnapshot.child("place_reviewCount").getValue().toString());
                                    click=Integer.parseInt(dataSnapshot.child("place_views").getValue().toString());

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


                // 아이템 클릭 이벤트
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        query.child(travel_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                mPlace_category=dataSnapshot.child("place_category").getValue().toString();
                                mPlace_id=dataSnapshot.child("post_id").getValue().toString();

                                ((DatabaseReference) query).child(travel_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        click=Integer.parseInt(dataSnapshot.child("place_views").getValue().toString());
                                        click++;
                                        final Map<String, Object> taskMap1 = new HashMap<String, Object>(); //업데이트 시킬 조회수
                                        taskMap1.put("place_views", click);
                                        ((DatabaseReference) query).child(travel_id).updateChildren(taskMap1);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                FirebaseDatabase.getInstance().getReference().child("place").child(mPlace_category).child(travel_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        click=Integer.parseInt(dataSnapshot.child("place_views").getValue().toString());
                                        click++;
                                        final Map<String, Object> taskMap1 = new HashMap<String, Object>(); //업데이트 시킬 조회수
                                        taskMap1.put("place_views", click);
                                        FirebaseDatabase.getInstance().getReference().child("place").child(mPlace_category).child(travel_id).updateChildren(taskMap1);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                Intent intent = new Intent(getApplicationContext(), PlaceDetailActivity.class);
                                intent.putExtra("place_id", travel_id);
                                intent.putExtra("place_category", mPlace_category);
                                startActivity(intent);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });
            }


        };

        mRecyclerView.setAdapter(mPlaceAdapter);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_travel_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 메뉴 클릭
                finish();
                return true;
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("알림");
                builder.setMessage("목록을 정리하시겠습니까?");
                builder.setPositiveButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"취소되었습니다.",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.setNegativeButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("link").removeValue();
                                mPlaceAdapter.startListening();
                            }
                        });
                builder.show();
                return true;
        }
        return false;
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

    public static class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.ItemViewHolder> {
        LinkActivity link = new LinkActivity();

        @NonNull
        @Override
        public LinkAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
            // return 인자는 ViewHolder 입니다.
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
            return new LinkAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            // RecyclerView의 총 개수 입니다.
            return link.mPlaceAdapter.getItemCount();
        }

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
}
