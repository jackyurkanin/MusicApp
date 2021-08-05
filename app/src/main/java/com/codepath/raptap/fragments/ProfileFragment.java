package com.codepath.raptap.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.raptap.databinding.FragmentProfileBinding;
import com.codepath.raptap.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {

    private Context context;
    private FragmentProfileBinding binding;
    private TextView tvBio;
    private TextView tvUsername;
    private ImageView ivProfilePic;
    private User user;
    private ParseFile profileImage;

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

        tvBio = binding.tvBio;
        tvUsername = binding.tvProfileHandle;
        ivProfilePic = binding.ivProfilePic;

        user = (User) ParseUser.getCurrentUser();
        tvBio.setText(user.getBio());
        tvUsername.setText(user.getUserName());
        profileImage = user.getProfilePic();

        if (profileImage != null){
            Glide.with(context).load(profileImage.getUrl()).circleCrop().into(ivProfilePic);
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}