package com.seniortest.activity.seniorproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
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

public class AdminAssignmentActivity extends AppCompatActivity {

    private String classassign,s;
    private ListView delassign;
    private ArrayList<String> assiglist;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_assignment);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        classassign=getIntent().getStringExtra("Class");
        delassign=(ListView)findViewById(R.id.deleteassign);
        assiglist=new ArrayList<String>();
        delassign.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("Assignment");
        db.orderByChild("coursename").equalTo(classassign).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        String assignment=data.child("title").getValue().toString();
                        assiglist.add(assignment);
                        adapter = new ArrayAdapter<String>(AdminAssignmentActivity.this, android.R.layout.simple_list_item_multiple_choice, assiglist);
                        delassign.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                } else {
                    Toasty.info(AdminAssignmentActivity.this, "no assignments exists", Toast.LENGTH_SHORT).show();
                }
                    }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    getMenuInflater().inflate(R.menu.delete, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.joinus) {
            int len=delassign.getCount();
            for (int i=len-1; i >=0; i--) { //iterates inside all items
                if(delassign.isItemChecked(i)){

                    s = (String)delassign.getAdapter().getItem(i); //gets the item at position i, and the adapter that the listview is using
                }} //gets the item at position idx, and the adapter that the listview is using
                    if (s != null) {

                        DatabaseReference dbs=FirebaseDatabase.getInstance().getReference().child("Assignment");
                        dbs.orderByChild("title").equalTo(s).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot data: dataSnapshot.getChildren()) {
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
