package com.likeonline.travelmaker.travelbook;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.likeonline.travelmaker.LoginActivity;
import com.likeonline.travelmaker.MyAppGlideModule;
import com.likeonline.travelmaker.R;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class BookAlbumFragment extends Fragment {

    Button albumBtn;
    private static final int PICK_FROM_ALBUM1 = 10;
    ArrayList < String > mArrayUri;
    public RequestManager mGlideRequestManager=null;
    MyAppGlideModule myAppGlideModule=new MyAppGlideModule();
    private static final int REQUEST_CODE = 123;
    private ArrayList<String> mResults = new ArrayList<>();

    int   PICK_IMAGE_MULTIPLE   =   1 ;
    String   imageEncoded ;
    List < String >   imagesEncodedList ;
    private GridView gvGallery ;
    private GalleryAdapter galleryAdapter ;

    // private String album;

    private long kakaoEmail;
    private String mUser_id;
    private static String mYour_id;
    private FirebaseAuth mFirebase_Auth;
    private ImageView imageView;
    private String key;
    private ArrayList <String> data;
    private ArrayList <String> data1;
    private ArrayList <String> data2;
    String picture;
    private static String mBook_id;
    static ProgressDialog progressDialog;
    static Boolean check=false;
    static Boolean check2=false;
    static Integer size1;
    static Integer size2;

    public void setmBook_id2(String mBook_id) {
        this.mBook_id = mBook_id;
    }
    public void setmYour_id(String mYour_id) {
        this.mYour_id = mYour_id;
    }

    public BookAlbumFragment() {
        // Required empty public constructor
    }

    public static BookAlbumFragment newInstance(int sectionNumber) {
        BookAlbumFragment fragment = new BookAlbumFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup View=(ViewGroup)inflater.inflate(R.layout.fragment_book_album, container, false);
        kakaoEmail=((LoginActivity) LoginActivity.context).kakaoEmail;
        Fresco.initialize(getContext());
        imageView=(ImageView) View.findViewById(R.id.album_imagae);
        gvGallery   =   ( GridView ) View.findViewById(R.id.bookalbum_grid);

        progressDialog = new ProgressDialog(getContext());

        if(kakaoEmail==0) {
            mUser_id = mFirebase_Auth.getInstance().getCurrentUser().getUid();
        } else {
            mUser_id= String.valueOf(kakaoEmail);
        }

/*
        gvGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            }

        });
*/

        data = new ArrayList<String>();
        data1= new ArrayList<String>();
        data2= new ArrayList<String>();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        if(mYour_id != null) {
            myRef = myRef.child("users").child(mYour_id).child("book").child(mBook_id).child("album");
        } else {
            myRef = myRef.child("users").child(mUser_id).child("book").child(mBook_id).child("album");
        }
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot albumshot:dataSnapshot.getChildren()){
                    picture=albumshot.child("album").getValue().toString();
                    data.add(picture);
                }
                galleryAdapter   =   new GalleryAdapter( getContext() , data) ;
                gvGallery . setAdapter ( galleryAdapter ) ;
                gvGallery . setVerticalSpacing ( gvGallery . getHorizontalSpacing ( ) ) ;
                ViewGroup . MarginLayoutParams  mlp   =   ( ViewGroup . MarginLayoutParams )   gvGallery
                        . getLayoutParams ( ) ;
                mlp . setMargins ( 0 ,   gvGallery . getHorizontalSpacing ( ) ,   0 ,   0 ) ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        albumBtn=View.findViewById(R.id.albumbtn);

        if (mYour_id != null) {
            if(!mYour_id.equals(mUser_id)) {
                albumBtn.setVisibility(View.GONE);
            }
        }

        albumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                Intent intent = new Intent () ;
                intent.setType ( "image/*" ) ;
                intent.putExtra (Intent.EXTRA_ALLOW_MULTIPLE ,   true) ;
                intent.setAction (Intent.ACTION_GET_CONTENT) ;
                startActivityForResult(Intent.createChooser(intent , "Select Picture" ) , PICK_IMAGE_MULTIPLE ) ;


            }
        });

        return View;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Toast.makeText(getContext(), "onActivityResult", Toast.LENGTH_SHORT).show();
        check2=false;

        try   {
            // When an Image is picked
            if   ( requestCode   ==   PICK_IMAGE_MULTIPLE   &&   resultCode   ==   RESULT_OK
                    &&   null   !=   data )   {
                // Get the Image from data
                progressDialog.setMessage("업로드 중입니다..");
                progressDialog.show();
                final String [ ]   filePathColumn   =   {   MediaStore . Images . Media . DATA   } ;
                imagesEncodedList   =   new   ArrayList < String > ( ) ;
                if ( data . getData ( ) != null ) {

                    final Uri  mImageUri = data . getData ( ) ;
                    final StorageReference albumImageRef = FirebaseStorage.getInstance().getReference().child("userAlbum");

                    albumImageRef.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return albumImageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            AlbumModel albumModel=new AlbumModel();
                            Uri downUri = task.getResult();
                            String imageUri1 = downUri.toString();
                            albumModel.album=imageUri1;
                            FirebaseDatabase.getInstance().getReference().child("users").child(mUser_id).child("book").child(mBook_id).child("album").push().setValue(albumModel);

                            FirebaseDatabase.getInstance().getReference().child("users").child(mUser_id).child("book").child(mBook_id).child("album").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    data1.clear();
                                    for(DataSnapshot albumshot:dataSnapshot.getChildren()){
                                        picture=albumshot.child("album").getValue().toString();
                                        data1.add(picture);

                                    }
                                    size1=data1.size();
                                    galleryAdapter   =   new GalleryAdapter( getContext() , data1) ;
                                    gvGallery . setAdapter ( galleryAdapter ) ;
                                    gvGallery . setVerticalSpacing ( gvGallery . getHorizontalSpacing ( ) ) ;
                                    ViewGroup . MarginLayoutParams  mlp   =   ( ViewGroup . MarginLayoutParams )   gvGallery
                                            . getLayoutParams ( ) ;
                                    mlp . setMargins ( 0 ,   gvGallery . getHorizontalSpacing ( ) ,   0 ,   0 ) ;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        /*    // Get the cursor
                            Cursor  cursor =getActivity().getContentResolver(). query (mImageUri,
                                    filePathColumn ,   null ,   null ,   null ) ;
                            // Move to first row
                            cursor . moveToFirst ( ) ;

                            int   columnIndex   =   cursor . getColumnIndex ( filePathColumn [ 0 ] ) ;
                            imageEncoded    =   cursor . getString ( columnIndex ) ;
                            cursor . close ( ) ;

                            mArrayUri   =   new   ArrayList < String > ( ) ;
                            mArrayUri . add ( imageUri1 ) ;
                            galleryAdapter   =   new GalleryAdapter( getContext() , mArrayUri ) ;
                            galleryAdapter.notifyDataSetChanged();
                            gvGallery . setAdapter ( galleryAdapter ) ;
                            gvGallery . setVerticalSpacing ( gvGallery . getHorizontalSpacing ( ) ) ;
                            ViewGroup . MarginLayoutParams  mlp   =   ( ViewGroup . MarginLayoutParams )   gvGallery
                                    . getLayoutParams ( ) ;
                            mlp . setMargins ( 0 ,   gvGallery . getHorizontalSpacing ( ) ,   0 ,   0 ) ;*/

                        }
                    });


                }   else   {
                    if   ( data . getClipData ( )   !=   null )   {
                        ClipData  mClipData   =   data . getClipData ( ) ;
                        mArrayUri   =   new   ArrayList < String > ( ) ;
                        final AlbumModel albumModel=new AlbumModel();
                        for   ( int   i   =   0 ;   i   <   mClipData . getItemCount ( ) ;   i ++ )   {
                            ClipData. Item  item   =   mClipData . getItemAt ( i ) ;
                            final Uri  uri   =   item . getUri ( ) ;
                            final StorageReference albumImageRef = FirebaseStorage.getInstance().getReference().child("userAlbum").child(uri.toString());
                            albumImageRef.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    return albumImageRef.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri downUri = task.getResult();
                                    String imageUri1 = downUri.toString();
                                    albumModel.album=imageUri1;
                                    FirebaseDatabase.getInstance().getReference().child("users").child(mUser_id).child("book").child(mBook_id).child("album").push().setValue(albumModel);

                                  /*  mArrayUri . add ( imageUri1 ) ;
                                    // Get the cursor
                                    Cursor cursor   =   getActivity().getContentResolver(). query (uri,   filePathColumn ,   null ,   null ,   null ) ;
                                    // Move to first row
                                     cursor . moveToFirst ( ) ;

                                    int   columnIndex   =   cursor . getColumnIndex ( filePathColumn [ 0 ] ) ;
                                    imageEncoded    =   cursor . getString ( columnIndex ) ;
                                    imagesEncodedList . add ( imageEncoded ) ;
                                    cursor . close ( ) ;

                                    galleryAdapter   =   new GalleryAdapter( getContext(), mArrayUri ) ;
                                    galleryAdapter.notifyDataSetChanged();
                                    gvGallery . setAdapter ( galleryAdapter ) ;
                                    gvGallery . setVerticalSpacing ( gvGallery . getHorizontalSpacing ( ) ) ;
                                    ViewGroup . MarginLayoutParams  mlp   =   ( ViewGroup . MarginLayoutParams )   gvGallery
                                            . getLayoutParams ( ) ;
                                    mlp . setMargins ( 0 ,   gvGallery . getHorizontalSpacing ( ) ,   0 ,   0 ) ;*/

                                }
                            });

                        }

                        FirebaseDatabase.getInstance().getReference().child("users").child(mUser_id).child("book").child(mBook_id).child("album").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                data1.clear();
                                for(DataSnapshot albumshot:dataSnapshot.getChildren()){
                                    picture=albumshot.child("album").getValue().toString();
                                    data1.add(picture);

                                }
                                size1=data1.size();
                                galleryAdapter   =   new GalleryAdapter( getContext() , data1) ;
                                gvGallery . setAdapter ( galleryAdapter ) ;
                                gvGallery . setVerticalSpacing ( gvGallery . getHorizontalSpacing ( ) ) ;
                                ViewGroup . MarginLayoutParams  mlp   =   ( ViewGroup . MarginLayoutParams )   gvGallery
                                        . getLayoutParams ( ) ;
                                mlp . setMargins ( 0 ,   gvGallery . getHorizontalSpacing ( ) ,   0 ,   0 ) ;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        Log. v ( "LOG_TAG" ,   "Selected Images"   +   mArrayUri . size ( ) ) ;
                    }
                }
            }   else   {
                //Toast. makeText ( getContext() ,   "You haven't picked Image" , Toast . LENGTH_LONG ) . show ( ) ;
            }
        }   catch   ( Exception   e )   {
            Toast . makeText ( getContext() ,   "Something went wrong" ,   Toast . LENGTH_LONG )
                    . show ( ) ;
        }

        check2=true;
        super . onActivityResult ( requestCode ,   resultCode ,   data ) ;

    }

    private void doWork() {
        progressDialog = ProgressDialog.show(getContext(), null, "업로드 중입니다...");

        Thread thread = new Thread(new Runnable() {
            public void run() {
                // 1. 필요한 작업
               /* if(check2==true) {
                    handler.sendEmptyMessage(0);
                    check2=false;
                }*/

            }
        });
        thread.start();
    }

    @Override
    public void onResume() {
        if(check2==true) {

            FirebaseDatabase.getInstance().getReference().child("users").child(mUser_id).child("book").child(mBook_id).child("album").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    data2.clear();
                    for(DataSnapshot albumshot:dataSnapshot.getChildren()){
                        picture=albumshot.child("album").getValue().toString();
                        data2.add(picture);
                    }
                    size2=data2.size();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

           FirebaseDatabase.getInstance().getReference().child("users").child(mUser_id).child("book").child(mBook_id).child("album").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        //Toast.makeText(getContext(), "onResume", Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessage(0);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


        super.onResume();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            progressDialog.dismiss(); // 다이얼로그 삭제
            // 2. 이후 처리
        }
    };
}