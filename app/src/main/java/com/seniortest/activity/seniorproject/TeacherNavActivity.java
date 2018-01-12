package com.seniortest.activity.seniorproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class TeacherNavActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView nav;
    private DatabaseReference retrievedata, addclass,retrieves;
    private CircleImageView image;
    private TextView name1,infowarn;
    private ListView list;
    private ArrayAdapter adapter;
    ArrayList<String> editlists;
    ArrayList<String> alist;
    private FirebaseUser user;
    private String input, section;
    private ImageView infoimage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_nav);
        retrievedata = FirebaseDatabase.getInstance().getReference().child("Teacher");
       user = FirebaseAuth.getInstance().getCurrentUser();
        toolbar = (Toolbar) findViewById(R.id.toolbar); //declare the toolbar in java code, it is created in xml
        setSupportActionBar(toolbar);
        infoimage=(ImageView)findViewById(R.id.infoimages);
        infowarn=(TextView)findViewById(R.id.textwarning);

        //method to set toolbar as the actionbar




        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);  //declare the drawerlayout in java code, it is created in xml
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //ActionbarDrawerToggle it adds the hamburger icon in toolbar

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        nav = (NavigationView) findViewById(R.id.nav_view); //declare nav bar in java code, created in xml
        image = (CircleImageView) nav.getHeaderView(0).findViewById(R.id.imgnav);

        list=(ListView)findViewById(R.id.listView);
         editlists = new ArrayList<>();


        name1 = (TextView) nav.getHeaderView(0).findViewById(R.id.name);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.update_profiles) {
                    Intent i=new Intent(TeacherNavActivity.this,UpdateProfileActivity.class);
                    startActivity(i);




                } else if (id == R.id.assignment) {
                    Intent i=new Intent(TeacherNavActivity.this,AssignmentCreationActivity.class);
                    startActivity(i);
                    finish();


                } else if (id == R.id.teach_nav_share) {
                    Intent i=new Intent(Intent.ACTION_VIEW); //opens the sms app to view the message, it is a static final string
                    i.setType("vnd.android-dir/mms-sms"); //type sms
                    i.putExtra("sms_body","Get this app from the Store!");
                    startActivity(i);

                } else if (id == R.id.teach_nav_send) {
                    FirebaseAuth.getInstance().signOut();
                    Intent i=new Intent(TeacherNavActivity.this,TeacherLoginActivity.class);
                    startActivity(i);
                    finish();
                }
                else if(id==R.id.support){
                    Intent i=new Intent(TeacherNavActivity.this, SupportsActivity.class);
                    startActivity(i);

                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        retrievedata.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {//to retrieve data
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Datasnapshot contains data from a firebase database location
                String name = dataSnapshot.child("name").getValue().toString(); //child has to be same as in database
                name1.setText(name);
                String url = dataSnapshot.child("image").getValue().toString();
              Glide.with(TeacherNavActivity.this).load(url).into(image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String selectedFromList = (String) list.getItemAtPosition(position);
                retrieves = FirebaseDatabase.getInstance().getReference().child("Class");
                retrieves.orderByChild("Classname").equalTo(selectedFromList).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot datas: dataSnapshot.getChildren()){
                        Intent i=new Intent(TeacherNavActivity.this,HomeActivity.class);
                        String values=datas.getKey();
                        i.putExtra("send_id",values);
                        i.putExtra("classname",selectedFromList);
                        startActivity(i);
                    }}

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });
        list.setLongClickable(true);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String selectedFromList = (String) list.getItemAtPosition(position);
                final DatabaseReference retriev = FirebaseDatabase.getInstance().getReference().child("Class");
                retriev.orderByChild("teachid").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            String classnames = datas.child("Classname").getValue().toString();
                            if (classnames.equals(selectedFromList)) {
                                String sect = datas.child("section").getValue().toString();
                                LayoutInflater li = LayoutInflater.from(TeacherNavActivity.this); //inflate converts xml to a view object to use in code
                                View promptsView = li.inflate(R.layout.infos, null); //two parameters, the xml and the root(null means the layout is a child of viewgroup

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        TeacherNavActivity.this); /*alertdialog.builder class,creates a builder for alert dialog, parameter is context */

                                alertDialogBuilder.setView(promptsView);
                                final TextView sections = (TextView) promptsView.findViewById(R.id.sections);
                                sections.setText(sect);
                                alertDialogBuilder.setCancelable(true); //true can be canceled with back key
                                alertDialogBuilder.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();


                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create(); //creates alert dialog from builder
                                alertDialog.show();

                            }
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
        getMenuInflater().inflate(R.menu.teacher_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.addclass) {
            LayoutInflater li = LayoutInflater.from(TeacherNavActivity.this); //inflate converts xml to a view object to use in code
            View promptsView = li.inflate(R.layout.dialogteacher, null); //two parameters, the xml and the root(null means the layout is a child of viewgroup

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    TeacherNavActivity.this); /*alertdialog.builder class,creates a builder for alert dialog, parameter is context */

            alertDialogBuilder.setView(promptsView); //set the vie object to the alertdialog builder

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.Dialogclass);
            ;//declare the edittext and use view.findviewbyid since its a view inside a view

            final EditText inputsection = (EditText) promptsView
                    .findViewById(R.id.dialogsection);
            alertDialogBuilder.setCancelable(false); //false cant be canceled with back key
            alertDialogBuilder.setPositiveButton("Save",
                    new DialogInterface.OnClickListener() { //listener invoked when postive button is clicked
                        public void onClick(DialogInterface dialog,int id) {//dialog that recieves the click and int which button

                               section=inputsection.getText().toString();
                               if(section.isEmpty()){
                                   Toasty.error(TeacherNavActivity.this,"Please add the section",Toast.LENGTH_SHORT).show();
                                   return;
                               }
                                input=userInput.getText().toString();
                                Log.i("Info",input);//get user input and set it to the result
                            Log.i("inputsection",section);
                            if(input.contains(" ")){
                                Toasty.error(TeacherNavActivity.this,"Unable to create, contains white spaces",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            DatabaseReference dbs=FirebaseDatabase.getInstance().getReference().child("Class");
                            dbs.orderByChild("Classname").equalTo(input).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        Toasty.error(TeacherNavActivity.this,"Class already exists, Please add your first letter in your name to it",Toast.LENGTH_SHORT).show();
                                            return;
                                    }
                                    else{
                                        retrieves = FirebaseDatabase.getInstance().getReference().child("Class").push();
                                        retrieves.child("Classname").setValue(input);
                                        retrieves.child("teachid").setValue(user.getUid());
                                        retrieves.child("section").setValue(section);
                                        editlists.add(input);
                                        HashSet<String> hashSet = new HashSet<String>();
                                        hashSet.addAll(editlists);
                                        editlists.clear();
                                        editlists.addAll(hashSet);
                                        if(editlists.isEmpty()){
                                            infoimage.setVisibility(View.VISIBLE);
                                            infowarn.setVisibility(View.VISIBLE);
                                        }
                                        else{
                                            infoimage.setVisibility(View.GONE);
                                            infowarn.setVisibility(View.GONE);
                                        }

                                        //Alphabetic sorting.
                                        Collections.sort(editlists);

                                        adapter=new ArrayAdapter<>(TeacherNavActivity.this,android.R.layout.simple_list_item_1,editlists);
                                        list.setAdapter(adapter);

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                    })
                    .setNegativeButton("Cancel",//listener invoked when negative button is clicked
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel(); //cancel the dialog on click negative button
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create(); //creates alert dialog from builder
            alertDialog.show();
            return true;


        }

        return super.onOptionsItemSelected(item);
    }






    @Override
    protected void onResume() {
        super.onResume();
        alist=new ArrayList<>();
        //to clear the data when onresume is called
        addclass=FirebaseDatabase.getInstance().getReference().child("Class");
             addclass.orderByChild("teachid").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {
                     if(dataSnapshot.exists()) {
                         for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                         String inputs = dataSnapshot1.child("Classname").getValue().toString();
                         alist.add(inputs);
                         HashSet<String> hashSet = new HashSet<String>();
                         hashSet.addAll(alist);
                         alist.clear();
                         alist.addAll(hashSet);

                         //Alphabetic sorting.
                         Collections.sort(editlists);
                         adapter = new ArrayAdapter<>(TeacherNavActivity.this, android.R.layout.simple_list_item_1, alist);

                         list.setAdapter(adapter);
                         adapter.notifyDataSetChanged();
                     }}
                     else{
                         infoimage.setVisibility(View.VISIBLE);
                         infowarn.setVisibility(View.VISIBLE);
                     }


                 }



                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                 }
             });

     }
}
