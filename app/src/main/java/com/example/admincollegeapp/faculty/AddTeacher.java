package com.example.admincollegeapp.faculty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.admincollegeapp.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddTeacher extends AppCompatActivity {

    private ImageView addTeacherImg;
    private TextView addTeacherName,addTeacherEmail,addTeacherPost;
    private Spinner addTeachercategory;
    private Button addTeacherBtn;
    private Bitmap bitmap;
    private final int REQ=1;
    String  category;
    private String name,email,post,downloadUrl;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private DatabaseReference reference,dbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);
        addTeacherImg=findViewById(R.id.addTeacherimg);
        addTeacherName=findViewById(R.id.addTeachrname);
        addTeacherEmail=findViewById(R.id.addTeachrEmail);
        addTeacherPost=findViewById(R.id.addTeachrPost);
        addTeachercategory=findViewById(R.id.addteachercategory);
        addTeacherBtn=findViewById(R.id.addTeacherbtn);

        progressDialog=new ProgressDialog(this);//initialize progress dialog

        reference= FirebaseDatabase.getInstance().getReference().child("teacher");
        storageReference= FirebaseStorage.getInstance().getReference();


        String[] items=new String[]{"Select Category","Computer Science","Mechanical","Physics","Chemistry"};
        addTeachercategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items));

        addTeachercategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category=addTeachercategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        addTeacherImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkValidation();
                Intent intent=new Intent(AddTeacher.this,UpdateFaculty.class);
                startActivity(intent);
            }
        });





    }

    private void checkValidation() {
       name=addTeacherName.getText().toString();
       email=addTeacherEmail.getText().toString();
       post=addTeacherPost.getText().toString();

       if(name.isEmpty()) {
           addTeacherName.setError("Empty");
           addTeacherName.requestFocus();

       }
       else if(email.isEmpty()) {
           addTeacherEmail.setError("Empty");
           addTeacherEmail.requestFocus();
       }
       else  if(post.isEmpty()) {
           addTeacherPost.setError("Empty");
           addTeacherPost.requestFocus();


       }
       else if(category.equals("Select Category")){
           Toast.makeText(this, "Please provide teacher category", Toast.LENGTH_SHORT).show();
       }
       else if(bitmap==null){
           progressDialog.setMessage("Uploading...");
           progressDialog.show();
           insertData();
       }
       else
       {  progressDialog.setMessage("Uploading...");
           progressDialog.show();
           uploadImage();
       }
    }

    private void insertData()
    {
        dbRef=reference.child(category);
        //for Store uniquekey in variable.
        final String uniqueKey=dbRef.push().getKey();





        TeacherData teacherData=new TeacherData(name,email,post,downloadUrl,uniqueKey);

        //store data in firebase
        dbRef.child(uniqueKey).setValue(teacherData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(AddTeacher.this, "Teacher added...", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddTeacher.this, "Something went wrong....", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadImage() {

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalImg=baos.toByteArray();
        final StorageReference filePath;
        filePath=storageReference.child("Teachers").child(finalImg+"jpg");
        final UploadTask uploadTask =filePath.putBytes(finalImg);
        uploadTask.addOnCompleteListener(AddTeacher.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                    insertData();
                                }
                            });
                        }
                    });
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(AddTeacher.this, "Something wrong...", Toast.LENGTH_SHORT).show();
                }
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
        addTeacherImg.setImageBitmap(bitmap);
    }
}

