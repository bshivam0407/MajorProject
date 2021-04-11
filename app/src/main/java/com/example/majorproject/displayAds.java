package com.example.majorproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class displayAds extends AppCompatActivity {

    ImageView imageView1, imageView2;
    String img1,img2;
    double prob1=0,prob2=0;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    final List<String> tags= new ArrayList<>();
    List<Image> arrayOfImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ads);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        Intent myIntent = getIntent();
        arrayOfImage = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("MAJOR PROJECT");
        storageReference = FirebaseStorage.getInstance().getReference("Test Image");
        databaseReference.child("outputTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i=0;i<3;i++) {
                    // category is an ArrayList you can declare above
                    Log.i("MESSAGE",(snapshot.child(String.valueOf(i)).getValue(String.class)));
                    tags.add(snapshot.child(String.valueOf(i)).getValue(String.class));
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
                    Image image = dataSnapshot.getValue(Image.class);
                    arrayOfImage.add(image);
                    Log.i("Message", image.getTags().get(0) + " "+ dataSnapshot.getKey());
                }
                displayImages();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    void displayImages()
    {
        for(int i=0;i<arrayOfImage.size();i++) {
            double prob = findProbility(arrayOfImage.get(i).getTags());
            if(prob1 == 0)
            {
                prob1 = prob;
                img1 = arrayOfImage.get(i).getId();
            }
            else if(prob2 == 0)
            {
                if(prob>=prob1)
                {
                    prob2=prob1;
                    img2=img1;
                    prob1=prob;
                    img1=arrayOfImage.get(i).getId();
                }
                else
                {
                    prob2 = prob;
                    img2=arrayOfImage.get(i).getId();
                }
            }
            else
            {
                if(prob>=prob1)
                {
                    prob2=prob1;
                    img2=img1;
                    prob1=prob;
                    img1=arrayOfImage.get(i).getId();
                }
                else if(prob>prob2)
                {
                    prob2 = prob;
                    img2=arrayOfImage.get(i).getId();
                }
            }

        }
        Log.i("Message",img1);
        Log.i("Message",img2);
        Picasso
                .get()
                .load(img1)
                .into(imageView1);
        Picasso
                .get()
                .load(img1)
                .into(imageView2);



    }
    double findProbility(List<String> imageTags)
    {
        int count = 0;
        try {
            if(imageTags.get(0).contains(tags.get(0)))
                count++;
            if(imageTags.get(1).contains(tags.get(1)))
                count++;
            if(imageTags.get(2).contains(tags.get(2)) || imageTags.get(2) == "none" || tags.get(2) == "none")
                count++;
        }
        catch (Exception e)
        {
            Log.i("Error",e.toString());
        }

        return (double)count/3.0;

    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }
}