package com.likeonline.travelmaker.board;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.likeonline.travelmaker.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Board_SearchActivity extends AppCompatActivity {

    // DB관련 선언 및 초기화
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance(); // 데이터베이스 선언 및 초기화
    private DatabaseReference mReference = mDatabase.getReference(); // 데이터베이스 값을 읽어오기 위한 레퍼런스 선언 및 초기화
    private FirebaseAuth mFirebase_Auth = FirebaseAuth.getInstance();; // 파이어베이스 계정 값 초기화
    private FirebaseUser mFirebase_User = mFirebase_Auth.getCurrentUser(); // 파이어베이스 유저 값 초기화

    // 어댑터 선언
    public FirebaseRecyclerAdapter<PostModel, PostListAdapter.ItemViewHolder> mPostAdapter;

    //String uid = mFirebase_User.getUid();
    //Query query = myRef.child("post").child(uid).orderByChild("post_title");
    /*public List<String> post_profileUrl;
    public List<String> post_title;
    public List<String> post_traveling;
    public List<String> post_tag;
    public List<String> post_views;
    public List<String> post_writer;
    public List<String> post_postingTime;*/
    private TextView search;
    private List<PostModel> items;
    private ArrayList<PostModel> arrayList;
    private ArrayList<PostModel> items1 = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_search);

        search=(TextView) findViewById(R.id.board_search);
        //툴바 세팅코드
        Toolbar board_toolbar = findViewById(R.id.board_searchtoolbar);
        setSupportActionBar(board_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.button_back);

        //데이터를 받아옴
        getData();

        /*search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = search.getText().toString().toLowerCase(Locale.getDefault());
                filter(text);

            }
        });*/
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

    private void getData() {
        RecyclerView board_postlist = findViewById(R.id.board_postlist);

        Query query = mReference.child("post");
        final FirebaseRecyclerOptions<PostModel> options = new FirebaseRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class)
                .build(); // 어떤 데이터를 어디서 가져올것이며 어떤 형태의 데이터 클래스의 결과를 반환할건지 리사이클러뷰 어댑터 옵션에 넣음

        //어댑터랑 연결해서 리니어 레이아웃에 뿌림
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        board_postlist.setLayoutManager(layoutManager);

        mPostAdapter = new FirebaseRecyclerAdapter<PostModel, PostListAdapter.ItemViewHolder>(options) {

            @NonNull
            @Override
            public PostListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_post, viewGroup, false);
                return new PostListAdapter.ItemViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(PostListAdapter.ItemViewHolder holder, int position, PostModel model) {
                // 포지션 값을 받아오고 그 포지션 아이템의 키값을 저장함
                final DatabaseReference postRef = getRef(position);
                final String post_id = postRef.getKey();

                /*items.add(mPostAdapter.getItem(position));
                arrayList= new ArrayList<PostModel>();
                arrayList.addAll(items);*/


                //Toast.makeText(Board_SearchActivity.this, items.toString(), Toast.LENGTH_SHORT).show();
                // 아이템 클릭 이벤트
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), PostDetailActivity.class);
                        intent.putExtra("post_id", post_id);
                        startActivity(intent);

                    }
                });

                // 사실상 bind 함수를 여기서 다하는중
                holder.post_title.setText(model.getPost_title());
                if (model.getUser_profileUrl() == null) {
                    holder.post_profile.setImageDrawable(ContextCompat.getDrawable(Board_SearchActivity.this,
                            R.drawable.mainicon));
                } else {
                    Glide.with(Board_SearchActivity.this)
                            .load(model.getUser_profileUrl())
                            .into(holder.post_profile);
                }
                if (model.getPost_traveling().equals("여행중")) {
                    holder.post_traveling.setImageDrawable(ContextCompat.getDrawable(Board_SearchActivity.this, R.drawable.traveling));
                }
                else {
                    holder.post_traveling.setImageDrawable(ContextCompat.getDrawable(Board_SearchActivity.this, R.drawable.traveling_yet));
                }
                holder.post_tag.setText(model.getPost_tag());
                //holder.post_views.setText(model.getPost_views());
                holder.post_writer.setText(model.getUser_name());
                holder.post_postingtime.setText(model.getPost_postingtime());

            }

        };
        board_postlist.setAdapter(mPostAdapter);
        //어따 쓰는지 모름
        /*mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();*/
    }

    /*public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        items.clear();
        if (charText.length() == 0) {
            items.addAll(arrayList);
        } else {
            for (PostModel recent : arrayList) {
                String name = recent.getPost_title();
                if (name.toLowerCase().contains(charText)) {
                    items.add(recent);
                }
            }
        }
        mPostAdapter.notifyDataSetChanged();
    }*/

    // 액티비티의 라이프사이클이 시작될 때 발생하는 이벤트
    @Override
    protected void onStart() {
        super.onStart();
        if (mPostAdapter != null){
            mPostAdapter.startListening(); // 이거 해야 어댑터 목록 읽기 시작
        }
    }

    // 액티비티의 라이프사이클이 끝날 때 발생하는 이벤트
    @Override
    protected void onStop() {
        super.onStop();
        if (mPostAdapter != null){
            mPostAdapter.stopListening(); // 이거 해야 어댑터 목록 안읽음
        }
    }



    public static class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ItemViewHolder> {

        Board_SearchActivity board = new Board_SearchActivity();


        @NonNull
        @Override
        public PostListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
            // return 인자는 ViewHolder 입니다.
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            return new PostListAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostListAdapter.ItemViewHolder holder, final int position) {
            // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
            /*holder.onBind(postmodels.get(position));

            PostListAdapter.ItemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    context.startActivity(intent);
                }
            });*/
        }

        @Override
        public int getItemCount() {
            // RecyclerView의 총 개수 입니다.
            return board.mPostAdapter.getItemCount();
        }

        /*void addItem(PostModel postModel) {
            // 외부에서 item을 추가시킬 함수입니다.
            postmodels.add(postModel);
        }*/

        // RecyclerView의 핵심인 ViewHolder 입니다.
        // 여기서 subView를 setting 해줍니다.
        public static class ItemViewHolder extends RecyclerView.ViewHolder {
            ImageView post_profile;
            ImageView post_traveling;
            TextView post_title;
            TextView post_tag;
            TextView post_views;
            TextView post_writer;
            TextView post_postingtime;

            ItemViewHolder(View itemView) {
                super(itemView);
                post_profile = itemView.findViewById(R.id.postitem_profile);
                post_title = itemView.findViewById(R.id.postitem_title);
                post_traveling = itemView.findViewById(R.id.postitem_traveling);
                post_tag = itemView.findViewById(R.id.postitem_tag);
                //post_views = itemView.findViewById(R.id.postitem_views);
                post_writer = itemView.findViewById(R.id.postitem_writer);
                post_postingtime = itemView.findViewById(R.id.postitem_postingtime);
            }

            /*void onBind(PostModel post) {
                post_profile.setImageURI(null);
                post_title.setText(post.getPost_title());
                post_tag.setText(post.getPost_tag());
                post_views.setText(post.getPost_views());
                post_writer.setText(post.getUser_name());
                post_postingtime.setText(post.getPost_postingtime());
            }*/
        }
    }

}
