package com.adhithya.track.homeMap;


import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.adhithya.track.model.LocationModel;
import com.adhithya.track.responce.Locationresponce;
import com.adhithya.track.service.Config;
import com.adhithya.track.service.Database;
import com.adhithya.track.service.RetroClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "LocationService";
    private static int PERMISSION_ACCESS_FINE_LOCATION = 1;

    // use the websmithing defaultUploadWebsite for testing and then check your
    // location with your browser here: https://www.websmithing.com/gpstracker/displaymap.php
    private String defaultUploadWebsite;

    private boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;

    private static int uid=0;
    private Database db;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = Database.getInstance();

        // if we are currently trying to get a location and the alarm manager has called this again,
        // no need to start processing a new location.
        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            startTracking();
        }

        return START_NOT_STICKY;
    }

    private void startTracking() {
        Log.d(TAG, "startTracking");

        // if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.e(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());
            uid= Config.getUID();
            Log.e("UID",""+uid);
            double lati,longil;
            lati=location.getLatitude();
            longil=location.getLongitude();

            SendLocation(lati,longil);
            // we have our desired accuracy of 500 meters so lets quit this service,
            // onDestroy will be called and stop our location uodates
            if (location.getAccuracy() < 500.0f) {
                stopLocationUpdates();
            }
        }
    }

    private void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // milliseconds
        locationRequest.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } catch (SecurityException se) {
            Log.e(TAG, "Go into settings and find Gps Tracker app and enable Location.");
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");

        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "GoogleApiClient connection has been suspended.");
    }


    private void SendLocation(double lati,double longi){
        Log.e("UID",""+uid);
        int uid = db.getLoggedUser().getUid();
        SEND(Config.KEY,Config.getUID(),lati,longi).enqueue(new Callback<Locationresponce>() {
            @Override
            public void onResponse(Call<Locationresponce> call, Response<Locationresponce> response) {
                if (response.body().getStatus()==1){

                    List<LocationModel> list=response.body().getUser();
                    LocationModel model=list.get(0);
                    Log.e("update",""+response.body().getMessage());

                    Log.e("MODEL",""+model.getLattitude()+" "+model.getLongtitude());
                }else {
                    Log.e("MODEL",""+response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<Locationresponce> call, Throwable t) {

                Log.e("MODEL",""+t);
            }
        });
    }


    private Call<Locationresponce> SEND(String key, int uid, double lati, double longi){
        return RetroClient.getApiService().sendLocation(key,String.valueOf(uid),String.valueOf(lati),String.valueOf(longi));
    }



}
