package com.example.college_forum_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.college_forum_app.models.AuthToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacksonandroidnetworking.JacksonParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView frm, logo, name1, name2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());

        if (isOnline()) {

            load();
        } else {
            try {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error")
                        .setMessage("Internet not available, Cross check your internet connectivity")
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                load();
                            }
                        }).show();
            } catch (Exception e) {
//                Log.d(Constants.TAG, "Show Dialog: " + e.getMessage());
            }
        }

    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    public void load() {
        logo = (TextView) findViewById(R.id.logo);
        name1 = (TextView) findViewById(R.id.name1);
        name2 = (TextView) findViewById(R.id.name2);
        frm = (TextView) findViewById(R.id.from);

        logo.animate().alpha(0f).setDuration(0);
        name1.animate().alpha(0f).setDuration(0);
        name2.animate().alpha(0f).setDuration(0);

        logo.animate().alpha(1f).setDuration(1000).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                name1.animate().alpha(1f).setDuration(800);
                name2.animate().alpha(1f).setDuration(800);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences storage = getSharedPreferences("College_Forum", MODE_PRIVATE);
                String auth_token = storage.getString("auth_token", "No token");

                if (auth_token.equals("No token")) {
                    Intent n = new Intent(MainActivity.this, Login.class);
                    startActivity(n);
                    finish();
                } else {

                    AndroidNetworking.get("http://192.168.40.55:8000/api/auth/users/me/")
                            .addHeaders("Authorization", "Token " + auth_token)
                            .setPriority(Priority.LOW)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    Intent z = new Intent(MainActivity.this, Home.class);
                                    startActivity(z);
                                    finish();

                                }

                                @Override
                                public void onError(ANError anError) {

                                    SharedPreferences sharedPreferences = getSharedPreferences("College_Forum", MODE_PRIVATE);
                                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

                                    myEdit.putString("auth_token", "");
                                    myEdit.apply();

                                    Intent z = new Intent(MainActivity.this, Login.class);
                                    startActivity(z);
                                    finish();
                                }
                            });
                }
            }
        }, 3000);
    }
}