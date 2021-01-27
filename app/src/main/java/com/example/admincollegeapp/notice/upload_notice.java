package com.example.admincollegeapp.notice;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.admincollegeapp.R;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class upload_notice extends AppCompatActivity {
CardView addImage;
private final int REQ=1;
private Bitmap bitmap;
private ImageView noticeImageView;
private EditText noticeTitle;
private Button uploadNoticeBtn;
private DatabaseReference reference,dbRef;
private StorageReference storageReference;
String downloadUrl="";
private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notice);
       //for storeDatabase in firebase.
        reference= FirebaseDatabase.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference();
         progressDialog=new ProgressDialog(this);

       noticeImageView =findViewById(R.id.noticeImageview);
       noticeTitle=findViewById(R.id.noticeTitle);
       uploadNoticeBtn=findViewById(R.id.uploadnoticebtn);
       addImage=findViewById(R.id.addGalleryImage);


        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        uploadNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noticeTitle.getText().toString().isEmpty()){
                    noticeTitle.setError("Empty");
                    noticeTitle.requestFocus();
                }
                else if(bitmap==null){
                    uploadData();
                }
                else{
                    uploadImage();
                }



            }
        });

    }


//day 3.......
    private void uploadImage() {
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalImg=baos.toByteArray();
        final StorageReference filePath;
        filePath=storageReference.child("Notice").child(finalImg+"jpg");
        final UploadTask uploadTask =filePath.putBytes(finalImg);
        uploadTask.addOnCompleteListener(upload_notice.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(upload_notice.this, "image is upload", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(upload_notice.this, "Something wrong...", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    private void uploadData() {
        dbRef=reference.child("Notice");
        //for Store uniquekey in variable.
        final String uniqueKey=dbRef.push().getKey();

        String title= noticeTitle.getText().toString();
        //for store date
        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd/MM/yy");
        String date=currentDate.format(calForDate.getTime());


        Calendar calForTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");
        String time=currentTime.format(calForTime.getTime());

        NoticeData noticeData=new NoticeData(title,downloadUrl,date,time,uniqueKey);

        //store data in firebase
        dbRef.child(uniqueKey).setValue(noticeData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(upload_notice.this, "Notice Upload...", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(upload_notice.this, "Something went wrong....", Toast.LENGTH_SHORT).show();
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
        noticeImageView.setImageBitmap(bitmap);
    }
}
