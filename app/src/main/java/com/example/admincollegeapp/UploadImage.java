package com.example.admincollegeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.admincollegeapp.notice.upload_notice;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UploadImage extends AppCompatActivity {
     CardView selectImage;
    private Spinner image_category;
    private Button uploadImage;
    private ImageView galleryImageview;
    private String category;
    private Bitmap bitmap;
    private final int REQ=1;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    String downloadUrl="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_image);

      selectImage  =findViewById(R.id.addGalleryImage1);
        uploadImage=findViewById(R.id.uploadnoticebtn1);
        image_category=findViewById(R.id.image_category);
        galleryImageview=findViewById(R.id.galleryImageview);

        reference= FirebaseDatabase.getInstance().getReference().child("gallery");
        storageReference= FirebaseStorage.getInstance().getReference().child("gallery");

        progressDialog=new ProgressDialog(this);

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap==null){
                    Toast.makeText(UploadImage.this, "please upload image....", Toast.LENGTH_SHORT).show();
                }else if (category.equals("Select Category")){
                    Toast.makeText(UploadImage.this, "select category..", Toast.LENGTH_SHORT).show();
                }

                uploadImage();
            }
        });


        String[] items=new String[]{"Select Category","convocation","Independence Day","Other Event"};
        image_category.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items));

        image_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category=image_category.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }); 
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void uploadImage() {

            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
            byte[] finalImg=baos.toByteArray();
            final StorageReference filePath;
            filePath=storageReference.child(finalImg+"jpg");
            final UploadTask uploadTask =filePath.putBytes(finalImg);
            uploadTask.addOnCompleteListener(UploadImage.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(UploadImage.this, "image is upload", Toast.LENGTH_SHORT).show();
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        downloadUrl=String.valueOf(uri);
                                        uploadData();



                                    }
                                });
                            }
                        });
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(UploadImage.this, "Something wrong...", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    private void uploadData() {
        reference=reference.child(category);
        //for Store uniquekey in variable.
        final String uniqueKey=reference.push().getKey();

        reference.child(uniqueKey).setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(UploadImage.this, "Image Upload...", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UploadImage.this, "Something went wrong....", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent pickImage=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage,1);

       pickImage.setType("image/*");
       pickImage.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(pickImage, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ &&resultCode==RESULT_OK);
        Uri uri=data.getData();
        try {
            bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
        } catch (IOException e) {

            e.printStackTrace();
        }
        galleryImageview.setImageBitmap(bitmap);
    }
    }

