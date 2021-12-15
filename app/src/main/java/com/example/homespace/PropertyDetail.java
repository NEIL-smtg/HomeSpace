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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

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
    ProgressBar progressBar;
    EditText category, title, description, builtup, landArea, price;
    Spinner tenure,furnishing,rooms,bedrooms,toilet;
    ProgressDialog progressDialog;

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

        back = (ImageView) findViewById(R.id.propertydetailback);

        category = (EditText) findViewById(R.id.category_gettxt);

        title = (EditText) findViewById(R.id.title_gettxt);
        description = (EditText) findViewById(R.id.description_gettxt);
        builtup = (EditText) findViewById(R.id.builtup_gettxt);
        landArea = (EditText) findViewById(R.id.landarea_gettxt);
        price = (EditText) findViewById(R.id.price_gettxt);
        imgUploader = (ImageView) findViewById(R.id.upload_img);

        tenure = (Spinner) findViewById(R.id.tenure_spinner);
        furnishing = (Spinner) findViewById(R.id.furnishing_spinner);
        rooms = (Spinner) findViewById(R.id.roomsNum_spinner);
        bedrooms = (Spinner) findViewById(R.id.bedRoomsNum_spinner);
        toilet = (Spinner) findViewById(R.id.toiletNum_spinner);

        progressBar = (ProgressBar) findViewById(R.id.imguploader_progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getIncomingIntent();

        progressDialog = new ProgressDialog(PropertyDetail.this);
        progressDialog.setMessage("Uploading images !!! please wait.....");
    }

    private void getIncomingIntent()
    {
        Bundle bundle = getIntent().getExtras();
        itemType = bundle.getString("itemType");
    }

    public void UploadImage(View view)
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
            }
            else
            {
                Toast.makeText(PropertyDetail.this, "Please Select Multiple Images", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void SubmitOnClick(View view)
    {
        //get all the property data
        // S stand for String , N stand for number
        String Scategory = category.getText().toString();
        String Stitle = title.getText().toString();
        String Sdescription = description.getText().toString();
        Float Fbuiltup =Float.parseFloat(builtup.getText().toString());
        Float FlandArea =Float.parseFloat(landArea.getText().toString());
        Float Fprice = Float.parseFloat(price.getText().toString());

        String Stenure = tenure.getSelectedItem().toString();
        String Sfurnishing = furnishing.getSelectedItem().toString();
        int Nrooms = Integer.parseInt(rooms.getSelectedItem().toString());
        int Nbedrooms = Integer.parseInt(bedrooms.getSelectedItem().toString());
        int Ntoilets = Integer.parseInt(toilet.getSelectedItem().toString());


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(itemType + "/" + id).push();
        pushID = reference.getKey();

        AuctionHelperClass auctionHelperClass = new AuctionHelperClass(
                Scategory,Stenure,Sfurnishing,Stitle,Sdescription,itemType,pushID,
                Fbuiltup,FlandArea,Fprice,Nrooms,Nbedrooms,Ntoilets
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