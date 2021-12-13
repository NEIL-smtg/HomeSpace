package com.example.homespace;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jsibbold.zoomage.ZoomageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GalleryFragment extends Fragment {


    //widgets
    private GridView gridView;
    private ZoomageView galleryImage;
    private ProgressBar progressBar;
    private TextView next;
    private ImageView shareClose;

    //constants
    private static final int NUM_GRID_COLUMNS = 3;

    //vars
    private ArrayList<String> directories;
    int imgPosition;
    Uri contentUri;
    String fileName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        directories = new ArrayList<>();
        imgPosition=0;

        galleryImage = (ZoomageView) view.findViewById(R.id.galleryImageView);
        gridView = (GridView) view.findViewById(R.id.gallery_gridView);

        progressBar = (ProgressBar) view.findViewById(R.id.gallery_progressBar);
        progressBar.setVisibility(View.GONE);

        shareClose = (ImageView) view.findViewById(R.id.ivCloseShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        next = (TextView) view.findViewById(R.id.gallery_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                setProfilePic();
            }
        });


        //SETUP gridview for each picture
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        ImageAdapter imageAdapter = new ImageAdapter(getActivity());
        gridView.setAdapter(imageAdapter);
        directories = imageAdapter.getDirectories();

        //initialise
        Glide.with(getActivity())
                .load(directories.get(0))
                .placeholder(R.drawable.ic_launcher_background)
                .into(galleryImage);


        //if any of the picture on clicked
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (directories != null && !directories.isEmpty())
                {
                    imgPosition = position;

                    File f = new File(directories.get(position));
                    contentUri = Uri.fromFile(f);
                    //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    //String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                    fileName = f.getName();

                    Glide.with(getActivity())
                            .load(contentUri)
                            .error(R.drawable.spacelogo)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(galleryImage);

                }
            }
        });

        return view;
    }

    private String getFileExt(Uri contentUri)
    {
        ContentResolver c = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    //next button on clicked
    private void setProfilePic()
    {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Setting up new profile picture.....");

        SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String id = preferences.getString("id","");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User/"+id+"/profilePic");

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(id+"/ProfilePicture/"+fileName);

        storageReference.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri)
                    {
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
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

               progressDialog.show();
            }
        });

        progressDialog.dismiss();
    }


}