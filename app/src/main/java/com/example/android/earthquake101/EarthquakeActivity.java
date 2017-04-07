package com.example.android.earthquake101;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.action;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final String USGS_URL ="https://earthquake.usgs.gov/fdsnws/event/1/query";
    private static final int EARTHQUAKE_LOADER_ID = 1;
    public boolean mTrigger=false;
    public int theCountress=0;
    private  EarthquakeAdaptor mAdapter;
    private ProgressBar progressBar;
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ListView earthquakeListView = (ListView) findViewById(R.id.theList);
        earthquakeListView.setEmptyView(mEmptyStateTextView);

        progressBar = (ProgressBar) findViewById(R.id.loadingSpinner);
        mEmptyStateTextView = (TextView) findViewById(R.id.emptyViewState);

        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnected();
        if(isConnected) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        }else{
            progressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText("No connection");
        }
        myTimerStarter();
        theCountress++;

        mAdapter = new EarthquakeAdaptor(this,new ArrayList<Earthquake>());
        earthquakeListView.setAdapter(mAdapter);
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Earthquake currentEarthquake = mAdapter.getItem(position);
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW,earthquakeUri);
                startActivity(websiteIntent);
            }
        });

//        Log.e(LOG_TAG,"onCreate() earthquakeactivity.java");
    }

    public void newLoader(){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnected();
        if(isConnected) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.restartLoader(EARTHQUAKE_LOADER_ID,null,EarthquakeActivity.this);
        }
        theCountress++;
        Log.e(LOG_TAG,"newLoader() earthquakeactivity.java");

    }

    public void myTimerStarter(){
        Timer timerObj = new Timer();
        TimerTask timerTaskObj = new TimerTask() {
            public void run() {
                Log.e(LOG_TAG,"myTimerStrater inside public void run() EarthQuakeActivity.java");
                newLoader();
            }
        };
        timerObj.schedule(timerTaskObj, 0, 300000);

        Log.e(LOG_TAG,"myTimerStarter last earthquakeactivity.java");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent intent = new Intent(EarthquakeActivity.this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
/**
 if (id == R.id.alarm_start){
 // TODO here is need to get updaed earthquake info.
 alarm.setAlarm(this);
 return true;
 }
 */
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String minMagnitude = sharedPreferences.getString(getString(R.string.settings_min_magnitude_key)
                , getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(USGS_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format","geojson");
        uriBuilder.appendQueryParameter("limit","30");

        uriBuilder.appendQueryParameter("minmag",minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);
//        Log.e(LOG_TAG,"onCreateLoader() earthquakeactivity.java");
        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes){
        progressBar.setVisibility(View.GONE);
        mEmptyStateTextView.setText("");
        mAdapter.clear();
        if(earthquakes!=null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }
//        Log.e(LOG_TAG,"onLoadFinished() earthquakeactivity.java");
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.clear();
//        Log.e(LOG_TAG,"onLoaderReset() earthquakeactivity.java");
    }


    private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<Earthquake>> {

        @Override
        protected List<Earthquake> doInBackground(String... urls) {
            // sanity check for string urls
            if(urls.length < 1 || urls[0] == null){
                return null;
            }

            String stringURL = urls[0];
            // use this url string to create a url
            // Create a fake earthquake_activity of earthquake locations.
            List<Earthquake> earthquakes = QueryUtils.fetchEarthquakeDate(stringURL);
//            Log.e(LOG_TAG,"doInBackground() earthquakeactivity.java");
            return earthquakes;
        }

        @Override
        protected void onPostExecute(List<Earthquake> data) {

            mAdapter.clear();

            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
//            Log.e(LOG_TAG,"onPostExecute() earthquakeactivity.java");
        }

    }
}
