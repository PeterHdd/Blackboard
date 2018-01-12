package com.seniortest.activity.seniorproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;


public class UpdateProfileActivity extends AppCompatActivity {

    private EditText inputPassword, date,inputname,phone,newpass;
    private Button btnupdate,cancelbtn;
    private CircleImageView img;
    private Uri uri;
    private static final int request_code=2;
    private String passwords,downloaduri,email;
    private StorageReference mStorage;
    private Boolean teach=false,student=false;
    private EditText inputEmail,newemail;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        img = (CircleImageView) findViewById(R.id.updateimg);
        newemail=(EditText)findViewById(R.id.newupdateemail);
        btnupdate = (Button) findViewById(R.id.update_button);
        newpass = (EditText) findViewById(R.id.updateconfpass);
        inputEmail = (EditText) findViewById(R.id.updateemail);
        inputPassword = (EditText) findViewById(R.id.updatepassword);
        date = (EditText) findViewById(R.id.updatedate);
        inputname = (EditText) findViewById(R.id.updatename);
        phone = (EditText) findViewById(R.id.updatephone);
        cancelbtn = (Button) findViewById(R.id.cancel_button);


        mStorage = FirebaseStorage.getInstance().getReference();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Student");
        final DatabaseReference refers = FirebaseDatabase.getInstance().getReference().child("Teacher");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() { //query on node
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //checks if exists and if the user is logged in
                if (dataSnapshot.exists() && user != null) {
                    String dates = dataSnapshot.child("date").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    email = dataSnapshot.child("email").getValue().toString();
                    String url = dataSnapshot.child("image").getValue().toString();
                    String phones = dataSnapshot.child("phone").getValue().toString();
                    passwords = dataSnapshot.child("password").getValue().toString();
                    Glide.with(UpdateProfileActivity.this).load(url).into(img);
                    inputEmail.setText(email);
                    phone.setText(phones);
                    date.setText(dates);
                    inputname.setText(name);
                    student=true;



                } else {
                    refers.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && user != null) {
                                String datess = dataSnapshot.child("date").getValue().toString();
                                String name = dataSnapshot.child("name").getValue().toString();
                                email = dataSnapshot.child("email").getValue().toString();
                                String url = dataSnapshot.child("image").getValue().toString();
                                String phoness = dataSnapshot.child("phone").getValue().toString();
                                passwords = dataSnapshot.child("password").getValue().toString();
                                Glide.with(UpdateProfileActivity.this).load(url).into(img);
                                inputEmail.setText(email);
                                phone.setText(phoness);
                                date.setText(datess);
                                inputname.setText(name);
                                teach=true;
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
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(teach){
                    Intent i=new Intent(UpdateProfileActivity.this,TeacherNavActivity.class);
                    startActivity(i);
                    finish();
                }
                else{
                    Intent i=new Intent(UpdateProfileActivity.this,StudentNavActivity.class);
                    startActivity(i);
                    finish();

                }
            }
        });



        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Intent.ACTION_GET_CONTENT); //lets you choose what apps u want based on type
                i.setType("image/*"); // /* type of image is unknown, to specify the type of data to return
                startActivityForResult(i,request_code);
            }
        });




        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String dates=date.getText().toString().trim();
                final String number=phone.getText().toString().trim();
                final String name=inputname.getText().toString().trim();
              final String email=inputEmail.getText().toString().trim();
              final String newemails=newemail.getText().toString().trim();
                final  String password = inputPassword.getText().toString().trim();
                final String newpassword=newpass.getText().toString().trim();


                if (TextUtils.isEmpty(name)) {
                    Toasty.error(getApplicationContext(), "Enter Your Name!", Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty(number)) {
                    Toasty.error(getApplicationContext(), "Enter your Number", Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty(dates)) {
                    Toasty.error(getApplicationContext(), "Enter Date of Birth!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toasty.error(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(newemails)) {
                    Toasty.error(getApplicationContext(), "Enter your new email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6&& newpassword.length()<6) {
                    Toasty.error(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(newpassword)){
                    Toasty.error(getApplicationContext(),"Please Enter a new Password!",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!passwords.equals(password)){
                    Toasty.error(getApplicationContext(),"Incorrect current password entered!",Toast.LENGTH_SHORT).show();
                    return;
                }




                AuthCredential credential = EmailAuthProvider
                        .getCredential(email,password);

// Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential) //reauthenticated
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updateEmail(newemails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                    user.updatePassword(newpassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                if(uri!=null) {
                                                    StorageReference filepath = mStorage.child(uri.getLastPathSegment());
                                                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                       downloaduri = taskSnapshot.getDownloadUrl().toString();
                                                       if(student=true){
                                                           ref.child(user.getUid()).child("image").setValue(downloaduri);
                                                       }
                                                       else{
                                                           refers.child(user.getUid()).child("image").setValue(downloaduri);
                                                       }
                                                    }
                                                });
                                                }

                                                                if(student=true) {
                                                                    ref.child(user.getUid()).child("date").setValue(dates);
                                                                    ref.child(user.getUid()).child("name").setValue(name);
                                                                    ref.child(user.getUid()).child("phone").setValue(number);
                                                                    ref.child(user.getUid()).child("email").setValue(newemails);
                                                                    ref.child(user.getUid()).child("password").setValue(newpassword);

                                                                    Toasty.success(UpdateProfileActivity.this,"Please Login Again",Toast.LENGTH_LONG).show();
                                                                    Intent i = new Intent(UpdateProfileActivity.this, StudentLoginActivity.class);
                                                                    startActivity(i);
                                                                    finish();
                                                                }

                                                                else {

                                                                                refers.child(user.getUid()).child("date").setValue(dates);
                                                                                refers.child(user.getUid()).child("email").setValue(newemails);
                                                                                refers.child(user.getUid()).child("name").setValue(name);
                                                                                refers.child(user.getUid()).child("phone").setValue(number);
                                                                                refers.child(user.getUid()).child("password").setValue(newpassword);

                                                                        Toasty.success(UpdateProfileActivity.this,"Please Login Again",Toast.LENGTH_LONG).show();
                                                                        Intent i = new Intent(UpdateProfileActivity.this, TeacherLoginActivity.class);
                                                                        startActivity(i);
                                                                        finish();


                                            }
                                        }}
                                    });}}
                                    });
                                }
                                else{
                                    Toasty.error(UpdateProfileActivity.this,"Authentication error: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });



            }

        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==request_code&&resultCode==RESULT_OK){
            uri=data.getData();
            img.setImageURI(uri);
        }

    }

}
