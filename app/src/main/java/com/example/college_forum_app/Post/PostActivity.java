package com.example.college_forum_app.Post;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import com.example.college_forum_app.Home;
import com.example.college_forum_app.R;
import com.example.college_forum_app.Utils.methods;
import com.example.college_forum_app.models.Photo;
import com.example.college_forum_app.models.Users;

public class PostActivity extends AppCompatActivity {

    ImageView postNow,backFromPost,addedImage;
    EditText addedCaption,AddedTag;

    methods method;

    int count = 0;
    int PICK_IMAGE_REQUEST=1;

    Uri imageUri;
    String RandomUId,userId;
    String postCount;
    String caption,tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postNow = (ImageView)findViewById(R.id.post_now);
        backFromPost = (ImageView)findViewById(R.id.back_from_post);
        addedImage = (ImageView)findViewById(R.id.added_image);
        addedCaption = (EditText)findViewById(R.id.added_caption);
        AddedTag = (EditText)findViewById(R.id.added_tags);

        method = new methods();
        count = getCount();

        backFromPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, Home.class));
                finish();
            }
        });

        postNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadimage();
            }
        });

        openFileChooser();

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();
            addedImage.setImageURI(imageUri);

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadimage() {
    }

//******************************FUNCTION TO ADD PHOTO TO FIREBASE STORAGE********
    public void addPost(String caption, String date_Created, String image_Path, String photo_id, String user_id, String tags){

        HashMap<String, String> hashMappp = new HashMap<>();
        hashMappp.put("caption", caption);
        hashMappp.put("date_Created", date_Created);
        hashMappp.put("image_Path", image_Path);
        hashMappp.put("photo_id", photo_id);
        hashMappp.put("tags", tags);
        hashMappp.put("user_id", user_id);
    }


//******************************FUNCTION TO GET POST TIME********
    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }
//******************************FUNCTION TO GET POST Count********
    public int getCount() {
        return 10;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}