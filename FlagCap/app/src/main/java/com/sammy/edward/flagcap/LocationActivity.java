package com.sammy.edward.flagcap;


import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;


public class LocationActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    GoogleApiClient googleApiClient;

    TextView latitude_value;
    TextView longitude_value;
    TextView lastUpdate;

    String timeOfLastUpdate;
    Location currentLocation;
    LocationRequest locationRequest;
    Boolean requestingLocationUpdates = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        latitude_value = (TextView) findViewById(R.id.location_latitude_value);
        longitude_value = (TextView) findViewById(R.id.location_longitude_value);
        lastUpdate = (TextView) findViewById(R.id.location_time_updated);
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLastKnownLocation();

        if(!requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(googleApiClient.isConnected() && !requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        timeOfLastUpdate = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    void getLastKnownLocation() {
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        timeOfLastUpdate = DateFormat.getTimeInstance().format(new Date());
        if (currentLocation != null) {
            updateUI();
        }
    }

    void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        requestingLocationUpdates = true;
    }

    void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        requestingLocationUpdates = false;
    }

    void updateUI() {
        latitude_value.setText(String.valueOf(currentLocation.getLatitude()));
        longitude_value.setText(String.valueOf(currentLocation.getLongitude()));
        lastUpdate.setText(timeOfLastUpdate);
    }
}
