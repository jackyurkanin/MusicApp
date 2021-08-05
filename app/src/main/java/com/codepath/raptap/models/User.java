package com.codepath.raptap.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

@ParseClassName("User")
public class User extends ParseUser {
    public static final String TAG = "UserClass";
    private static final String KEY_BIO = "bio";
    private static final String KEY_LIBRARY = "library";
    private static final String KEY_NAME = "username";
    private static final String KEY_PROFILE_PICTURE = "picture";

    public String getBio() {
        return getString(KEY_BIO);
    }

    public void setBio(String bio) {
        put(KEY_BIO, bio);
    }

    public String getUserName() {
        return getString(KEY_NAME);
    }

    public void setUserName(String username) {
        put(KEY_NAME, username);
    }

    public JSONArray getLibrary() {
        return getJSONArray(KEY_LIBRARY);
    }

    public void setLibrary(JSONArray library) {
        put(KEY_LIBRARY, library);
    }

    public ParseFile getProfilePic() {
        return getParseFile(KEY_PROFILE_PICTURE);
    }

    public void setProfilePic(ParseFile parseFile) {
        put(KEY_PROFILE_PICTURE, parseFile);
    }
}
