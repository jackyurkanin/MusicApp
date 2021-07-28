package com.codepath.raptap.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.raptap.R;
import com.codepath.raptap.databinding.ActivitySoundBinding;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SoundActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = "SoundActivity";
    public static final String DEBUG = "DEBUG";
    private ActivitySoundBinding binding;
    private Context context;
    private static final int TRACK_SAMPLE_RATE = 20000;
    private static final int TRACK_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int TRACK_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int myStream = 0;
    private int minBufferSize;
    private AudioRecord recorder = null;
    //    private MediaPlayer mp;
    private AudioTrack track;
    private volatile boolean isPressed;
    private static final float BASE_FREQUENCY = (float) 27.5;
    private volatile float synth_frequency = BASE_FREQUENCY;
    private static final float SCALAR = 2000;
    private int HEIGHT;
    private int WIDTH;
    private boolean recording;
    private AudioTask audioSynth;
    private final Executor executor = Executors.newSingleThreadExecutor(); // change according to your requirements


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySoundBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = this;

//        mp = MediaPlayer.create(context, R.raw.sound);

        view.setOnTouchListener(this);
        audioSynth = new AudioTask();

    }

    @Override
    public void onPause() {
        super.onPause();
        isPressed = false;
    }

    private void setUpAudioTrack() {
        minBufferSize = AudioTrack.getMinBufferSize(TRACK_SAMPLE_RATE, TRACK_CHANNELS, TRACK_AUDIO_ENCODING);
        Log.e("BUFFER", String.valueOf(minBufferSize));
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        AudioFormat format = new AudioFormat.Builder()
                .setChannelMask(TRACK_CHANNELS)
                .setEncoding(TRACK_AUDIO_ENCODING)
                .setSampleRate(TRACK_SAMPLE_RATE)
                .build();
        track = new AudioTrack(attributes, format, minBufferSize, AudioTrack.MODE_STREAM, myStream);
    }

    @Override
    public boolean onTouch(View v, MotionEvent touchEvent) {
        // bounds: 0 < x < 1079
        float xPos = touchEvent.getX();
        // bounds: 0 < x < 1818
        float yPos = touchEvent.getY();
        HEIGHT = v.getHeight();
        WIDTH = v.getWidth();
        Log.i("TAG", "At position: (" + String.valueOf(xPos) + ", " + String.valueOf(yPos) + ")");
        // ToDo:
        //  Use x & y to control frequency and pitch using sythesizer class
        //  write to AudioTrack & AudioRecord
        //  Change synth_frequency to be base + SCALAR * y_coordinate/y_max
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isPressed = true;
                synth_frequency = BASE_FREQUENCY + SCALAR * yPos / HEIGHT;
                Log.i(TAG, String.valueOf(synth_frequency));
                executeAsync(audioSynth);
                break;
            case MotionEvent.ACTION_UP:
                isPressed = false;
                break;
            case MotionEvent.ACTION_CANCEL:
            default:
                break;
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
//                mp.release();
                finish();
                return true;
            case R.id.miEdit:
                recording = false;
//                mp.release();
                Intent editIntent = new Intent(context, EditActivity.class);
                startActivity(editIntent);
                finish();
                return true;
            case R.id.miStart:
                // ToDo:
                //  Need to make it start the playing after you press then set visibilty to gone and set visibility to appear when enter
//                item.setVisible(false);
                setUpAudioTrack();
                track.play();
                recording = true;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void executeAsync(Callable<short[]> callable) {
        executor.execute(() -> {
            try {
                short[] val = callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private class AudioTask implements Callable<short[]> {

        @Override
        public short[] call() {
            short[] buffer = new short[minBufferSize];
            while (isPressed) {
                float angular_frequency = (float) (2*Math.PI) * synth_frequency / TRACK_SAMPLE_RATE;
                Log.e(TAG, "In callable");
                float angle = angular_frequency;
                for (int i = 0; i < buffer.length; ++i) {
                    buffer[i] = (short) (Short.MAX_VALUE * ((float) Math.sin(angle)));
                    angle += angular_frequency;
                }
                track.write(buffer, 0, buffer.length);
            }
            return buffer;
        }
    }
}
