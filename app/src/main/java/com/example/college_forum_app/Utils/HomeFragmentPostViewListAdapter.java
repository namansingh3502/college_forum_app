package com.example.college_forum_app.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.college_forum_app.models.Likes;
import com.example.college_forum_app.models.Posts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
//import com.example.college_forum_app.Profile.ViewComments;
import com.example.college_forum_app.R;
import com.example.college_forum_app.models.Photo;
import com.example.college_forum_app.models.Users;

import org.json.JSONObject;

public class HomeFragmentPostViewListAdapter extends ArrayAdapter<Posts> {

    public interface OnLoadMoreItemsListener {
        void onLoadMoreItems();
    }

    OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    private static final String TAG = "HomePostViewListAdapter";

    private LayoutInflater mInflater;
    private int mLayoutResource;
    private Context mContext;
    private String currentUsername = "";
    private ProgressBar mProgressBar;

    public HomeFragmentPostViewListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Posts> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource = resource;
        this.mContext = context;
    }

    static class ViewHolder {
        CircleImageView mprofileImage;
        String likesString = "";
        TextView username, timeDetla, body, likes, comments, mTags;
        SquareImageView image;
        Button like_btn, comment_btn;

        Users settings = new Users();
        boolean likeByCurrentUser = false;
        Heart heart;
        GestureDetector detector;
        Photo photo;
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

        //set the profile image
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(getItem(position).getUser().getUser_image(), holder.mprofileImage);

        //set post text
        holder.username.setText(getItem(position).getUser().getFull_name());
        if (getItem(position).getBody().isEmpty())
            holder.body.setVisibility(View.GONE);
        else if (getItem(position).getIs_edited())
            holder.body.setText("Edited : \n\n" + getItem(position).getBody());
        else
            holder.body.setText(getItem(position).getBody());

        //set post tags
        holder.mTags.setText(getItem(position).getPosted_in_string());

        //set likes string
        ArrayList<Likes> likes = getItem(position).getLikes();
        if (holder.likeByCurrentUser) {
            holder.likes.setText("Liked by you, " + likes.get(0).getFull_name() + " and " + (likes.size() - 2) + " others.");
            holder.like_btn.setText("Liked");
        } else {
            holder.likes.setText("Liked by " + likes.get(0).getFull_name() + " and " + (likes.size() - 1) + " others.");
        }

        //set like button
        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("\n\npost id " + getItem(position).getId());
                final String[] str = {new String()};
                ArrayList<Likes> likes = getItem(position).getLikes();

                SharedPreferences storage = mContext.getSharedPreferences("College_Forum", mContext.MODE_PRIVATE);
                String auth_token = storage.getString("auth_token", "No token");
                String like_url = "http://192.168.40.254:8000/api/forum/" + getItem(position).getId() + "/like_post";

                if (holder.likeByCurrentUser) {
                    AndroidNetworking.post(like_url)
                            .addHeaders("Authorization", auth_token)
                            .setPriority(Priority.LOW)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    str[0] = "Liked by " + likes.get(0).getFull_name() + " and " + (likes.size() - 1) + " others.";
                                    holder.likes.setText(str[0]);
                                    holder.like_btn.setText("Like");
                                    holder.likeByCurrentUser = false;
                                }

                                @Override
                                public void onError(ANError anError) {
                                    System.out.println("\n\nError while updating like");
                                }
                            });
                } else {
                    AndroidNetworking.post(like_url)
                            .addHeaders("Authorization", auth_token)
                            .setPriority(Priority.LOW)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    str[0] = "Liked by you, " + likes.get(0).getFull_name() + " and " + (likes.size() - 1) + " others.";
                                    holder.likes.setText(str[0]);
                                    holder.like_btn.setText("Liked");
                                    holder.likeByCurrentUser = true;
                                }

                                @Override
                                public void onError(ANError anError) {
                                    System.out.println("\n\nError while updating like");
                                }
                            });
                }
            }
        });


//        holder.image.setVisibility(View.INVISIBLE);


        //set the comment
//        final List<Comments> comments = getItem(position).getComments();
//        holder.comments.setText("View all " + comments.size() + " comments");
//        holder.comments.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: loading comment thread for " + getItem(position).getPhoto_id());
//                Intent b = new Intent(mContext, ViewComments.class);
//                //Create the bundle
//                Bundle bundle = new Bundle();
//                //Add your data from getFactualResults method to bundle
//                bundle.putParcelable("Photo", getItem(position));
//                b.putExtra("commentcount",comments.size());
//                //Add the bundle to the intent
//                b.putExtras(bundle);
//                mContext.startActivity(b);
//
//            }
//        });

/*        set the time it was posted
        String timestampDifference = getTimestampDifference(getItem(position));
        if(!timestampDifference.equals("0")){
            holder.timeDetla.setText(timestampDifference + " DAYS AGO");
        }else{
            holder.timeDetla.setText("TODAY");
        }*/

        if (reachedEndOfList(position)) {
            loadMoreData();
        }

        return convertView;
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

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {

        ViewHolder mHolder;

        public GestureListener(ViewHolder holder) {
            mHolder = holder;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed: Singletap detected.");

/*            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child("Photo")
                    .child(mHolder.photo.getPhoto_id())
                    .child("likes");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        String keyID = singleSnapshot.getKey();

                        //case1: Then user already liked the photo
                        if(mHolder.likeByCurrentUser &&
                                singleSnapshot.getValue(Likes.class).getFull_name()
                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            mReference.child("Photo")
                                    .child(mHolder.photo.getPhoto_id())
                                    .child("likes")
                                    .child(keyID)
                                    .removeValue();
///
                            mReference.child("User_Photo")
                                    .child(mHolder.photo.getUser_id())
                                    .child(mHolder.photo.getPhoto_id())
                                    .child("likes")
                                    .child(keyID)
                                    .removeValue();

                            mHolder.heart.toggleLike();
                            getLikesString(mHolder);
                        }
                        //case2: The user has not liked the photo
                        else if(!mHolder.likeByCurrentUser){
                            //add new like
                            addNewLike(mHolder);
                            break;
                        }
                    }
                    if(!dataSnapshot.exists()){
                        //add new like
                        addNewLike(mHolder);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/

            return true;
        }
    }

    /**
     * Returns a string representing the number of days ago the post was made
     *
     * @return
     */
    private String getTimestampDifference(Photo photo) {
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = photo.getDate_Created();
        try {
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24)));
        } catch (ParseException e) {
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage());
            difference = "0";
        }
        return difference;
    }

}
