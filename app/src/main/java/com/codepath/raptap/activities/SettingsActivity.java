package com.codepath.raptap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.raptap.R;
import com.codepath.raptap.databinding.ActivitySettingsBinding;
import com.parse.ParseUser;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private Context context;
    private EditText etUsername;
    private EditText etBio;
    private ImageView ivSettingsPic;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = this;

        etUsername = binding.etUsername;
        etBio = binding.etBio;
        ivSettingsPic = binding.ivSettingsPic;
        btnSubmit = binding.btnSubmit;
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String bio = etBio.getText().toString();
                updateUser(username, bio);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miReturn:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateUser(String username, String bio) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Other attributes than "email" will remain unchanged!
            currentUser.put("username", username);
            currentUser.put("bio", bio);
            // Saves the object.
            currentUser.saveInBackground(e -> {
                if(e==null){
                    //Save successfull
                    Toast.makeText(this, "Successful Update", Toast.LENGTH_SHORT).show();
                }else{
                    // Something went wrong while saving
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}