package com.example.homespace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainPage extends AppCompatActivity {

    ImageView userProfile,add,comparisonCart,setting;
    NotificationBadge badge;

    SharedPreferences preferences;
    String id;

    RecyclerView recyclerView;
    RecyclerViewAdapter mainAdapter;

//    GoogleMap map;
//    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        userProfile = (ImageView) findViewById(R.id.mainpage_profilepic);
        add = (ImageView) findViewById(R.id.add);
        comparisonCart = (ImageView) findViewById(R.id.cart);
        setting = (ImageView) findViewById(R.id.setting);
        badge = (NotificationBadge) findViewById(R.id.notification_badge);

        preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        id = preferences.getString("id","");

        //searchView = (SearchView) findViewById(R.id.mainpage_search_view);

        setProfilePicture();
        SetRecommendation();
        UpdateCartCount();
        //search();
    }

//    private void search()
//    {
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                String location = searchView.getQuery().toString();
//                List<Address> addressList = null;
//
//                if (location !=null || !location.equals(""))
//                {
//                    Geocoder geocoder = new Geocoder(MainPage.this);
//                    try {
//                        addressList = geocoder.getFromLocationName(location,10);
//
//                    }
//                    catch (IOException e)
//                    {
//                        e.printStackTrace();
//                    }
//
//
//                    Address address = addressList.get(0);
//                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//                    map.addMarker(new MarkerOptions().position(latLng).title(location));
//                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
//                }
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
//    }


    private void UpdateCartCount()
    {
        //initialise
        badge.setText(""+CartItems.agent.size());

        CartItems.itemAdded.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                badge.setText(""+CartItems.agent.size());
            }
        });
    }


    private void setProfilePicture()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User/" + id + "/profilePic");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    Glide
                            .with(MainPage.this)
                            .load(snapshot.child("imgURL").getValue().toString())
                            .into(userProfile);

                    //update SharedPreference
                    SharedPreferences.Editor editor = getApplicationContext()
                            .getSharedPreferences("MyPrefs",MODE_PRIVATE)
                            .edit();

                    editor.putString("photoURL",snapshot.child("imgURL").getValue().toString());
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { Log.d("tag",error.toString()); }
        });
    }

    private void SetRecommendation()
    {
        recyclerView = (RecyclerView) findViewById(R.id.mainpage_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //get agents
        FirebaseRecyclerOptions<AgentInfoAdapter> options =
                new FirebaseRecyclerOptions.Builder<AgentInfoAdapter>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Agents"), AgentInfoAdapter.class)
                        .build();


        mainAdapter = new RecyclerViewAdapter(options,MainPage.this);
        recyclerView.setAdapter(mainAdapter);

        mainAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position)
            {
                AgentInfoAdapter agentPropertyItemOnClick = new AgentInfoAdapter();
                agentPropertyItemOnClick = options.getSnapshots().get(position);


                Intent intent = new Intent(MainPage.this, PropertyActionPage.class);
                intent.putExtra("agentPropertyItemOnClick", agentPropertyItemOnClick);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainAdapter.startListening();
    }


    //error when back from other new activity
    /*@Override
    protected void onStop() {
        super.onStop();
        mainAdapter.stopListening();
    }*/


    public void ProfileOnClick(View view)
    {
        Intent i = new Intent(MainPage.this,agentViewProfilePage.class);
        startActivity(i);
    }

    public void addItemOnClick(View view)
    {
        Intent i = new Intent(MainPage.this, AddItem.class);
        startActivity(i);
    }

    public void CartOnClick(View view)
    {
        Intent i = new Intent(MainPage.this,Cart.class);
        startActivity(i);
    }

    public void SettingOnClick(View view)
    {
        Intent i = new Intent(MainPage.this,Setting.class);
        startActivity(i);
    }


}