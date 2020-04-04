package com.example.newsfeed.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.example.newsfeed.model.Article;

import java.util.List;

public class ArticleDiffCallback extends DiffUtil.Callback {

    private List<Article> mOldArticle;
    private List<Article> mNewArticle;


    public ArticleDiffCallback(List<Article> oldArticle,List<Article> newArticle)
    {
        this.mOldArticle = oldArticle;
        this.mNewArticle = newArticle;
    }

    @Override
    public int getOldListSize() {
        return mOldArticle.size();
    }

    @Override
    public int getNewListSize() {
        return mNewArticle.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {

        return mOldArticle.get(oldItemPosition).getSource().getId().equals(mNewArticle.get(newItemPosition).getSource().getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Article oldArticle = mOldArticle.get(oldItemPosition);
        Article newArticle = mNewArticle.get(newItemPosition);

        return oldArticle.getSource().getId().equals(newArticle.getSource().getId());
    }
}
