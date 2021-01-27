package com.example.admincollegeapp.faculty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.admincollegeapp.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class Update_Teacher extends AppCompatActivity {
 private ImageView updateteacherImage;
 private TextView updateTeacherName,updateTeacherEmail,updateTeacherPost;
 private Button updateTeacherBtn,deleteTeacherBtn;
 private String name,email,post,image,downloadUrl,category,uniqueKey;
 private Bitmap bitmap=null;
 private final int REQ=1;
    private ProgressDialog progressDialog;

    private StorageReference storageReference;
    private DatabaseReference reference,dbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update__teacher);

        name=getIntent().getStringExtra("name");
        email=getIntent().getStringExtra("email");
        post=getIntent().getStringExtra("post");
        image=getIntent().getStringExtra("image");

         uniqueKey=getIntent().getStringExtra("key");
       category=getIntent().getStringExtra("category");



        updateteacherImage=findViewById(R.id.updateteacherImage);
        updateTeacherName=findViewById(R.id.updateTeacherName);
        updateTeacherEmail=findViewById(R.id.updateTeacherEmail);
        updateTeacherPost=findViewById(R.id.updateTeacherPost);
        updateTeacherBtn=findViewById(R.id.updateTeacherBtn);
        deleteTeacherBtn=findViewById(R.id.deleteTeacherBtn);

        reference=FirebaseDatabase.getInstance().getReference().child("teacher");
        storageReference= FirebaseStorage.getInstance().getReference();

        progressDialog=new ProgressDialog(this);

        try {
            Picasso.get().load(image).placeholder(R.drawable.profile).into(updateteacherImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateTeacherName.setText(name);
        updateTeacherEmail.setText(email);
        updateTeacherPost.setText(post);


        updateteacherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        updateTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=updateTeacherName.getText().toString();
                email=updateTeacherEmail.getText().toString();
                post=updateTeacherPost.getText().toString();



                checkValidation();
            }
        });

        deleteTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });



    }

    private void deleteData() {

        reference.child(category).child(uniqueKey).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(Update_Teacher.this, "Teacher deleted successfully...", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(Update_Teacher.this,UpdateFaculty.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Update_Teacher.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkValidation() {
        if(name.isEmpty()){
            updateTeacherName.setError("Empty");
            updateTeacherName.requestFocus();
        }else  if(email.isEmpty()){
            updateTeacherEmail.setError("Empty");
            updateTeacherEmail.requestFocus();
        }else  if(post.isEmpty()){
            updateTeacherPost.setError("Empty");
            updateTeacherPost.requestFocus();
        } else if(bitmap==null){


            updateData(image);


        }else{
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            uploadImage();
        }
    }

    private void uploadImage() {

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalImg=baos.toByteArray();
        final StorageReference filePath;
        filePath=storageReference.child("Teachers").child(finalImg+"jpg");
        final UploadTask uploadTask =filePath.putBytes(finalImg);
        uploadTask.addOnCompleteListener(Update_Teacher.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl=String.valueOf(uri);
                                    updateData(downloadUrl);
                                }
                            });
                        }
                    });
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(Update_Teacher.this, "Something wrong...", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void updateData(String s) {
        HashMap hp=new HashMap();
        hp.put("name",name);
        hp.put("email",email);
        hp.put("post",post);
        hp.put("image",s);

        String uniqueKey=getIntent().getStringExtra("key");
        String category=getIntent().getStringExtra("category");
        reference.child(category).child(uniqueKey).updateChildren(hp).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(Update_Teacher.this, "Teacher updated successfully...", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Update_Teacher.this,UpdateFaculty.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Update_Teacher.this, "Something went wrong..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent pickImage=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage,1);
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
        updateteacherImage.setImageBitmap(bitmap);
    }
}
