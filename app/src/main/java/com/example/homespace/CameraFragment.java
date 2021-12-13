package com.example.homespace;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CameraFragment extends Fragment {

    //constants
    private static final String TAG = "PhotoFragment";
    private static final int PHOTO_FRAGMENT_NUM = 1;
    private static final int GALLERY_FRAGMENT_NUM = 2;
    private static final int CAMERA_REQUEST_CODE = 102;//5;
    private static final int REQUEST_IMAGE_CAPTURE =1 ;

    //vars
    Uri imageUri;
    String currentPhotoPath, fileName;

    //widget
    ProgressBar progressBar;
    ImageView tmpProfilePic;
    TextView next;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_takephoto, container, false);

        tmpProfilePic = (ImageView) view.findViewById(R.id.tmpProfilePic);
        progressBar = (ProgressBar) view.findViewById(R.id.camera_progressBar);

        next = (TextView) view.findViewById(R.id.takephoto_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                setProfilePic();
            }
        });

        Button btnLaunchCamera = (Button) view.findViewById(R.id.btnLaunchCamera);

        btnLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ShareActivity)getActivity()).getCurrentTabNumber() == PHOTO_FRAGMENT_NUM)
                {
                    if (((ShareActivity)getActivity()).checkPermissions(Permissions.CAMERA_PERMISSIONS[0]))
                    {
                        dispatchTakePictureIntent();
                        //Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //startActivityForResult(cameraIntent,CAMERA_REQUEST_CODE);
                    }
                    else
                    {
                        Intent intent = new Intent(getActivity(),ShareActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == getActivity().RESULT_OK)
        {
            File f = new File(currentPhotoPath);
            imageUri = Uri.fromFile(f);
            fileName = f.getName();

            tmpProfilePic.setImageURI(imageUri);
            next.setVisibility(View.VISIBLE);

            //add picture to gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(imageUri);
            getActivity().sendBroadcast(mediaScanIntent);

        }
    }



    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile
                (
                        imageFileName,      /* prefix */
                        ".jpg",        /* suffix */
                        storageDir          /* directory */
                );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }


    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null)
        {
            // Create the File where the photo should go
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }


    private void setProfilePic()
    {
        SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String id = preferences.getString("id","");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User/"+id+"/profilePic");

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(id+"/ProfilePicture/"+fileName);

        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        progressBar.setVisibility(View.GONE);
                        reference.child("imgURL").removeValue();
                        reference.child("imgURL").setValue(uri.toString());

                        Toast.makeText(getActivity(), "Profile Picture changed", Toast.LENGTH_SHORT).show();

                        getActivity().finish();

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Intent intent = new Intent(getActivity(),ShareActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                Toast.makeText(getActivity(), "Failed to set profile picture", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot)
            {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress((int) progress);
            }
        });
    }

}