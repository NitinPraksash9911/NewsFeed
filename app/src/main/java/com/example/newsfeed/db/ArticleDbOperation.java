package com.example.newsfeed.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.newsfeed.db.ArticleContract.*;
import com.example.newsfeed.model.Article;

public class ArticleDbOperation {

    private ArticleDbHelper articleDbHelper;

    private Context mContex;


    public ArticleDbOperation(Context context) {
        this.mContex = context;
    }

    private SQLiteDatabase getWritableDb() {
        if (articleDbHelper == null) {
            articleDbHelper = new ArticleDbHelper(mContex);
        }
        return articleDbHelper.getWritableDatabase();
    }

    public synchronized Cursor getAllSavedArticle() {
        return getWritableDb().query(ArticleEntity.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                ArticleEntity.COLUMN_PUBLISH_AT + " DESC");
    }

    public synchronized boolean addArticleInDb(Article article) {

        String rawQuery = "SELECT * FROM " + ArticleEntity.TABLE_NAME + " WHERE " + ArticleEntity.COLUMN_PUBLISH_AT + "=? And " + ArticleEntity.COLUMN_SOURCE_ID + "=?";

        if (getWritableDb().rawQuery(rawQuery, new String[]{article.getPublishedAt(), article.getSource().getId()}).getCount() > 0) {
            /* record exist */
            return false;
        } else {
            ContentValues cv = new ContentValues();
            cv.put(ArticleEntity.COLUMN_SOURCE_ID, article.getSource().getId());
            cv.put(ArticleEntity.COLUMN_SOURCE_NAME, article.getSource().getName());
            cv.put(ArticleEntity.COLUMN_AUTHOR, article.getAuthor());
            cv.put(ArticleEntity.COLUMN_TITLE, article.getTitle());
            cv.put(ArticleEntity.COLUMN_DESC, article.getDescription());
            cv.put(ArticleEntity.COLUMN_URL, article.getUrl());
            cv.put(ArticleEntity.COLUMN_URL_TO_IMAGE, article.getUrlToImage());
            cv.put(ArticleEntity.COLUMN_PUBLISH_AT, article.getPublishedAt());
            cv.put(ArticleEntity.COLUMN_CONTENT, article.getContent());
            getWritableDb().insert(ArticleEntity.TABLE_NAME, null, cv);

        }

        getWritableDb().close();


        return true;

    }

    public synchronized void deleteArticle(Article article) {
        if (article.getPublishedAt() != null && article.getSource().getId() != null) {
            getWritableDb().delete(ArticleEntity.TABLE_NAME, ArticleEntity.COLUMN_PUBLISH_AT + "=? And " + ArticleEntity.COLUMN_SOURCE_ID + "=?", new String[]{article.getPublishedAt(), article.getSource().getId()});
            getWritableDb().close();
        }
    }

}
