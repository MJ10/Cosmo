package io.mokshjn.cosmo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by moksh on 31/1/17.
 */


public class StorageUtils {

    private final static String STORAGE = " io.mokshjn.cosmo.STORAGE";
    private SharedPreferences preferences;
    private String AUDIO_LIST = "io.mokshjn.cosmo.AUDIO_LIST";
    private String CURRENT_QUEUE = "io.mokshjn.cosmo.CURRENT_QUEUE";
    private Context context;

    public StorageUtils(Context context) {
        this.context = context;
    }

    public static void storeMediaID(Context context, String mediaID) {
        SharedPreferences preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("mediaID", mediaID);
        editor.apply();
    }

    public static String getMediaID(Context context) {
        String mediaID;
        SharedPreferences preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        mediaID = preferences.getString("mediaID", "null");
        return mediaID;
    }

    public void storeSong(ArrayList<MediaMetadataCompat> arrayList) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString(AUDIO_LIST, json);
        editor.apply();
    }

    public ArrayList<MediaMetadataCompat> loadSongs() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        String json = preferences.getString(AUDIO_LIST, null);
        Type type = new TypeToken<ArrayList<MediaMetadataCompat>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    public void storePosition(int position) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("audioPosition", position);
        editor.apply();
    }

    public int loadPosition() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getInt("audioPosition", -1);//return -1 if no data found
    }

    public void storeCurrentPlayingQueue(ArrayList<MediaBrowserCompat.MediaItem> tracks) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(tracks);
        editor.putString(CURRENT_QUEUE, json);
        editor.apply();
    }

    public ArrayList<MediaBrowserCompat.MediaItem> getCurrentPlayingQueue() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        String json = preferences.getString(CURRENT_QUEUE, null);
        Type type = new TypeToken<ArrayList<MediaMetadataCompat>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }
}