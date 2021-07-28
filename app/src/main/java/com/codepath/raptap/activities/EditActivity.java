package com.codepath.raptap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.raptap.R;
import com.codepath.raptap.databinding.ActivityEditBinding;
import com.codepath.raptap.databinding.ActivitySoundBinding;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = "EditActivity";
    public static final String DEBUG = "DEBUG";
    private ActivityEditBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sound_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miBack:
//                mp.release();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}