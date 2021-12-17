package com.example.homespace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class editProperty extends AppCompatActivity {

    //widgets
    ImageView back,add;
    ArrayList<ImageView> editImage = new ArrayList<>();
    ImageView editImage1,editImage2,editImage3,editImage4,editImage5,editImage6,editImage7,editImage8,editImage9;
    Spinner category,tenure,furnishing,bedrooms,toilets;
    EditText title,builtup,landarea,price,description;
    TextView save;

    //vars
    AuctionHelperClass aucData;
    String id,itemType,pushID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_property);

        aucData = getIntent().getParcelableExtra("model");
        id = getIntent().getStringExtra("id");
        itemType = aucData.getItemType();
        pushID = aucData.getPushID();


        //widgets
        setWidgetID();

        //get data from firebase
        getData();


        //set listener for save
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavetoFirebase();
            }
        });
    }

    private void SavetoFirebase()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(itemType + "/" + id + "/" + pushID);

        AuctionHelperClass newData = new AuctionHelperClass
                (
                        category.getSelectedItem().toString(),tenure.getSelectedItem().toString(),furnishing.getSelectedItem().toString(),
                        title.getEditableText().toString(),description.getEditableText().toString(),
                        itemType,pushID,
                        Float.parseFloat(builtup.getEditableText().toString()),Float.parseFloat(landarea.getEditableText().toString()),
                        Float.parseFloat(price.getEditableText().toString()),Integer.parseInt(bedrooms.getSelectedItem().toString()),
                        Integer.parseInt(toilets.getSelectedItem().toString())
                );

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating..Please wait..");
        progressDialog.show();

        Map<String,Object> updates = new HashMap<String,Object>();
        updates.put("category",newData.getCategory());
        updates.put("tenure",newData.getTenure());
        updates.put("furnishing",newData.getFurnishing());
        updates.put("title",newData.getTitle());
        updates.put("description",newData.getTitle());
        updates.put("builtup",newData.getBuiltup());
        updates.put("landArea",newData.getLandArea());
        updates.put("price",newData.getPrice());
        updates.put("bedrooms",newData.getBedrooms());
        updates.put("toilets",newData.getToilets());

        reference.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(editProperty.this, "Property Updated successfully", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(editProperty.this, "Property Updated failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            }
        });
    }


    private void getData()
    {
        title.setText(aucData.getTitle());
        builtup.setText(aucData.getBuiltup()+"");
        landarea.setText(aucData.getLandArea()+"");
        price.setText(aucData.getPrice()+"");
        description.setText(aucData.getDescription());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(itemType + "/" + id + "/" + pushID +"/PropertyImage");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=0;

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    Glide
                            .with(editProperty.this)
                            .load(ds.getValue().toString())
                            .into(editImage.get(i));

                    i++;
                }

                for (int j = i; i < 9; i++)
                {
                    editImage.get(i).setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});

    }

    private void setWidgetID()
    {
        editImage1 = (ImageView) findViewById(R.id.editImage1);
        editImage2 = (ImageView) findViewById(R.id.editImage2);
        editImage3 = (ImageView) findViewById(R.id.editImage3);
        editImage4 = (ImageView) findViewById(R.id.editImage4);
        editImage5 = (ImageView) findViewById(R.id.editImage5);
        editImage6 = (ImageView) findViewById(R.id.editImage6);
        editImage7 = (ImageView) findViewById(R.id.editImage7);
        editImage8 = (ImageView) findViewById(R.id.editImage8);
        editImage9 = (ImageView) findViewById(R.id.editImage9);

        editImage.add(editImage1);
        editImage.add(editImage2);
        editImage.add(editImage3);
        editImage.add(editImage4);
        editImage.add(editImage5);
        editImage.add(editImage6);
        editImage.add(editImage7);
        editImage.add(editImage8);
        editImage.add(editImage9);


        back = (ImageView) findViewById(R.id.edit_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }});

        add = (ImageView) findViewById(R.id.edit_addImage);

        category = (Spinner) findViewById(R.id.editCategory);
        tenure = (Spinner) findViewById(R.id.editTenure);
        furnishing = (Spinner) findViewById(R.id.editFurnishing);
        bedrooms = (Spinner) findViewById(R.id.editBedroom);
        toilets = (Spinner) findViewById(R.id.editToilet);

        title = (EditText) findViewById(R.id.editTitle);
        builtup = (EditText) findViewById(R.id.editBuiltup);
        landarea = (EditText) findViewById(R.id.editLandArea);
        price = (EditText) findViewById(R.id.editPrice);
        description = (EditText) findViewById(R.id.editDescription);

        save = (TextView) findViewById(R.id.edit_save);

    }
}