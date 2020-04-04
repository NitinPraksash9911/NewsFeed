package com.example.newsfeed.db;

import android.provider.BaseColumns;

public class ArticleContract {

    private ArticleContract(){

    }

    public static final class ArticleEntity implements BaseColumns {
        public static final String TABLE_NAME = "articleList";

        public static final String COLUMN_SOURCE_ID = "sourceId";
        public static final String COLUMN_SOURCE_NAME = "sourceName";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESC = "description";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_URL_TO_IMAGE = "urlToImage";
        public static final String COLUMN_PUBLISH_AT = "publishAt";
        public static final String COLUMN_CONTENT = "content";

    }
}
