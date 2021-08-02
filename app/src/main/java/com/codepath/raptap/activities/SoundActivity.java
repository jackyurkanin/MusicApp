package com.codepath.raptap.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.raptap.R;
import com.codepath.raptap.databinding.ActivitySoundBinding;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SoundActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = "SoundActivity";
    public static final String DEBUG = "DEBUG";
    private static final int TRACK_SAMPLE_RATE = 8000;
    private static final int TRACK_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int TRACK_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int RECORD_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int myStream = 0;

    private int HEIGHT;
    private int WIDTH;
    private int minBufferSize;
    private int minBufferSizeRec;
    private ActivitySoundBinding binding;
    private Context context;
    private MediaRecorder recorder;
    private AudioTrack track;
    private AudioTask audioSynth;
    private volatile boolean isPressed;
    private volatile boolean recording;
    private volatile float synth_frequency = BASE_FREQUENCY;
    private volatile float amplitude;
    private static final float BASE_FREQUENCY = (float) 27.5;
    private static final float SCALAR = 2000;
    private short[] totalBuffer;
    private final Executor executor = Executors.newSingleThreadExecutor(); // change according to your requirements

    public SoundActivity() {
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySoundBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = this;

        view.setOnTouchListener(this);
        audioSynth = new AudioTask();
    }

    @Override
    public void onPause() {
        super.onPause();
        isPressed = false;
        track.release();
        recorder.stop();
        recorder.release();
    }

    private void setUpAudioTrack() {
        minBufferSize = AudioTrack.getMinBufferSize(TRACK_SAMPLE_RATE, TRACK_CHANNELS, TRACK_AUDIO_ENCODING);
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

    private void setUpAudioRecord() {
//        minBufferSizeRec = AudioRecord.getMinBufferSize(TRACK_SAMPLE_RATE, RECORD_CHANNELS, TRACK_AUDIO_ENCODING);
//        Log.e(DEBUG, String.valueOf(minBufferSizeRec) + "    " + minBufferSize);
//        recorder = new AudioRecord(MediaRecorder.AudioSource.UNPROCESSED, TRACK_SAMPLE_RATE, RECORD_CHANNELS, TRACK_AUDIO_ENCODING, minBufferSizeRec);
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.UNPROCESSED);
        recorder.setAudioChannels(RECORD_CHANNELS);
        recorder.setAudioEncoder(TRACK_AUDIO_ENCODING);

        try {
            recorder.prepare();
        }
        catch (IOException e) {
            // handle error
        }
        catch (IllegalStateException e) {
            // handle error
        }
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

        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isPressed = true;
                synth_frequency = BASE_FREQUENCY + SCALAR * yPos / HEIGHT;
                amplitude = xPos / WIDTH;
                Log.i(TAG, "Frequency: " + String.valueOf(synth_frequency));
                Log.i(TAG, "Amplitude: " + String.valueOf(amplitude));
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
                track.release();
                recorder.stop();
                Intent editIntent = new Intent(context, EditActivity.class);
                // extra
                startActivity(editIntent);
                finish();
                return true;
            case R.id.miStart:
                // ToDo:
                //  Need to make it start the playing after you press then set visibilty to gone and set visibility to appear when enter
                setUpAudioTrack();
                setUpAudioRecord();
//                track.flush();
                track.play();
                recorder.start();
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
                Log.e(TAG, "Failed to call synth thread");
                e.printStackTrace();
            }
        });
    }

    private class AudioTask implements Callable<short[]> {

        @Override
        public short[] call() {
            short[] buffer = new short[minBufferSize];
            int offsetRec = 0;
            while (isPressed) {
                float angular_frequency = (float) (2 * Math.PI) * synth_frequency / TRACK_SAMPLE_RATE;
                Log.e(TAG, "Angular Freq: " + angular_frequency);
                float angle = angular_frequency;
                int counter = 1;
                for (int i = 0; i < buffer.length; ++i) {
//                    buffer[i] = (short) (Short.MAX_VALUE * ((float) Math.sin(angle)));
                    buffer[i] = (short) (Short.MAX_VALUE * ((float) Math.sin(angular_frequency * counter / minBufferSize)));
//                    buffer[i] = (short) (Short.MAX_VALUE * ((float) Math.sin(angle) * amplitude));
//                    angle += angular_frequency;
                    counter += 1;
                }
                track.write(buffer, 0, buffer.length);

//                recorder.read(buffer, offsetRec, buffer.length);
//                offsetRec += buffer.length;
            }
            return buffer;
        }
    }
}
