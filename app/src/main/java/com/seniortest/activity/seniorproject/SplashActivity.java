package com.seniortest.activity.seniorproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    private DatabaseReference teacher,student;
    private FirebaseUser user;
    private String dataTitle,dataMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser(); //gets the currentuser that is authenticated
        teacher = FirebaseDatabase.getInstance().getReference().child("Teacher"); //gets references of the node teacher
        student = FirebaseDatabase.getInstance().getReference().child("Student");

        if (user != null) {
            teacher.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() { //query on node
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && user != null) { //checks if exists and if the user is logged in
                        Intent i = new Intent(SplashActivity.this, TeacherNavActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        student.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && user != null) {
                                    Intent i = new Intent(SplashActivity.this, StudentNavActivity.class);
                                    if (getIntent().getExtras() != null) {
                                         dataTitle = getIntent().getExtras().getString("title");
                                         if(dataTitle!=null){
                                        i.putExtra("title", dataTitle);
                                        dataMessage = getIntent().getExtras().getString("message");
                                        i.putExtra("message", dataMessage);
                                         }}
                                         else{

                                    }
                                    startActivity(i);
                                    finish();
                                }
                                else {

                                        Intent i = new Intent(SplashActivity.this, SecondActivity.class);
                                        startActivity(i);
                                        finish();

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            Intent i = new Intent(SplashActivity.this, SecondActivity.class);
            startActivity(i);
            finish();
        }


        }
    }