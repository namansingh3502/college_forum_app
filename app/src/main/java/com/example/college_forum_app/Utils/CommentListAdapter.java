package com.example.college_forum_app.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import com.example.college_forum_app.R;
import com.example.college_forum_app.models.Comments;
import com.example.college_forum_app.models.Users;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommentListAdapter extends ArrayAdapter<Comments> {

    private LayoutInflater mInflater;
    private int layoutResource;
    private Context mContext;

    public CommentListAdapter(@NonNull Context context, @LayoutRes int resource,
                              @NonNull List<Comments> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
    }

    private static class ViewHolder{
        TextView comment, username;
        CircleImageView profileImage;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.username = convertView.findViewById(R.id.username);
            holder.comment = convertView.findViewById(R.id.comment_posted);
            holder.profileImage = convertView.findViewById(R.id.user_img);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //set username
        holder.username.setText(getItem(position).getUser().getFull_name());

        //set the profile image
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(getItem(position).getUser().getUser_image(), holder.profileImage);

        holder.comment.setText(getItem(position).getBody());

        return convertView;
    }

}
