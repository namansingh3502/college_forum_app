package com.example.college_forum_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.college_forum_app.Post.PostActivity;
import com.example.college_forum_app.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jacksonandroidnetworking.JacksonParserFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class Home extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navigationView = findViewById(R.id.insta_bottom_navigation);

        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());

        navigationView.setOnNavigationItemSelectedListener(this);

        loadUserDetails();

        loadFragment(new HomeFragment());
    }

    private void loadUserDetails() {
        SharedPreferences storage = getSharedPreferences("College_Forum", MODE_PRIVATE);
        String auth_token = storage.getString("auth_token", "No token");

        AndroidNetworking.get("http://192.168.40.254:8000/api/auth/user/")
                .addHeaders("Authorization", auth_token)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        SharedPreferences sharedPreferences = getSharedPreferences("College_Forum", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        try {
                            myEdit.putString("user_id", response.get("id").toString());
                            myEdit.putString("username", response.get("username").toString());
                            myEdit.putString("full_name", response.get("full_name").toString());
                            myEdit.putString("user_image", response.get("user_image").toString());
                            myEdit.apply();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        SharedPreferences sharedPreferences = getSharedPreferences("College_Forum", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();

                        myEdit.putString("auth_token", "");
                        myEdit.apply();

                        Intent z = new Intent(Home.this, Login.class);
                        startActivity(z);
                        finish();
                    }
                });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }

        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.Home:
                fragment = new HomeFragment();
                break;


            case R.id.post:
                fragment = null;
                startActivity(new Intent(Home.this, PostActivity.class));
                break;

//            case R.id.profile:
//                fragment = new com.example.college_forum_app.Profile.ProfileFragment();
//                break;
        }
        return loadFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}