package com.codepath.raptap;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.raptap.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = "MainActivity";
    public static final String DEBUG = "DEBUG";
    private ActivityMainBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = MainActivity.this;

        view.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent touchEvent) {
        float xPos = touchEvent.getX();
        float yPos = touchEvent.getY();
        Log.e("TAG", "At position: (" + String.valueOf(xPos) + ", " + String.valueOf(yPos) + ")");
        return true;
    }
}