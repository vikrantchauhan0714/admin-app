package com.example.admincollegeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.admincollegeapp.faculty.UpdateFaculty;
import com.example.admincollegeapp.notice.DeleteNotice;
import com.example.admincollegeapp.notice.upload_notice;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
 CardView uploadNotice,addgalleryImage,addEBook,faculty,addDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uploadNotice=findViewById(R.id.addNotice);
        addgalleryImage=findViewById(R.id.addGalleryImage);
        addEBook=findViewById(R.id.addEBook);
        faculty=findViewById(R.id.addFaculty);
        addDelete=findViewById(R.id.addDelete);
        uploadNotice.setOnClickListener(this);
        addgalleryImage.setOnClickListener(this);
        addEBook.setOnClickListener(this);
        faculty.setOnClickListener(this);
        addDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch(view.getId()){
            case R.id.addNotice:
                 intent=new Intent(MainActivity.this, upload_notice.class);
                startActivity(intent);
                break;
            case R.id.addGalleryImage:
                 intent=new Intent(MainActivity.this,UploadImage.class);
                startActivity(intent);
                break;
            case R.id.addEBook:
                 intent=new Intent(MainActivity.this,UploadPdf.class);
                startActivity(intent);
                break;
            case R.id.addFaculty:
                intent=new Intent(MainActivity.this, UpdateFaculty.class);
                startActivity(intent);
                break;
            case R.id.addDelete:
                intent=new Intent(MainActivity.this, DeleteNotice.class);
                startActivity(intent);
                break;
        }


    }
}
