package com.example.homespace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PropertyActionPage extends AppCompatActivity {

    AgentInfoAdapter agent;
    ArrayList<String> sliderItems;
    String id,pushID,itemType;

    //widget
    CircleImageView back, agentProPic;
    ViewPager viewPager;
    TextView title,description,category,builtup, landArea, tenure, furnishing, roomNum, toiletNum, price;
    Button addToCart, Chat;

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


        //property detail widget
        viewPager = (ViewPager) findViewById(R.id.slide_adapter_viewpager);
        title = (TextView) findViewById(R.id.property_title);
        description = (TextView) findViewById(R.id.property_description);
        category = (TextView) findViewById(R.id.category);
        builtup = (TextView) findViewById(R.id.builtup);
        landArea = (TextView) findViewById(R.id.landArea);
        tenure = (TextView) findViewById(R.id.tenure);
        furnishing = (TextView) findViewById(R.id.furnishing);
        roomNum = (TextView) findViewById(R.id.roomNum);
        toiletNum = (TextView) findViewById(R.id.toiletNum);
        price = (TextView) findViewById(R.id.price);

        //bottom toolbar widget
        addToCart = (Button) findViewById(R.id.addtocart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToCart();
            }
        });

        Chat = (Button) findViewById(R.id.chat);

        //agent
        agentProPic = (CircleImageView) findViewById(R.id.agent);

        //arrow back
        back = (CircleImageView) findViewById(R.id.action_page_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        DisplayImages();
        DisplayPropertyDetail();
        SetupAgentProPic();
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
                //get detail and set
                if (snapshot.child("description").exists())
                {
                    description.setText(snapshot.child("description").getValue().toString());
                }

                if (snapshot.child("category").exists())
                {
                    category.setText(snapshot.child("category").getValue().toString());
                }

                if (snapshot.child("builtup").exists())
                {
                    builtup.setText(snapshot.child("builtup").getValue().toString() + " ft");
                }

                if (snapshot.child("landArea").exists())
                {
                    landArea.setText(snapshot.child("landArea").getValue().toString() + " ft");
                }

                if (snapshot.child("tenure").exists())
                {
                    tenure.setText(snapshot.child("tenure").getValue().toString());
                }

                if (snapshot.child("furnishing").exists())
                {
                    furnishing.setText(snapshot.child("furnishing").getValue().toString());
                }

                if (snapshot.child("rooms").exists())
                {
                    roomNum.setText(snapshot.child("rooms").getValue().toString());
                }

                if (snapshot.child("toilets").exists())
                {
                    toiletNum.setText(snapshot.child("toilets").getValue().toString());
                }

                if (snapshot.child("price").exists())
                {
                    price.setText("RM " + snapshot.child("price").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void SetupAgentProPic()
    {
        String ProfilePicLink = agent.getProfilePic();
        Glide
                .with(PropertyActionPage.this)
                .load(ProfilePicLink)
                .into(agentProPic);

        agentProPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start intent agentViewProfilePage
                //pass agent....
            }
        });
    }

    private void addItemToCart()
    {
        //pass agent
        CartItems.agent.add(agent);

        Toast.makeText(PropertyActionPage.this, "Added to cart", Toast.LENGTH_SHORT).show();
    }

}