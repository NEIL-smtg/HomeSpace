package com.example.homespace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PropertyDetail extends AppCompatActivity {


    StorageReference storageReference;
    Uri imageUri;
    //DatabaseReference reference;
    String id;
    ArrayList <Uri> ImageList;
    ArrayList <String> urlStrings;
    String itemType, pushID;

    //widgets
    ImageView back,imgUploader;
    ArrayList<ImageView> editImage = new ArrayList<>();
    ImageView editImage1,editImage2,editImage3,editImage4,editImage5,editImage6,editImage7,editImage8,editImage9;
    EditText title, description, builtup, landArea, price;
    Spinner category,tenure,furnishing,bedrooms,toilet;
    ProgressDialog progressDialog;
    Button submit;

    //CONSTANT
    public static final int GALLERY_PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_detail);

        ImageList = new ArrayList<>();

        //get user id
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        id = preferences.getString("id","");

        setWidgetID();

        getIncomingIntent();
    }

    private void setWidgetID(){
        submit =(Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitOnClick();
            }
        });

        title = (EditText) findViewById(R.id.addTitle);
        description = (EditText) findViewById(R.id.addDescription);
        builtup = (EditText) findViewById(R.id.addBuiltup);
        landArea = (EditText) findViewById(R.id.addLandArea);
        price = (EditText) findViewById(R.id.addPrice);
        imgUploader = (ImageView) findViewById(R.id.upload_img);

        category = (Spinner) findViewById(R.id.addCategory);
        tenure = (Spinner) findViewById(R.id.addTenure);
        furnishing = (Spinner) findViewById(R.id.addFurnishing);
        bedrooms = (Spinner) findViewById(R.id.addBedroom);
        toilet = (Spinner) findViewById(R.id.addToilet);

        back = (ImageView) findViewById(R.id.addBack);
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


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgUploader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage();
            }
        });

        progressDialog = new ProgressDialog(PropertyDetail.this);
        progressDialog.setMessage("Uploading images !!! please wait.....");
    }

    private void getIncomingIntent()
    {
        Bundle bundle = getIntent().getExtras();
        itemType = bundle.getString("itemType");
    }

    public void UploadImage()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(galleryIntent, GALLERY_PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== GALLERY_PICK_IMAGE && resultCode == Activity.RESULT_OK )
        {
            if (data.getClipData() != null)
            {
                int countClipData = data.getClipData().getItemCount();
                int currentImagesSelected = 0;

                while (currentImagesSelected < countClipData) {
                    imageUri = data.getClipData().getItemAt(currentImagesSelected).getUri();
                    ImageList.add(imageUri);
                    currentImagesSelected++;
                }


                for (int i = 0; i < ImageList.size() ; i++)
                {
                    Glide
                    .with(PropertyDetail.this)
                    .load(ImageList.get(i))
                    .into(editImage.get(i));
                }

            }
            else
            {
                Toast.makeText(PropertyDetail.this, "Please Select Multiple Images", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void SubmitOnClick()
    {
        //get all the property data
        // S stand for String , N stand for number
        String Scategory = category.getSelectedItem().toString();
        String Stitle = title.getText().toString();
        String Sdescription = description.getText().toString();
        Float Fbuiltup =Float.parseFloat(builtup.getText().toString());
        Float FlandArea =Float.parseFloat(landArea.getText().toString());
        Float Fprice = Float.parseFloat(price.getText().toString());

        String Stenure = tenure.getSelectedItem().toString();
        String Sfurnishing = furnishing.getSelectedItem().toString();
        int Nbedrooms = Integer.parseInt(bedrooms.getSelectedItem().toString());
        int Ntoilets = Integer.parseInt(toilet.getSelectedItem().toString());


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(itemType + "/" + id).push();
        pushID = reference.getKey();

        AuctionHelperClass auctionHelperClass = new AuctionHelperClass(
                Scategory,Stenure,Sfurnishing,Stitle,Sdescription,itemType,pushID,
                Fbuiltup,FlandArea,Fprice,Nbedrooms,Ntoilets
        );

        reference.setValue(auctionHelperClass);

        //upload image to storage
        StoreImageToFireStorage();
    }

    private void StoreImageToFireStorage()
    {
        urlStrings = new ArrayList<>();
        progressDialog.show();

        storageReference = FirebaseStorage.getInstance().getReference(itemType + "/ImagesFolder");

        for (int upload_count = 0; upload_count < ImageList.size(); upload_count++) {
            Uri individualImage = ImageList.get(upload_count);
            final StorageReference ImageName = storageReference.child("Images" + individualImage.getLastPathSegment());

            ImageName.putFile(individualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri)
                        {
                            urlStrings.add(String.valueOf(uri));

                            if (urlStrings.size() == ImageList.size())
                            {
                                storeLink(urlStrings);
                            }
                        }
                    });
                }
            });
        }
    }

    private void storeLink(ArrayList<String> urlStrings)
    {
        HashMap<String, String> hashMap = new HashMap<>();

        //set thumbnail for property card
        String thumbnail = urlStrings.get(0);

        for (int i = 0; i <urlStrings.size() ; i++)
        {
            hashMap.put("ImgLink"+i, urlStrings.get(i));
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(itemType+"/" + id + "/" + pushID );

        databaseReference.child("PropertyImage").setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(PropertyDetail.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                        UpdateAgent(thumbnail);
                        finish();
                    }
                }
            }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(PropertyDetail.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        progressDialog.dismiss();

        ImageList.clear();
    }

    private void UpdateAgent(String thumbnail)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Agents");

        //get user profile picture link
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String img = preferences.getString("photoURL","");


        AgentInfoAdapter agent = new AgentInfoAdapter(img,title.getText().toString(),thumbnail,id,itemType,pushID);

        reference.push().setValue(agent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    Toast.makeText(PropertyDetail.this, "Agent updated", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(PropertyDetail.this, "Agent upload failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}