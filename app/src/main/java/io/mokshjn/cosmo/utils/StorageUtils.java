package io.mokshjn.cosmo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.lang.reflect.Type;

import io.mokshjn.cosmo.models.Song;

/**
 * Created by moksh on 31/1/17.
 */


public class StorageUtils {

    private final String STORAGE = " io.mokshjn.cosmo.STORAGE";
    private SharedPreferences preferences;
    private Context context;

    public StorageUtils(Context context) {
        this.context = context;
    }

    public void storeSong(ArrayList<Song> arrayList) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("audioArrayList", json);
        editor.apply();
    }

    public ArrayList<Song> loadSongs() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        String json = preferences.getString("audioArrayList", null);
        Type type = new TypeToken<ArrayList<Song>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    public void storeAudioIndex(int index) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("audioIndex", index);
        editor.apply();
    }

    public int loadSongPosition() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getInt("audioIndex", -1);//return -1 if no data found
    }

    public void clearCachedAudioPlaylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}