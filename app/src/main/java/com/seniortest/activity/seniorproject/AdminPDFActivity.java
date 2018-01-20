package com.seniortest.activity.seniorproject;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class AdminPDFActivity extends AppCompatActivity {

    private ListView deletes;
    private DatabaseReference delpdfs;
    private ArrayList<String> listdelpdf;
    private ArrayAdapter adapter;
    private String s;
    private StorageReference refers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pdf);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        delpdfs = FirebaseDatabase.getInstance().getReference().child("PDF");
        deletes=(ListView)findViewById(R.id.deletepdfs);
        deletes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listdelpdf = new ArrayList<String>();
        refers= FirebaseStorage.getInstance().getReference();
        delpdfs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String name = data.child("Ptitle").getValue().toString();
                        listdelpdf.add(name);
                        adapter = new ArrayAdapter<String>(AdminPDFActivity.this, android.R.layout.simple_list_item_multiple_choice, listdelpdf);
                        deletes.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                } else {
                    Toasty.info(AdminPDFActivity.this, "no pdfs exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        deletes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String selectedFromList = (String) deletes.getItemAtPosition(i);
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("PDF");
                ref.orderByChild("Ptitle").equalTo(selectedFromList).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot data: dataSnapshot.getChildren()){
                            String classnames=data.child("cname").getValue().toString();
                            AlertDialog.Builder builder=new AlertDialog.Builder(AdminPDFActivity.this);
                            builder.setTitle("Class");
                            builder.setMessage("Class: " + classnames);
                            builder.setPositiveButton("OK", null);
                            builder.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.joinus) {
            int len=deletes.getCount();
            for (int i=len-1; i >=0; i--) { //iterates inside all items
                if(deletes.isItemChecked(i)){

                    s = (String)deletes.getAdapter().getItem(i); //gets the item at position i, and the adapter that the listview is using
                }} //gets the item at position idx, and the adapter that the listview is using
            if(s!=null){

                DatabaseReference data=FirebaseDatabase.getInstance().getReference().child("PDF");
                data.orderByChild("Ptitle").equalTo(s).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot data: dataSnapshot.getChildren()) {
                            String pdfurls=data.child("PDFurl").getValue().toString();
                            refers= FirebaseStorage.getInstance().getReferenceFromUrl(pdfurls);
                            refers.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                }
                            });
                            data.getRef().removeValue();
                            adapter.remove(s);
                            adapter.notifyDataSetChanged();
                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }}

        return super.onOptionsItemSelected(item);
    }
}
