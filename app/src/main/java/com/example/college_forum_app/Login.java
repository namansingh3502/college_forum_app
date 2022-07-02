package com.example.college_forum_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Login extends AppCompatActivity {

    TextView createacc;
    TextInputLayout Username, Pass;
    Button login;
    TextView Forgotpassword;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        createacc = (TextView) findViewById(R.id.signup);

        try {
            Username = (TextInputLayout) findViewById(R.id.login_email);
            Pass = (TextInputLayout) findViewById(R.id.login_password);
            login = (Button) findViewById(R.id.Login_btn);
            Forgotpassword = (TextView) findViewById(R.id.forgotpass);


            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    username = Objects.requireNonNull(Username.getEditText()).getText().toString().trim();
                    password = Pass.getEditText().getText().toString().trim();
                    if (isValid()) {
                        System.out.println("Logged in");
                    }
                }
            });

            createacc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Login.this, Registration.class);
                    startActivity(intent);
                    finish();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isValid() {
        Username.setErrorEnabled(false);
        Username.setError("");
        Pass.setErrorEnabled(false);
        Pass.setError("");

        boolean isValidUsername = false, isValidPassword = false;
        if (TextUtils.isEmpty(username)) {
            Username.setErrorEnabled(true);
            Username.setError("Email is required");
        } else {
            isValidUsername = true;
        }
        if (TextUtils.isEmpty(password)) {
            Pass.setErrorEnabled(true);
            Pass.setError("Password is required");
        } else {
            isValidPassword = true;
        }
        return isValidUsername && isValidPassword;
    }
}
