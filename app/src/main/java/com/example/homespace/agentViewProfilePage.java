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

import de.hdodenhof.circleimageview.CircleImageView;

public class agentViewProfilePage extends AppCompatActivity {

    //widget
    TextView agentName, agentEmail, agentPhone, agentCompany, agentDescription;
    CircleImageView agentpropic,back;
    RecyclerView recyclerView;

    //vars
    String id,itemType;
    AgentInfoAdapter agent;
    agentProfileRecyclerView mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_view_profile_page);

        //get incoming intent
        agent = getIntent().getParcelableExtra("agentData");
        id = agent.getId();
        itemType = agent.getItemType();

        //widgets
        agentName = (TextView) findViewById(R.id.agentProfileName);
        agentEmail = (TextView) findViewById(R.id.agentEmail);
        agentPhone = (TextView) findViewById(R.id.agentPhone);
        agentCompany = (TextView) findViewById(R.id.agentCompany);
        agentDescription = (TextView) findViewById(R.id.agentDescription);
        agentpropic = (CircleImageView) findViewById(R.id.propic);
        recyclerView = (RecyclerView) findViewById(R.id.agentprofile_recyclerview);

        back = (CircleImageView) findViewById(R.id.agentprofile_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish();}
        });

        setup();
        setupRecyclerView();
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


    private void setupRecyclerView()
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //get property data
        FirebaseRecyclerOptions<AuctionHelperClass> options =
                new FirebaseRecyclerOptions.Builder<AuctionHelperClass>()
                        .setQuery(FirebaseDatabase.getInstance().getReference( itemType+"/"+id), AuctionHelperClass.class)
                        .build();


        mainAdapter = new agentProfileRecyclerView(options,agentViewProfilePage.this, id);
        recyclerView.setAdapter(mainAdapter);


        mainAdapter.setOnItemClickListener(new agentProfileRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Agents");

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        AgentInfoAdapter agentfromdb= new AgentInfoAdapter();
                        ArrayList<AgentInfoAdapter> agentdblist = new ArrayList<>();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            agentfromdb = ds.getValue(AgentInfoAdapter.class);

                            //work in mainpage
                            if (agentfromdb.getId().equals(agent.getId()))
                            {
                                agentdblist.add(agentfromdb);
                            }
                        }


                        Intent intent = new Intent(agentViewProfilePage.this, PropertyActionPage.class);
                        intent.putExtra("agentPropertyItemOnClick", agentdblist.get(position));
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        });
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
        Glide
                .with(agentViewProfilePage.this)
                .load(agent.getProfilePic())
                .into(agentpropic);
    }

}