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

import com.codepath.raptap.R;
import com.codepath.raptap.databinding.ActivityMainBinding;
import com.codepath.raptap.fragments.HomeFragment;
import com.codepath.raptap.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private Context context;
    private Fragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = this;

        fragmentManager = getSupportFragmentManager();
        fragment = HomeFragment.newInstance(this);

        bottomNavigationView = binding.bottomNavigation;
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.miHome:
                                fragment = HomeFragment.newInstance(context);
                                break;
                            case R.id.miProfile:
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
        bottomNavigationView.setSelectedItemId(R.id.miHome);
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
            case R.id.miStudio:
                Intent studio = new Intent(MainActivity.this, SoundActivity.class);
                startActivity(studio);
            case R.id.miSettings:
                Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settings);
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