package com.example.admincollegeapp.notice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admincollegeapp.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DeleteNotice extends AppCompatActivity {

    private RecyclerView deletedNoticeRecycle;
    private ProgressBar progressBar;
    private ArrayList <NoticeData> list;
   private NoticeAdapter adapter;
   private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_notice);

        deletedNoticeRecycle=findViewById(R.id.deletedNoticeRecycler);
        progressBar=findViewById(R.id.progressBar);
        reference= FirebaseDatabase.getInstance().getReference().child("Notice");
        deletedNoticeRecycle.setLayoutManager(new LinearLayoutManager(this));
        deletedNoticeRecycle.setHasFixedSize(true);

        getNotice();
    }

    private void getNotice() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list=new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    NoticeData data=snapshot.getValue(NoticeData.class);
                    list.add(data);
                }
                adapter=new NoticeAdapter(DeleteNotice.this,list);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                deletedNoticeRecycle.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(DeleteNotice.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
