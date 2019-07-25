package com.likeonline.travelmaker;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String mPlace_id;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String mPlace_category;
    private String title;
    private String wedo;  // place 위도
    private String gyoungdo; //place 경도
    private String placeAddress;

    String splash_background;
    private FirebaseRemoteConfig firebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mPlace_id = getIntent().getStringExtra("place_id");
        mPlace_category=getIntent().getStringExtra("place_category");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = firebaseRemoteConfig.getString("splash_background");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));// 윈도우 상태바 색상 =>파이어베이스로 설정
        }

        //Toast.makeText(this, mPlace_id, Toast.LENGTH_SHORT).show();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //Toast.makeText(this, wedo+gyoungdo+title, Toast.LENGTH_SHORT).show();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        myRef.child("place").child(mPlace_category).child(mPlace_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wedo=dataSnapshot.child("place_wedo").getValue().toString();
                gyoungdo=dataSnapshot.child("place_gyoungdo").getValue().toString();
                title=dataSnapshot.child("place_title").getValue().toString();
                placeAddress=dataSnapshot.child("place_address").getValue().toString();

                // Add a marker in Sydney and move the camera

                LatLng place= new LatLng(Double.parseDouble(wedo), Double.parseDouble(gyoungdo));
                mMap.addMarker(new MarkerOptions().position(place).title(title) .snippet(placeAddress)).showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place,16));

                //mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
