package com.likeonline.travelmaker.mypage;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.likeonline.travelmaker.model.ReportModel;

public class gradeReportActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String uid;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    public FirebaseRecyclerAdapter<ReportModel, ReportListAdapter.ItemViewHolder> mReportAdapter;
    private long kakaoEmail;
    private String key;
    private String report; //이력
    String splash_background;
    private TextView textView; //신뢰도점수와 등급점수 표시할곳
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_report);

        Toolbar report_toolbar =findViewById(R.id.report_toolbar);
        setSupportActionBar(report_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.button_back);


        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = firebaseRemoteConfig.getString(getString(R.string.rc_color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }

        textView=(TextView) findViewById(R.id.grade_text);

        kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;

        database=FirebaseDatabase.getInstance();
        myRef=database.getReference();

        if(kakaoEmail==0) {
            uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            uid = String.valueOf(kakaoEmail);
        }

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user_score=dataSnapshot.child("user_score").getValue().toString();
                Float user_trustCount=Float.parseFloat(dataSnapshot.child("user_trustCount").getValue().toString());
                Float user_trustScore=Float.parseFloat(dataSnapshot.child("user_trustScore").getValue().toString());
                if(Float.isNaN(user_trustScore/user_trustCount)){
                    textView.setText("등급 점수: "+user_score+"점 / "+"평점: "+0);
                }else{
                    textView.setText("등급 점수: "+user_score+"점 / "+"평점: "+String.valueOf(String.format("%.1f",user_trustScore/user_trustCount)));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.report_postlist);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        ((LinearLayoutManager) mLayoutManager).setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setAdapter(mReportAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        final DatabaseReference query =  myRef.child("users").child(uid).child("report");

        FirebaseRecyclerOptions<ReportModel> options = new FirebaseRecyclerOptions.Builder<ReportModel>()
                .setQuery(query, ReportModel.class)
                .build(); // 어떤 데이터를 어디서 가져올것이며 어떤 형태의 데이터 클래스의 결과를 반환할건지 리사이클러뷰 어댑터 옵션에 넣음

        //어댑터랑 연결해서 리니어 레이아웃에 뿌림

        mReportAdapter = new FirebaseRecyclerAdapter<ReportModel, ReportListAdapter.ItemViewHolder>(options) {


            @NonNull
            @Override
            public  ReportListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_grade_report, viewGroup, false);
                return new  ReportListAdapter.ItemViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ReportListAdapter.ItemViewHolder holder, int position, @NonNull final ReportModel model) {
                // 사실상 bind 함수를 여기서 다하는중
                //holder.textView.setText("라라라");
                holder.textView.setText(model.getReport());
            }

        };

        mRecyclerView.setAdapter(mReportAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mReportAdapter != null){
            mReportAdapter.startListening(); // 이거 해야 어댑터 목록 읽기 시작
        }
    }

    // 액티비티의 라이프사이클이 끝날 때 발생하는 이벤트
    @Override
    public void onStop() {
        super.onStop();
        if (mReportAdapter != null){
            mReportAdapter.stopListening(); // 이거 해야 어댑터 목록 안읽음
        }
    }

    public static class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ItemViewHolder> {

       com.likeonline.travelmaker.mypage.gradeReportActivity gradeReportActivity=new gradeReportActivity();
        @NonNull
        @Override
        public ReportListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
            // return 인자는 ViewHolder 입니다.
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade_report, parent, false);
            return new ReportListAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {

        }


        @Override
        public int getItemCount() {
            // RecyclerView의 총 개수 입니다.
            return gradeReportActivity.mReportAdapter.getItemCount();
        }

        public static class ItemViewHolder extends RecyclerView.ViewHolder {
                TextView textView;
            ItemViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.report_text);
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
