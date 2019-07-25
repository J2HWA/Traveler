package com.likeonline.travelmaker.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.likeonline.travelmaker.MapsActivity;
import com.likeonline.travelmaker.R;
import com.likeonline.travelmaker.place.PlaceActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PlaceDetail_content extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private TextView park;
    private TextView time;
    private TextView address;
    private TextView price;
    private Button tel;
    private String mPlace_id;
    private String mPlace_category;
    private Button map;
    //PlaceActivity.PlaceholderFragment placeholderFragment=new PlaceActivity.PlaceholderFragment();

    public void setmPlace_id(String mPlace_id) {
        this.mPlace_id = mPlace_id;
    }

    public void setmPlace_category(String mPlace_category) {
        this.mPlace_category = mPlace_category;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View View=(ViewGroup)inflater.inflate(R.layout.fragment_place_detail_content, container, false);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        //Toast.makeText(getActivity(), mPlace_id, Toast.LENGTH_SHORT).show();

        park=(TextView) View.findViewById(R.id.content_park);
        time=(TextView) View.findViewById(R.id.content_time);
        address=(TextView) View.findViewById(R.id.content_adrress);
        price=(TextView) View.findViewById(R.id.content_price);
        tel=(Button) View.findViewById(R.id.content_tel);
        map=(Button) View.findViewById(R.id.content_map);

        myRef.child("place").child(mPlace_category).child(mPlace_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String park1= dataSnapshot.child("place_park").getValue().toString();
                String address1= dataSnapshot.child("place_address").getValue().toString();
                String price1=dataSnapshot.child("place_price").getValue().toString();
                String tel1= dataSnapshot.child("place_tel").getValue().toString();
                String time1=dataSnapshot.child("place_time").getValue().toString();


                park.setText(park1);
                address.setText(address1);
                price.setText(price1);
                tel.setText(tel1);
                time.setText(time1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Uri uri = Uri.parse("tel:"+tel.getText().toString());
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);

            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent=new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("place_id", mPlace_id);
                intent.putExtra("place_category", mPlace_category);
                startActivity(intent);
            }
        });

        return View;
    }
}
