package com.example.college_forum_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.example.college_forum_app.Post.PostActivity;
import com.example.college_forum_app.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jacksonandroidnetworking.JacksonParserFactory;

public class Home extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navigationView = findViewById(R.id.insta_bottom_navigation);

        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());

        navigationView.setOnNavigationItemSelectedListener(this);
        String name = getIntent().getStringExtra("PAGE");

        loadFragment(new HomeFragment());

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