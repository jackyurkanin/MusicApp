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
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.raptap.R;
import com.codepath.raptap.databinding.ActivitySoundBinding;
import com.parse.ParseFile;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SoundActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = "SoundActivity";
    public static final String DEBUG = "DEBUG";

    // Use 8000Hz for emulator and 44100 for phone it really should be at least double the frequency we want
    private static final int TRACK_SAMPLE_RATE = 44100;
    private static final int TRACK_CHANNELS = AudioFormat.CHANNEL_OUT_STEREO;
    private static final int TRACK_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int RECORD_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int ATTACK_TIME = 250;
    private static final int RELEASE_TIME = 100;
    private static final int STREAM = 0;

    private static final float BASE_FREQUENCY = (float) 27.5;
    private static final float SCALAR = 1000;
    private static final float SCALAR_TWO = 1000;

    private int HEIGHT;
    private int WIDTH;
    private int minBufferSize;
    private int minBufferSizeRec;

    private ActivitySoundBinding binding;
    private Context context;
    private AudioRecord recorder;
    private AudioTrack track;
//    private AudioTask audioSynth;
    private AudioPlayTask audioPlaySynth;
    private AudioRecordTask audioRecordSynth;

    private FileOutputStream os;
    private BufferedOutputStream bos;
    private DataOutputStream dos;
    private File file;
    private Thread recordingThread;

    private volatile boolean isPressed;
    private volatile boolean isRecording;
    private volatile float frequencyOne = BASE_FREQUENCY;
    private volatile float frequencyTwo = BASE_FREQUENCY;

