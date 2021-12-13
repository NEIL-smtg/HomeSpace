package com.example.homespace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AddItem extends AppCompatActivity {

    ImageView auction,rent,rentroom,forsale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        auction = (ImageView) findViewById(R.id.auctionn);
        rent = (ImageView) findViewById(R.id.rentt);
        rentroom = (ImageView) findViewById(R.id.rentroom);
        forsale = (ImageView) findViewById(R.id.salee);
    }

    public void back(View view)
    {
        finish();
    }


    public void AuctionOnClick(View view)
    {
        Intent i = new Intent(this,PropertyDetail.class);
        i.putExtra("itemType","Auction");
        startActivity(i);
    }

    public void SaleOnClick(View view)
    {
        Intent i = new Intent(this,PropertyDetail.class);
        i.putExtra("itemType","Sale");
        startActivity(i);
    }

    public void RoomOnClick(View view)
    {
        Intent i = new Intent(this,PropertyDetail.class);
        i.putExtra("itemType","Room");
        startActivity(i);
    }

    public void RentOnClick(View view)
    {
        Intent i = new Intent(this,PropertyDetail.class);
        i.putExtra("itemType","Rent");
        startActivity(i);
    }
}