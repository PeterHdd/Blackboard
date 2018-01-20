package com.seniortest.activity.seniorproject;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class TeacherListPDFActivity extends AppCompatActivity {

    private DatabaseReference db;
    private ListView lists;
    private ArrayList<String> stringslists;
    private String text,pdf,s;
    private ArrayAdapter adapter;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail_pdf);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        lists=(ListView)findViewById(R.id.detailpdf);
        stringslists=new ArrayList<>();
        user= FirebaseAuth.getInstance().getCurrentUser();

        db= FirebaseDatabase.getInstance().getReference().child("PDF");
        db.orderByChild("teacherid").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        pdf=data.child("PDFurl").getValue().toString();
                        s=data.child("Ptitle").getValue().toString();
                        stringslists.add(s);

                        adapter = new ArrayAdapter(TeacherListPDFActivity.this, android.R.layout.simple_list_item_1, stringslists);
                        lists.setAdapter(adapter);

                    }
                }
                else{
                    Toasty.info(TeacherListPDFActivity.this,"No pdf files exists", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        lists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String selectedFromList = (String)lists.getItemAtPosition(i);
                db.orderByChild("Ptitle").equalTo(selectedFromList).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot data: dataSnapshot.getChildren()){
                            String pdfs=data.child("PDFurl").getValue().toString();
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(pdfs))); //it will let you download/ view and create a new uri object that references a location in another app

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });

    }

}
