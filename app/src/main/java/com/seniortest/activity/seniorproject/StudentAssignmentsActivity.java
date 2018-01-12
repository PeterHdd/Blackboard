package com.seniortest.activity.seniorproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class StudentAssignmentsActivity extends AppCompatActivity {
    private DatabaseReference assignment;
    private String classnames;
    private ListView assignlist;
    private ArrayList<String> listofassign;
    private ArrayAdapter<String> adapter;
    private String titlename;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_assignments);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        classnames=getIntent().getStringExtra("classname");
        assignlist=(ListView)findViewById(R.id.listassignment);
        listofassign=new ArrayList<>();
        assignment= FirebaseDatabase.getInstance().getReference().child("Assignment");
        Query get=assignment.orderByChild("coursename").equalTo(classnames);
        get.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                     titlename=data.child("title").getValue().toString();
                    listofassign.add(titlename);
                    adapter=new ArrayAdapter<String>(StudentAssignmentsActivity.this,android.R.layout.simple_list_item_1,listofassign);
                    assignlist.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }}
                else{
                    Toasty.info(StudentAssignmentsActivity.this,"No assignment curently",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        assignlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String selectedFromList = (String) assignlist.getItemAtPosition(position);
                Intent i=new Intent(StudentAssignmentsActivity.this,DetailAssignmentActivity.class);
                i.putExtra("titlename",selectedFromList);
                i.putExtra("coursename",classnames);
                startActivity(i);

            }
        });

    }

}
