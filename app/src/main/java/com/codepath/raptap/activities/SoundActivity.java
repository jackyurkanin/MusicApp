package com.codepath.raptap.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.raptap.R;
import com.codepath.raptap.databinding.ActivitySoundBinding;

public class SoundActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = "SoundActivity";
    public static final String DEBUG = "DEBUG";
    private ActivitySoundBinding binding;
    private Context context;
    private MediaPlayer mp;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySoundBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = this;
        mp = MediaPlayer.create(context, R.raw.sound);
        view.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent touchEvent) {
        // bounds: 0 < x < 1079
        float xPos = touchEvent.getX();
        // bounds: 0 < x < 1818
        float yPos = touchEvent.getY();
        Log.i("TAG", "At position: (" + String.valueOf(xPos) + ", " + String.valueOf(yPos) + ")");
        // To Do:
        //  Use x & y to control frequency and pitch

        try {
            switch(touchEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mp == null) {
                        mp = MediaPlayer.create(context, R.raw.sound);
                    }
                    if (mp.isPlaying()) {
                        mp.stop();
                        mp.reset();
                    }
                    mp.start();
                    break;
                case MotionEvent.ACTION_UP:
                    mp.stop();
                    mp.reset();
                    mp.release();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
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
                mp.release();
                finish();
                return true;
            case R.id.miEdit:
                mp.release();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}