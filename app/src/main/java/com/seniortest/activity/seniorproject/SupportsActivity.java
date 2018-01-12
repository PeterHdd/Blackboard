package com.seniortest.activity.seniorproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SupportsActivity extends AppCompatActivity {

    private Button buttonSend;
    private TextView textTo;
    private EditText textSubject;
    private EditText textMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supports);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        buttonSend = (Button) findViewById(R.id.buttonSend);
        textTo = (TextView) findViewById(R.id.TextviewTo);
        textSubject = (EditText) findViewById(R.id.editTextSubject);
        textMessage = (EditText) findViewById(R.id.editTextMessage);
        textTo.setText("peterhd3412@gmail.com");

        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String to = textTo.getText().toString();         //retrieve text
                String subject = textSubject.getText().toString();   //retrieve text
                String message = textMessage.getText().toString();

                Intent email = new Intent(Intent.ACTION_SEND);  //intent with action send to be able to sent it, these strings are static
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{to}); //send intent to this email
                email.putExtra(Intent.EXTRA_SUBJECT, subject); //extra_subject is a string that holds the subject under class intent
                email.putExtra(Intent.EXTRA_TEXT, message);

                //need this to prompts email client only
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :")); //choose which email app to send into

            }
        });
    }
}

