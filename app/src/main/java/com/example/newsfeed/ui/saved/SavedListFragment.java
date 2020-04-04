package com.example.newsfeed.ui.saved;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsfeed.R;
import com.example.newsfeed.adapter.SavedArticleAdapter;
import com.example.newsfeed.databinding.FragmentSavedBinding;
import com.example.newsfeed.db.ArticleDbOperation;
import com.example.newsfeed.model.Article;

public class SavedListFragment extends Fragment implements SavedArticleAdapter.SavedArticleListener {

    private FragmentSavedBinding binding;
    private SavedArticleAdapter articleAdapter;
    private ArticleDbOperation articleDbOperation;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_saved, container, false);

        articleDbOperation = new ArticleDbOperation(getContext());
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        initRecyclerView();
    }

    private void initRecyclerView() {
        binding.articleRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        Cursor cursor = articleDbOperation.getAllSavedArticle();
        if (cursor.getCount() > 0) {
            binding.noItemTv.setVisibility(View.GONE);
            articleAdapter = new SavedArticleAdapter(cursor, this);
            binding.articleRv.setAdapter(articleAdapter);
        } else {
            binding.noItemTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void delete(Article article, int position) {
        articleDbOperation.deleteArticle(article);
        articleAdapter.updateCursor(articleDbOperation.getAllSavedArticle());
        articleAdapter.notifyItemRemoved(position);

        if (articleDbOperation.getAllSavedArticle().getCount() == 0) {
            binding.noItemTv.setVisibility(View.VISIBLE);

        }
    }
}
