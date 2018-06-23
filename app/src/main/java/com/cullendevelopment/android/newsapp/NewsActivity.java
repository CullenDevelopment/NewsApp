package com.cullendevelopment.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    /**
     * Constant value for the News loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;

    /**
     * Adapter for the list of news
     */
    private NewsAdapter mAdapter;

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = NewsActivity.class.getSimpleName();

    /**
     * URL for news data from the Guardian data set
     */
    private static final String NEWS_REQUEST_URL =
            "https://content.guardianapis.com/search?show-tags=contributor&api-key=7baa2ef8-9e2a-4c2c-aa31-c96deca4f05f";

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);

        //Empty xml page to display "No internet" or "no news" messages.
        mEmptyStateTextView = (TextView) findViewById(R.id.empty);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of news as input
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);


        /*
        Set an item click listener on the ListView, which sends an intent to a web browser
        to open a website with more information about the selected news.
        */
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news item that was clicked on
                News currentNews = (News) mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getUrl());

                // Create a new intent to view the news URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
        //Checking if there is an internet connection
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        //if internet connection not present give error message
        if (!isConnected) {
            // Set empty state text to display "No internet."
            mEmptyStateTextView.setText(R.string.no_internet);
        }else {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            /*
            Initialize the loader. Pass in the int ID constant defined above and pass in null for
            the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            because this activity implements the LoaderCallbacks interface).
            */
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        }
    }

    //Creating the loader
    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new NewsLoader(this, NEWS_REQUEST_URL);

    }

    //Once load has finished, stop the loading indicator
    @Override
    public void onLoadFinished
            (Loader<List<News>> loader, List<News> news) {
        //Hide progress bar
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No news found." if no news found
        mEmptyStateTextView.setText(R.string.no_news);
        // Clear the adapter of previous news data
        mAdapter.clear();

        /*
        If there is a valid list of {@link News}, then add them to the adapter's
        data set. This will trigger the ListView to update.
        */
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    //Clearing the  loader once it has finished it is reset
    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}
