package com.example.majorproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class displayAds extends AppCompatActivity {

    ImageView imageView1, imageView2;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    final String[] tags= new String[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ads);
        Intent myIntent = getIntent();


        databaseReference = FirebaseDatabase.getInstance().getReference("MAJOR PROJECT");
        storageReference = FirebaseStorage.getInstance().getReference("Test Image");
        databaseReference.child("outputTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i=0;i<3;i++) {
                    // category is an ArrayList you can declare above
                    Log.i("MESSAGE",(snapshot.child(String.valueOf(i)).getValue(String.class)));
                    tags[i] = snapshot.child(String.valueOf(i)).getValue(String.class);
                    retrieveImages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    void retrieveImages()
    {
        databaseReference.child("Images").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Log.i("Message",dataSnapshot.getValue().toString());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}