package com.seniortest.activity.seniorproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class AssignmentCreationActivity extends AppCompatActivity {

    private FirebaseUser user;
    private Spinner spinner;
    private DatabaseReference db;
    private ArrayList<String> classes;
    private EditText title, body;
    private Button btn;
    private String spinners;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        user= FirebaseAuth.getInstance().getCurrentUser();
        spinner=(Spinner)findViewById(R.id.spinner);
        title=(EditText)findViewById(R.id.editTexttitle);
        body=(EditText)findViewById(R.id.editTextBody);
        btn=(Button)findViewById(R.id.save);
        classes=new ArrayList<>();
        db= FirebaseDatabase.getInstance().getReference().child("Class");
        db.orderByChild("teachid").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    String clsnames=data.child("Classname").getValue().toString();
                    classes.add(clsnames);
                    ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(AssignmentCreationActivity.this, android.R.layout.simple_spinner_item, classes);
                    aAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //defines the layout dropdown view
                    spinner.setAdapter(aAdapter);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(AssignmentCreationActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
                builder1.setMessage("Please note if saved it cannot be changed");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Confirm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final String titles=title.getText().toString();
                               final String bodys=body.getText().toString();

                                     if(spinner.getSelectedItem()!=null) {
                                         spinners = spinner.getSelectedItem().toString();
                                     }

                               if(spinners==null){
                                   Toasty.info(AssignmentCreationActivity.this,"there is no class", Toast.LENGTH_SHORT).show();
                                   return;
                               }
                                if(TextUtils.isEmpty(titles)){
                                    Toasty.info(AssignmentCreationActivity.this,"Please write title", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if(TextUtils.isEmpty(bodys)){
                                    Toasty.info(AssignmentCreationActivity.this,"Please write assignment", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if(TextUtils.isEmpty(spinners)){
                                    Toasty.info(AssignmentCreationActivity.this,"Please choose class", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                DatabaseReference refs=FirebaseDatabase.getInstance().getReference().child("Assignment");
                                Query q=refs.orderByChild("title").equalTo(titles);
                                q.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                           if(dataSnapshot.exists()){
                                               Toasty.error(AssignmentCreationActivity.this,"Title already exists, Please write another one",Toast.LENGTH_SHORT).show();
                                               return;
                                           }
                                            else{
                                                DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Assignment").push();
                                                ref.child("coursename").setValue(spinners);
                                                ref.child("title").setValue(titles);
                                                ref.child("body").setValue(bodys);
                                                Intent i =new Intent(AssignmentCreationActivity.this,TeacherNavActivity.class);
                                                startActivity(i);
                                                Toasty.success(AssignmentCreationActivity.this,"Assignment send to students",Toast.LENGTH_SHORT).show();
                                                finish();

                                            }
                                        }



                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            }
                        });

                builder1.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(AssignmentCreationActivity.this,TeacherNavActivity.class);
        startActivity(i);
        finish();
    }
}
