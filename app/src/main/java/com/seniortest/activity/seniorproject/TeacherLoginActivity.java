package com.seniortest.activity.seniorproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class TeacherLoginActivity extends AppCompatActivity {


    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private Button btnSignup, btnLogin, btnReset;
    private DatabaseReference teachers,teacher,admin;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        // set the view now

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        admin=FirebaseDatabase.getInstance().getReference().child("Admin");
        admin.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                     key=data.getKey();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        teacher= FirebaseDatabase.getInstance().getReference().child("Teacher");


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(v.getContext(),TeacherSignUpActivity.class);
                startActivity(i);

            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeacherLoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final  String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toasty.error(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toasty.error(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                final ProgressDialog progressDialog = ProgressDialog.show(TeacherLoginActivity.this,"Please wait","Processing",true);
                Runnable runnable=new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.cancel();
                    }
                };
                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(runnable, 3000);
                teachers= FirebaseDatabase.getInstance().getReference().child("Student");
                Query queries=teachers.orderByChild("email").equalTo(email);
                queries.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Toasty.info(TeacherLoginActivity.this,"This email does not exist ",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            return;
                        }
                        else{
                             //authenticate user

                                        auth.signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(TeacherLoginActivity.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        // If sign in fails, display a message to the user. If sign in succeeds
                                                        // the auth state listener will be notified and logic to handle the
                                                        // signed in user can be handled in the listener.
                                                        progressDialog.dismiss();
                                                        if (!task.isSuccessful()) {
                                                            // there was an error

                                                            Toasty.error(TeacherLoginActivity.this, "Authentication failed: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();


                                                        } else if(email.equals("peterhd3412@gmail.com")) {
                                                            Intent intent = new Intent(TeacherLoginActivity.this, AdminActivity.class);
                                                            if(key!=null) {
                                                                admin.child(key).child("password").setValue(password);
                                                            }
                                                            startActivity(intent);
                                                            finish();
                                                        }else{
                                                            final Intent intent = new Intent(TeacherLoginActivity.this, TeacherNavActivity.class);
                                                            teacher.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    if(dataSnapshot.exists()){
                                                                        for(DataSnapshot data: dataSnapshot.getChildren()){
                                                                            String keys=data.getKey();
                                                                            teacher.child(keys).child("password").setValue(password);
                                                                            progressDialog.dismiss();
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }
                                                                    }
                                                                    else{
                                                                        Toasty.info(TeacherLoginActivity.this,"This account has been deleted ",Toast.LENGTH_SHORT).show();
                                                                        return;
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });

                                                        }

                                                    }
                                                });

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                                    }

                });



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}



