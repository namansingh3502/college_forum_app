package com.example.college_forum_app.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.college_forum_app.models.AuthToken;
import com.example.college_forum_app.models.Posts;
import com.example.college_forum_app.models.Users;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacksonandroidnetworking.JacksonParserFactory;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


import com.example.college_forum_app.Home;
import com.example.college_forum_app.R;
import com.example.college_forum_app.Utils.HomeFragmentPostViewListAdapter;
import com.example.college_forum_app.Utils.UniversalImageLoader;
import com.example.college_forum_app.models.Comments;
import com.example.college_forum_app.models.Photo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private ArrayList<Posts> mPosts;
    private ArrayList<Photo> mPaginatedPhotos;
    private ListView mListView;
    private HomeFragmentPostViewListAdapter mAdapter;
    private int mResults;
    ImageView message;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, null);
        mListView = v.findViewById(R.id.FragmentHome_postListView);
        ArrayList<String> mFollowing = new ArrayList<>();
        mPosts = new ArrayList<>();
        mPaginatedPhotos = new ArrayList<>();

        loadPosts();

        return v;
    }

    private void loadPosts() {
        Log.d(TAG, "getFollowing: searching for following");

        SharedPreferences storage = this.requireActivity().getSharedPreferences("College_Forum", Context.MODE_PRIVATE);
        String auth_token = storage.getString("auth_token", "No token");

        AndroidNetworking.get("http://192.168.40.55:8000/api/forum/posts/10000")
                .addHeaders("Authorization", "Token " + auth_token)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray arr = null;
                        JSONObject post_obj, user_details, post_details, likes_details;

                        try {
                            arr = (JSONArray) response.get("posts");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int i = 0; i < arr.length(); i++) {
                            try {

                                ObjectMapper objectMapper = new ObjectMapper();

                                post_obj = (JSONObject) arr.get(i);
                                user_details = (JSONObject) post_obj.get("user");
                                post_details = (JSONObject) post_obj.get("post");

                                Posts post = new Posts();
                                Users user = objectMapper.readValue(user_details.toString(), Users.class);

                                post.setId((Integer) post_details.get("id"));
                                post.setBody((String) post_details.get("body"));
                                post.setTime((String) post_details.get("time"));
                                post.setIs_edited((Boolean) post_details.get("is_edited"));

                                post.setUser(user);
                                mPosts.add(post);

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        System.out.println("\n\nerror while getting post data\n\n");
                    }
                });
    }


    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void getPhotos() {
        Log.d(TAG, "getPhotos: getting photos");
/*        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        for(int i = 0; i < mFollowing.size(); i++){
            final int count = i;
            Query query = reference
                    .child("User_Photo")
                    .child(mFollowing.get(i))
                    .orderByChild("user_id")
                    .equalTo(mFollowing.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        Photo photo = new Photo();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        photo.setCaption(objectMap.get("caption").toString());
                        photo.setTags(objectMap.get("tags").toString());
                        photo.setPhoto_id(objectMap.get("photo_id").toString());
                        photo.setUser_id(objectMap.get("user_id").toString());
                        photo.setDate_Created(objectMap.get("date_Created").toString());
                        photo.setImage_Path(objectMap.get("image_Path").toString());


                        ArrayList<Comments> comments = new ArrayList<Comments>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child("comments").getChildren()){
                            Comments comment = new Comments();
                            comment.setUser_id(dSnapshot.getValue(Comments.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comments.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comments.class).getDate_created());
                            comments.add(comment);
                        }

                        photo.setComments(comments);
                        mPhotos.add(photo);
                    }
                    if(count >= mFollowing.size() -1){
                        //display our photos
                        displayPhotos();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }*/
    }

    private void displayPhotos() {
       /* if (mPhotos != null) {
            try {
                Collections.sort(mPhotos, new Comparator<Photo>() {
                    @Override
                    public int compare(Photo o1, Photo o2) {
                        return o2.getDate_Created().compareTo(o1.getDate_Created());
                    }
                });

                int iterations = mPhotos.size();

                if (iterations > 10) {
                    iterations = 10;
                }

                mResults = 10;
                for (int i = 0; i < iterations; i++) {
                    mPaginatedPhotos.add(mPhotos.get(i));
                }

                mAdapter = new HomeFragmentPostViewListAdapter(getActivity(), R.layout.fragment_home_post_viewer, mPaginatedPhotos);
                mListView.setAdapter(mAdapter);

            } catch (NullPointerException e) {
                Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage());
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage());
            }
        }*/
    }

    public void displayMorePhotos() {
/*        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try {

            if (mPhotos.size() > mResults && mPhotos.size() > 0) {

                int iterations;
                if (mPhotos.size() > (mResults + 10)) {
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                } else {
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = mPhotos.size() - mResults;
                }

                //add the new photos to the paginated results
                for (int i = mResults; i < mResults + iterations; i++) {
                    mPaginatedPhotos.add(mPhotos.get(i));
                }
                mResults = mResults + iterations;
                mAdapter.notifyDataSetChanged();
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage());
        }*/
    }
}
