package com.example.android.earthquake101;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.android.earthquake101.EarthquakeActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */

    private QueryUtils() { }

    public static List<Earthquake> fetchEarthquakeDate(String requestUrl) {

        URL url = createUrl(requestUrl);

        // Stores the response from the url
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch(IOException e) {
            Log.i(LOG_TAG, "Tried this url = " + e);
        }
        List<Earthquake> earthquakes = extractFeatureFromJson(jsonResponse);

//        Log.e(LOG_TAG,"doInBackground() QueryUtils.java");

        return earthquakes;
    }


    /**
     * Return a earthquake_activity of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Earthquake> extractEarthquakes(String stringUrl) {

        // create a URL object from the string url
        URL myUrl = createUrl(stringUrl);

        String jsonResponse = "";
        // now we have the url object, we need to use it to make a connection
        // make sure the method check url if is valid
        // call makeHttpRequest method
        try {
            jsonResponse = makeHttpRequest(myUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Earthquake> earthquakes = new ArrayList<>();

        Date date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("LLL dd,yyyy");

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.

        // only extract if json response not empty
        if(TextUtils.isEmpty(jsonResponse)){
            return null;
        }

        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a earthquake_activity of Earthquake objects with the corresponding data.

            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray earthquakeArray  = baseJsonResponse.getJSONArray("features");

            for (int i = 0; i < earthquakeArray.length(); i++) {

                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);
                JSONObject properties = currentEarthquake.getJSONObject("properties");
                double mag = properties.getDouble("mag");
                String location = properties.getString("place");
                long time = properties.getLong("time");
                String url = properties.getString("detail");
                earthquakes.add(new Earthquake(mag, location,time, url));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the earthquake_activity of earthquakes
//        Log.e(LOG_TAG,"extractEarthquakes() QueryUtils.java");
        return earthquakes;
    }


    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
//        Log.e(LOG_TAG,"onCreateUrl() QueryUtils.java");
        return url;
    }


    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);

        }catch (IOException e){
            Log.i(LOG_TAG,"HttpURLConnection" + e);
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
//        Log.e(LOG_TAG,"makeHttpResponse() QueryUtils.java");
        return jsonResponse ;
    }


    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
//        Log.e(LOG_TAG,"readFromStream() QueryUtils.java");
        return output.toString();
    }


    private static List<Earthquake> extractFeatureFromJson(String earthquakeJson) {
        if (TextUtils.isEmpty(earthquakeJson)) {
            return null;
        }

        List<Earthquake> earthquakes = new ArrayList<>();

        try {

            JSONObject baseJsonResponse = new JSONObject(earthquakeJson);
            JSONArray earthquakeArray = baseJsonResponse.getJSONArray("features");

            for (int i = 0; i < earthquakeArray.length(); i++) {

                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);
                JSONObject properties = currentEarthquake.getJSONObject("properties");

                double magnitude = properties.getDouble("mag");
                String location = properties.getString("place");
                long time = properties.getLong("time");
                String url = properties.getString("url");

//                Log.e(LOG_TAG, "exectFeatureFromJson() ");
                Earthquake earthquake = new Earthquake(magnitude, location, time, url);
                earthquakes.add(earthquake);

            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "extractFeatureFromJson() " + e);
        }
//       Log.e(LOG_TAG, "extractFeatureFromJson()  QueryUtils.java");
        return earthquakes;
    }
}
