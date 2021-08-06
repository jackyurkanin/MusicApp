package com.codepath.raptap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.codepath.raptap.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = this;


    }
}