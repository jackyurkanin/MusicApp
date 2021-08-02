package com.codepath.raptap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.parse.ParseUser;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = "EditActivity";
    public static final String DEBUG = "DEBUG";
    private ActivityEditBinding binding;
    private EditText etSongName;
    private EditText etSongDescription;
    private ImageView ivCoverPic;
    private Button btnMusicSubmit;
    private static Sound music;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        context = this;
        etSongName = binding.etSongName;
        etSongDescription = binding.etSongDescription;
        ivCoverPic = binding.ivCoverPic;
        btnMusicSubmit = binding.btnMusicSubmit;
        btnMusicSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    makeMusicPost();
                    Toast.makeText(context,"Success!!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e(TAG, "Error: " + e);
                    Toast.makeText(context, "Error making sound: check that all fields are populated!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void makeMusicPost() {
        music = new Sound();
        music.setDescription(etSongDescription.getText().toString());
        music.setUser(ParseUser.getCurrentUser());
        music.setSongName(etSongName.getText().toString());
//        music.setCoverPicture(ivCoverPic);
//        ToDo:
//          need to make song into savable format
//          music.setSound();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miDone:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}