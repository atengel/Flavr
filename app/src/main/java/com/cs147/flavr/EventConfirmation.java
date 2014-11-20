//NOTICE: THIS CODE CONTAINS MATERIAL THAT IS FREELY DISTRIBUTED BY GOOGLE.INC
package com.cs147.flavr;



import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.IntentSender;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import java.sql.Time;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import java.lang.String;
import java.util.Calendar;
import com.google.android.gms.maps.model.LatLng;
import android.location.Location;
import android.os.AsyncTask;
import android.location.Geocoder;
import java.util.Locale;
import android.location.Address;
import java.util.List;
import java.io.IOException;

public class EventConfirmation extends FragmentActivity{
    private TextView mAddress;
    public LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng loc = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if(address == null) return null;
            Address location = address.get(0);
            loc = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loc;
    }

    public void printEventInfo(String [] information) {
        TextView eventTitle = (TextView) findViewById(R.id.confirmation_food);
        eventTitle.setText(information[0]);
        TextView food = (TextView) findViewById(R.id.confirmation_event);
        food.setText("Event: "+information[1]);
        TextView description = (TextView) findViewById(R.id.confirmation_description);
        description.setText("Description: "+information[2]);
        TextView location = (TextView) findViewById(R.id.confirmation_location);
        location.setText("Location: "+information[3]);
        TextView tags = (TextView) findViewById(R.id.confirmation_tags);
        tags.setText("Tags: "+information[4]);
        TextView capacity = (TextView) findViewById(R.id.confirmation_capacity);
        capacity.setText("Capacity: "+information[5]);



//            TextView keywords = (TextView) findViewById(R.id.keywords);
//            keywords.setText("Keywords: "+information[4]);
    }

    private void printEventExpiry(int[] times) {
        Calendar c = Calendar.getInstance();
        int sysHour = c.get(Calendar.HOUR_OF_DAY);
        int sysMin = c.get(Calendar.MINUTE);
        int startHour = times[0];
        int startMin = times[1];
        int endHour = times[2];
        int endMin = times[3];
        TextView startText = (TextView) findViewById(R.id.confirmation_start_time);
        if (startHour > sysHour) {
            int hourDiff = startHour - sysHour;
            int minDiff = startMin - sysMin;
            if(sysMin > startMin) hourDiff--;
            startText.setText("Your event starts in " + Integer.toString(hourDiff) + "hours and "+ Integer.toString(minDiff)+"minutes.");
        }
        if(startHour == sysHour && startMin >= sysMin) {
            int minDiff = startMin - sysMin;
            startText.setText("Your event starts in "+ Integer.toString(minDiff)+" minutes.");
        }
        TextView endText = (TextView) findViewById(R.id.confirmation_end_time);
        if (endHour > sysHour) {
            int hourDiff = endHour - sysHour;
            int minDiff = endMin - sysMin;
            if(sysMin > endMin) hourDiff--;
            endText.setText("Your event ends in " + Integer.toString(hourDiff) + "hours and "+ Integer.toString(minDiff)+"minutes.");
        }
        if(endHour == sysHour && endMin >= sysMin) {
            int minDiff = endMin - sysMin;
            endText.setText("Your event expires in "+ Integer.toString(minDiff)+" minutes.");
        }

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent eventInfo = getIntent();
        setContentView(R.layout.activity_event_confirmation);
        String[] eventInformation = eventInfo.getStringArrayExtra(createEvent.EVENT_STRINGS);
        LatLng location = getLocationFromAddress(eventInformation[3]);
        GoogleMap gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        gMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        int[] eventTimes = eventInfo.getIntArrayExtra(createEvent.EVENT_TIMES);
        printEventInfo(eventInformation);
        printEventExpiry(eventTimes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_confirmation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //
    //    public void printConfirmation(View view) {
    //
    //    }
}
