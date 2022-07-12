package com.example.college_forum_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.college_forum_app.Post.PostActivity;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class Registration extends AppCompatActivity {

    TextView already_have_account;
    TextInputLayout First_name, Middle_name, Last_name, Username, Email, Pass1, Pass2, Mobile;
    String username, pass1, pass2, first_name, middle_name = "", last_name, email, mobile, gender;

    Button register;
    AnimationDrawable anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        already_have_account = findViewById(R.id.AlreadyHavesignin);

        Username = findViewById(R.id.Username);
        Pass1 = findViewById(R.id.password);
        Pass2 = findViewById(R.id.re_enter_password);
        First_name = findViewById(R.id.First_name);
        Middle_name = findViewById(R.id.Middle_name);
        Last_name = findViewById(R.id.Last_name);
        Email = findViewById(R.id.email);
        Mobile = findViewById(R.id.mobilenoo);
//        Gender = (TextInputLayout) findViewById(R.id.gender);
//        Department = (TextInputLayout) findViewById(R.id.department);

        register = (Button) findViewById(R.id.signup_button);

//******************************BACKGROUND ANIMATION*************************
        RelativeLayout container = findViewById(R.id.relative_registration);

        anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(6000);
        anim.setExitFadeDuration(2000);

//******************************BACKGROUND ANIMATION*************************

        already_have_account.setOnClickListener(new View.OnClickListener() {
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

                username = Username.getEditText().getText().toString().trim();
                pass1 = Pass1.getEditText().getText().toString().trim();
                pass2 = Pass2.getEditText().getText().toString().trim();
                first_name = First_name.getEditText().getText().toString().trim();
                middle_name = Middle_name.getEditText().getText().toString().trim();
                last_name = Last_name.getEditText().getText().toString().trim();
                email = Email.getEditText().getText().toString().trim();
                mobile = Mobile.getEditText().getText().toString().trim();

//                gender = Gender.getEditText().getText().toString().trim();
//                department = Department.getEditText().getText().toString().trim();

                if (isValid()) {

                    JSONObject data = new JSONObject();
                    JSONObject gender = new JSONObject();
                    JSONObject department = new JSONObject();

                    try {
                        gender.put("value", "M");
                        gender.put("label", "Male");
                        department.put("value", "CSE");
                        department.put("label", "Computer Science and Engineering");

                        data.put("username", username);
                        data.put("password1", pass1);
                        data.put("password2", pass2);
                        data.put("first_name", first_name);
                        data.put("middle_name", middle_name);
                        data.put("last_name", last_name);
                        data.put("email", email);
                        data.put("phone", mobile);

                        data.put("gender", gender);
                        data.put("department", department);

                        registerUser(data);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    System.out.println("error in reg");
                }
            }
        });

    }

    String emailpattern = "[a-zA-Z0-9._-]+@sjbit.edu.in";

    public boolean isValid() {
        Username.setErrorEnabled(false);
        Username.setError("");
        Pass1.setErrorEnabled(false);
        Pass1.setError("");
        Pass2.setErrorEnabled(false);
        Pass2.setError("");
        First_name.setErrorEnabled(false);
        First_name.setError("");
        Last_name.setErrorEnabled(false);
        Last_name.setError("");
        Email.setErrorEnabled(false);
        Email.setError("");
//        Gender.setErrorEnabled(false);
//        Gender.setError("");
//        Department.setErrorEnabled(false);
//        Department.setError("");
        Mobile.setErrorEnabled(false);
        Mobile.setError("");

        boolean isValidName = false, isValidEmail = false, isValidPassword = false;
        boolean isValidMobile = false, isValidUsername = false;

        if (TextUtils.isEmpty(username)) {
            Username.setErrorEnabled(true);
            Username.setError("Username cannot be empty");
        } else {
            isValidUsername = true;
        }
        if (TextUtils.isEmpty(pass1)) {
            Pass1.setErrorEnabled(true);
            Pass1.setError("Password is required");
        } else {
            if (pass1.length() < 6) {
                Pass1.setErrorEnabled(true);
                Pass1.setError("Password is too weak");
            } else {
                isValidPassword = true;
            }
        }
        if (TextUtils.isEmpty(pass2)) {
            Pass2.setErrorEnabled(true);
            Pass2.setError("Password is required");
        } else {
            if (Pass1.equals(Pass2)) {
                Pass2.setErrorEnabled(true);
                Pass2.setError("Password not same");
            } else {
                isValidPassword = true;
            }
        }
        if (TextUtils.isEmpty(first_name)) {
            First_name.setErrorEnabled(true);
            First_name.setError("First Name is required");
        } else {
            isValidName = true;
        }
        if (TextUtils.isEmpty(last_name)) {
            Last_name.setErrorEnabled(true);
            Last_name.setError("Last Name is required");
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
        if (TextUtils.isEmpty(mobile)) {
            Mobile.setErrorEnabled(true);
            Mobile.setError("Mobile number is required");
        } else {
            if (mobile.length() < 10) {
                Mobile.setErrorEnabled(true);
                Mobile.setError("Invalid mobile number");
            } else {
                isValidMobile = true;
            }
        }

//        if (TextUtils.isEmpty(gender)) {
//            Gender.setErrorEnabled(true);
//            Gender.setError("Field cannot be empty");
//        } else {
//            isValidGender = true;
//        }

        return isValidName && isValidEmail && isValidPassword && isValidMobile && isValidUsername;
    }

    public void registerUser(JSONObject userDetails) {

        SharedPreferences storage = getSharedPreferences("College_Forum", MODE_PRIVATE);
        String auth_token = storage.getString("auth_token", "No token");

        AndroidNetworking.post("http://192.168.40.254:8000/api/auth/register")
                .addHeaders("Authorization", auth_token)
                .addBodyParameter("data", String.valueOf(userDetails))
                .setContentType("application/json")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent z = new Intent(Registration.this, Login.class);
                        startActivity(z);
                        finish();
                    }

                    @Override
                    public void onError(ANError anError) {
                        System.out.println("\n\nerror while creating post " + anError.getErrorBody());
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (anim != null && !anim.isRunning())
            anim.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }

}
