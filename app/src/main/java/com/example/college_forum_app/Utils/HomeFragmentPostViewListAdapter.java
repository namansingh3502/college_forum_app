package com.example.college_forum_app.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.college_forum_app.Profile.ViewComments;
import com.example.college_forum_app.R;
import com.example.college_forum_app.models.Likes;
import com.example.college_forum_app.models.Posts;
import com.example.college_forum_app.models.Users;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragmentPostViewListAdapter extends ArrayAdapter<Posts> {

    private static final String TAG = "HomePostViewListAdapter";
    OnLoadMoreItemsListener mOnLoadMoreItemsListener;
    private LayoutInflater mInflater;
    private int mLayoutResource;
    private Context mContext;
    private String currentUsername = "";
    private ProgressBar mProgressBar;
    private boolean likeByCurrentUser = false;

    public HomeFragmentPostViewListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Posts> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource = resource;
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutResource, parent, false);
            holder = new ViewHolder();

            holder.mprofileImage = (CircleImageView) convertView.findViewById(R.id.fragment_home_post_viewer_user_img);
            holder.username = (TextView) convertView.findViewById(R.id.fragment_home_post_viewer_username);
//            holder.image = (SquareImageView) convertView.findViewById(R.id.fragment_home_post_viewer_post_image);
            holder.body = (TextView) convertView.findViewById(R.id.fragment_home_post_viewer_body);
            holder.mTags = (TextView) convertView.findViewById(R.id.fragment_home_post_viewer_txt_tags);

            holder.likes = (TextView) convertView.findViewById(R.id.fragment_home_post_viewer_txt_likes);
            holder.like_btn = (Button) convertView.findViewById(R.id.fragment_home_post_viewer_like_button);
            holder.comment_btn = (Button) convertView.findViewById(R.id.fragment_home_post_viewer_comment_button);

            holder.detector = new GestureDetector(mContext, new GestureListener(holder));
//            mProgressBar = (ProgressBar)convertView.findViewById(R.id.fragment_home_post_viewer_ProgressBar);
//            holder.timeDetla = (TextView) convertView.findViewById(R.id.fragment_home_post_viewer_txt_timePosted);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //set the profile name
        holder.username.setText(getItem(position).getUser().getFull_name());

        //set the profile image
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(getItem(position).getUser().getUser_image(), holder.mprofileImage);

        //set post tags
        holder.mTags.setText(getItem(position).getPosted_in_string());

        //set post text
        if (getItem(position).getBody().isEmpty())
            holder.body.setVisibility(View.GONE);
        holder.body.setText(
                (getItem(position).getIs_edited() ? "Edited : \n\n" : "")
                        + getItem(position).getBody()
        );

        //set likes string
        holder.likes.setText(setLikeString(getItem(position).getLikes()));

        //set like buttoon text
        holder.like_btn.setText(likeByCurrentUser ? "Liked" : "Like");

        //set like button
        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("\n\npost id " + getItem(position).getId());

                ArrayList<Likes> likes = getItem(position).getLikes();

                SharedPreferences storage = mContext.getSharedPreferences("College_Forum", mContext.MODE_PRIVATE);
                String auth_token = storage.getString("auth_token", "No token");
                String user_full_name = storage.getString("full_name", "No user");
                Integer user_id = Integer.parseInt(storage.getString("user_id", "No user"));

                String update_like_url = "http://192.168.40.254:8000/api/forum/" + getItem(position).getId() + "/like_post";

                AndroidNetworking.post(update_like_url)
                        .addHeaders("Authorization", auth_token)
                        .setPriority(Priority.LOW)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {

                                Likes current_user = new Likes(user_full_name, user_id);

                                System.out.println("\n\nlike counts " + likes.size() + " " + likeByCurrentUser);

                                if (likeByCurrentUser) {
                                    getItem(position).removeLike(current_user);
                                } else {
                                    getItem(position).addLike(current_user);
                                }
                                likeByCurrentUser = !likeByCurrentUser;

                                System.out.println("\n\nlike counts " + likes.size() + " " + likeByCurrentUser);

                                holder.like_btn.setText(likeByCurrentUser ? "Liked" : "Like");
                                holder.likes.setText(setLikeString(getItem(position).getLikes()));

                            }

                            @Override
                            public void onError(ANError anError) {
                                System.out.println("\n\nError while updating like");
                            }
                        });
            }
        });

        //set the comment
        holder.comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent b = new Intent(mContext, ViewComments.class);
                b.putExtra("post_id", getItem(position).getId());
                mContext.startActivity(b);

            }
        });

        if (reachedEndOfList(position)) {
            loadMoreData();
        }

        return convertView;
    }

    private String setLikeString(ArrayList<Likes> likes) {
        SharedPreferences storage = getContext().getSharedPreferences("College_Forum", Context.MODE_PRIVATE);
        String user_id = storage.getString("user_id", "No user");

        //if post has likes then check if it is liked by current user else mark false
        if (likes.size() > 0) {
            likeByCurrentUser = checkLikedByCurrentUser(likes, user_id);
            StringBuilder likeString = new StringBuilder("Liked by ");

            if (likeByCurrentUser) likeString.append("you");

            if (likes.size() > 1) {
                if (!likeByCurrentUser) {
                    likeString.append(likes.get(0).getFull_name());
                } else if (likeByCurrentUser && user_id.equals(likes.get(0).getUser_id().toString())) {
                    likeString.append(", " + likes.get(1).getFull_name());
                } else if (likeByCurrentUser && !user_id.equals(likes.get(0).getUser_id().toString())) {
                    likeString.append(", " + likes.get(0).getFull_name());
                }
            }

            if (likes.size() > 2) {
                if (likeByCurrentUser) {
                    likeString.append(" and " + (likes.size() - 2) + " other.");
                } else {
                    likeString.append(" and " + (likes.size() - 1) + " other.");
                }
            }

            return likeString.toString();
        } else {
            likeByCurrentUser = false;
            return "Be the first to like.";
        }
    }

    private boolean checkLikedByCurrentUser(ArrayList<Likes> likes, String user_id) {
        for (Likes like : likes)
            if (user_id.equals(like.getUser_id().toString()))
                return true;

        return false;
    }

    private boolean reachedEndOfList(int position) {
        return position == getCount() - 1;
    }

    private void loadMoreData() {

        try {
            mOnLoadMoreItemsListener = (OnLoadMoreItemsListener) getContext();
        } catch (ClassCastException e) {
            Log.e(TAG, "loadMoreData: ClassCastException: " + e.getMessage());
        }

        try {
            mOnLoadMoreItemsListener.onLoadMoreItems();
        } catch (NullPointerException e) {
            Log.e(TAG, "loadMoreData: ClassCastException: " + e.getMessage());
        }
    }



    public interface OnLoadMoreItemsListener {
        void onLoadMoreItems();
    }

    static class ViewHolder {
        CircleImageView mprofileImage;
        String likesString = "";
        TextView username, timeDetla, body, likes, comments, mTags;
        SquareImageView image;
        Button like_btn, comment_btn;

        Users settings = new Users();
        Heart heart;
        GestureDetector detector;
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {

        ViewHolder mHolder;

        public GestureListener(ViewHolder holder) {
            mHolder = holder;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

    }

}
