package com.seniortest.activity.seniorproject;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentPDFActivity extends AppCompatActivity {
    private DatabaseReference db;
    private String classnames;
    private ListView lists;
    private ArrayList<String> listofstrings;
    private ArrayAdapter<String> adapter;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_pdf);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        listofstrings=new ArrayList<String>();
        lists=(ListView)findViewById(R.id.listing);
        user= FirebaseAuth.getInstance().getCurrentUser();
       db = FirebaseDatabase.getInstance().getReference().child("ClassStudent");
       db.orderByChild("studentid").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()){
                   for(DataSnapshot data: dataSnapshot.getChildren()){
                       String values = data.child("classid").getValue().toString();
                       DatabaseReference classjoin = FirebaseDatabase.getInstance().getReference().child("Class");
                       if(values!=null){
                           classjoin.orderByKey().equalTo(values).addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   for(DataSnapshot data: dataSnapshot.getChildren()){
                                       classnames = data.child("Classname").getValue().toString();
                                       listofstrings.add(classnames);
                                       adapter = new ArrayAdapter(StudentPDFActivity.this, android.R.layout.simple_list_item_1, listofstrings);
                                       lists.setAdapter(adapter);
                                   }
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {

                               }
                           });
                       }
                   }
               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
       lists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
               final String selectedFromList = (String)lists.getItemAtPosition(position);
               Intent i=new Intent(StudentPDFActivity.this,StudentDetailPDFActivity.class);
               i.putExtra("classnm",selectedFromList);
               startActivity(i);
           }
       });
    }
}
