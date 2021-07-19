package com.codepath.raptap.activities;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySoundBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = this;
        mp = MediaPlayer.create(context, R.raw.sound);
        // buildSoundBoard();
        view.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent touchEvent) {
        // bounds: 0 < x < 1079
        float xPos = touchEvent.getX();
        // bounds: 0 < x < 1818
        float yPos = touchEvent.getY();
        Log.i("TAG", "At position: (" + String.valueOf(xPos) + ", " + String.valueOf(yPos) + ")");
        try {
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
                mp.release();
                mp = MediaPlayer.create(context, R.raw.sound);
            }
            mp.start();
//            Log.e(DEBUG, String.valueOf(mp.));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}