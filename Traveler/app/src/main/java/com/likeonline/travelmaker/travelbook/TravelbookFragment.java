package com.likeonline.travelmaker.travelbook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.likeonline.travelmaker.LoginActivity;
import com.likeonline.travelmaker.R;

import java.util.HashMap;
import java.util.Map;

public class TravelbookFragment extends Fragment {

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance(); // 데이터베이스 선언 및 초기화
    private DatabaseReference mReference = mDatabase.getReference(); // 데이터베이스 값을 읽어오기 위한 레퍼런스 선언 및 초기화
    private FirebaseAuth mFirebase_Auth = FirebaseAuth.getInstance(); // 파이어베이스 계정 값 초기화
    private RecyclerView mRecyclerView;

    private String mUser_id;
    private String mUser_name;
    private int mUser_score;
    private String mBook_title;
    private String mBook_category;
    private int mBook_like;
    private int mBook_views;
    private boolean mBook_private;

    private ImageButton mBook_createBtn;
    private ImageButton mBook_privateBtn;
    private ImageButton mBook_deleteBtn;

    private static String BookClickMode = "View";

    // 어댑터 선언
    public FirebaseRecyclerAdapter<BookModel, BookListHolder> mBookAdapter;

    public TravelbookFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_travelbook, container, false);

        mBook_createBtn = rootview.findViewById(R.id.book_create);
        mBook_privateBtn = rootview.findViewById(R.id.book_private);
        mBook_deleteBtn = rootview.findViewById(R.id.book_delete);

        mRecyclerView = rootview.findViewById(R.id.book_list);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = 85;
                outRect.left = 85;
            }
        });
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        long kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;
        if(kakaoEmail==0) {
            mUser_id = mFirebase_Auth.getInstance().getCurrentUser().getUid();
        } else {
            mUser_id= String.valueOf(kakaoEmail);
        }

        mReference.child("users").child(mUser_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user = dataSnapshot.child("userName").getValue().toString();
                mUser_score = Integer.parseInt(dataSnapshot.child("user_score").getValue().toString());
                mUser_name = user;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mBook_category = "계획중";
        mBook_like = 0;
        mBook_views = 0;
        mBook_private = false;

        mBook_createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final EditText book_title = new EditText(getContext());

                builder.setTitle("트래블 북 추가");
                builder.setMessage("제목을 입력해주세요");
                builder.setView(book_title);
                builder.setNegativeButton("입력",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mBook_title = book_title.getText().toString();
                                BookModel model = new BookModel(
                                        mBook_title,
                                        mBook_category,
                                        mBook_like,
                                        mBook_views,
                                        mBook_private);
                                mReference.child("users").child(mUser_id).child("book").push().setValue(model);
                            }
                        });
                builder.setPositiveButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                    }
                });
                alert.show();
            }
        });

        // 보호 버튼 클릭 이벤트
        mBook_privateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "공개 설정을 변경할 트래블 북을 선택해주세요.", Toast.LENGTH_SHORT).show();
                BookClickMode = "Private";
            }
        });

        // 삭제 버튼 클릭 이벤트
        mBook_deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "삭제할 트래블 북을 선택해주세요.", Toast.LENGTH_SHORT).show();
                BookClickMode = "Delete";
            }
        });

        final DatabaseReference query = mReference.child("users").child(mUser_id).child("book");
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
                // 아이템 클릭 이벤트
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (BookClickMode) {
                            case "View":
                                Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                                intent.putExtra("book_id", getRef(position).getKey());
                                startActivity(intent);
                                break;
                            case "Private":
                                if (model.isBook_private()) {
                                    final Map<String, Object> taskMap = new HashMap<String, Object>();//프로필 이름
                                    taskMap.put("book_private", false);
                                    mReference.child("users").child(mUser_id).child("book").child(getRef(position).getKey()).updateChildren(taskMap);
                                    holder.book_image.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_travelbook_normal));
                                    Toast.makeText(getContext(), "해당 트래블북은 마이페이지에서 공개로\n                    설정되었습니다.", Toast.LENGTH_LONG).show();
                                    BookClickMode = "View";
                                    break;
                                } else {
                                    final Map<String, Object> taskMap = new HashMap<String, Object>();//프로필 이름
                                    taskMap.put("book_private", true);
                                    mReference.child("users").child(mUser_id).child("book").child(getRef(position).getKey()).updateChildren(taskMap);
                                    holder.book_image.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_travelbook_normal_protect));
                                    Toast.makeText(getContext(), "해당 트래블북은 마이페이지에서 비공개로\n                    설정되었습니다.", Toast.LENGTH_LONG).show();
                                    BookClickMode = "View";
                                    break;
                                }
                            case "Delete":
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                // 제목셋팅
                                alertDialogBuilder.setTitle("트래블 북 삭제");
                                // AlertDialog 셋팅
                                alertDialogBuilder
                                        .setMessage("정말로 트래블 북을 삭제하시겠습니까?")
                                        .setCancelable(false)
                                        .setNegativeButton("예",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // 게시글 삭제
                                                        mReference.child("users").child(mUser_id).child("book").child(getRef(position).getKey()).removeValue();
                                                        mUser_score-=3;
                                                        final Map<String, Object> taskMap = new HashMap<String, Object>();
                                                        taskMap.put("user_score", mUser_score);
                                                        FirebaseDatabase.getInstance().getReference().child("users").child(mUser_id).updateChildren(taskMap);
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
                                BookClickMode = "View";
                                break;
                        }
                    }
                });

                // 사실상 bind 함수를 여기서 다하는중
                holder.book_title.setText(model.getBook_title());

                if (model.isBook_private()) {
                    holder.book_image.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_travelbook_normal_protect));
                } else {
                    holder.book_image.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_travelbook_normal));
                }
                /*if (model.getBook_category().equals("계획중")) {

                } else if (model.getBook_category().equals("여행중")) {

                } else if (model.getBook_category().equals("다녀옴")) {

                }*/
            }
        };

        mRecyclerView.setAdapter(mBookAdapter);

        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mBookAdapter != null) {
            mBookAdapter.startListening(); // 이거 해야 어댑터 목록 읽기 시작
        }
    }

    // 액티비티의 라이프사이클이 끝날 때 발생하는 이벤트
    @Override
    public void onStop() {
        super.onStop();
        if (mBookAdapter != null) {
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
