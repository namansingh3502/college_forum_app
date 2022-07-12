package com.example.college_forum_app.Profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.google.firebase.auth.FirebaseAuth;

import com.example.college_forum_app.Login;
import com.example.college_forum_app.R;

import okhttp3.Response;

public class Account_Settings extends AppCompatActivity {

    TextView editProfile,logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account__settings);

        editProfile = (TextView)findViewById(R.id.edit_profile);
        logout = (TextView)findViewById(R.id.logout);

/*        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Account_Settings.this, EditProfile.class);
                startActivity(intent);
            }
        });*/

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Account_Settings.this)
                        .setMessage("Are you sure you want to Logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                SharedPreferences storage = getSharedPreferences("College_Forum", MODE_PRIVATE);
                                String auth_token = storage.getString("auth_token", "No token");


                                AndroidNetworking.post("http://192.168.40.254:8000/api/auth/token/logout/")
                                        .addHeaders("Authorization", auth_token)
                                        .setPriority(Priority.LOW)
                                        .build()
                                        .getAsOkHttpResponse(new OkHttpResponseListener() {
                                            @Override
                                            public void onResponse(Response response) {
                                                SharedPreferences.Editor myEdit = storage.edit();
                                                myEdit.putString("auth_token", "");
                                                myEdit.apply();

                                                Intent intent = new Intent(Account_Settings.this, Login.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                                startActivity(intent);
                                            }

                                            @Override
                                            public void onError(ANError anError) {
                                                System.out.println("Error while logout.");
                                            }
                                        });

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


    }
}