package com.example.college_forum_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

import com.example.college_forum_app.models.Passwords;
import com.example.college_forum_app.models.Users;
import com.example.college_forum_app.models.privatedetails;

public class Registration extends AppCompatActivity {

    TextView alreadyhaveacc;
    TextInputLayout Fname, Username, Email, Pass, Mobileno, Gender;
    EditText Birth;
    int year, month, day;
    Button register;
    String fname, username, email, pass, mobileno, gender, birth, userid;
    AnimationDrawable anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        alreadyhaveacc = (TextView) findViewById(R.id.AlreadyHavesignin);
        Birth = (EditText) findViewById(R.id.birthdate);
        Fname = (TextInputLayout) findViewById(R.id.Fullname);
        Username = (TextInputLayout) findViewById(R.id.Username);
        Email = (TextInputLayout) findViewById(R.id.signup_email);
        Pass = (TextInputLayout) findViewById(R.id.signup_password);
        Gender = (TextInputLayout) findViewById(R.id.gender);
        Mobileno = (TextInputLayout) findViewById(R.id.mobilenoo);


        register = (Button) findViewById(R.id.signup_button);

//******************************BACKGROUND ANIMATION*************************
        RelativeLayout container = (RelativeLayout) findViewById(R.id.relative_registration);

        anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(6000);
        anim.setExitFadeDuration(2000);

//******************************BACKGROUND ANIMATION*************************


        Birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Registration.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Birth.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        alreadyhaveacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registration.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fname = Fname.getEditText().getText().toString().trim();
                username = Username.getEditText().getText().toString().trim();
                email = Email.getEditText().getText().toString().trim();
                mobileno = Mobileno.getEditText().getText().toString().trim();
                pass = Pass.getEditText().getText().toString().trim();
                gender = Gender.getEditText().getText().toString().trim();
                birth = Birth.getText().toString().trim();

                if (isValid()) {
                    System.out.println("fname "+ fname);
                    System.out.println("uname "+ username);
                    System.out.println("email "+ email);
                    System.out.println("mobile "+ mobileno);
                    System.out.println("pass "+ pass);
                    System.out.println("gender "+ gender);
                    System.out.println("birth "+ birth);

                    System.out.println("Registered successfully");
                }
            }
        });

    }

    String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public boolean isValid() {
        Email.setErrorEnabled(false);
        Email.setError("");
        Fname.setErrorEnabled(false);
        Fname.setError("");
        Username.setErrorEnabled(false);
        Username.setError("");
        Pass.setErrorEnabled(false);
        Pass.setError("");
        Mobileno.setErrorEnabled(false);
        Mobileno.setError("");
        Gender.setErrorEnabled(false);
        Gender.setError("");

        boolean isValidName = false, isValidEmail = false, isValidPassword = false;
        boolean isValidMobileNo = false, isValidGender = false, isValidUsername = false;

        if (TextUtils.isEmpty(fname)) {
            Fname.setErrorEnabled(true);
            Fname.setError("Full Name is required");
        } else {
            isValidName = true;
        }
        if (TextUtils.isEmpty(email)) {
            Email.setErrorEnabled(true);
            Email.setError("Email is required");
        } else {
            if (email.matches(emailpattern)) {
                isValidEmail = true;
            } else {
                Email.setErrorEnabled(true);
                Email.setError("Enter a valid Email Address");
            }
        }
        if (TextUtils.isEmpty(pass)) {
            Pass.setErrorEnabled(true);
            Pass.setError("Password is required");
        } else {
            if (pass.length() < 6) {
                Pass.setErrorEnabled(true);
                Pass.setError("password is too weak");
            } else {
                isValidPassword = true;
            }
        }
        if (TextUtils.isEmpty(mobileno)) {
            Mobileno.setErrorEnabled(true);
            Mobileno.setError("Mobile number is required");
        } else {
            if (mobileno.length() < 10) {
                Mobileno.setErrorEnabled(true);
                Mobileno.setError("Invalid mobile number");
            } else {
                isValidMobileNo = true;
            }
        }
        if (TextUtils.isEmpty(gender)) {
            Gender.setErrorEnabled(true);
            Gender.setError("Field cannot be empty");
        } else {
            isValidGender = true;
        }
        if (TextUtils.isEmpty(username)) {
            Username.setErrorEnabled(true);
            Username.setError("Field cannot be empty");
        } else {
            isValidUsername = true;
        }
        boolean  is_valid = isValidName && isValidEmail && isValidPassword && isValidMobileNo && isValidGender && isValidUsername;
        return is_valid;
    }

    //******************************FUNCTIONS TO ADD DATA'S TO FIREBASE*************************
    public void addUsers(String Discription, String FullName, String Username, String Website) {

//        Users user = new Users(
//                Discription,
//                "0",
//                "0",
//                FullName,
//                "0",
//                "https://firebasestorage.googleapis.com/v0/b/instagram-clone-291e7.appspot.com/o/generalProfilePhoto%2Fdefualt_insta_pic.png?alt=media&token=e9834979-a141-48fd-87b6-a2074e7dbc9b",
//                Username,
//                Website,
//                userid
//        );
//        databaseReference.child("Users").child(useridd).setValue(user);
    }

    public void addPrivateDetails(String user_id, String email, String gender, String birthdate, String phoneNumber) {

        privatedetails details = new privatedetails(
                user_id,
                email,
                gender,
                birthdate,
                phoneNumber
        );
//        databaseReference.child("Privatedetails").child(useridd).setValue(details);
    }

    public void addPasswords(String passwords) {

        Passwords pass = new Passwords(passwords);
//        databaseReference.child("Passwords").child(useridd).setValue(pass);

    }
//*******************************************************************************

    //******************************BACKGROUND ANIMATION*************************
    // Starting animation:- start the animation on onResume.
    @Override
    protected void onResume() {
        super.onResume();
        if (anim != null && !anim.isRunning())
            anim.start();
    }

    // Stopping animation:- stop the animation on onPause.
    @Override
    protected void onPause() {
        super.onPause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }
//****************************************************************************

}