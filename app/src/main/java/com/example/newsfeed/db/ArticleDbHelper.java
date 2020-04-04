package com.example.newsfeed.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.newsfeed.db.ArticleContract.*;

public class ArticleDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "articlelist.db";
    private static final int DB_VERSION = 1;

    ArticleDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_ARTICLE_TABLE = "CREATE TABLE " +
                ArticleEntity.TABLE_NAME + " (" +
                ArticleEntity._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ArticleEntity.COLUMN_SOURCE_ID + " TEXT, " +
                ArticleEntity.COLUMN_SOURCE_NAME + " TEXT, " +
                ArticleEntity.COLUMN_AUTHOR + " TEXT, " +
                ArticleEntity.COLUMN_TITLE + " TEXT, " +
                ArticleEntity.COLUMN_DESC + " TEXT NOT NULL, " +
                ArticleEntity.COLUMN_URL + " TEXT NOT NULL, " +
                ArticleEntity.COLUMN_URL_TO_IMAGE + " TEXT, " +
                ArticleEntity.COLUMN_PUBLISH_AT + " TEXT, " +
                ArticleEntity.COLUMN_CONTENT + " TEXT" +
                ");";

        db.execSQL(CREATE_ARTICLE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ArticleEntity.TABLE_NAME);
    }
}
