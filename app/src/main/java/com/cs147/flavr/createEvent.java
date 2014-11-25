package com.cs147.flavr;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.io.FileNotFoundException;
import java.lang.String;
import android.widget.Toast;
import android.net.Uri;
import java.io.InputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.java.jddac.common.type.ArgMap;


public class createEvent extends Activity {
    //Temporary array for event string information.
    public final static String EVENT_STRINGS = "temp";
    //Temporary array for event time information.
    public final static String EVENT_TIMES = "tempTimes";
    //Temp integer to hold image later.
    public final static int SELECT_PHOTO = 100;

    public static Bitmap yourSelectedImage;
    public static Uri selectedImage;

    /*Extracts string from an edit text field where the user inputs data.
    */
    public String extractStringFromID(int id) {
        EditText editText = (EditText) findViewById(id);
        return editText.getText().toString();
    }

    private void giveToastError(String error) {
        if(error == "foodType")  Toast.makeText(getApplicationContext(), "Please enter a valid food type.", Toast.LENGTH_LONG).show();
        else if(error == "eventTitle")  Toast.makeText(getApplicationContext(), "Please enter a valid event title.", Toast.LENGTH_LONG).show();
        else if(error == "location")  Toast.makeText(getApplicationContext(), "Please enter a valid location.", Toast.LENGTH_LONG).show();
    }

    public void uploadImage(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);

    }

    private int[] parseTimes() {
        int[] times = new int[4];
        TimePicker startTimer = (TimePicker) findViewById(R.id.start_time);
        int startHour = startTimer.getCurrentHour();
        int startMinute = startTimer.getCurrentMinute();
        times[0] = startHour;
        times[1] = startMinute;
        TimePicker endTimer = (TimePicker) findViewById(R.id.end_time);
        int endHour = endTimer.getCurrentHour();
        int endMinute = endTimer.getCurrentMinute();
        times[2] = endHour;
        times[3] = endMinute;
        return times;
    }
    /*Upon the clicking of the submit button, reads all the strings and times from the event listing and puts them into an respective arrays to be passed into
    *the next activity of confirming the event. Bundles these into an extra for the intent
    * and goes to event confirmation.
    */
    public void submitEvent(View view) {

        String[] eventInformation = new String[6];
        String foodType = extractStringFromID(R.id.food_type);
        String eventTitle = extractStringFromID(R.id.event_title);
        String description = extractStringFromID(R.id.description);
        String location = extractStringFromID(R.id.location);
        String tags = extractStringFromID(R.id.tags);
        String capacity = extractStringFromID(R.id.capacity);
        if (foodType.length() == 0) giveToastError("foodType");
        else if (eventTitle.length() == 0) giveToastError("eventTitle");
        else if (location.length() == 0) giveToastError("location");
        else {
            eventInformation[0] = foodType;
            eventInformation[1] = eventTitle;
            eventInformation[2] = description;
            eventInformation[3] = location;
            eventInformation[4] = tags;
            eventInformation[5] = capacity;
            int [] timeInfo = parseTimes();
            ArgMap newEvent = new ArgMap();
            newEvent.put(GetFoodList.FOOD, foodType);
            newEvent.put(GetFoodList.EVENT, eventTitle);
            newEvent.put(GetFoodList.LOCATION, location);
            newEvent.put(GetFoodList.IMAGE, yourSelectedImage);
            MainActivity.events.add(0, newEvent);
            Intent submit = new Intent(this, EventConfirmation.class);
            Bundle event = new Bundle();
            event.putIntArray(EVENT_TIMES, timeInfo);
            event.putStringArray(EVENT_STRINGS, eventInformation);
            submit.putExtras(event);
            startActivity(submit);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
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
    @Override
    /* Gets image from user for event and converts to Bitmap
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    try {
                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}


