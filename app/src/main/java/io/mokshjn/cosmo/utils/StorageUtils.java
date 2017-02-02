package io.mokshjn.cosmo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
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

    public final class SongAdapter extends TypeAdapter<ArrayList<Song>> {

        @Override
        public void write(JsonWriter out, ArrayList<Song> value) throws IOException {

        }

        @Override
        public ArrayList<Song> read(JsonReader in) throws IOException {
            ArrayList<Song> songList = new ArrayList<>();
            in.beginObject();
            while (in.hasNext()) {
                in.beginObject();
            }
            return songList;
        }
    }
}