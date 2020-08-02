package com.locationTraker.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.locationTraker.LocationUtils;
import com.locationTraker.R;
import com.locationTraker.db.LocationDatabase;
import com.locationTraker.preference.AppPreferences;
import com.locationTraker.ui.MainActivity;
import com.locationTraker.ui.MapsActivity;
import com.locationTraker.utils.Keys;

import java.util.ArrayList;

import static com.locationTraker.LocationTracker.CHANNEL_ID;

public class LocationNotifyService extends Service {

    private Runnable runnable;

    private LocationDatabase db;

    private Handler handler;

    private LocationReceiver receiver;

    private boolean stopService = false;


    @Override
    public void onCreate() {
        super.onCreate();
        stopService = false;
    }


    @Override
    public void onDestroy() {

        stopService = true;
        try {
            if (handler != null) {
                handler.removeCallbacks(runnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Notification for services
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Service")
                .setContentText("Service has started")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        AppPreferences.setSharedPreferences(getApplicationContext());
        db = new LocationDatabase(getApplicationContext());

        receiver = new LocationReceiver();
        try {
            getApplicationContext().registerReceiver(receiver, new IntentFilter(LocationReceiver.ACTION));
        } catch (Exception e) {
            e.printStackTrace();
        }

        startIteration();

        return START_NOT_STICKY;

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    class LocationData {
        String longitude;
        String latitude;

        public LocationData(String longitude, String latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }


    /**
     * Send Repeated PayLoad in the opted time interval
     */
    private void startIteration() {

        handler = new Handler(getMainLooper());
        final int intervalTime = 15000;

        runnable = new Runnable() {
            public void run() {

                if (stopService)
                    return;

                String longitude = AppPreferences.getString(Keys.LONG);
                String latitude = AppPreferences.getString(Keys.LAT);

//                ArrayList<LocationData> locationData = new ArrayList<>();
//
//                locationData.add(new LocationData("27.6715873", "85.2810544"));
//                locationData.add(new LocationData("27.6718805", "85.2808137"));
//                locationData.add(new LocationData("27.6710213", "85.2800275"));
//                locationData.add(new LocationData("27.6703382", "85.2796617"));
//
//                for (int i = 0; i < locationData.size(); i++) {
//                    LocationData data = locationData.get(i);
//                    db.insertLog(data.longitude, data.latitude);
//                }

                if (!longitude.equals("") || !latitude.equals("") ){
                    db.insertLog(longitude, latitude);
                }

//                new LocationUtils(LocationNotifyService.this).getLastLocation();

                broadcastIntent("Location Tracker Enabled");

                handler.postDelayed(this, intervalTime);
            }
        };
        handler.postDelayed(runnable, intervalTime);

    }


    /**
     * Sends Broadcast to broad cast receiver
     *
     * @param msg message to broadcast
     */
    public void broadcastIntent(String msg) {
        Intent intent = new Intent();
        intent.setAction(LocationReceiver.ACTION);
        intent.putExtra(Keys.BROADCAST_MSG, msg);
        sendBroadcast(intent);
    }

}
