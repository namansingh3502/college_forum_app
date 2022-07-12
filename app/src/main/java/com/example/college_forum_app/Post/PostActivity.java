package com.example.college_forum_app.Post;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.IOException;
import java.util.ArrayList;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.college_forum_app.Home;
import com.example.college_forum_app.Login;
import com.example.college_forum_app.R;
import com.example.college_forum_app.models.ChannelTags;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PostActivity extends AppCompatActivity {

    ImageView postNow, backFromPost;
    EditText add_body;
    TextView add_channel, selected_channel;

    ArrayList<ChannelTags> channelList = new ArrayList<>();

    boolean[] selectedChannel;
    String[] channelNameArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postNow = (ImageView) findViewById(R.id.post_now);
        backFromPost = (ImageView) findViewById(R.id.back_from_post);
        add_channel = (TextView) findViewById(R.id.add_channel);
        add_body = (EditText) findViewById(R.id.add_body);
        selected_channel = (TextView) findViewById(R.id.selected_channels);

        getChannels();

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

                JSONObject postData = new JSONObject();
                JSONArray selectedChannelList = new JSONArray();

                for (int i = 0; i < channelList.size(); i++) {
                    JSONObject channel = new JSONObject();
                    if (selectedChannel[i]) {
                        try {
                            channel.put("id", channelList.get(i).getId());
                            channel.put("name", channelList.get(i).getName());

                            selectedChannelList.put(channel);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    postData.put("body", add_body.getText().toString());
                    postData.put("channel_list", selectedChannelList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                createPost(postData);
            }
        });
    }

    public void createPost(JSONObject postObject) {
        SharedPreferences storage = getSharedPreferences("College_Forum", MODE_PRIVATE);
        String auth_token = storage.getString("auth_token", "No token");

        AndroidNetworking.post("http://192.168.40.254:8000/api/forum/new_post")
                .addHeaders("Authorization", auth_token)
                .addBodyParameter("data", String.valueOf(postObject))
                .setContentType("multipart/form-data; boundary=----WebKitFormBoundaryNwYuv6mxLUy4Zd3D")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent z = new Intent(PostActivity.this, Home.class);
                        startActivity(z);
                        finish();
                    }

                    @Override
                    public void onError(ANError anError) {
                        System.out.println("\n\nerror while creating post " + anError.getErrorBody());
                    }
                });
    }

    public void getChannels() {
        SharedPreferences storage = getSharedPreferences("College_Forum", MODE_PRIVATE);
        String auth_token = storage.getString("auth_token", "No token");

        AndroidNetworking.get("http://192.168.40.254:8000/api/forum/channel_list")
                .addHeaders("Authorization", auth_token)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        channelNameArray = new String[response.length()];

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                ChannelTags channel = objectMapper.readValue(response.get(i).toString(), ChannelTags.class);
                                channelList.add(channel);
                                channelNameArray[i] = channel.getName();
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        setChannelDropbox();
                    }

                    @Override
                    public void onError(ANError anError) {
                        System.out.println("\n\nchannels " + anError);

                    }
                });
    }

    public void setChannelDropbox() {

        selectedChannel = new boolean[channelNameArray.length];

        add_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);

                // set title
                builder.setTitle("Select Channel");

                // set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(channelNameArray, selectedChannel, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        selectedChannel[i] = b;
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder str = new StringBuilder();

                        for (int j = 0; j < channelList.size(); j++) {
                            if (selectedChannel[j]) {
                                str.append("#").append(channelNameArray[j]).append("  ");
                            }
                        }
                        selected_channel.setText(str);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // use for loop
                        for (int j = 0; j < selectedChannel.length; j++) {
                            // remove all selection
                            selectedChannel[j] = false;
                            // clear language list
                            channelList.clear();
                            // clear text view value
                        }
                    }
                });
                // show dialog
                builder.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}