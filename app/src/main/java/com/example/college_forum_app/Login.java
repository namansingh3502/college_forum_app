package com.example.college_forum_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.college_forum_app.models.AuthToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputLayout;
import com.jacksonandroidnetworking.JacksonParserFactory;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class Login extends AppCompatActivity {

    TextView CreateAccount;
    TextInputLayout Username, Pass;
    Button login;
    TextView ForgotPassword;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());

        CreateAccount = findViewById(R.id.signup);

        try {
            Username = findViewById(R.id.login_email);
            Pass = findViewById(R.id.login_password);
            login = findViewById(R.id.Login_btn);
            ForgotPassword = findViewById(R.id.forgotpass);


            login.setOnClickListener(v -> {

                username = Objects.requireNonNull(Username.getEditText()).getText().toString().trim();
                password = Objects.requireNonNull(Pass.getEditText()).getText().toString().trim();
                if (isValid()) {

                    AndroidNetworking.post("http://192.168.40.254:8000/api/auth/token/login/")
                            .addBodyParameter("username", username)
                            .addBodyParameter("password", password)
                            .setPriority(Priority.LOW)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    ObjectMapper objectMapper = new ObjectMapper();
                                    AuthToken authToken = null;
                                    try {
                                        authToken = objectMapper.readValue(response.toString(), AuthToken.class);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    SharedPreferences sharedPreferences = getSharedPreferences("College_Forum", MODE_PRIVATE);
                                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

                                    assert authToken != null;
                                    myEdit.putString("auth_token", "Token " + authToken.getAuth_token());
                                    myEdit.apply();

                                    Intent z = new Intent(Login.this, Home.class);
                                    startActivity(z);
                                    finish();

                                }

                                @Override
                                public void onError(ANError anError) {
                                    // handle error
                                    new AlertDialog.Builder(Login.this)
                                            .setTitle("Error")
                                            .setMessage("Username or Password incorrect.")
                                            .setCancelable(false)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setNeutralButton("OK", (dialog, which) -> {
//                                                    load();
                                            }).show();
                                }
                            });
                }
            });

            CreateAccount.setOnClickListener(v -> {
                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
                finish();
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
            Username.setError("Username is required");
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
