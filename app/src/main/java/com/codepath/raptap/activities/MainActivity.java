package com.codepath.raptap.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.raptap.R;
import com.codepath.raptap.databinding.ActivityMainBinding;
import com.codepath.raptap.fragments.HomeFragment;
import com.codepath.raptap.fragments.ProfileFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private Context context;
    private Fragment fragment;
    private SmoothBottomBar bottomBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = this;

        fragmentManager = getSupportFragmentManager();
        fragment = HomeFragment.newInstance(this);

        bottomBar = binding.bottomBar;
        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                switch (i) {
                    case 0:
                        fragment = HomeFragment.newInstance(context);
                        break;
                    case 1:
                        fragment = ProfileFragment.newInstance(context);
                        break;
                    default:
                        fragment = ProfileFragment.newInstance(context);
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        // Set default selection
        bottomBar.setItemActiveIndex(0);
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miLogout:
                ParseUser.logOut();
                if(ParseUser.getCurrentUser() == null)
                    goToLoginActivity();
                return true;
            case R.id.miSettings:
                Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settings);
                return true;
            case R.id.miStudio:
                Intent studio = new Intent(MainActivity.this, SoundActivity.class);
                startActivity(studio);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}