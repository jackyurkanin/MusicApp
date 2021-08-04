package com.codepath.raptap.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.raptap.R;
import com.codepath.raptap.databinding.ActivityEditBinding;
import com.codepath.raptap.databinding.ActivitySoundBinding;
import com.codepath.raptap.models.Sound;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = "EditActivity";
    public static final String KEY_MADE_SOUND = "song";
    private static final int TRACK_SAMPLE_RATE = 44100;
    private static final int TRACK_CHANNELS = AudioFormat.CHANNEL_OUT_STEREO;
    private static final int TRACK_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int STREAM = 0;
    private int minBufferSize;

    private Sound music;
    private ActivityEditBinding binding;
    private EditText etSongName;
    private EditText etSongDescription;
    private ImageView ivCoverPic;
    private Button btnMusicSubmit;
    private Button btnPlay;
    private Context context;
    private File soundFile;
    private AudioTrack songPlayer;
    private FileInputStream inputStream;
    private short[] songBytes;
    private BufferedInputStream bis;
    private DataInputStream dis;
    private ParseFile musicFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        context = this;
        soundFile = (File) getIntent().getExtras().get(KEY_MADE_SOUND);
        etSongName = binding.etSongName;
        etSongDescription = binding.etSongDescription;
        ivCoverPic = binding.ivCoverPic;
        btnMusicSubmit = binding.btnMusicSubmit;
        btnMusicSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    makeMusicPost();
                } catch (Exception e) {
                    Log.e(TAG, "Error: " + e);
                    Toast.makeText(context, "Error making sound: check that all required fields are populated: Name & Description", Toast.LENGTH_SHORT).show();
                }
                etSongDescription.setText("");
                etSongName.setText("");
//                ivCoverPic.setImageResource(0);
                Intent backToMainIntent = new Intent(EditActivity.this, MainActivity.class);
                startActivity(backToMainIntent);
                finish();
            }
        });

        btnPlay = binding.btnPlay;
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpPlayer();
            }
        });
    }

    private void makeMusicPost() {
        music = new Sound();
        music.setDescription(etSongDescription.getText().toString());
        music.setUser(ParseUser.getCurrentUser());
        music.setSongName(etSongName.getText().toString());
//        music.setCoverPicture(new ParseFile(photoFile));
        musicFile = new ParseFile(soundFile);
        music.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //Save was done
                    music.setSound(musicFile);
                    music.setDescription(etSongDescription.getText().toString());
                    music.setUser(ParseUser.getCurrentUser());
                    music.setSongName(etSongName.getText().toString());
                    Toast.makeText(context, "Success!!", Toast.LENGTH_SHORT).show();
                } else {
                    //Something went wrong
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUpPlayer() {
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
        songPlayer = new AudioTrack(attributes, format, minBufferSize, AudioTrack.MODE_STREAM, STREAM);
        songBytes = new short[(int) soundFile.length()];
        try {
            inputStream = new FileInputStream(soundFile);
            bis = new BufferedInputStream(inputStream);
            dis = new DataInputStream(bis);
            if (dis.available() > 0) {
                int i = 0;
                while (dis.available() > 0 && i < songBytes.length){
                    songBytes[i] = dis.readShort();
                    i++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        songPlayer.play();
        songPlayer.write(songBytes, 0, songBytes.length);
        songPlayer.stop();
        songPlayer.release();
    }
}