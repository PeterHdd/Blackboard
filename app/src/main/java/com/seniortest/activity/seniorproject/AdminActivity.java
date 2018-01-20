package com.seniortest.activity.seniorproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

   private  Button classes,student,teacher,logout,delepdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
         classes=(Button)findViewById(R.id.delclass);
        student=(Button)findViewById(R.id.delstudent);
        logout=(Button)findViewById(R.id.logoutad);
      teacher=(Button)findViewById(R.id.delteacher);
      delepdf=(Button)findViewById(R.id.delpdf);

      classes.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent i=new Intent(AdminActivity.this,AdminClassActivity.class);
              startActivity(i);
          }
      });
      student.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent=new Intent(AdminActivity.this,AdminStudentActivity.class);
              startActivity(intent);
          }
      });

      teacher.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent=new Intent(AdminActivity.this,AdminTeacherActivity.class);
              startActivity(intent);
          }
      });
      logout.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              FirebaseAuth.getInstance().signOut();
              Intent i=new Intent(AdminActivity.this,TeacherLoginActivity.class);
              startActivity(i);
              finish();
          }
      });
      delepdf.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent intent=new Intent(AdminActivity.this,AdminPDFActivity.class);
              startActivity(intent);
          }
      });

    }

}
