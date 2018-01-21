package com.seniortest.activity.seniorproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;


import de.hdodenhof.circleimageview.CircleImageView;

public class StudentNavActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView nav;
    private DatabaseReference retrievedata, newtable;
    private CircleImageView image;
    private TextView name1,hides;
    private Button joinbtn;
    private static final int Result_code=0;
    ArrayList<String> returndata;
    private ArrayAdapter adapter;
    private FirebaseUser currentuser;
    private String retclass;
    ArrayList alists;
    private ListView views1;
    private String values,s,dataTitle,datamessage;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_nav);
        retrievedata = FirebaseDatabase.getInstance().getReference().child("Student");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        toolbar = (Toolbar) findViewById(R.id.toolbar); //declare the toolbar in java code, it is created in xml
        setSupportActionBar(toolbar);  //method to set toolbar as the actionbar
        returndata=new ArrayList<>();
        image = (CircleImageView) findViewById(R.id.imgnav);
        hides=(TextView)findViewById(R.id.hide);

        joinbtn=(Button)findViewById(R.id.joinbtn);
            currentuser=FirebaseAuth.getInstance().getCurrentUser();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);  //declare the drawerlayout in java code, it is created in xml
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //ActionbarDrawerToggle it adds the hamburger icon in toolbar

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        alists=new ArrayList<String>();
        views1=(ListView)findViewById(R.id.listView2);
        views1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        nav = (NavigationView) findViewById(R.id.nav_view); //declare nav bar in java code, created in xml
        image = (CircleImageView) nav.getHeaderView(0).findViewById(R.id.imgnav);
        name1 = (TextView) nav.getHeaderView(0).findViewById(R.id.name);
        if (getIntent()!=null && getIntent().getExtras() != null) {
                        dataTitle = getIntent().getExtras().getString("title");
                        datamessage = getIntent().getExtras().getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Message");
                        builder.setMessage("title: " + dataTitle + "\n" + "message: " + datamessage);
                        builder.setPositiveButton("OK", null);
                        builder.show();

                   }

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id==R.id.recievepdf){
                    Intent i=new Intent(StudentNavActivity.this,StudentPDFActivity.class);
                    startActivity(i);

                }

                if (id == R.id.update_profile) {
                    Intent i=new Intent(StudentNavActivity.this,UpdateProfileActivity.class);
                    startActivity(i);


                } else if (id == R.id.nav_share) {
                    Intent i=new Intent(Intent.ACTION_VIEW);
                    i.setType("vnd.android-dir/mms-sms"); //type sms
                    i.putExtra("sms_body","Get this app from the Store!");
                    startActivity(i);

                } else if (id == R.id.nav_send) {
                    FirebaseAuth.getInstance().signOut();
                    Intent i=new Intent(StudentNavActivity.this,StudentLoginActivity.class);
                    startActivity(i);
                    finish();

                }
                else if(id==R.id.support){
                    Intent i=new Intent(StudentNavActivity.this,SupportsActivity.class);
                    startActivity(i);

                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        joinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(StudentNavActivity.this,StudentSearchActivity.class);
                startActivityForResult(i,Result_code); //provide a result code and opens new activity
            }
        });

        retrievedata.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {//to retrieve data
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Datasnapshot contains data from a firebase database location
                String name = dataSnapshot.child("name").getValue().toString(); //child has to be same as in database
                name1.setText(name);
                String url = dataSnapshot.child("image").getValue().toString();
                Glide.with(StudentNavActivity.this).load(url).into(image);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        views1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String selectedFromList = (String) views1.getItemAtPosition(position);
                if(views1.isItemChecked(position)){
                     //do not do anything

                }
                else{
                    Intent intent=new Intent(StudentNavActivity.this,StudentAssignmentsActivity.class);
                    intent.putExtra("classname",selectedFromList);
                    startActivity(intent);
                }

            }
        });
    }

        @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.student_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if(id ==R.id.joinus){
            int len=views1.getCount();
            for (int i=len-1; i >=0; i--) { //iterates inside all items
                if(views1.isItemChecked(i)){

                    s = (String)views1.getAdapter().getItem(i); //gets the item at position i, and the adapter that the listview is using
               }}
                    if(s!=null) {
                        DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("Class");
                        db.orderByChild("Classname").equalTo(s).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(final DataSnapshot data: dataSnapshot.getChildren()){
                                        final String key=data.getKey();
                                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("ClassStudent");
                                        ref.orderByChild("studentid").equalTo(currentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                   for(DataSnapshot data: dataSnapshot.getChildren()){
                                                       final String classid=data.child("classid").getValue().toString();
                                                       String keys=data.getKey();
                                                    DatabaseReference refs=FirebaseDatabase.getInstance().getReference().child("ClassStudent").child(keys);
                                                    refs.orderByChild("classid").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if(classid.equals(key)){
                                                                dataSnapshot.getRef().removeValue();
                                                                adapter.remove(s);
                                                                FirebaseMessaging.getInstance().unsubscribeFromTopic(s);
                                                                adapter.notifyDataSetChanged();
                                                                if(adapter.isEmpty()){
                                                                    hides.setVisibility(View.VISIBLE);
                                                                }
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                  }}
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) { //method is called when second activity calls setResult and finishes
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Result_code) { //checks if requestcode is equal to the result code sent
            if (resultCode == RESULT_OK) {  //checks if resultcode is equal to the result_ok received

                // get String data from Intent
                final String returnString = data.getStringExtra("value");
                returndata.add(returnString);
                // set text view with string
                HashSet<String> hashSet = new HashSet<String>();
                hashSet.addAll(returndata);
                returndata.clear();
                returndata.addAll(hashSet);
                if(returndata.size()==0){
                    hides.setVisibility(View.VISIBLE);
                }
                else{
                    hides.setVisibility(View.GONE);
                }

                //Alphabetic sorting.
                Collections.sort(returndata);

                adapter=new ArrayAdapter(StudentNavActivity.this,android.R.layout.simple_list_item_single_choice,returndata);
                views1.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                FirebaseMessaging.getInstance().subscribeToTopic(returnString);
                final DatabaseReference getid=FirebaseDatabase.getInstance().getReference().child("Class");
               ValueEventListener valueEventListener1= new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       for(DataSnapshot child: dataSnapshot.getChildren()) {
                           String classnames = child.child("Classname").getValue().toString();
                           if (returnString.equals(classnames)) {
                               String getids = child.getKey();
                               newtable = FirebaseDatabase.getInstance().getReference().child("ClassStudent").push();
                               newtable.child("classid").setValue(getids);
                               newtable.child("studentid").setValue(currentuser.getUid());
                               final DatabaseReference students=FirebaseDatabase.getInstance().getReference().child("Student");
                               ValueEventListener listener=new ValueEventListener() {
                                   @Override
                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                       for(DataSnapshot data: dataSnapshot.getChildren()){
                                           String studentid=data.getKey();
                                       String students=currentuser.getUid();
                                       if(studentid.equals(students)) {
                                           String names = data.child("name").getValue().toString();
                                           String url=data.child("image").getValue().toString();
                                           newtable.child("name").setValue(names);
                                           newtable.child("image").setValue(url);

                                       }

                                       }

                                   }

                                   @Override
                                   public void onCancelled(DatabaseError databaseError) {

                                   }
                               };
                                students.addListenerForSingleValueEvent(listener);


                           }
                       }
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }

               };

               getid.addListenerForSingleValueEvent(valueEventListener1);

            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        alists.clear();
        returndata.clear();
        DatabaseReference newtables = FirebaseDatabase.getInstance().getReference().child("ClassStudent");
        newtables.orderByChild("studentid").equalTo(currentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        values = data.child("classid").getValue().toString();


                        DatabaseReference classjoin = FirebaseDatabase.getInstance().getReference().child("Class");
                        if (values != null)
                            classjoin.orderByKey().equalTo(values).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    retclass = dataSnapshot.child("Classname").getValue().toString();
                                    alists.add(retclass);
                                    HashSet<String> hashSet = new HashSet<String>();
                                    hashSet.addAll(alists);
                                    alists.clear();
                                    alists.addAll(hashSet);

                                    adapter = new ArrayAdapter(StudentNavActivity.this, android.R.layout.simple_list_item_single_choice, alists);

                                    views1.setAdapter(adapter);

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
                    }

                } else {
                    hides.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}