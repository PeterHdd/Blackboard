package com.seniortest.activity.seniorproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailAssignmentActivity extends AppCompatActivity {

   private Button shares;
   private String coursname,titlename,body,title;
   private DatabaseReference assigns;
   private TextView bodyassigned,titleassigned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_assignment);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        coursname=getIntent().getStringExtra("coursename");
        titlename=getIntent().getStringExtra("titlename");
        shares=(Button)findViewById(R.id.shares);
       bodyassigned=(TextView)findViewById(R.id.bodyofassign);
       titleassigned=(TextView)findViewById(R.id.titleofassign);
        assigns= FirebaseDatabase.getInstance().getReference().child("Assignment");
        assigns.orderByChild("coursename").equalTo(coursname).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                     title=data.child("title").getValue().toString();
                    if(title.equals(titlename)){
                       DatabaseReference assignedtitle= FirebaseDatabase.getInstance().getReference().child("Assignment");
                       assignedtitle.orderByChild("title").equalTo(titlename).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {
                                   for (DataSnapshot data : dataSnapshot.getChildren()) {
                                           String coursen=data.child("coursename").getValue().toString();
                                           if(coursname.equals(coursen)) {
                                               body = data.child("body").getValue().toString();
                                               titleassigned.setText(titlename);
                                               bodyassigned.setText(body);
                                           }


                                   }

                           }

                           @Override
                           public void onCancelled(DatabaseError databaseError) {

                           }
                       });

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        shares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, titlename);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT," Your "+ coursname + " Assignment is: "+" \n "+ body);
                startActivity(Intent.createChooser(sharingIntent,"Choose the following social media"));

            }
        });

    }

}
