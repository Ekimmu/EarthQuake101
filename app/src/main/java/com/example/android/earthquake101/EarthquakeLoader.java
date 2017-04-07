package com.example.android.earthquake101;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;


public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    public static final String LOG_TAG = EarthquakeLoader.class.getName();

    private String mUrl;

    public EarthquakeLoader(Context context,String mUrl) {
        super(context);
        this.mUrl = mUrl;
//        Log.e(LOG_TAG,"EarthquakeLoader() EarthquakeLoader.java");
    }
    protected void onStartLoading(){
//        Log.e(LOG_TAG,"onStartLoading() EarthquakeLoader.java");
        forceLoad();
    }
    @Override
    public List<Earthquake> loadInBackground() {
        if(mUrl == null) {
            return null;
        }
        List<Earthquake> earthquakes = QueryUtils.fetchEarthquakeDate(mUrl);
//        Log.e(LOG_TAG,"loadinBackground() EarthquakeLoader.java");
        return earthquakes;
    }
}
