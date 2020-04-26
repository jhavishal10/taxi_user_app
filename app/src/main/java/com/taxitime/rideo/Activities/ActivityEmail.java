 package com.taxitime.rideo.Activities;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.taxitime.rideo.Helper.SharedHelper;
import com.taxitime.rideo.R;
import com.taxitime.rideo.Utils.MyTextView;
import com.taxitime.rideo.Utils.Utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityEmail extends AppCompatActivity {

    ImageView backArrow;
    Button nextBtn;
    EditText email;
    MyTextView register, forgetPassword;
    LinearLayout lnrBegin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        email = (EditText)findViewById(R.id.enter_ur_mailID);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        register = (MyTextView) findViewById(R.id.register);
        forgetPassword = (MyTextView) findViewById(R.id.forgetPassword);
        lnrBegin = (LinearLayout) findViewById(R.id.lnrBegin);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().equals("") || email.getText().toString().equalsIgnoreCase(getString(R.string.sample_mail_id))){

                    displayMessage(getString(R.string.email_validation));

                }else{
                    if((!isValidEmail(email.getText().toString()))){

                        displayMessage(getString(R.string.email_validation));

                    }else{
                        Utilities.hideKeyboard(ActivityEmail.this);
                        SharedHelper.putKey(ActivityEmail.this,"email",email.getText().toString());
                        Intent mainIntent = new Intent(ActivityEmail.this, ActivityPassword.class);
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }


                }
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(ActivityEmail.this,"password", "");
                Utilities.hideKeyboard(ActivityEmail.this);
                Intent mainIntent = new Intent(ActivityEmail.this, RegisterActivity.class);
                mainIntent.putExtra("isFromMailActivity", true);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(ActivityEmail.this,"password", "");
                Utilities.hideKeyboard(ActivityEmail.this);
                Intent mainIntent = new Intent(ActivityEmail.this, ForgetPassword.class);
                mainIntent.putExtra("isFromMailActivity", true);
                startActivity(mainIntent);
            }
        });

    }

    public void displayMessage(String toastString){
        try{
            Snackbar.make(getCurrentFocus(),toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, toastString, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
}