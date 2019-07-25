package com.likeonline.travelmaker.travelbook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.likeonline.travelmaker.R;

import java.util.HashMap;
import java.util.Map;

public class TravelBookViewActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance(); // 데이터베이스 선언 및 초기화
    private DatabaseReference mReference = mDatabase.getReference(); // 데이터베이스 값을 읽어오기 위한 레퍼런스 선언 및 초기화
    private FirebaseAuth mFirebase_Auth = FirebaseAuth.getInstance(); // 파이어베이스 계정 값 초기화
    private RecyclerView mRecyclerView;

    private String mUser_id;

    // 어댑터 선언
    public FirebaseRecyclerAdapter<BookModel, BookListHolder> mBookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travek_book_view);

        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        String splash_background = firebaseRemoteConfig.getString("splash_background");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.travelbook_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.button_back);

        mRecyclerView = findViewById(R.id.travelbook_list_view);
        mUser_id = mFirebase_Auth.getCurrentUser().getUid();

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = 10;
                outRect.left = 10;
            }
        });

        final DatabaseReference query =  mReference.child("users").child(mUser_id).child("book");
        final FirebaseRecyclerOptions<BookModel> options = new FirebaseRecyclerOptions.Builder<BookModel>()
                .setQuery(query, BookModel.class)
                .build(); // 어떤 데이터를 어디서 가져올것이며 어떤 형태의 데이터 클래스의 결과를 반환할건지 리사이클러뷰 어댑터 옵션에 넣음

        //어댑터랑 연결해서 그리드 레이아웃에 뿌림
        mBookAdapter = new FirebaseRecyclerAdapter<BookModel, BookListHolder>(options) {

            @NonNull
            @Override
            public BookListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_book, viewGroup, false);
                return new BookListHolder(view);
            }


            @Override
            protected void onBindViewHolder(final BookListHolder holder, final int position, final BookModel model) {
                // 포지션 값을 받아오고 그 포지션 아이템의 키값을 저장함

                // 아이템 클릭 이벤트
                holder.book_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(model.isBook_private()) {
                            Toast.makeText(TravelBookViewActivity.this, "비공개로 설정된 트래블북은 첨부할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TravelBookViewActivity.this);
                            // 제목셋팅
                            alertDialogBuilder.setTitle("트래블 북 첨부");
                            // AlertDialog 셋팅
                            alertDialogBuilder
                                    .setMessage(model.getBook_title() + " 트래블 북을 게시글에 첨부하시겠습니까?")
                                    .setCancelable(false)
                                    .setNegativeButton("예",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // post 데이터에 트래블 북 아이디값 추가
                                                   Intent resultIntent = new Intent();
                                                   resultIntent.putExtra("book_id", getRef(position).getKey());
                                                   resultIntent.putExtra("book_title", model.getBook_title());
                                                   setResult(RESULT_OK, resultIntent);
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
                            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface arg0) {
                                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                                }
                            });
                            // 다이얼로그 보여주기
                            alertDialog.show();
                        }
                    }
                });

                // 사실상 bind 함수를 여기서 다하는중
                holder.book_title.setText(model.getBook_title());

                if(model.isBook_private()) {
                    holder.book_image.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_travelbook_normal_protect));
                }
                /*if (model.getBook_category().equals("계획중")) {

                } else if (model.getBook_category().equals("여행중")) {

                } else if (model.getBook_category().equals("다녀옴")) {

                }*/
            }
        };
        mRecyclerView.setAdapter(mBookAdapter);
    }

    //메뉴 선택
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 메뉴 클릭
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    public static class BookListHolder extends RecyclerView.ViewHolder {
        TextView book_title;
        ImageView book_image;

        public BookListHolder(@NonNull View itemView) {
            super(itemView);
            book_title = itemView.findViewById(R.id.book_title);
            book_image = itemView.findViewById(R.id.book_image);
        }
    }
}
