package com.example.android.earthquake101;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.VIBRATOR_SERVICE;

public class EarthquakeAdaptor extends ArrayAdapter<Earthquake> {

    public static final String LOG_TAG = EarthquakeAdaptor.class.getName();

    String holderForPlace="";

    public EarthquakeAdaptor(Activity context, ArrayList<Earthquake> earthquakes) {   // public constructor
        super(context, 0, earthquakes);   // uses super class
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(   // use list_item.xml to house each listItemView
                    R.layout.list_item, parent, false);
        }

        Earthquake currentEarthquake = getItem(position); // where in the currentEarthquake are we
//        Log.e(LOG_TAG,"getView testing to see position EarthquakeLoader.java"+position);
        if (position==0) {
            if (holderForPlace == "") {
                holderForPlace = currentEarthquake.getLocation();
            }
        }
        Log.e(LOG_TAG,"holderForPlace     " + holderForPlace);

        TextView magnitudeView = (TextView) listItemView.findViewById(R.id.magnitude);
        magnitudeView.setText(String.valueOf(currentEarthquake.getMagnitude()));

        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeView.getBackground();
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getMagnitude());
        magnitudeCircle.setColor(magnitudeColor);


        TextView kmNearTextView = (TextView) listItemView.findViewById(R.id.location_offset);
        TextView placeTextView = (TextView) listItemView.findViewById(R.id.primary_location);
        kmNearTextView.isDirty();

        String place = currentEarthquake.getLocation();
        if(position ==0) {
            if (!holderForPlace.equals(place)) {
                //TODO here is where we need to vibrate the phone !!!
                Vibrator v = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(10000);
                Log.e(LOG_TAG,"TODO inside of holderForPlace != place ");
                holderForPlace = place;
            }
        }

        if (place.contains("km")) {

            int indexOfOF = place.indexOf("of");

            String firstPart = place.substring(0,indexOfOF+2);
            String secondPart = place.substring(indexOfOF+3);

            kmNearTextView.setText(firstPart);
            placeTextView.setText(secondPart);

        }else {
            kmNearTextView.setText(getContext().getString(R.string.near_the));
            placeTextView.setText(place);
        }

        Date dateObject = new Date(currentEarthquake.getTimeInMilliseconds());  // get current date and time

        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        String formattedDate = formatDate(dateObject); // goto method to format the date
        dateView.setText(formattedDate);

        TextView timeView = (TextView) listItemView.findViewById(R.id.time);
        String formattedTime = formatTime(dateObject); // goto method to format the time
        timeView.setText(formattedTime);

        Log.e(LOG_TAG,"EarthquakeAdaptor getView()");
        return listItemView;
    }
    private int getMagnitudeColor(double magnitude) {   // method uses in determine which color to use based on the mag value
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
//        Log.e(LOG_TAG,"getMagnitudeColor() EarthquakeLoader.java"+magnitudeFloor);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;         // colors are defined in the colors.xml file
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }

        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }

    private String formatDate(Date dateObject){
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");   // this is the line that formats the date
        return dateFormat.format(dateObject);
    }
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm: a");   // this is the line that formats the time
        return timeFormat.format(dateObject);
    }
}
