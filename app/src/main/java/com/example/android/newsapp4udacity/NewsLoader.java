package com.example.android.newsapp4udacity;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String mUrl;

    //THIS WILL MAKE A NEW LOADER
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    //BACKGROUND THREAD
    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        //FETCHING THE DATA FROM JSON
        List<News> news = QueryUtils.fetchNewsData(mUrl);
        return news;
    }
}
