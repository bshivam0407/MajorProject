package com.example.majorproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button uploadButton;
    private DatabaseReference databaseReference;
    private  StorageReference storageReference;
    private static  final  int PICK_IMAGE=1;
    int imageCountFirebase=0;
    ArrayList<Uri> ImageUriList = new ArrayList<>();
    ArrayList<byte[]> ImageArray= new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uploadButton= (Button) findViewById(R.id.upload);

        databaseReference =FirebaseDatabase.getInstance().getReference("MAJOR PROJECT");
        storageReference = FirebaseStorage.getInstance().getReference("MAJOR PROJECT");

        Log.i("LOG_TAG",Integer.toString(imageCountFirebase));
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageCount();


            }
        });

    }
    void uploadImage()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK)
        {
            if(data.getClipData()!=null)
            {
                int count = data.getClipData().getItemCount();
                for(int i=0;i<count;i++)
                {
                    Uri ImageUri = data.getClipData().getItemAt(i).getUri();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), ImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    scaled.compress(Bitmap.CompressFormat.JPEG,50, stream);
                    byte[] byteArray = stream.toByteArray();
                    ImageUriList.add(ImageUri);
                    ImageArray.add(byteArray);
                }
                uploadImageToFirebase(0,count);
            }
        }

    }
    void uploadImageToFirebase(int i,int count){
        if(i==count)
        {
            Toast.makeText(MainActivity.this,"Image Uploaded",Toast.LENGTH_SHORT).show();
            databaseReference.child("ImageCount").setValue(imageCountFirebase);
        }
        else
        {
            final StorageReference ImageName =storageReference.child("Img" + Integer.toString(imageCountFirebase));
            ImageName.putBytes(ImageArray.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url =uri.toString();
                            databaseReference.child("Images").child(Integer.toString(imageCountFirebase)).child("id").setValue(url).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    imageCountFirebase++;
                                    uploadImageToFirebase(i+1,count);
                                }
                            });
                        }
                    });

                }
            });
        }
    }
    void  getImageCount(){
        databaseReference.child("ImageCount").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this,"Error, try again!",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    String count= task.getResult().getValue().toString();
                    imageCountFirebase =  Integer.parseInt(count);
                    uploadImage();

                }
            }
        });

    }


}