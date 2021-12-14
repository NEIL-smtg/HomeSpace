package com.example.homespace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Cart extends AppCompatActivity {

    //widgets
    public static ImageView back, cancel, delete, compare;
    public static Button radioButton_unchecked,radioButton_checked;
    TextView select;

    ArrayList<AgentInfoAdapter> cart_agentlist;
    RecyclerView recyclerView;
    CartRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        //widget
        cancel = (ImageView) findViewById(R.id.cart_cancel_compare) ;
        delete = (ImageView) findViewById(R.id.cart_delete);
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

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CartItems.compareList.clear();
                CartItems.comparelistchecker.setValue(false);

                CardView cardView = (CardView) findViewById(R.id.cart_cardview);
                cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
            }
        });


        CartItems.updateRecyclerView.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean newBoolean) {
                if (newBoolean)
                {
                    setupCart();

                }
            }
        });

        compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(Cart.this, PropertyCompare);
                //startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    /*
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }*/

    private void setupCart() {
        cart_agentlist = CartItems.agent;

        recyclerView = (RecyclerView) findViewById(R.id.cart_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //get all the agents from firebase
        FirebaseRecyclerOptions<AgentInfoAdapter> options =
                new FirebaseRecyclerOptions.Builder<AgentInfoAdapter>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Agents"), AgentInfoAdapter.class)
                        .build();


        // keep the agents only if their property in cart
        for (int i=0 ; i<options.getSnapshots().size() ; i++)
        {
            if (options.getSnapshots().get(i).getPushID() != cart_agentlist.get(i).pushID)
            {
                options.getSnapshots().remove(i);
            }
        }

        //display recycler view
        adapter = new CartRecyclerViewAdapter(options,Cart.this);
        recyclerView.setAdapter(adapter);

        //when item on click
        adapter.setOnItemClickListener(new CartRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                AgentInfoAdapter agentPropertyItemOnClick = new AgentInfoAdapter();
                agentPropertyItemOnClick = options.getSnapshots().get(position);


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
                    compare.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.VISIBLE);
                }
                else
                {
                    back.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.INVISIBLE);
                    compare.setVisibility(View.INVISIBLE);
                    delete.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

}