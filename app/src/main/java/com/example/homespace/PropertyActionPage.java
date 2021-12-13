package com.example.homespace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PropertyActionPage extends AppCompatActivity {

    AgentInfoAdapter agent;
    ArrayList<String> sliderItems;
    String id,pushID,itemType;

    //widget
    CircleImageView back;
    ViewPager viewPager;
    TextView title,description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_action_page);

        sliderItems = new ArrayList<>();

        //get incoming intent agent data
        agent = getIntent().getParcelableExtra("agentPropertyItemOnClick");
        id = agent.getId();
        pushID = agent.getPushID();
        itemType = agent.getItemType();


        //widget
        viewPager = (ViewPager) findViewById(R.id.slide_adapter_viewpager);
        title = (TextView) findViewById(R.id.property_title);
        description = (TextView) findViewById(R.id.property_description);

        back = (CircleImageView) findViewById(R.id.action_page_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        DisplayImages();
        DisplayPropertyDetail();
    }


    private void DisplayImages()
    {
        //firebase reference path
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(itemType + "/" + id + "/" + pushID + "/PropertyImage");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    sliderItems.add(ds.getValue().toString());
                }
                ImageSliderAdapter adapter = new ImageSliderAdapter(PropertyActionPage.this,sliderItems);
                viewPager.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void DisplayPropertyDetail()
    {
        title.setText(agent.getTitle());

        //firebase reference
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(itemType + "/" + id + "/" + pushID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.child("description").exists())
                {
                    description.setText(snapshot.child("description").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

}