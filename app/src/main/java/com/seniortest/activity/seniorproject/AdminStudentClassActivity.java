package com.seniortest.activity.seniorproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class AdminStudentClassActivity extends AppCompatActivity {

    private String classstudent;
    private ListView studentclass;
    private DatabaseReference classstudents;
    private ArrayList<String> studentclasses;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_student_class);
        classstudent=getIntent().getStringExtra("studentname");
        studentclass=(ListView)findViewById(R.id.studentclasses);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        classstudents = FirebaseDatabase.getInstance().getReference().child("ClassStudent");
        studentclasses = new ArrayList<String>();
        classstudents.orderByChild("name").equalTo(classstudent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                for(DataSnapshot datas:dataSnapshot.getChildren()){

                    String name=datas.child("classid").getValue().toString();
                    DatabaseReference classids = FirebaseDatabase.getInstance().getReference().child("Class");
                    classids.orderByKey().equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot datas: dataSnapshot.getChildren()){
                                if(datas.exists()) {
                                    String classname = datas.child("Classname").getValue().toString();
                                    studentclasses.add(classname);
                                    adapter = new ArrayAdapter<String>(AdminStudentClassActivity.this, android.R.layout.simple_list_item_1, studentclasses);
                                    studentclass.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }

                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }
            else{
                    Toasty.info(AdminStudentClassActivity.this, "no classes exists for this student", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
