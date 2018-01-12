package com.seniortest.activity.seniorproject;

import android.content.Intent;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class AdminStudentActivity extends AppCompatActivity {

    private ListView deletestudent;
    private DatabaseReference delstudent;
    private ArrayList<String> listdelstudent;
    private ArrayAdapter adapter;
    private String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_student);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        delstudent = FirebaseDatabase.getInstance().getReference().child("Student");
        deletestudent=(ListView)findViewById(R.id.deletestudents);
        deletestudent.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listdelstudent = new ArrayList<String>();
       delstudent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String name = data.child("name").getValue().toString();
                        listdelstudent.add(name);
                        adapter = new ArrayAdapter<String>(AdminStudentActivity.this, android.R.layout.simple_list_item_multiple_choice, listdelstudent);
                        deletestudent.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                } else {
                    Toasty.info(AdminStudentActivity.this, "no students exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        deletestudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String selectedFromList = (String) deletestudent.getItemAtPosition(position);
                if(deletestudent.isItemChecked(position)){
                    //do not do anything
                }
                else{
                    Intent i=new Intent(AdminStudentActivity.this,AdminStudentClassActivity.class);
                    i.putExtra("studentname",selectedFromList);
                    startActivity(i);
                }

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
            int len=deletestudent.getCount();
            for (int i=len-1; i >=0; i--) { //iterates inside all items
                if(deletestudent.isItemChecked(i)){

                    s = (String)deletestudent.getAdapter().getItem(i); //gets the item at position i, and the adapter that the listview is using
                }} //gets the item at position idx, and the adapter that the listview is using
                    if(s!=null){

                        DatabaseReference data=FirebaseDatabase.getInstance().getReference().child("Student");
                        data.orderByChild("name").equalTo(s).addListenerForSingleValueEvent(new ValueEventListener() {
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
