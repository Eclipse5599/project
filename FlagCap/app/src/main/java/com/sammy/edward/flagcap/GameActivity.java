package com.sammy.edward.flagcap;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class GameActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, OnMapReadyCallback {

    GoogleApiClient googleApiClient;
    GoogleMap theMap;

    Location currentLocation;
    LatLng currentCoordinates;
    LocationRequest locationRequest;
    Boolean requestingLocationUpdates = false;

    HashMap<String, Marker> flags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        flags = new HashMap<>();

        View mapView = findViewById(R.id.game_mapView);
        mapView.setClickable(false);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.game_map);
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
        updateUI();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        theMap = map;

        LatLng lingon32 = new LatLng(59.4633094, 17.9470539);
        flags.put("Flag", placeFlag(lingon32));

        map.moveCamera(CameraUpdateFactory.zoomTo(15));
        applyMapSettings();
    }

    void applyMapSettings () {
        UiSettings mapSettings = theMap.getUiSettings();

        mapSettings.setCompassEnabled(false);
        mapSettings.setMapToolbarEnabled(false);
        mapSettings.setRotateGesturesEnabled(false);
        mapSettings.setScrollGesturesEnabled(false);
        mapSettings.setTiltGesturesEnabled(false);
        mapSettings.setZoomGesturesEnabled(false);

    }

    void getLastKnownLocation() {
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
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
        currentCoordinates = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        theMap.moveCamera(CameraUpdateFactory.newLatLng(currentCoordinates));
    }

    Marker placeFlag(LatLng pos) {
        MarkerOptions newFlag = new MarkerOptions();
        newFlag.position(pos);
        newFlag.title("FlagMarker" + flags.size());
        newFlag.icon(BitmapDescriptorFactory.fromResource(R.drawable.flag));

        return theMap.addMarker(newFlag);
    }
}
