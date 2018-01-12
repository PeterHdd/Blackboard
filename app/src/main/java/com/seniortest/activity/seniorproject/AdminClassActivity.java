package com.seniortest.activity.seniorproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class AdminClassActivity extends AppCompatActivity {

    private ListView deleteclasses;
    private DatabaseReference delclass;
    private ArrayList<String> listdelclasses;
    private ArrayAdapter adapter;
    private String s,key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_class);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        delclass = FirebaseDatabase.getInstance().getReference().child("Class");
        deleteclasses = (ListView) findViewById(R.id.deleteclasses);
        deleteclasses.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listdelclasses = new ArrayList<String>();
        delclass.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String classname = data.child("Classname").getValue().toString();
                         key=data.getKey();
                        listdelclasses.add(classname);
                        adapter = new ArrayAdapter<String>(AdminClassActivity.this, android.R.layout.simple_list_item_multiple_choice, listdelclasses);
                        deleteclasses.setAdapter(adapter);
                    }

                } else {
                    Toasty.info(AdminClassActivity.this, "no classes exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        deleteclasses.setLongClickable(true);
        deleteclasses.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String selectedFromList = (String) deleteclasses.getItemAtPosition(position);
                final DatabaseReference retriev = FirebaseDatabase.getInstance().getReference().child("Class");
                retriev.orderByChild("Classname").equalTo(selectedFromList).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot data: dataSnapshot.getChildren()){
                            final String teachid=data.child("teachid").getValue().toString();
                            String key=data.getKey();
                            DatabaseReference referes=FirebaseDatabase.getInstance().getReference().child("Class");
                            referes.orderByKey().equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                                        final String section=datas.child("section").getValue().toString();
                                        DatabaseReference name=FirebaseDatabase.getInstance().getReference().child("Teacher");
                                        name.orderByKey().equalTo(teachid).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for(DataSnapshot data: dataSnapshot.getChildren()){
                                                    String teachname=data.child("name").getValue().toString();

                                                    LayoutInflater li = LayoutInflater.from(AdminClassActivity.this); //inflate converts xml to a view object to use in code
                                                    View promptsView = li.inflate(R.layout.info, null); //two parameters, the xml and the root(null means the layout is a child of viewgroup

                                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                                            AdminClassActivity.this); /*alertdialog.builder class,creates a builder for alert dialog, parameter is context */

                                                    alertDialogBuilder.setView(promptsView);
                                                    final TextView sections = (TextView) promptsView.findViewById(R.id.sections);
                                                    final TextView nameteacher=(TextView)promptsView.findViewById(R.id.nameteacher);
                                                    sections.setText(section);
                                                    nameteacher.setText(teachname);
                                                    alertDialogBuilder.setCancelable(true); //true can be canceled with back key
                                                    alertDialogBuilder.setPositiveButton("Ok",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    dialog.dismiss();


                                                                }
                                                            });
                                                    AlertDialog alertDialog = alertDialogBuilder.create(); //creates alert dialog from builder
                                                    alertDialog.show();

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                    }}

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
                return true;
            }
        });


        deleteclasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String selectedFromList = (String) deleteclasses.getItemAtPosition(position);
                if(deleteclasses.isItemChecked(position)){
                    //do not do anything
                }
                else{
                    Intent i=new Intent(AdminClassActivity.this,AdminAssignmentActivity.class);
                    i.putExtra("Class",selectedFromList);
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
            int len=deleteclasses.getCount(); //gets items in the listview
            for (int i=len-1; i >=0; i--) { //iterates inside all items
                if(deleteclasses.isItemChecked(i)){

                    s = (String)deleteclasses.getAdapter().getItem(i); //gets the item at position i, and the adapter that the listview is using
                }} //gets the item at position idx, and the adapter that the listview is using
                    if (s != null) {
                        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Class");
                        data.orderByChild("Classname").equalTo(s).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot data: dataSnapshot.getChildren()){
                                    data.getRef().removeValue();
                                    adapter.remove(s);
                                    adapter.notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                         DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Assignment");
                         ref.orderByChild("coursename").equalTo(s).addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {
                                 if(dataSnapshot.exists()){
                                     for(DataSnapshot data: dataSnapshot.getChildren()){
                                     data.getRef().removeValue();
                                 }}
                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {

                             }
                         });

                    }
                }
        return super.onOptionsItemSelected(item);
    }


}
