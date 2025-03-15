package com.duc.chatting.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.duc.chatting.data.model.Audio;
import com.duc.chatting.data.model.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MediaDB extends SQLiteOpenHelper {

    private Context context;

    // table
    private final String IMAGE_TABLE = "images";
    private final String AUDIO_TABLE = "audios";

    // field: image
    private final String IMAGE_ID = "id";
    private final String IMAGE_NAME = "name";
    private final String IMAGE_PATH = "path";

    // field: audio
    private final String AUDIO_ID = "id";
    private final String AUDIO_NAME = "name";
    private final String AUDIO_PATH = "path";


    public MediaDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryImage =
                "CREATE TABLE " + IMAGE_TABLE + " (" +
                        IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        IMAGE_NAME + " TEXT, " +
                        IMAGE_PATH + " TEXT )";
        String queryAudio =
                "CREATE TABLE " + AUDIO_TABLE + " (" +
                        AUDIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        AUDIO_NAME + " TEXT, " +
                        AUDIO_PATH + " TEXT )";
        db.execSQL(queryImage);
        db.execSQL(queryAudio);
    }

    public void removeAudio(String id, String filePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(AUDIO_TABLE, AUDIO_ID + " = ? ",
                new String[]{id});
        db.close();
        File file = new File(filePath);
        file.delete();
    }

    public void removeImage(String id, String filePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(IMAGE_TABLE, IMAGE_ID + " = ? ",
                new String[]{id});
        db.close();
        File file = new File(filePath);
        file.delete();
    }

    public void updateAudioName(String id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AUDIO_NAME, name);
        db.update(AUDIO_TABLE, values, AUDIO_ID + " = ?",
                new String[]{id});
        db.close();
    }

    public void updateImageName(String id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMAGE_NAME, name);
        db.update(IMAGE_TABLE, values, IMAGE_ID + " = ?",
                new String[]{id});
        db.close();
    }

    public List<Image> getAllImages() {
        List<Image> images = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + IMAGE_TABLE, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String path = cursor.getString(2);
                Image image = new Image(id, name, path);
                images.add(image);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return images;
    }

    public List<Audio> getAllAudios() {
        List<Audio> audios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + AUDIO_TABLE, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String path = cursor.getString(2);
                Audio audio = new Audio(id, name, path);
                audios.add(audio);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return audios;
    }


    public void addImage(Image image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMAGE_NAME, image.getName());
        values.put(IMAGE_PATH, image.getPath());
        db.insert(IMAGE_TABLE, null, values);
        db.close();
    }

    public void addAudio(Audio audio) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AUDIO_NAME, audio.getName());
        values.put(AUDIO_PATH, audio.getPath());
        db.insert(AUDIO_TABLE, null, values);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
