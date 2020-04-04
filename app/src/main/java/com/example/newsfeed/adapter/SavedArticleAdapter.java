package com.example.newsfeed.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newsfeed.db.ArticleContract.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsfeed.databinding.ListItemLayoutBinding;
import com.example.newsfeed.model.Article;
import com.example.newsfeed.model.Source;

public class SavedArticleAdapter extends RecyclerView.Adapter<SavedArticleAdapter.SavedArticleViewHolder> {

    private Cursor mCursor;
    private SavedArticleListener listener;

    public SavedArticleAdapter(Cursor mCursor, SavedArticleListener listener) {
        this.mCursor = mCursor;
        this.listener = listener;
    }


    @NonNull
    @Override
    public SavedArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ListItemLayoutBinding binding = ListItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new SavedArticleViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedArticleViewHolder holder, int position) {

        if (!mCursor.moveToPosition(position)) {
            return;
        }


        holder.mBinding.removeIcon.setVisibility(View.VISIBLE);
        holder.mBinding.saveIcon.setVisibility(View.GONE);

        holder.bindTo(getArticle());


    }

    private Article getArticle() {
        String author = mCursor.getString(mCursor.getColumnIndex(ArticleEntity.COLUMN_AUTHOR));
        String url = mCursor.getString(mCursor.getColumnIndex(ArticleEntity.COLUMN_URL));
        String urlToImage = mCursor.getString(mCursor.getColumnIndex(ArticleEntity.COLUMN_URL_TO_IMAGE));
        String publishedAt = mCursor.getString(mCursor.getColumnIndex(ArticleEntity.COLUMN_PUBLISH_AT));
        String description = mCursor.getString(mCursor.getColumnIndex(ArticleEntity.COLUMN_DESC));
        String source_id = mCursor.getString(mCursor.getColumnIndex(ArticleEntity.COLUMN_SOURCE_ID));
        String source_name = mCursor.getString(mCursor.getColumnIndex(ArticleEntity.COLUMN_SOURCE_NAME));

        Article article = new Article();
        article.setAuthor(author);
        article.setUrl(url);
        article.setUrlToImage(urlToImage);
        article.setPublishedAt(publishedAt);
        article.setDescription(description);

        Source sourceObj = new Source();

        sourceObj.setId(source_id);
        sourceObj.setName(source_name);

        article.setSource(sourceObj);
        return article;
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void updateCursor(Cursor newCursor) {

        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;


    }

    class SavedArticleViewHolder extends RecyclerView.ViewHolder {

        ListItemLayoutBinding mBinding;
        SavedArticleListener mListener;

        SavedArticleViewHolder(ListItemLayoutBinding binding, SavedArticleListener listener) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.mListener = listener;
        }

        private void bindTo(Article article) {
            mBinding.setArticle(article);

            mBinding.removeIcon.setOnClickListener(v -> {
                mListener.delete(mBinding.getArticle(), getAdapterPosition());
            });

            mBinding.executePendingBindings();

        }


    }

    public interface SavedArticleListener {
        void delete(Article article, int position);
    }
}
