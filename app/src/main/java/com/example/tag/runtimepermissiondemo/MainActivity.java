package com.example.tag.runtimepermissiondemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 123;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = MainActivity.this;
        Button showCalender = (Button) findViewById(R.id.showCalender);
        showCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent calIntent = new Intent(Intent.ACTION_INSERT);
//                calIntent.setData(CalendarContract.Events.CONTENT_URI);
//                startActivity(calIntent); orr

//                Calendar cal = Calendar.getInstance();
//                Intent intent = new Intent(Intent.ACTION_EDIT);
//                intent.setType("vnd.android.cursor.item/event");
//                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,cal.getTimeInMillis());
//                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,cal.getTimeInMillis()+60*60*1000);
//                intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
//                intent.putExtra(CalendarContract.Events.TITLE, "Event title here");
//                intent.putExtra(CalendarContract.Events.DESCRIPTION, "This is a sample description");
//                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Event Address");
//                intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY");
//                startActivity(intent);

                //if we do this thing using INTENT then permission is not required
                boolean result = checkPermission();
                if (result) {
                    writeCalendarEvent();
                }
            }
        });
    }

    private void writeCalendarEvent() {
        final ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.CALENDAR_ID, 1);

        event.put(CalendarContract.Events.TITLE, "title");
        event.put(CalendarContract.Events.DESCRIPTION, "description");
        event.put(CalendarContract.Events.EVENT_LOCATION, "location");

        event.put(CalendarContract.Events.DTSTART, 18000000);//startTimeMillis
        event.put(CalendarContract.Events.DTEND, 1800000000);//endTimeMillis
        event.put(CalendarContract.Events.ALL_DAY, 0);   // 0 for false, 1 for true
        event.put(CalendarContract.Events.HAS_ALARM, 1); // 0 for false, 1 for true

        String timeZone = TimeZone.getDefault().getID();
        event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone);

        Uri baseUri;
        if (Build.VERSION.SDK_INT >= 8) {
            baseUri = Uri.parse("content://com.android.calendar/events");
        } else {
            baseUri = Uri.parse("content://calendar/events");
        }

        getApplicationContext().getContentResolver().insert(baseUri, event);
        Toast.makeText(getApplicationContext(), "Event Created", Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission()
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_CALENDAR)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write calendar permission is necessary to write event!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_CALENDAR:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    writeCalendarEvent();
                } else {
                    //code for deny
                }
                break;
        }
    }
}
