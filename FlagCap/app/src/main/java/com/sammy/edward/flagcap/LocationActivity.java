package com.sammy.edward.flagcap;


import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.Date;


public class LocationActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, OnMapReadyCallback {

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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.location_map);
        mapFragment.getMapAsync(this);
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

    @Override
    public void onMapReady(GoogleMap map) {
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng lingon32 = new LatLng(59.4633094, 17.9470539);
        map.addMarker(new MarkerOptions().position(lingon32).title("Marker at lingonv. 32"));
        map.moveCamera(CameraUpdateFactory.zoomTo(10));
        map.moveCamera(CameraUpdateFactory.newLatLng(lingon32));
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
