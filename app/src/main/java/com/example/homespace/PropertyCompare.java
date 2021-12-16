package com.example.homespace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PropertyCompare extends AppCompatActivity {

    private ArrayList<AgentInfoAdapter> compareList;
    String id,itemType,pushID;
    RecyclerView recyclerView;
    CompareAdapter compareAdapter;

    //widget
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_compare);

        compareList = new ArrayList<>();
        compareList = CartItems.compareList;

        back = (ImageView) findViewById(R.id.compare_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });

        recyclerView = (RecyclerView) findViewById(R.id.compare_recyclerView);

        setupTableView();
    }

    private void setupTableView()
    {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(PropertyCompare.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        compareAdapter = new CompareAdapter(this,compareList);
        recyclerView.setAdapter(compareAdapter);
    }


}