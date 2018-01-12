package com.seniortest.activity.seniorproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class StudentSearchActivity extends AppCompatActivity implements Filterable {

    private SearchView searching;
    private DatabaseReference getclass,refs;
    private ListView listclasses;
    private ArrayList<String> getclasses;
    private ArrayAdapter adapter;
    private String retrieves, keys,classid,keyz;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_search);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        searching = (SearchView) findViewById(R.id.searches);
        listclasses = (ListView) findViewById(R.id.recycler);
        getclass = FirebaseDatabase.getInstance().getReference().child("Class");
        user=FirebaseAuth.getInstance().getCurrentUser();
        getclasses = new ArrayList<>();
        listclasses.setChoiceMode(ListView.CHOICE_MODE_SINGLE); //to choose one item only
        getclass.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                     retrieves = dataSnapshot.child("Classname").getValue().toString();
                     keys=dataSnapshot.getKey();
                    getclasses.add(retrieves);
                    adapter = new ArrayAdapter(StudentSearchActivity.this, android.R.layout.simple_list_item_single_choice, getclasses); //radio button to choose one item single_choice, multiple_choice u choose more than one

                    listclasses.setAdapter(adapter);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        searching.setOnQueryTextListener(new SearchView.OnQueryTextListener() { //create a listener for searchview
            @Override
            public boolean onQueryTextSubmit(String query) { //triggered when search is pressed
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) { //is called when the user types each character in the text field
                if(adapter!=null) {
                    adapter.getFilter().filter(newText);
                }
                else if(adapter==null){
                    Toasty.error(StudentSearchActivity.this,"No classes available yet",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        listclasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });
        listclasses.setLongClickable(true);
        listclasses.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String selectedFromList = (String) listclasses.getItemAtPosition(position);
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

                                                LayoutInflater li = LayoutInflater.from(StudentSearchActivity.this); //inflate converts xml to a view object to use in code
                                                View promptsView = li.inflate(R.layout.info, null); //two parameters, the xml and the root(null means the layout is a child of viewgroup

                                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                                        StudentSearchActivity.this); /*alertdialog.builder class,creates a builder for alert dialog, parameter is context */

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
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.joinus) {
            SparseBooleanArray a = this.listclasses.getCheckedItemPositions(); //gets checked items

            StringBuffer str = new StringBuffer("");
            for (int i=0; i < a.size(); i++){ //iterates inside all items

                if (a.valueAt(i)) { //checks if "a" at that value is checked
                    int idx = a.keyAt(i);//keyat is the position of the item
                    Log.i("integer",""+idx);


                    final String s = (String)this.listclasses.getAdapter().getItem(idx); //gets the item at position idx, and the adapter that the listview is using
                    final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Class");
                    ref.orderByChild("Classname").equalTo(s).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot data:dataSnapshot.getChildren()){
                                final String key=data.getKey(); //classid
                                Log.i("hello","goodbye");  //once
                                 refs=FirebaseDatabase.getInstance().getReference();
                                refs.child("ClassStudent").orderByChild("classid").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) { //condition where studentid=uid
                                        if(dataSnapshot.exists()){ //twice since two people have this class
                                            for(DataSnapshot data: dataSnapshot.getChildren()) {  //iterate
                                                classid = data.child("classid").getValue().toString();
                                                keyz = data.getKey();
                                               final String currentuser=FirebaseAuth.getInstance().getCurrentUser().getUid();//get classid
                                                Log.i("clasnae", "123345");
                                                Log.i("clasnae", keys);
                                                refs.child("ClassStudent").orderByChild("studentid").equalTo(currentuser).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if(dataSnapshot.child(keyz).exists()){
                                                            Toasty.error(StudentSearchActivity.this,"You are already registered in this class",Toast.LENGTH_SHORT).show();
                                                        }
                                                        else{
                                                            Intent intent = new Intent();
                                                            intent.putExtra("value", s.toString());
                                                            setResult(RESULT_OK, intent);
                                                            Log.d("here", s.toString());
                                                            finish();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }

                                            }
                                            else{
                                            Intent intent = new Intent();
                                            intent.putExtra("value", s.toString());
                                            setResult(RESULT_OK, intent);
                                            Log.d("here", s.toString());
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


                }
            }

        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_join, menu);
        return true;

    }

    @Override
    public Filter getFilter() {
        return null;
    }

}
