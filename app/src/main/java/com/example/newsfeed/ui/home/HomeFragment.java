

package com.example.newsfeed.ui.home;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsfeed.R;
import com.example.newsfeed.adapter.ArticleAdapter;
import com.example.newsfeed.databinding.FragmentHomeBinding;
import com.example.newsfeed.db.ArticleDbOperation;
import com.example.newsfeed.model.Article;
import com.example.newsfeed.model.ArticleModel;
import com.example.newsfeed.network.ApiServices;
import com.example.newsfeed.network.AsyncTaskClass;
import com.example.newsfeed.network.DownloadCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;


public class HomeFragment extends Fragment implements DownloadCallback, ArticleAdapter.ListItemListener {

    private ArticleAdapter articleAdapter;
    private FragmentHomeBinding homeBinding;
    private AsyncTaskClass asyncTaskClass;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    private Boolean isOpen = false;
    private ArticleDbOperation articleDbOperation;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        homeBinding.progressHorizontal.setVisibility(View.VISIBLE);

        /*this class for database operation like insert, delete*/
        articleDbOperation = new ArticleDbOperation(getContext());

        initRecyclerView();
        startDownload();

        /*this FAB is using for sorting the article by date
         * 1. new to old
         * 2. old to new
         * */
        setFabAnimationAndClick();

        /*search article by  source_name and author */

        return homeBinding.getRoot();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    private void initRecyclerView() {
        homeBinding.articleRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        articleAdapter = new ArticleAdapter(this);
        homeBinding.articleRv.setAdapter(articleAdapter);


    }


    private void setFabAnimationAndClick() {
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_anitclock);

        //FAB click actions
        fabClickSet();

        /*
         * FAB icon hide when recycler view scroll down and show on scroll up
         * */
        fabAnimationOnScroll();

    }

    private void fabAnimationOnScroll() {
        homeBinding.articleRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && homeBinding.mainFab.getVisibility() == View.VISIBLE) {
                    if (isOpen) {
                        homeBinding.mainFab.performClick();
                    }
                    homeBinding.mainFab.hide();


                } else if (dy < 0 && homeBinding.mainFab.getVisibility() != View.VISIBLE) {
                    homeBinding.mainFab.show();


                }
            }
        });
    }


    private void fabClickSet() {
        homeBinding.mainFab.setOnClickListener(v -> {
            if (isOpen) {

                homeBinding.increaseDateTv.setVisibility(View.INVISIBLE);
                homeBinding.decreaseDateTv.setVisibility(View.INVISIBLE);
                homeBinding.fabDecrease.startAnimation(fab_close);
                homeBinding.fabIncrease.startAnimation(fab_close);
                homeBinding.mainFab.startAnimation(fab_anticlock);
                homeBinding.fabDecrease.setClickable(false);
                homeBinding.fabIncrease.setClickable(false);
                isOpen = false;


            } else {

                homeBinding.increaseDateTv.setVisibility(View.VISIBLE);
                homeBinding.decreaseDateTv.setVisibility(View.VISIBLE);
                homeBinding.fabDecrease.startAnimation(fab_open);
                homeBinding.fabIncrease.startAnimation(fab_open);
                homeBinding.mainFab.startAnimation(fab_clock);
                homeBinding.fabDecrease.setClickable(true);
                homeBinding.fabIncrease.setClickable(true);
                isOpen = true;
            }
        });


        homeBinding.fabIncrease.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Increase order", Toast.LENGTH_SHORT).show();
            articleAdapter.sortByAscending();
        });

        homeBinding.fabDecrease.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Decrsace  order", Toast.LENGTH_SHORT).show();
            articleAdapter.sortByDescending();
        });

    }

    @Override
    public void onDestroy() {
        // Cancel task when Fragment is destroyed.
        cancelDownload();
        super.onDestroy();
    }

    /**
     * Start non-blocking execution of DownloadTask.
     */
    public void startDownload() {
        cancelDownload();
        asyncTaskClass = new AsyncTaskClass(this);
        asyncTaskClass.execute(ApiServices.NEWS_API);
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing DownloadTask execution.
     */
    public void cancelDownload() {
        if (asyncTaskClass != null) {
            asyncTaskClass.cancel(true);
        }
    }

    @Override
    public void updateFromDownload(Object result) {
        if (result != null) {
            ArticleModel data = new Gson().fromJson(result.toString(), ArticleModel.class);
            articleAdapter.setArticleList(data.getArticles());
        }

    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
    }

    @Override
    public void onProgressUpdate(int progressCode) {
        switch (progressCode) {
            case Progress.ERROR:
                showSnackBar("Something went wrong", Color.RED);
                break;
            case Progress.SUCCESS:
                showSnackBar("Loaded successfully", Color.GREEN);
                homeBinding.progressHorizontal.setVisibility(View.GONE);
                break;
            case Progress.NO_NETWORK:
                showSnackBar("Please check you internet connection", Color.DKGRAY);
                break;
        }

    }

    private void showSnackBar(String msg, int color) {
        Snackbar snackbar = Snackbar.make(homeBinding.homeLayout, msg, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(color);
        snackbar.show();
    }

    @Override
    public void finishDownloading() {
        cancelDownload();
    }


    @Override
    public void itemListener(String articleUrl) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(articleUrl));
        String title = String.valueOf(getResources().getText(R.string.app_name));
        Intent chooser = Intent.createChooser(intent, title);
        startActivity(chooser);
    }


    @Override
    public void saveArticle(Article article) {
        boolean check = articleDbOperation.addArticleInDb(article);
        if (check) {
            showSnackBar("Saved successfully", Color.BLUE);
        } else {
            showSnackBar("Already Saved", Color.DKGRAY);

        }
    }
}


