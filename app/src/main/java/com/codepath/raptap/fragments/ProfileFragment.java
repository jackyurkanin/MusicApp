package com.codepath.raptap.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.raptap.databinding.FragmentProfileBinding;
import com.codepath.raptap.models.User;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    private static final String KEY_BIO = "bio";
    private static final String KEY_LIBRARY = "library";
    private static final String KEY_PROFILE_PICTURE = "picture";
    private Context context;
    private FragmentProfileBinding binding;
    private TextView tvBio;
    private TextView tvUsername;
    private ImageView ivProfilePic;
    private ParseUser user;
    private ParseFile profileImage;
    private String username;
    private String bio;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(Context context) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.context = context;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
        View v = binding.getRoot();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvBio = binding.tvBio;
        tvUsername = binding.tvUsername;
        ivProfilePic = binding.ivProfilePic;

        user = ParseUser.getCurrentUser();
        username = user.getUsername();
        getUserFields();

        tvUsername.setText(username);

        JSONArray library = user.getJSONArray(KEY_LIBRARY);
        // ToDo:
        //   need to add recycler function for songs in library
    }

    public void getUserFields() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        query.findInBackground((users, e) -> {
            if (e == null) {
                // The query was successful, returns the users that matches
                // the criteria.
                for(ParseUser user1 : users) {
                    Log.d("User List ", (user1.getUsername()));
                    bio = user1.getString(KEY_BIO);
                    tvBio.setText(bio);
                    profileImage = user1.getParseFile(KEY_PROFILE_PICTURE);
                    Log.e(TAG, String.valueOf(profileImage));
                    if (profileImage != null){
                        Glide.with(context).load(profileImage.getUrl()).circleCrop().into(ivProfilePic);
                    }
                }
            } else {
                // Something went wrong.
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}