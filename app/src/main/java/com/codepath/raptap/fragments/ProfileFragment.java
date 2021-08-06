package com.codepath.raptap.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.raptap.FeedAdapter;
import com.codepath.raptap.LibraryAdapter;
import com.codepath.raptap.databinding.FragmentProfileBinding;
import com.codepath.raptap.models.EndlessRecyclerViewScrollListener;
import com.codepath.raptap.models.Sound;
import com.codepath.raptap.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    private static final String KEY_BIO = "bio";
    private static final String KEY_PROFILE_PICTURE = "picture";
    private Context context;
    private FragmentProfileBinding binding;
    private TextView tvBio;
    private TextView tvUsername;
    private ImageView ivProfilePic;
    private RecyclerView rvLibrary;
    private SwipeRefreshLayout swipeContainerLibrary;
    private ParseUser user;
    private ParseFile profileImage;
    private String username;
    private String bio;
    protected LibraryAdapter adapter;
    protected List<Sound> librarySounds;
    private EndlessRecyclerViewScrollListener scrollListenerLibrary;
    private DividerItemDecoration libraryDivider;


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

        rvLibrary = binding.rvLibrary;
        swipeContainerLibrary = binding.swipeContainerLibrary;
        librarySounds = new ArrayList<>();
        adapter = new LibraryAdapter(context, librarySounds);

        swipeContainerLibrary.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainerLibrary.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvLibrary.setAdapter(adapter);
        rvLibrary.setLayoutManager(linearLayoutManager);
        libraryDivider = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        rvLibrary.addItemDecoration(libraryDivider);
        rvLibrary.setAdapter(adapter);
        scrollListenerLibrary = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(totalItemsCount);
            }
        };
        rvLibrary.addOnScrollListener(scrollListenerLibrary);
        queryPosts();
    }

    public void getUserFields() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        query.findInBackground((users, e) -> {
            if (e == null) {
                // The query was successful, returns the users that matches
                // the criteria.
                for(ParseUser user1 : users) {
                    bio = user1.getString(KEY_BIO);
                    tvBio.setText(bio);
                    profileImage = user1.getParseFile(KEY_PROFILE_PICTURE);
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

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        adapter.clear();
        queryPosts();
    }

    private void queryPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Sound> query = ParseQuery.getQuery(Sound.class);
        // include data referred by user key
//        query.include(Sound.KEY_USER);
        query.whereEqualTo("user", user.getObjectId());
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Sound>() {
            @Override
            public void done(List<Sound> sounds, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // save received posts to list and notify adapter of new data
                librarySounds.addAll(sounds);
                adapter.notifyDataSetChanged();
                swipeContainerLibrary.setRefreshing(false);
            }
        });
    }

    public void loadNextDataFromApi(int offset) {
        ParseQuery<Sound> query = ParseQuery.getQuery(Sound.class);
        // include data referred by user key
        query.include(Sound.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(20);
        //Skip the first offset amount of post
        query.setSkip(offset);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Sound>() {
            @Override
            public void done(List<Sound> sounds, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                // Add all the posts we received back to the end of our posts list
                librarySounds.addAll(librarySounds.size(), sounds);
                // notify the adapter we added new stuff to the end of the list
                adapter.notifyItemRangeInserted(offset, sounds.size() - 1);
                swipeContainerLibrary.setRefreshing(false);
            }
        });
    }
}