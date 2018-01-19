package com.seniortest.activity.seniorproject;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class UploadPDFActivity extends AppCompatActivity {
    private Spinner spinner;
    private DatabaseReference db;
    private ArrayList selectclasses;
    private FirebaseUser user;
    private Button btn,btns;
    private TextView txt;
    private Integer request_code=0;
    private Uri uri;
    private String spinners;
    private StorageReference mStorage;
    private EditText txts;
    private Button cancels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        spinner=(Spinner)findViewById(R.id.selectspinner);
        btn=(Button)findViewById(R.id.uplpdf);
        txt=(TextView)findViewById(R.id.pdftext);
        btns=(Button)findViewById(R.id.savebtn);
        txts=(EditText)findViewById(R.id.orignaltitle);
        cancels=(Button)findViewById(R.id.cancels);
        mStorage=FirebaseStorage.getInstance().getReference();
        user= FirebaseAuth.getInstance().getCurrentUser();
        selectclasses=new ArrayList<>();
        db= FirebaseDatabase.getInstance().getReference().child("Class");
        db.orderByChild("teachid").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    String classnames=data.child("Classname").getValue().toString();
                    selectclasses.add(classnames);
                    ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(UploadPDFActivity.this, android.R.layout.simple_spinner_item, selectclasses);
                    aAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //defines the layout dropdown view
                    spinner.setAdapter(aAdapter);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Pdf"), request_code);
            }
        });
        cancels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(UploadPDFActivity.this,TeacherNavActivity.class);
                startActivity(i);
                finish();

            }
        });

        btns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String s=txts.getText().toString();
                if(spinner.getSelectedItem()!=null) {
                    spinners = spinner.getSelectedItem().toString();
                }
                if(spinners==null){
                    Toasty.info(UploadPDFActivity.this,"there is no class", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(s)){
                    Toasty.info(UploadPDFActivity.this,"Please write title", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(spinners)){
                    Toasty.info(UploadPDFActivity.this,"Please choose class", Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseReference refs=FirebaseDatabase.getInstance().getReference().child("PDF");
                Query q=refs.orderByChild("Ptitle").equalTo(s);
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Toasty.error(UploadPDFActivity.this,"Title already exists, Please write another one",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else{
                            db=FirebaseDatabase.getInstance().getReference().child("PDF").push();
                            StorageReference filepath=mStorage.child("pdf/"+uri.getLastPathSegment()); //puts the uri in the storage
                            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloaduri = taskSnapshot.getDownloadUrl().toString();
                                    db.child("PDFurl").setValue(downloaduri);
                                    db.child("cname").setValue(spinners);
                                    db.child("teacherid").setValue(user.getUid());
                                    db.child("Ptitle").setValue(s);

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Toasty.success(UploadPDFActivity.this,"PDF sent to students", Toast.LENGTH_LONG).show();
                startActivity(new Intent(UploadPDFActivity.this,TeacherNavActivity.class));
                finish();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==request_code&&resultCode==RESULT_OK){ //checks if the requestcode is same as request_code to know how to respond

            uri=data.getData();  //data.getData() is used to get the uri that was selected
            txt.setText(""+uri); //and here we set the uri to the imageview, so we have a picture in the uri
    }
}
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(UploadPDFActivity.this,TeacherNavActivity.class);
        startActivity(i);
        finish();
    }

}
