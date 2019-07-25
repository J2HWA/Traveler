package com.likeonline.travelmaker.place;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.likeonline.travelmaker.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlaceCreateActivity extends AppCompatActivity {
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReference = mDatabase.getReference(); // 데이터베이스 사용을 위한 선언

    String splash_background;
    private FirebaseRemoteConfig firebaseRemoteConfig;

    private static final int PICK_FROM_ALBUM1 = 10;
    Uri imageURI1;
    CircleImageView profile1;
    // private String mPlace_id; // 여행지를 구분하기 위한 아이디
    private Spinner mPlace_area; // 어느지역의 여행지인지 구분
    private Spinner mPlace_category;
    private ImageView mPlace_img; // 여행지의 이미지
    private Uri mPlace_imgUri; // 여행지의 이미지 uri
    private TextView mPlace_title; // 여행지의 이름
    private RatingBar mPlace_rating; // 여행지의 점수
    private TextView mPlace_tag; // 여행지의 태그
    private Integer mPlace_views; // 여행지의 조회수
    // 여기부턴 detail에서 필요한 정보들입니다.
    private TextView mPlace_content;
    private TextView mPlace_address; // 여행지의 주소
    private TextView mPlace_tel; // 여행지의 연락처
    private TextView mPlace_price; // 여행지의 가격
    private TextView mPlace_park; // 여행지의 주차가능 여부
    private TextView mPlace_wedo; // 여행지의 지도 위도
    private TextView mPlace_gyoungdo; // 여행지의 지도 경도
    private float ratingScore; //토탈 리뷰 평점
    private TextView mPlace_total_rating;
    private float reviewCount; //리뷰 갯수
    private Boolean data = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_create);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.placecreate_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = firebaseRemoteConfig.getString("splash_background");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }

        mPlace_area = findViewById(R.id.placecreate_area);
        mPlace_category = findViewById(R.id.placecreate_category);
        mPlace_img = findViewById(R.id.placecreate_dogimage);
        mPlace_title = findViewById(R.id.placecreate_title);
        mPlace_rating = findViewById(R.id.placecreate_rating);
        mPlace_tag = findViewById(R.id.placecreate_tag);
        mPlace_views = 0; // 임시 값

        mPlace_content = findViewById(R.id.placecreate_content);
        mPlace_address = findViewById(R.id.placecreate_address);
        mPlace_tel = findViewById(R.id.placecreate_tel);
        mPlace_price = findViewById(R.id.placecreate_price);
        mPlace_park = findViewById(R.id.placecreate_park);
        mPlace_wedo = findViewById(R.id.placecreate_wedo);
        mPlace_gyoungdo = findViewById(R.id.placecreate_gyoungdo);
        mPlace_total_rating=findViewById(R.id.placecreate_totalRating);

        reviewCount=0;

        mPlace_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK); //사진을 가져옴
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM1);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_FROM_ALBUM1 && resultCode == RESULT_OK) {
            mPlace_img.setImageURI(data.getData());//가운데 뷰를 바꿈
            this.data = true;
            imageURI1 = data.getData();//이미지경로원본
        }
    }

    //메뉴 연결
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_create, menu);
        return true;
    }

    //메뉴 선택
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: // 뒤로가기 메뉴 클릭
                finish();
                return true;
            case R.id.action_posting: // 글작성 메뉴 클릭 이때 모든 정보들이 데이터베이스로 들어가야함
                final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("place_imgUrl");
                try {
                    profileImageRef.putFile(imageURI1).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return profileImageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            mPlace_imgUri = task.getResult();
                            String category = mPlace_category.getSelectedItem().toString();
                            if (task.isSuccessful()) {
                                PlaceModel model = new PlaceModel(
                                        mPlace_area.getSelectedItem().toString(),
                                        mPlace_imgUri.toString(), // 임시 값
                                        mPlace_title.getText().toString(),
                                        0, // 임시 값
                                        mPlace_tag.getText().toString(),
                                        mPlace_views,
                                        mPlace_content.getText().toString(),
                                        mPlace_address.getText().toString(),
                                        mPlace_tel.getText().toString(),
                                        mPlace_price.getText().toString(),
                                        mPlace_park.getText().toString(),
                                        Double.parseDouble(mPlace_wedo.getText().toString()),
                                        Double.parseDouble(mPlace_gyoungdo.getText().toString()),
                                        Float.valueOf(mPlace_total_rating.getText().toString()),
                                        reviewCount
                                );
                                mReference.child("place").child(category).push().setValue(model); // 노드 생성 및 데이터 입력

                                Toast.makeText(PlaceCreateActivity.this, "여행지가 생성되었습니다", Toast.LENGTH_SHORT).show();
                                PlaceCreateActivity.this.finish();
                            }
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(this, "뭐 하나 안넣으셨음 에러코드 : " + e, Toast.LENGTH_SHORT).show();
                }
        }

            /*    FoodModel model = new FoodModel(
                        mPlace_area.getSelectedItem().toString(),
                        mPlace_category.getSelectedItem().toString(),
                        "value", // 임시 값
                        mPlace_title.getText().toString(),
                        0, // 임시 값
                        mPlace_tag.getText().toString(),
                        mPlace_views,
                        mPlace_address.getText().toString(),
                        mPlace_tel.getText().toString()
                );
                mReference.child("place").push().setValue(model); // 노드 생성 및 데이터 입력
                Toast.makeText(this, "여행지가 생성되었습니다", Toast.LENGTH_SHORT).show();
                finish();
                return true;*/
        return super.onOptionsItemSelected(item);
    }
}