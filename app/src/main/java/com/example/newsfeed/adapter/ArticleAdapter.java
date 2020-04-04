package com.example.newsfeed.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsfeed.databinding.ListItemLayoutBinding;
import com.example.newsfeed.model.Article;
import com.example.newsfeed.utils.ArticleDiffCallback;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private List<Article> mArticleList = new ArrayList<>();

    private ListItemListener mItemLister;

    public ArticleAdapter(ListItemListener lister) {
        this.mItemLister = lister;

    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ListItemLayoutBinding binding = ListItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ArticleViewHolder(binding, mItemLister);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {

        holder.listItemLayoutBinding.saveIcon.setVisibility(View.VISIBLE);
        holder.listItemLayoutBinding.removeIcon.setVisibility(View.GONE);

        holder.bindTo(mArticleList.get(position));

    }

    public void setArticleList(List<Article> articleList) {
        ArticleDiffCallback diffCallback = new ArticleDiffCallback(this.mArticleList, articleList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.mArticleList.clear();
        this.mArticleList.addAll(articleList);


        diffResult.dispatchUpdatesTo(this);
    }

    public void sortByAscending() {
        Collections.sort(mArticleList, (list1, list2) -> list1.getPublishedAt().compareTo(list2.getPublishedAt()));
        notifyDataSetChanged();
    }

    public void sortByDescending() {
        Collections.sort(mArticleList, (list1, list2) -> list2.getPublishedAt().compareTo(list1.getPublishedAt()));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }


    class ArticleViewHolder extends RecyclerView.ViewHolder {
        ListItemLayoutBinding listItemLayoutBinding;
        ListItemListener mItemLister;

        public ArticleViewHolder(@NonNull ListItemLayoutBinding binding, ListItemListener lister) {
            super(binding.getRoot());
            this.listItemLayoutBinding = binding;
            this.mItemLister = lister;
        }

        private void bindTo(Article article) {
            listItemLayoutBinding.setArticle(article);
            /*go to browser*/
            listItemLayoutBinding.readMoreTv.setOnClickListener(v -> mItemLister.itemListener(listItemLayoutBinding.getArticle().getUrl()));

            /*to save article*/
            listItemLayoutBinding.saveIcon.setOnClickListener(v -> mItemLister.saveArticle(listItemLayoutBinding.getArticle()));
        }
    }


    /*
     * to click of list item*/
    public interface ListItemListener {

        /*used to open full article in browser*/
        void itemListener(String articleUrl);

        /*used to save article in local database*/
        void saveArticle(Article article);

    }

}
