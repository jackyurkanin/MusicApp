package com.codepath.raptap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.raptap.R;
import com.codepath.raptap.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import io.github.muddz.styleabletoast.StyleableToast;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private static final String TAG = "LoginActivity";
    private Button btnLogin;
    private Button btnSignUp;
    private Button btnNewUser;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etSignupUser;
    private EditText etSignupPass;
    private Animation in;
    private Animation out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (ParseUser.getCurrentUser() != null) {
            goToMainActivity();
        }


        etUsername = binding.etUsername;
        etPassword = binding.etPassword;
        etSignupPass = binding.etSignupPass;
        etSignupUser = binding.etSignupUser;

        in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(1500);
        out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(1500);

        btnNewUser = binding.btnNewUser;
        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenChange();
            }
        });

        btnLogin = binding.btnLogin;
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Username = etUsername.getText().toString();
                String Password = etPassword.getText().toString();
                Log.i(TAG, Username + "  "+ Password);
                login(Username, Password);
            }
        });

        btnSignUp = binding.btnSignup;
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Username = etSignupUser.getText().toString();
                String Password = etSignupPass.getText().toString();
                Log.i(TAG, Username + "  "+ Password);
                newUser(Username, Password);
            }
        });
    }

    private void screenChange() {
//        // get rid of button, make everything else visible

        btnSignUp.startAnimation(in);
        btnNewUser.startAnimation(out);
        btnLogin.startAnimation(out);
        etSignupUser.startAnimation(in);
        etSignupPass.startAnimation(in);
        etUsername.startAnimation(out);
        etPassword.startAnimation(out);
        btnNewUser.setVisibility(View.GONE);
        etPassword.setVisibility(View.GONE);
        etUsername.setVisibility(View.GONE);
        btnLogin.setVisibility(View.GONE);
        btnSignUp.setVisibility(View.VISIBLE);
        etSignupUser.setVisibility(View.VISIBLE);
        etSignupPass.setVisibility(View.VISIBLE);
    }

    private void newUser(String username, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        // Other fields can be set just like any other ParseObject,
        // using the "put" method, like this: user.put("attribute", "its value");
        // If this field does not exists, it will be automatically created

        user.signUpInBackground(e -> {
            if (e == null) {
                // Hooray! Let them use the app now.
                goToMainActivity();
                StyleableToast.makeText(this, "Successful!", Toast.LENGTH_SHORT, R.style.mytoast).show();
            } else {
                // Sign up didn't succeed. Look at the ParseException
                // to figure out what went wrong
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                StyleableToast.makeText(this, e.getMessage(), Toast.LENGTH_LONG, R.style.mytoast).show();

            }
        });
    }

    private void login(String username, String password) {
        Log.i(TAG, "Logging in...");

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Login error", e);
                    return;
                }
                goToMainActivity();
//                StyleableToast.makeText(LoginActivity.this, "Successful!", Toast.LENGTH_SHORT, R.style.mytoast).show();
                new StyleableToast
                        .Builder(LoginActivity.this)
                        .text("Successful!")
                        .textColor(Color.parseColor("#7E2228"))
                        .backgroundColor(Color.parseColor("#A7A7A7"))
                        .show();
            }
        });
    }

    private void goToMainActivity() {
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
        finish();
    }
}