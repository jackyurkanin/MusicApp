package com.codepath.raptap.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Sound")
public class Sound extends ParseObject {

    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_SOUND = "sound";
    public static final String KEY_USER = "user";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
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
}
