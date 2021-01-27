package com.example.admincollegeapp.faculty;
import com.example.admincollegeapp.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UpdateFaculty extends AppCompatActivity {
FloatingActionButton floatingActionButton;
private RecyclerView csDepartment,physicsDepartment,mechDepartment,chemDepartment;
private LinearLayout csNoData,mcNoData,physicsNoData,chemNoData;
private List<TeacherData> list1,list2,list3,list4;

private DatabaseReference reference,dbRef;
TeacherAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_faculty);

        floatingActionButton=findViewById(R.id.floatingbtn);
        //allRecyclerView
        csDepartment=findViewById(R.id.csDepartment);
        physicsDepartment=findViewById(R.id.physDepartment);
        mechDepartment=findViewById(R.id.mechDepartment);
        chemDepartment=findViewById(R.id.chemDepartment);
        //not data
        csNoData=findViewById(R.id.csNoData);
        mcNoData=findViewById(R.id.mcNoData);
        physicsNoData=findViewById(R.id.physicsNoData);
        chemNoData=findViewById(R.id.chemNoData);

        reference= FirebaseDatabase.getInstance().getReference().child("teacher");

        csDepartment();
        mechDepartment();
        physicsDepartment();
        chemDepartment();


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateFaculty.this,AddTeacher.class));
            }
        });
    }






    private void csDepartment() {
        dbRef=reference.child("Computer Science");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list1=new ArrayList<>();
                if(!dataSnapshot.exists()){
                    csNoData.setVisibility(View.VISIBLE);
                    csDepartment.setVisibility(View.GONE);
                }else{
                    csNoData.setVisibility(View.GONE);
                    csDepartment.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        TeacherData data=snapshot.getValue(TeacherData.class);
                        list1.add(data);

                    }

                    csDepartment.setHasFixedSize(true);
                    csDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));

                    adapter=new TeacherAdapter(list1,UpdateFaculty.this,"Computer Science");
                    csDepartment.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateFaculty.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mechDepartment() {
        dbRef=reference.child("Mechanical");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list2=new ArrayList<>();
                if(!dataSnapshot.exists()){
                    mcNoData.setVisibility(View.VISIBLE);
                    mechDepartment.setVisibility(View.GONE);
                }else{
                    mcNoData.setVisibility(View.GONE);
                    mechDepartment.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        TeacherData data=snapshot.getValue(TeacherData.class);
                        list2.add(data);

                    }

                    mechDepartment.setHasFixedSize(true);
                    mechDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));

                    adapter=new TeacherAdapter(list2,UpdateFaculty.this,"Mechanical");
                    mechDepartment.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateFaculty.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void physicsDepartment() {
        dbRef=reference.child("Physics");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list3=new ArrayList<>();
                if(!dataSnapshot.exists()){
                    physicsNoData.setVisibility(View.VISIBLE);
                    physicsDepartment.setVisibility(View.GONE);
                }else{
                    physicsNoData
                            .setVisibility(View.GONE);
                    physicsDepartment .setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        TeacherData data=snapshot.getValue(TeacherData.class);
                        list3.add(data);

                    }

                    physicsDepartment.setHasFixedSize(true);
                    physicsDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));

                    adapter=new TeacherAdapter(list3,UpdateFaculty.this,"Physics");
                    physicsDepartment.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateFaculty.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void chemDepartment() {
        dbRef=reference.child("Chemistry");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list4=new ArrayList<>();
                if(!dataSnapshot.exists()){
                    chemNoData.setVisibility(View.VISIBLE);
                    chemDepartment.setVisibility(View.GONE);
                }else{
                    chemNoData.setVisibility(View.GONE);
                    chemDepartment.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        TeacherData data=snapshot.getValue(TeacherData.class);
                        list4.add(data);

                    }

                    chemDepartment.setHasFixedSize(true);
                    chemDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));

                    adapter=new TeacherAdapter(list4,UpdateFaculty.this,"Chemistry");
                    chemDepartment.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateFaculty.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
