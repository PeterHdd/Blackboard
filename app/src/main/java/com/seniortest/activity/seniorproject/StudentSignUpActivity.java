package com.seniortest.activity.seniorproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;


public class StudentSignUpActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, date,inputname,phone,confpass;
    private Button btnSignUp, btnResetPassword;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase, newStudent;
    private FirebaseUser mCurrentUser;
    private CircleImageView img;
    private StorageReference mStorage;
    private Uri uri;
    private static final int request_code=2;
    private Boolean clicked=false;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sign_up);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        mStorage= FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Student");
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        img=(CircleImageView)findViewById(R.id.imgview);
        confpass=(EditText)findViewById(R.id.confpass);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        date=(EditText)findViewById(R.id.date);
        inputname=(EditText)findViewById(R.id.name);
        phone=(EditText)findViewById(R.id.phone);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked=true;
                Intent i=new Intent(Intent.ACTION_GET_CONTENT); //lets you choose what apps u want based on type
                i.setType("image/*"); // /* type of image is unknown, to specify the type of data to return
                startActivityForResult(i,request_code);
            }
        });
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), ResetPasswordActivity.class));
            }
        });



        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               final String dates=date.getText().toString().trim();
               final String number=phone.getText().toString().trim();
               final String name=inputname.getText().toString().trim();
              final  String email = inputEmail.getText().toString().trim();
              final  String password = inputPassword.getText().toString().trim();
              final String confirmpass=confpass.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toasty.error(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
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

                if (password.length() < 6) {
                    Toasty.error(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(confirmpass)){
                    Toasty.error(getApplicationContext(),"Please Confirm your Password!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!confirmpass.equals(password)){
                    Toasty.error(getApplicationContext(),"Password is not the same!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!clicked){
                    Toasty.error(getApplicationContext(),"Please insert an Image",Toast.LENGTH_SHORT).show();
                    return;
                }


                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(StudentSignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toasty.info(getApplicationContext(), "creation of account was: " + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toasty.error(getApplicationContext(), "Authentication failed: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                     mCurrentUser= task.getResult().getUser();
                                    newStudent=mDatabase.child(mCurrentUser.getUid());
                                        StorageReference filepath = mStorage.child(uri.getLastPathSegment());
                                        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                String downloaduri = taskSnapshot.getDownloadUrl().toString();
                                                newStudent.child("email").setValue(email);
                                                newStudent.child("password").setValue(password);
                                                newStudent.child("name").setValue(name);
                                                newStudent.child("date").setValue(dates);
                                                newStudent.child("phone").setValue(number);
                                                newStudent.child("image").setValue(downloaduri);


                                            }
                                        });




                                    startActivity(new Intent(StudentSignUpActivity.this,StudentLoginActivity.class));
                                    finish();
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






