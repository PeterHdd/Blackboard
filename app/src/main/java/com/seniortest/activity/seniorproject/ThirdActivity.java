package com.seniortest.activity.seniorproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class ThirdActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);


        Button teachbtn=(Button)findViewById(R.id.teacher);
        Button studentbtn=(Button)findViewById(R.id.student);
        studentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(ThirdActivity.this, StudentLoginActivity.class);
                startActivity(in);
                finish();

            }
        });
        teachbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(ThirdActivity.this, TeacherLoginActivity.class);
                startActivity(in);
                finish();

            }
        });
    }
}