//    private final Executor executor = Executors.newSingleThreadExecutor(); // change according to your requirements
    private final Executor executorPlay = Executors.newSingleThreadExecutor();
    private final Executor executorRecord = Executors.newSingleThreadExecutor();


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
//        audioSynth = new AudioTask();
        audioPlaySynth = new AudioPlayTask();
        audioRecordSynth = new AudioRecordTask();
    }

    @Override
    public void onPause() {
        super.onPause();
        isPressed = false;
        if (track != null) {
            track.release();
        }
        if (recorder != null) {
            recorder.stop();
            recorder.release();
        }
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
        track = new AudioTrack(attributes, format, minBufferSize, AudioTrack.MODE_STREAM, STREAM);
    }

    private void setUpAudioRecord() throws FileNotFoundException {
        minBufferSizeRec = 10 * AudioRecord.getMinBufferSize(TRACK_SAMPLE_RATE, RECORD_CHANNELS, TRACK_AUDIO_ENCODING);
        recorder = new AudioRecord(MediaRecorder.AudioSource.UNPROCESSED, TRACK_SAMPLE_RATE, RECORD_CHANNELS, TRACK_AUDIO_ENCODING, minBufferSize);
        file = new File(context.getFilesDir().getAbsolutePath() +"/sound.pcm");
        if (file.exists())
            file.delete();
        Log.i(TAG,"Delete Files");
        try {
            file.createNewFile();
            Log.i(TAG,"Create a file");
        } catch (IOException e) {
            Log.i(TAG,"Failed to create");
            throw new IllegalStateException("Failed to create" + file.toString());
        }

        os = new FileOutputStream(file);
        bos = new BufferedOutputStream(os);
        dos = new DataOutputStream(bos);
    }

    @Override
    public boolean onTouch(View v, MotionEvent touchEvent) {
        // bounds: 0 < x < 1079
        float xPos = touchEvent.getX();
        // bounds: 0 < x < 1818
        float yPos = touchEvent.getY();
        HEIGHT = v.getHeight();
        WIDTH = v.getWidth();
//        Log.i("TAG", "At position: (" + String.valueOf(xPos) + ", " + String.valueOf(yPos) + ")");

        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isPressed = true;
                frequencyOne = BASE_FREQUENCY + SCALAR * yPos / HEIGHT;
                frequencyTwo = BASE_FREQUENCY + SCALAR_TWO * xPos / WIDTH;
//                Log.i(TAG, "Freq due to yPos: " + String.valueOf(frequencyOne));
//                Log.i(TAG, "Freq due to xPos: " + String.valueOf(frequencyTwo));
//                executeAsync(audioSynth);
                executePlayAsync(audioPlaySynth);
                executeRecordAsync(audioRecordSynth);
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
                isRecording = false;
                track.release();
                recorder.stop();
                try {
                    dos.flush();
                    dos.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close out audioRecord");
                    e.printStackTrace();
                }
                Intent editIntent = new Intent(context, EditActivity.class);
                editIntent.putExtra("song", file);
                startActivity(editIntent);
                finish();
                return true;
            case R.id.miStart:
                // ToDo:
                //  Need to make it start the playing after you press then set visibilty to gone and set visibility to appear when enter
//                item.setEnabled(false);
                setUpAudioTrack();
                try {
                    setUpAudioRecord();
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "Failed to create AudioRecord");
                    e.printStackTrace();
                }
                isRecording = true;
                track.play();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    public void executeAsync(Callable<short[]> callable) {
//        executor.execute(() -> {
//            try {
//                callable.call();
//            } catch (Exception e) {
//                Log.e(TAG, "Failed to call synth thread");
//                e.printStackTrace();
//            }
//        });
//    }

    public void executePlayAsync(Callable<short[]> callable) {
        executorPlay.execute(() -> {
            try {
                callable.call();
            } catch (Exception e) {
                Log.e(TAG, "Failed to call synth thread");
                e.printStackTrace();
            }
        });
    }

    public void executeRecordAsync(Callable<short[]> callable) {
        executorRecord.execute(() -> {
            try {
                callable.call();
            } catch (Exception e) {
                Log.e(TAG, "Failed to call synth thread");
                e.printStackTrace();
            }
        });
    }

    private class AudioPlayTask implements Callable<short[]> {
        @Override
        public short[] call() throws IOException {
            short[] buffer = new short[minBufferSize];
            int time = 0;
            while (isPressed) {
                for (int i = 1; i < buffer.length; ++i) {
                    float angleOne = (float) (2 * Math.PI) * time * frequencyOne / TRACK_SAMPLE_RATE;
                    float angleTwo = (float) (2 * Math.PI) * time * frequencyTwo / TRACK_SAMPLE_RATE;
                    if (isPressed) {
                        if (time <= ATTACK_TIME) {
                            buffer[i] = (short) (Short.MAX_VALUE * time / ATTACK_TIME * ((float) Math.sin((angleOne + angleTwo)/2)));
                        } else {
                            buffer[i] = (short) (Short.MAX_VALUE * ((float) Math.sin((angleOne + angleTwo)/2)));
                        }
                    } else {
                        int diff = buffer.length - i;
                        if (diff >= RELEASE_TIME) {
                            buffer[i] = (short) (Short.MAX_VALUE * ((float) Math.sin((angleOne + angleTwo)/2)));
                        } else {
                            buffer[i] = (short) (Short.MAX_VALUE * diff/RELEASE_TIME * ((float) Math.sin(angleOne)));
                        }
                    }
                    time += 1;
                }
                track.write(buffer, 0, buffer.length);
            }
            return null;
        }
    }

    private class AudioRecordTask implements Callable<short[]> {
        @Override
        public short[] call() throws IOException {
            short[] buffer = new short[minBufferSizeRec];
            int time = 0;
            recorder.startRecording();
            while (isRecording) {
                if (isPressed) {
                    for (int i = 1; i < buffer.length; ++i) {
                        float angleOne = (float) (2 * Math.PI) * time * frequencyOne / TRACK_SAMPLE_RATE;
                        float angleTwo = (float) (2 * Math.PI) * time * frequencyTwo / TRACK_SAMPLE_RATE;
                        if (isPressed) {
                            if (time <= ATTACK_TIME) {
                                buffer[i] = (short) (Short.MAX_VALUE * time / ATTACK_TIME * ((float) Math.sin((angleOne + angleTwo) / 2)));
                            } else {
                                // buffer[i] = (short) (Short.MAX_VALUE * ((float) Math.sin(angleOne * (1 + frequencyTwo * Math.sin(angleOne)))));
                                // buffer[i] = (short) (Short.MAX_VALUE * ((float) Math.sin(angleOne)));
                                buffer[i] = (short) (Short.MAX_VALUE * ((float) Math.sin((angleOne + angleTwo) / 2)));
                            }
                        } else {
                            int diff = buffer.length - i;
                            if (diff >= RELEASE_TIME) {
                                // buffer[i] = (short) (Short.MAX_VALUE * ((float) Math.sin(angleOne)));
                                buffer[i] = (short) (Short.MAX_VALUE * ((float) Math.sin((angleOne + angleTwo) / 2)));
                            } else {
                                buffer[i] = (short) (Short.MAX_VALUE * diff / RELEASE_TIME * ((float) Math.sin((angleOne + angleTwo) / 2)));
                            }
                        }
                        time += 1;
                    }
                } else {
                    int count = 0;
                    for (int i = 1; i < buffer.length; ++i) {
                        float angleOne = (float) (2 * Math.PI) * time * frequencyOne / TRACK_SAMPLE_RATE;
                        float angleTwo = (float) (2 * Math.PI) * time * frequencyTwo / TRACK_SAMPLE_RATE;
                        if (isPressed) {
                            if (time <= ATTACK_TIME) {
                                buffer[i] = (short) (Short.MAX_VALUE * time / ATTACK_TIME * ((float) Math.sin((angleOne + angleTwo) / 2)));
                            } else {
                                buffer[i] = (short) (Short.MAX_VALUE * ((float) Math.sin((angleOne + angleTwo) / 2)));
                            }
                        } else {
                            if (count < RELEASE_TIME) {
                                buffer[i] = (short) (Short.MAX_VALUE * (RELEASE_TIME - count) / RELEASE_TIME * ((float) Math.sin((angleOne + angleTwo) / 2)));
                            } else {
                                buffer[i] = (short) 0;
                            }
                            count += 1;
                        }
                        time += 1;
                    }
                }
                int bufferReadResult = recorder.read(buffer, 0, buffer.length);
                for (int i = 0; i < bufferReadResult; i++) {
                    try {
                        dos.writeShort(buffer[i]);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to write short to DataOutputStream");
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}
