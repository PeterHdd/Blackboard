package com.seniortest.activity.seniorproject;


import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference myref;
    private String so;
    private Query fk;
    private String classnames,input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        so = getIntent().getStringExtra("send_id"); //get classid
        classnames=getIntent().getStringExtra("classname"); //get class name, for the message node later
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myref = FirebaseDatabase.getInstance().getReference().child("ClassStudent");
        fk = myref.orderByChild("classid").equalTo(so); //query on classid

        fk.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {


                }
                else{
                    Toasty.error(HomeActivity.this,"No Students Registered yet",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        final FirebaseRecyclerAdapter<ClassStudent, AViewHolder> recyclerAdapter = new FirebaseRecyclerAdapter<ClassStudent, AViewHolder>(
                ClassStudent.class,
                R.layout.row,
                AViewHolder.class,
                fk
        ) {


            @Override
            public long getItemId(int position) {
                return super.getItemId(position);
            }

            @Override
            public int getItemViewType(int position) {

                return super.getItemViewType(position);

            }


            @Override
            protected void populateViewHolder(AViewHolder viewholder, ClassStudent model, int position) {
                viewholder.setImage(model.getImage());
                viewholder.setName(model.getName());

            }


        };
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(mDividerItemDecoration); //to add lines between items


        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();

}



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_nav, menu); //inflate the menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.addclass) {
           final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
           final DatabaseReference myref = FirebaseDatabase.getInstance().getReference().child("Teacher");

            LayoutInflater li = LayoutInflater.from(HomeActivity.this); //inflate converts xml to a view object to use in code
            View promptsView = li.inflate(R.layout.sendmessage, null); //two parameters, the xml and the root(null means the layout is a child of viewgroup

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    HomeActivity.this); /*alertdialog.builder class,creates a builder for alert dialog, parameter is context */
             alertDialogBuilder.setMessage("Notification");
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setView(promptsView); //set the vie object to the alertdialog builder

            final TextView txt=(TextView)promptsView.findViewById(R.id.Dialogclasss);//declare the edittext and use view.findviewbyid since its a view inside a view
            // set dialog message
           txt.setText(classnames);
            final EditText texts=(EditText)promptsView.findViewById(R.id.messagebody);
            alertDialogBuilder.setCancelable(false); //false cant be canceled with back key
            alertDialogBuilder.setPositiveButton("Send",
                    new DialogInterface.OnClickListener() { //listener invoked when postive button is clicked
                        public void onClick(DialogInterface dialog,int id) {//dialog that recieves the click and int which button
                        input=texts.getText().toString();
                            myref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String name=dataSnapshot.child("name").getValue().toString();
                                    DatabaseReference  retrieves = FirebaseDatabase.getInstance().getReference().child("messages").push();
                                    retrieves.child("title").setValue(classnames);
                                    retrieves.child("message").setValue(input);
                                    retrieves.child("teachname").setValue(name);



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

    public static class AViewHolder extends RecyclerView.ViewHolder {
    View mView;
    TextView titles;
    ImageView imageView;

    public AViewHolder(View itemView) {
        super(itemView);
        mView = itemView; //so you can use it later on

    }

    public void setName(String name) { //it does not matter syntax
        titles = (TextView) mView.findViewById(R.id.title);
        titles.setText(name);

    }

    public void setImage(String image) {
        imageView = (ImageView) mView.findViewById(R.id.image);
        Glide.with(mView.getContext()).load(image).into(imageView);

    }


}
}