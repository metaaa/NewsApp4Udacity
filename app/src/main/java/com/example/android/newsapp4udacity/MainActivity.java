package com.example.android.newsapp4udacity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final String BASE_REQUEST_URL = "https://content.guardianapis.com/search?page-size=20&show-tags=contributor&show-fields=all&api-key=2a9971b4-3d4c-4f7e-b079-f24aa3566bb1";

    private ListView listView;
    private EditText searchText;
    private TextView emptyStateView;
    private Button searchButton;
    private ProgressBar progressIndicator;
    private LoaderManager mLoaderManager;
    private NewsAdapter mAdapter;
    private String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ENABLE THE DISPLAY HOME ON THE MENUBAR
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        //FINDING THE VIEWS
        listView = (ListView) findViewById(R.id.list);
        searchText = (EditText) findViewById(R.id.search_text);
        emptyStateView = (TextView) findViewById(R.id.empty_state_view);
        searchButton = (Button) findViewById(R.id.search_button);
        progressIndicator = (ProgressBar) findViewById(R.id.progress_indicator);

        //SETTING UP THE PROGRESS INDICATOR
        mLoaderManager = getLoaderManager();

        if (isConnected()) {
            mLoaderManager.initLoader(1, null, this);
        } else {
            progressIndicator.setVisibility(View.GONE);
            //ERROR MESSAGE TO THE USER
            String alertMessage = getString(R.string.error_no_internet);
            new AlertDialog.Builder(this).setMessage(alertMessage).show();
        }

        listView.setEmptyView(emptyStateView);

        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //GIVING THE LINT TO THE BROWSER
                News itemList = (News) adapterView.getAdapter().getItem(position);
                Uri webSite;
                String title = itemList.getmTitle();
                if (itemList.getmUrl() != null){
                    webSite = Uri.parse(itemList.getmUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, webSite);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "\"" + title + "\" " + getString(R.string.error_not_available), Toast.LENGTH_LONG).show();
                }
            }
        });

        //ACTIVATE A LISTENER TO THE SEARCH TEXT
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });
        //ACTIVATE A LISTENER TO THE SEARCH TEXT
        searchButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
    }

    //CHECKING NETWORK CONNECTION
    private Boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        Boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void search() {
        //EXCECUTED AFTER THE SEARCH BUTTON IS CLICKED
        if (isConnected()) {
            mQuery = searchText.getText().toString();
            progressIndicator.setVisibility(View.VISIBLE);
            emptyStateView.setText("");
            //restart the loader with the new data
            mLoaderManager.restartLoader(1, null, this);
        } else {
            String message = getString(R.string.error_no_internet);
            new AlertDialog.Builder(this).setMessage(message).show();
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        //GETTING THE PREFERENCES
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String maxResults = sharedPreferences.getString(
                getString(R.string.settings_max_results_key),
                getString(R.string.settings_max_results_default));
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        String section = sharedPreferences.getString(
                getString(R.string.settings_section_key),
                getString(R.string.settings_section_default)
        );
        //GETTING THE BASE REQUEST URL
        Uri baseUri = Uri.parse(BASE_REQUEST_URL);
        //QUERY BUILDER
        Uri.Builder uriBuilder = baseUri.buildUpon();
        if (mQuery != null && !mQuery.isEmpty()) {
            uriBuilder.appendQueryParameter("q", mQuery);
        }
        uriBuilder.appendQueryParameter("page-size", maxResults); //MAX NEWS IN THE LIST
        uriBuilder.appendQueryParameter("order-by", orderBy.toLowerCase()); //ORDER OF THE LIST
        if (!section.equals("all")) {
            uriBuilder.appendQueryParameter("section", section.toLowerCase()); //SECTION
        }
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        if (isConnected()) {
            //CLEARS THE ADAPTER
            mAdapter.clear();
            //HIDE THE PROGRESS INDICATOR
            progressIndicator.setVisibility(View.GONE);
            //SETTING THE EMPTY STATE TEXTVIEW
            String message = getString(R.string.error_not_found);
            emptyStateView.setText(message);
            //ADDING THE DATA TO THE ADAPTER
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        } else {
            progressIndicator.setVisibility(View.GONE);
            emptyStateView.setText(R.string.error_no_internet);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        //CLEARS THE ADAPTER
        mAdapter.clear();
    }

    //SETTINGS MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

