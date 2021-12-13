package com.example.homespace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    EditText username,email,password,phonenum;
    ImageView profile_pic,back,edit1,edit2,edit3;
    Button save;
    SharedPreferences preferences;

    //data retreived from database
    String dbusername,dbpassword,dbphone,link;

    String id,userpic;

    Boolean isUsernameChanged,isPasswordChanged,isPhoneChanged;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        isUsernameChanged = isPasswordChanged = isPhoneChanged =false;

        preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        id = preferences.getString("id","");
        userpic = preferences.getString("photoURL","");

        dbusername=dbpassword=dbphone="";

        username = (EditText) findViewById(R.id.profile_username);
        password = (EditText) findViewById(R.id.profile_password);
        phonenum = (EditText) findViewById(R.id.profile_phonenumber);
        email = (EditText) findViewById(R.id.profile_email);

        profile_pic = (ImageView) findViewById(R.id.profile_pic);
        edit1 = (ImageView) findViewById(R.id.edit1);
        edit2 = (ImageView) findViewById(R.id.edit2);
        edit3 = (ImageView) findViewById(R.id.edit3);


        back = (ImageView) findViewById(R.id.profile_back);
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save = (Button) findViewById(R.id.profile_save);

        GetDataFromFirebase();

        EditTextClickListener(edit1,username);
        EditTextClickListener(edit2,password);
        EditTextClickListener(edit3,phonenum);

    }


    //change photo onclick
    public void ChangeProfilePicture(View view)
    {
        Intent gallery = new Intent(Profile.this, ShareActivity.class);
        gallery.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(gallery);
    }



    private void GetDataFromFirebase()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User/"+id);

        //get profile picture
        reference.child("profilePic").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    Glide
                            .with(Profile.this)
                            .load(snapshot.child("imgURL").getValue().toString())
                            .into(profile_pic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        //get user data
        reference.child("info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {
                    dbusername = snapshot.child("username").getValue().toString();

                    username.setText(dbusername);

                    email.setText(snapshot.child("email").getValue().toString());

                    if (snapshot.child("phone").exists())
                    {
                        dbphone = snapshot.child("phone").getValue().toString();
                        phonenum.setText(dbphone);
                    }

                    if (snapshot.child("password").exists())
                    {
                        dbpassword = snapshot.child("password").getValue().toString();
                        password.setText(dbpassword);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


    private void EditTextClickListener(ImageView edit, EditText text)
    {
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setEnabled(true);
                isTextChanged(text);
            }
        });
    }


    private void isTextChanged(EditText edittext)
    {
        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {

                if (edittext.getId() == username.getId() )
                {
                    if (!s.toString().equals(dbusername))
                    {
                        isUsernameChanged = true;
                    }
                    else
                    {
                        isUsernameChanged = false;
                    }
                }
                else if (edittext.getId() == password.getId())
                {
                    if (!s.toString().equals(dbpassword))
                    {
                        isPasswordChanged = true;
                    }
                    else
                    {
                        isPasswordChanged = false;
                    }
                }
                else if (edittext.getId() == phonenum.getId())
                {
                    if (!s.toString().equals(dbphone))
                    {
                        isPhoneChanged = true;
                    }
                    else
                    {
                        isPhoneChanged = false;
                    }
                }

                setSaveButtonVisibility();
            }
        });
    }


    private void setSaveButtonVisibility()
    {
        if (isUsernameChanged || isPasswordChanged || isPhoneChanged)
        {
            save.setVisibility(View.VISIBLE);
        }
        else
        {
            save.setVisibility(View.GONE);
        }
    }

    public void saveDataToFirebase(View view)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User/"+id);

        UserHelperClass updateUserData = new UserHelperClass
                (
                        username.getEditableText().toString(),
                        email.getEditableText().toString(),
                        password.getEditableText().toString(),
                        phonenum.getEditableText().toString()
                );

        reference.child("info").setValue(updateUserData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(Profile.this, "Profile saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Profile.this, "Failed to save", Toast.LENGTH_SHORT).show();
            }
        });

    }
}