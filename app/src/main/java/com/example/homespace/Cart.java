package com.example.homespace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Cart extends AppCompatActivity {

    //widgets
    ImageView back, cancel, compare;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //widget
        cancel = (ImageView) findViewById(R.id.cart_cancel_compare) ;
        compare = (ImageView) findViewById(R.id.cart_compare);

        back = (ImageView) findViewById(R.id.cart_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setupCart();
        observeCompareList();
        setupButtonListener();
    }


    private void setupButtonListener()
    {
        //deselect all property if cancel on click
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartItems.compareList.clear();
                CartItems.comparelistchecker.setValue(false);
                setupCart();
            }
        });


        compare.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //start new intent to compare
                Intent intent = new Intent(Cart.this, PropertyCompare.class);
                startActivity(intent);
            }
        });

    }


    private void setupCart() {

        recyclerView = (RecyclerView) findViewById(R.id.cart_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartAdapter cartAdapter = new cartAdapter(Cart.this);
        recyclerView.setAdapter(cartAdapter);

        //when item on click
        cartAdapter.setOnItemClickListener(new cartAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                AgentInfoAdapter agentPropertyItemOnClick = new AgentInfoAdapter();
                agentPropertyItemOnClick = CartItems.agent.get(position);


                Intent intent = new Intent(Cart.this, PropertyActionPage.class);
                intent.putExtra("agentPropertyItemOnClick", agentPropertyItemOnClick);
                startActivity(intent);
            }
        });
    }

    private void observeCompareList()
    {
        CartItems.comparelistchecker.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean newBoolean)
            {
                if (newBoolean==true)
                {
                    back.setVisibility(View.INVISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                    if (CartItems.compareList.size() > 1)
                    {
                        compare.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    back.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.INVISIBLE);
                    compare.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

}