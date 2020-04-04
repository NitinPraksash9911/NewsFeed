package com.example.newsfeed.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.example.newsfeed.R;
import com.example.newsfeed.network.imageloader.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BindingAdapterClass {

    @BindingAdapter("dateConverter")
    public static void convertDate(TextView textView, String date) {

        String DATE_FORMAT_I = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        String DATE_FORMAT_O = "h:mm a dd/MMM/yy";

        SimpleDateFormat spf = new SimpleDateFormat(DATE_FORMAT_I, Locale.UK);
        Date newDate = null;
        try {
            newDate = spf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat(DATE_FORMAT_O, Locale.UK);
        date = spf.format(newDate);
        textView.setText(date);


    }


    @BindingAdapter("loadImage")
    public static void loadImage(ImageView imageView, String imageUrl) {
        ImageLoader imgLoader = new ImageLoader(imageView.getContext());
        imgLoader.DisplayImage(imageUrl, R.drawable.ic_launcher_foreground, imageView);
    }

}
