package com.seniortest.activity.seniorproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
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

public class AdminTeacherActivity extends AppCompatActivity {

    private ListView deletestudent;
    private DatabaseReference delstudent;
    private ArrayList<String> listdelstudent;
    private ArrayAdapter adapter;
    private String s;
    private StorageReference re;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_student);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        delstudent = FirebaseDatabase.getInstance().getReference().child("Teacher");
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
                        adapter = new ArrayAdapter<String>(AdminTeacherActivity.this, android.R.layout.simple_list_item_multiple_choice, listdelstudent);
                        deletestudent.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        Toasty.info(AdminTeacherActivity.this, "Check classes page before deleting here", Toast.LENGTH_SHORT).show();

                    }

                } else {
                    Toasty.info(AdminTeacherActivity.this, "no teachers exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

                         DatabaseReference data=FirebaseDatabase.getInstance().getReference().child("Teacher");
                        data.orderByChild("name").equalTo(s).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(DataSnapshot datas: dataSnapshot.getChildren()) {
                                        String image=datas.child("image").getValue().toString();
                                        re= FirebaseStorage.getInstance().getReferenceFromUrl(image);
                                        re.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // File deleted successfully
                                            }
                                        });
                                        String teachid=datas.getKey();
                                        datas.getRef().removeValue();
                                        adapter.remove(s);
                                        adapter.notifyDataSetChanged();
                                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Class");
                                        ref.orderByChild("teachid").equalTo(teachid).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    for(DataSnapshot data:dataSnapshot.getChildren()) {
                                                        data.getRef().removeValue();
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }

                                }

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
