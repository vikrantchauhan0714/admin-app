package com.example.admincollegeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class UploadPdf extends AppCompatActivity {

    CardView addPdf;
    private final int REQ=1;
    private EditText pdfTitle;
    private Button uploadpdfBtn;
    private DatabaseReference databaseReferencere;
    private StorageReference storageReference;
    String downloadUrl="";
    private ProgressDialog progressDialog;
    private Uri pdfData;
    private TextView pdfTextview;
    private String pdfName,title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);

        databaseReferencere= FirebaseDatabase.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference();
        progressDialog=new ProgressDialog(this);

        pdfTitle=findViewById(R.id.pdfTitle);
        uploadpdfBtn=findViewById(R.id.uploadPdfbtn);
        addPdf=findViewById(R.id.addPdf);
        pdfTextview=findViewById(R.id.pdfTextview);


        addPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        uploadpdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title=pdfTitle.getText().toString();
                if(title.isEmpty()){
                    pdfTitle.setError("Empty");
                    pdfTitle.requestFocus();
                }
                else if (pdfData==null){
                    Toast.makeText(UploadPdf.this, "please upload pdf...", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadPdf();
                }
            }
        });


    }

    private void uploadPdf()
    {
        progressDialog.setTitle("please wait...");
        progressDialog.setMessage("Uploading pdf");
        progressDialog.show();
        StorageReference reference=storageReference.child("Pdf/"+pdfName+"-"+System.currentTimeMillis()+".pdf");
        reference.putFile(pdfData)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uritask =taskSnapshot.getStorage().getDownloadUrl();
                        while(!uritask.isComplete());
                            Uri uri =uritask.getResult();
                            uploadData(String.valueOf(uri));

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UploadPdf.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadData(String downloadUrl) {
        String uniqueKey=databaseReferencere.child("pdf").push().getKey();

        HashMap data=new HashMap();
        data.put("pdfTitle",title);
        data.put("pdfName",pdfName);
        data.put("pdfUrl",downloadUrl);

        databaseReferencere.child("pdf").child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                Toast.makeText(UploadPdf.this, "Pdf uploaded succesfully..", Toast.LENGTH_SHORT).show();
                pdfTitle.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UploadPdf.this, "Failed to upload pdf...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void openGallery() {
       Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
       intent.setType("application/pdf");

       startActivityForResult(Intent.createChooser(intent,"Select Pdf File"),REQ);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ &&resultCode==RESULT_OK);

        pdfData=data.getData();

        if (pdfData.toString().startsWith("content://")){
            Cursor cursor=null;
            try {
                cursor = UploadPdf.this.getContentResolver().query(pdfData, null, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    pdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if (pdfData.toString().startsWith("file://"))
        {
            pdfName=new File(pdfData.toString()).getName();
        }

        pdfTextview.setText(pdfName);

    }
}
