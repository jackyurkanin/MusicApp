package com.codepath.raptap.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@ParseClassName("Sound")
public class Sound extends ParseObject {

    public static final String TAG = "SoundClass";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_SOUND = "sound";
    private static final String KEY_NAME = "name";
    public static final String KEY_CREATEDAT = "createdAt";
    private static final String KEY_COVER_PICTURE = "cover";
    public static final String KEY_USER = "user";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public String getSongName() {
        return getString(KEY_NAME);
    }

    public void setSongName(String description) {
        put(KEY_NAME, description);
    }

    public ParseFile getSound() {
        return getParseFile(KEY_SOUND);
    }

    public void setSound(ParseFile parseFile) {
        put(KEY_SOUND, parseFile);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {
        put(KEY_USER, parseUser);
    }

    public ParseFile getCoverPic() {
        return getParseFile(KEY_COVER_PICTURE);
    }

    public void setCoverPicture(ParseFile parseFile) {
        put(KEY_COVER_PICTURE, parseFile);
    }
}
