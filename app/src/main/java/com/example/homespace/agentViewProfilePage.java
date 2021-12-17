package com.example.homespace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class agentViewProfilePage extends AppCompatActivity {

    //widget
    TextView agentName, agentEmail, agentPhone, agentCompany, agentDescription, edit_profile_btn;
    CircleImageView agentpropic,back;
    ArrayList<RecyclerView> recyclerView = new ArrayList<>();

    //vars
    String id;
    AgentInfoAdapter agent;
    ArrayList<agentProfileRecyclerView> mainAdapter = new ArrayList<>();
    boolean isMyself;
    ArrayList<String> typelist = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_view_profile_page);

        typelist.add("Auction");
        typelist.add("Rent");
        typelist.add("Room");
        typelist.add("Sale");

        SharedPreferences preferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);

        //get incoming intent
        if (getIntent().getParcelableExtra("agentData") != null) {
            agent = getIntent().getParcelableExtra("agentData");
            id = agent.getId();
        }
        else
        {
            id = preferences.getString("id","");
        }


        //widgets
        agentName = (TextView) findViewById(R.id.agentProfileName);
        agentEmail = (TextView) findViewById(R.id.agentEmail);
        agentPhone = (TextView) findViewById(R.id.agentPhone);
        agentCompany = (TextView) findViewById(R.id.agentCompany);
        agentDescription = (TextView) findViewById(R.id.agentDescription);
        agentpropic = (CircleImageView) findViewById(R.id.propic);
        edit_profile_btn = (TextView) findViewById(R.id.edit_profile_btn);

        back = (CircleImageView) findViewById(R.id.agentprofile_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish();}
        });

        if (preferences.getString("id","").equals(id))
        {
            isMyself = true;

            edit_profile_btn.setVisibility(View.VISIBLE);
            edit_profile_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(agentViewProfilePage.this, Profile.class);
                    startActivity(intent);

                }
            });
        }

        RecyclerView recyclerView1 = (RecyclerView) findViewById(R.id.agentprofile_recyclerview1);
        RecyclerView recyclerView2 = (RecyclerView) findViewById(R.id.agentprofile_recyclerview2);
        RecyclerView recyclerView3 = (RecyclerView) findViewById(R.id.agentprofile_recyclerview3);
        RecyclerView recyclerView4 = (RecyclerView) findViewById(R.id.agentprofile_recyclerview4);

        recyclerView.add(recyclerView1);
        recyclerView.add(recyclerView2);
        recyclerView.add(recyclerView3);
        recyclerView.add(recyclerView4);

        setup();
        setupRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (int i = 0; i < typelist.size(); i++) {
            mainAdapter.get(i).startListening();
        }
    }


    //error when back from other new activity
    /*@Override
    protected void onStop() {
        super.onStop();
        mainAdapter.get(i).stopListening();
    }*/


    private void setupRecyclerView()
    {
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        for (int i = 0; i < typelist.size() ; i++) {
            recyclerView.get(i).setLayoutManager(new LinearLayoutManager(this));

            //get property data
            FirebaseRecyclerOptions<AuctionHelperClass> options =
                    new FirebaseRecyclerOptions.Builder<AuctionHelperClass>()
                            .setQuery(FirebaseDatabase.getInstance().getReference(typelist.get(i) + "/" + id), AuctionHelperClass.class)
                            .build();


            agentProfileRecyclerView adapter = new agentProfileRecyclerView(options, agentViewProfilePage.this, id, isMyself);

            mainAdapter.add(adapter);
            recyclerView.get(i).setAdapter(mainAdapter.get(i));


            mainAdapter.get(i).setOnItemClickListener(new agentProfileRecyclerView.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Agents");

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            AgentInfoAdapter agentfromdb = new AgentInfoAdapter();
                            ArrayList<AgentInfoAdapter> agentdblist = new ArrayList<>();

                            for (DataSnapshot ds : snapshot.getChildren()) {
                                agentfromdb = ds.getValue(AgentInfoAdapter.class);

                                //work in mainpage
                                if (agentfromdb.getId().equals(agent.getId())) {
                                    agentdblist.add(agentfromdb);
                                }
                            }


                            Intent intent = new Intent(agentViewProfilePage.this, PropertyActionPage.class);
                            intent.putExtra("agentPropertyItemOnClick", agentdblist.get(position));
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            });
        }
    }

    private void setup()
    {
        //setup text
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User/"+ id +"/info");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.child("email").exists())
                {
                    agentEmail.setText(snapshot.child("email").getValue().toString());
                }

                if (snapshot.child("username").exists())
                {
                    agentName.setText(snapshot.child("username").getValue().toString());
                }

                if (snapshot.child("phoneNum").exists())
                {
                    agentPhone.setText(snapshot.child("phoneNum").getValue().toString());
                }
                else
                {
                    agentPhone.setText("null");
                }

                if (snapshot.child("company").exists())
                {
                    agentCompany.setText(snapshot.child("company").getValue().toString());
                }
                else
                {
                    agentCompany.setText("null");
                }

                if (snapshot.child("description").exists())
                {
                    agentDescription.setText(snapshot.child("description").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        //setup profile pic
        if (agent != null)
        {
            Glide
                    .with(agentViewProfilePage.this)
                    .load(agent.getProfilePic())
                    .into(agentpropic);
        }
        else
        {
            reference = FirebaseDatabase.getInstance().getReference("User/" + id + "/profilePic");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("imgURL").exists())
                    {
                        Glide
                                .with(agentViewProfilePage.this)
                                .load(snapshot.child("imgURL").getValue().toString())
                                .into(agentpropic);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }});
        }


    }

}