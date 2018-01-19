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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class StudentDetailPDFActivity extends AppCompatActivity {

    private DatabaseReference db;
    private ListView lists;
    private ArrayList<String> stringslists;
    private String classesnames,text;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail_pdf);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        lists=(ListView)findViewById(R.id.detailpdf);
        stringslists=new ArrayList<>();

        classesnames=getIntent().getStringExtra("classnm");
        db= FirebaseDatabase.getInstance().getReference().child("PDF");
        db.orderByChild("cname").equalTo(classesnames).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        final String pdf=data.child("PDFurl").getValue().toString();
                        String s=data.child("Ptitle").getValue().toString();
                        stringslists.add(s);

                        adapter = new ArrayAdapter(StudentDetailPDFActivity.this, android.R.layout.simple_list_item_1, stringslists);
                        lists.setAdapter(adapter);
                        lists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                final String selectedFromList = (String)lists.getItemAtPosition(i);
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(pdf)));

                            }
                        });
                    }
                }
                else{
                    Toasty.info(StudentDetailPDFActivity.this,"No pdf files exists", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

}
