package com.codepath.raptap.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.raptap.FeedAdapter;
import com.codepath.raptap.databinding.FragmentHomeBinding;
import com.codepath.raptap.models.EndlessRecyclerViewScrollListener;
import com.codepath.raptap.models.Sound;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Context context;
    private FragmentHomeBinding binding;
    private static final String TAG = "HomeFragment";
    private RecyclerView rvFeed;
    protected FeedAdapter adapter;
    protected List<Sound> allSounds;
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout swipeContainer;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(Context context) {
        HomeFragment fragment = new HomeFragment();
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
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);
        View v = binding.getRoot();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvFeed = binding.rvFeed;
        swipeContainer = binding.swipeContainer;
        allSounds = new ArrayList<>();
        adapter = new FeedAdapter(context, allSounds);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvFeed.setAdapter(adapter);
        rvFeed.setLayoutManager(linearLayoutManager);
//        rvFeed.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        rvFeed.setAdapter(adapter);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(totalItemsCount);
            }
        };
        rvFeed.addOnScrollListener(scrollListener);
        // query posts from Parstagram
        queryPosts();
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
        query.include(Sound.KEY_USER);
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
                allSounds.addAll(sounds);
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
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
                allSounds.addAll(allSounds.size(), sounds);
                // notify the adapter we added new stuff to the end of the list
                adapter.notifyItemRangeInserted(offset, sounds.size() - 1);
                swipeContainer.setRefreshing(false);
            }
        });
    }
}