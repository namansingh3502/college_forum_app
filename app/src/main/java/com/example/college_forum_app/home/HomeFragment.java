package com.example.college_forum_app.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.college_forum_app.Profile.Account_Settings;
import com.example.college_forum_app.models.ChannelTags;
import com.example.college_forum_app.models.Image;
import com.example.college_forum_app.models.Likes;
import com.example.college_forum_app.models.Posts;
import com.example.college_forum_app.models.Users;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.io.IOException;
import java.util.ArrayList;


import com.example.college_forum_app.R;
import com.example.college_forum_app.Utils.HomeFragmentPostViewListAdapter;
import com.example.college_forum_app.Utils.UniversalImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private ArrayList<Posts> mPosts;
    private ArrayList<ChannelTags> mChannelTags;
    private ArrayList<Posts> mPaginatedPosts;
    private ArrayList<Likes> mLikes;
    private ListView mListView;
    private ArrayList<String> mImagePaths;
    ImageView account_setting_menu;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, null);
        mListView = v.findViewById(R.id.FragmentHome_postListView);
        mPosts = new ArrayList<>();
        mPaginatedPosts = new ArrayList<>();
        ArrayList<String> mFollowing = new ArrayList<>();

        account_setting_menu = v.findViewById(R.id.accountSettingMenu);
        account_setting_menu.setOnClickListener(v1 -> {
            Intent intent =new Intent(getActivity(),Account_Settings.class);
            startActivity(intent);
        });

        loadPosts();
        initImageLoader();

        return v;
    }

    private void loadPosts() {
        Log.d(TAG, "getFollowing: searching for following");

        SharedPreferences storage = this.requireActivity().getSharedPreferences("College_Forum", Context.MODE_PRIVATE);
        String auth_token = storage.getString("auth_token", "No token");

        AndroidNetworking.get("http://192.168.40.254:8000/api/forum/posts/10000")
                .addHeaders("Authorization", auth_token)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray arr = null, channel_tag_details = null, like_details = null, image_details = null;
                        JSONObject post_obj, user_details, post_details;

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
                                image_details = (JSONArray) post_obj.get("media");

                                channel_tag_details = (JSONArray) post_details.get("posted_in");
                                like_details = (JSONArray) post_details.get("likes");

                                Posts post = new Posts();
                                Users user = objectMapper.readValue(user_details.toString(), Users.class);

                                post.setId((Integer) post_details.get("id"));
                                post.setBody((String) post_details.get("body"));
                                post.setTime((String) post_details.get("time"));
                                post.setIs_edited((Boolean) post_details.get("is_edited"));

                                mChannelTags = new ArrayList<>();
                                for (int j = 0; j < channel_tag_details.length(); j++) {
                                    ChannelTags channelTags = objectMapper.readValue(channel_tag_details.get(j).toString(), ChannelTags.class);
                                    mChannelTags.add(channelTags);
                                }

                                mLikes = new ArrayList<Likes>();
                                for (int j = 0; j < like_details.length(); j++) {
                                    Likes like = objectMapper.readValue(like_details.get(j).toString(), Likes.class);
                                    mLikes.add(like);
                                }

                                mImagePaths = new ArrayList<String>();
                                for (int j=0; j < image_details.length(); j++){
                                    Image image = objectMapper.readValue(image_details.get(j).toString(), Image.class);
                                    mImagePaths.add(image.getFile());
                                }

                                post.setImage_urls(mImagePaths);
                                post.setPosted_in(mChannelTags);
                                post.setLikes(mLikes);
                                post.setUser(user);

                                mPosts.add(post);

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                        displayPosts();
                    }

                    @Override
                    public void onError(ANError anError) {
                        System.out.println("\n\nerror while getting post data\n\n");
                    }
                });
    }

    void displayPosts() {
        Log.e(TAG, "\n\ndisplayPosts: Display posts");

        if (mPosts != null) {

            try {

                int iteration = mPosts.size();

                if (iteration > 10) {
                    iteration = 10;
                }

                int mResults = 10;

                for (int i = 0; i < iteration; i++) {
                    mPaginatedPosts.add(mPosts.get(i));
                }

                HomeFragmentPostViewListAdapter mAdapter = new HomeFragmentPostViewListAdapter(requireActivity(), R.layout.fragment_home_post_viewer, mPaginatedPosts);
                mListView.setAdapter(mAdapter);

            } catch (NullPointerException e) {
                Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage());
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage());
            }
        }
    }


    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
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
