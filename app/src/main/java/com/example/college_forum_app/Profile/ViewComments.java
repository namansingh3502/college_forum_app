package com.example.college_forum_app.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.college_forum_app.Home;
import com.example.college_forum_app.R;
import com.example.college_forum_app.Utils.CommentListAdapter;
import com.example.college_forum_app.models.ChannelTags;
import com.example.college_forum_app.models.Comments;
import com.example.college_forum_app.models.Likes;
import com.example.college_forum_app.models.Posts;
import com.example.college_forum_app.models.Users;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ViewComments extends AppCompatActivity {

    private static final String TAG ="ViewComments" ;

    //vars
    ArrayList<Comments> mComments;
    Integer commentCount;

    private EditText commentText;
    private ListView ListView;
    private TextView postComment;
    ImageView profileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comments);
        
        //widgets
        ImageView backArrow = (ImageView) findViewById(R.id.back_from_view_comment);
        backArrow.setOnClickListener(v1 -> {
            Intent intent =new Intent(ViewComments.this, Home.class);
            startActivity(intent);
        });

        ListView = (ListView) findViewById(R.id.listView);

        profileImage = (ImageView)findViewById(R.id.user_img);
        commentText = (EditText) findViewById(R.id.comment);
        postComment = (TextView)findViewById(R.id.post_comment) ;

        mComments = new ArrayList<>();

        getCommentList();

    }

    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addNewComment(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);

//        String commentID = myRef.push().getKey();

        Comments comment = new Comments();
        comment.setBody(newComment);
//        comment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

//        //insert into photos node
//        myRef.child("Photo")
//                .child(mphoto.getPhoto_id())
//                .child("comments")
//                .child(commentID)
//                .setValue(comment);
//
//        //insert into user_photos node
//        myRef.child("User_Photo")
//                .child(mphoto.getUser_id())
//                .child(mphoto.getPhoto_id())
//                .child("comments")
//                .child(commentID)
//                .setValue(comment);

    }

    private void getCommentList(){

        Bundle bundle = getIntent().getExtras();
        Integer post_id = (Integer) bundle.get("post_id");

        SharedPreferences storage = getSharedPreferences("College_Forum", Context.MODE_PRIVATE);
        String auth_token = storage.getString("auth_token", "No token");

        String get_comments_url = "http://192.168.40.254:8000/api/forum/comment/" + post_id + "/1000";

        AndroidNetworking.get(get_comments_url)
                .addHeaders("Authorization", auth_token)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray arr = null, channel_tag_details, like_details, image_details;
                        JSONObject comment_obj, user_details, post_details;

                        try {
                            arr = (JSONArray) response.get("comment");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int i = 0; i < Objects.requireNonNull(arr).length(); i++) {
                            try {
                                ObjectMapper objectMapper = new ObjectMapper();
                                Comments comment = new Comments();

                                comment_obj = (JSONObject) arr.get(i);

                                user_details = (JSONObject) comment_obj.get("user");
                                Users user = objectMapper.readValue(user_details.toString(), Users.class);

                                comment.setId((Integer) comment_obj.get("id"));
                                comment.setBody((String) comment_obj.get("body"));
                                comment.setTime((String) comment_obj.get("time"));
                                comment.setUser(user);

                                mComments.add(comment);

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                        setupWidgets();
                    }

                    @Override
                    public void onError(ANError anError) {
                        System.out.println("\n\nerror while getting post data\n\n");
                    }
                });

        /*myRef.child("Photo")
                .child(mphoto.getPhoto_id())
                .child("comments")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        Log.d(TAG, "onChildAdded: child added.");

                        Query query = myRef
                                .child("Photo")
                                .orderByChild("photo_id")
                                .equalTo(mphoto.getPhoto_id());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dsnapshot) {
                                for ( DataSnapshot singleSnapshot :  dsnapshot.getChildren()){
                                    Photo photo = new Photo();
                                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                                    photo.setCaption(objectMap.get("caption").toString());
                                    photo.setTags(objectMap.get("tags").toString());
                                    photo.setPhoto_id(objectMap.get("photo_id").toString());
                                    photo.setUser_id(objectMap.get("user_id").toString());
                                    photo.setDate_Created(objectMap.get("date_Created").toString());
                                    photo.setImage_Path(objectMap.get("image_Path").toString());

                                    mComments.clear();
                                    Comments firstComment = new Comments();
                                    firstComment.setUser_id(mphoto.getUser_id());
                                    firstComment.setComment(mphoto.getCaption());
                                    firstComment.setDate_created(mphoto.getDate_Created());
                                    mComments.add(firstComment);

//                                    List<Comments> commentsList = new ArrayList<Comments>();
                                    for (DataSnapshot dSnapshot : singleSnapshot
                                            .child("comments").getChildren()){
                                        Comments comments = new Comments();
                                        comments.setUser_id(dSnapshot.getValue(Comments.class).getUser_id());
                                        comments.setComment(dSnapshot.getValue(Comments.class).getComment());
                                        comments.setDate_created(dSnapshot.getValue(Comments.class).getDate_created());
                                        mComments.add(comments);
                                    }
                                    photo.setComments(mComments);
                                    mphoto=photo;
                                    setupWidgets();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/
    }

    private void setupWidgets(){

        CommentListAdapter adapter = new CommentListAdapter(this,R.layout.layout_each_comment, mComments);
        ListView.setAdapter(adapter);

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!commentText.getText().toString().isEmpty()){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment(commentText.getText().toString());

                    commentText.setText("");
                    closeKeyboard();
                }else{
                    Toast.makeText(ViewComments.this, "you can't post a blank comment", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addCommentNotification(String comment , String userid , String postid){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications");
//
//        HashMap<String, Object> hashMappp = new HashMap<>();
//        hashMappp.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
//        hashMappp.put("text", "Commented!"+comment);
//        hashMappp.put("postid",postid);
//        hashMappp.put("ispost", true);
//        reference.child(userid).push().setValue(hashMappp);

    }

}