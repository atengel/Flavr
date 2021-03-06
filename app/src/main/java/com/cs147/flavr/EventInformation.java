package com.cs147.flavr;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.java.jddac.common.type.ArgMap;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;


public class EventInformation extends Activity {
    public ArgMap currEvent;

    /* Set action bar font to system standard.
     */
    private void createCustomActionBar() {
        int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title","id","android");
        TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
        Typeface alegreya = Typeface.createFromAsset(getAssets(),"fonts/alegreyasanssc_bold.ttf");
        actionBarTitleView.setTypeface(alegreya);
        getActionBar().setTitle("Event Information");
    }

    /* If the user decides to retract their RSVP, revert the event information page to
    original format.
     */
    public void unAttend(View view) {
        Button going = (Button) findViewById(R.id.im_going);
        going.setBackgroundDrawable(getResources().getDrawable(R.drawable.roundedbuttonblue));
        going.setText("I'm Going!");
        going.setActivated(true);
        String currAttendance = currEvent.getString(GetFoodList.ATTENDANCE);
        int attendance = Integer.parseInt(currAttendance);
        attendance--;
        currEvent.put(GetFoodList.ATTENDANCE, attendance);
        currEvent.put(GetFoodList.ATTENDING, 0);
        Button notGoing = (Button) findViewById(R.id.not_going);
        notGoing.setVisibility(View.GONE);
    }

    /* Allow the user to see a list of their created events.
     */
    private void userEvents() {
        Intent events = new Intent(this, UserEvents.class);
        startActivity(events);
    }

    /* Allow user to RSVP to event, add button to allow them to retract RSVP.
     */
    public void respondToInvite(View view) {
        Button going = (Button) findViewById(R.id.im_going);
        going.setBackgroundDrawable(getResources().getDrawable(R.drawable.roundedbuttongreen));
        going.setText("See You There");
        going.setActivated(false);
        String currAttendance = currEvent.getString(GetFoodList.ATTENDANCE);
        int attendance = Integer.parseInt(currAttendance);
        attendance++;
        currEvent.put(GetFoodList.ATTENDANCE, attendance);
        currEvent.put(GetFoodList.ATTENDING, 1);
        Button notGoing = (Button) findViewById(R.id.not_going);
        notGoing.setVisibility(View.VISIBLE);
    }

    /* Add all the event data to the necessary fields
     */
    private void fillData(ArgMap event) {
        TextView eventTitle = (TextView) findViewById(R.id.get_event_food);
        Typeface alegreya = Typeface.createFromAsset(getAssets(), "fonts/alegreyasanssc_regular.ttf");
        Typeface open = Typeface.createFromAsset(getAssets(), "fonts/opensans_regular.ttf");
        eventTitle.setText(event.getString(GetFoodList.FOOD));
        eventTitle.setTypeface(alegreya);
        TextView food = (TextView) findViewById(R.id.get_event_event);
        food.setTypeface(alegreya);
        food.setText(event.getString(GetFoodList.EVENT));
        TextView description = (TextView) findViewById(R.id.get_event_description);
        description.setTypeface(open);
        description.setText(event.getString(GetFoodList.DESCRIPTION));
        TextView location = (TextView) findViewById(R.id.get_event_location);
        location.setTypeface(alegreya);
        location.setText(event.getString(GetFoodList.LOCATION));
        TextView distance = (TextView) findViewById(R.id.distance);
        distance.setTypeface(alegreya);
        distance.setText(event.getString(GetFoodList.DISTANCE) + " miles away");
        TextView attendance = (TextView) findViewById(R.id.get_event_attendance);
        attendance.setTypeface(open);
        SpannableStringBuilder sb = new SpannableStringBuilder(event.getString(GetFoodList.ATTENDANCE)+" attending.");
        StyleSpan b = new StyleSpan(Typeface.BOLD);
        sb.setSpan(b, 0, 0+event.getString(GetFoodList.ATTENDANCE).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        attendance.setText(sb);
        ImageView eventImage = (ImageView) findViewById(R.id.get_event_image);
        int imageResource = getResources().getIdentifier(event.getString(GetFoodList.IMAGE), null, "com.cs147.flavr");
        if(imageResource != 0) {
            Drawable custom = getResources().getDrawable((imageResource));
            eventImage.setImageDrawable(custom);
        }
        else eventImage.setImageBitmap((Bitmap) event.get(GetFoodList.IMAGE));

    }

    /* Use google play services in order to look up an address and display it on the map/
     */
    private LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng loc = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) return null;
            if(!address.isEmpty()) {
                Address location = address.get(0);
                if (location != null)
                    loc = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return loc;
    }

    /* Calculate and print the amount of time remaining until the event expires.
     */
    private void printEventExpiry(ArgMap event) {
        Calendar c = Calendar.getInstance();
        int sysHour = c.get(Calendar.HOUR_OF_DAY);
        int sysMin = c.get(Calendar.MINUTE);
        Typeface open = Typeface.createFromAsset(getAssets(), "fonts/opensans_regular.ttf");
        int endHour = Integer.parseInt(event.getString(GetFoodList.END_HOUR));
        int endMin = Integer.parseInt(event.getString(GetFoodList.END_MIN));
        TextView endText = (TextView) findViewById(R.id.get_event_endtime);
        endText.setTypeface(open);
        if (endHour > sysHour) {
            int hourDiff = endHour - sysHour;
            int minDiff = endMin - sysMin;
            if(sysMin > endMin) {
                hourDiff--;
                minDiff += 60;
            }
            String plural = " hours.";
            if(hourDiff == 1) plural = " hour.";
            endText.setText("Expires in: " + Integer.toString(hourDiff) + plural);
        }
        if(endHour == sysHour && endMin >= sysMin) {
            int minDiff = endMin - sysMin;
            endText.setText("Expires in: "+ Integer.toString(minDiff)+" minutes.");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(this);
        setContentView(R.layout.activity_event_information);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        Typeface alegreya = Typeface.createFromAsset(getAssets(), "fonts/alegreyasanssc_bold.ttf");
        Button notGoing = (Button) findViewById(R.id.not_going);
        Button going = (Button) findViewById(R.id.im_going);
        notGoing.setTypeface(alegreya);
        going.setTypeface(alegreya);
        notGoing.setTextColor(Color.WHITE);
        going.setTextColor(Color.WHITE);
        notGoing.setVisibility(View.GONE);
        Intent eventInfo = getIntent();
        createCustomActionBar();
        int listPosition = 0;
        listPosition = eventInfo.getIntExtra(GetFoodList.EXTRA, listPosition);
        ArgMap event = MainActivity.events.get(listPosition);
        if(Integer.parseInt(event.getString(GetFoodList.ATTENDING)) == 1) {
            going.setBackgroundDrawable(getResources().getDrawable(R.drawable.roundedbuttongreen));
            going.setText("See You There");
            going.setActivated(false);
            notGoing.setVisibility(View.VISIBLE);
        }
        currEvent = event;
        fillData(event);
        printEventExpiry(event);
        LatLng location = getLocationFromAddress(event.getString(GetFoodList.LOCATION));
        if(location != null) {
            GoogleMap gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.get_map)).getMap();
            gMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            Marker place = gMap.addMarker(new MarkerOptions()
                    .position(location));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.myevents3:
                userEvents();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
