package com.sammy.edward.flagcap;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;


public class LocationActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, OnMapReadyCallback {

    GoogleApiClient googleApiClient;
    GoogleMap theMap;

    TextView latitude_value;
    TextView longitude_value;
    TextView altitude_value;
    TextView water_value;
    TextView lastUpdate;

    String timeOfLastUpdate;
    Location currentLocation;
    LocationRequest locationRequest;
    Boolean requestingLocationUpdates = false;

    private WaterCheckerReceiver resultReceiver;

    HashMap<String, Marker> markers;

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
        altitude_value = (TextView) findViewById(R.id.location_altitude_value);
        water_value = (TextView) findViewById(R.id.location_water_value);
        lastUpdate = (TextView) findViewById(R.id.location_time_updated);

        markers = new HashMap();

        resultReceiver = new WaterCheckerReceiver(new Handler());

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
        theMap = map;

        LatLng lingon32 = new LatLng(59.4633094, 17.9470539);
        markers.put("marker1", placeFlag(lingon32));
        map.moveCamera(CameraUpdateFactory.zoomTo(15));
        map.moveCamera(CameraUpdateFactory.newLatLng(lingon32));


        //Check the if Norrviken is considered water.
        checkIfWater(new LatLng(59.46175, 17.93359));
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
        double lat = currentLocation.getLatitude();
        double lng = currentLocation.getLongitude();

        checkIfWater(new LatLng(lat, lng));

        latitude_value.setText(String.valueOf(lat));
        longitude_value.setText(String.valueOf(lng));
        altitude_value.setText(String.valueOf(currentLocation.getAltitude()) + " meters");
        lastUpdate.setText(timeOfLastUpdate);
        if(markers.get("currentLocation") != null) {
            markers.get("currentLocation").setPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
        } else {
            markers.put("currentLocation", theMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("currentLocation")));
        }
    }

    void checkIfWater (LatLng location) {
        Intent intent = new Intent(this, WaterChecker.class);
        intent.putExtra(Constants.WATER_RECEIVER, resultReceiver);
        intent.putExtra(Constants.WATER_POINT, location);
        startService(intent);
    }

    Marker placeFlag(LatLng pos) {
        MarkerOptions newFlag = new MarkerOptions();
        newFlag.position(pos);
        newFlag.title("FlagMarker" + markers.size());
        newFlag.icon(BitmapDescriptorFactory.fromResource(R.drawable.flag));

        return theMap.addMarker(newFlag);
    }

    class WaterCheckerReceiver extends ResultReceiver {
        public WaterCheckerReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == Constants.SUCCESS_RESULT) {
                boolean result = resultData.getBoolean(Constants.WATER_BOOLEAN_RESULT_DATA_KEY);
                water_value.setText(String.valueOf(result));
            }
        }
    }

}
