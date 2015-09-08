package com.sammy.edward.flagcap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import extra.Converter;
import extra.DualValue;
import extra.Mathematics;
import extra.MyOutput;
import extra.ObjectSerializer;

public class GameActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, OnMapReadyCallback {

    private GoogleApiClient googleApiClient;
    private GoogleMap theMap;
    private Location gamePoint;
    private NewFlagLocationReceiver resultReceiver;
    private int currentPointsCollected = 0;
    private Location currentLocation;
    private LatLng currentCoordinates;
    private LocationRequest locationRequest;
    private Boolean requestingLocationUpdates = false;
    private ArrayList<Marker> flags;
    private TextView pointWindow;
    private SeekBar zoomLevel;
    private int currentZoomLevel;
    private boolean generateFlags = false;
    private final int AMOUNT_OF_FLAGS_WISHED = 20;
    private final double PICK_RADIUS = 0.05; //is 10 meters
    private final int UPDATE_RATE = 2500;
    int gameCode;
    private boolean recreating = false;
    private boolean flagsGenerated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        gameCode = intent.getIntExtra(Constants.GAMECODE, Constants.NEW_GAME_CODE);
        if (gameCode == Constants.NEW_GAME_CODE) {
            Log.d("Gamecode", "New Game");
        } else if (gameCode == Constants.CONTINUE_GAME_CODE) {
            Log.d("Gamecode", "Continue Game");
        }

        resultReceiver = new NewFlagLocationReceiver(new Handler());

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_RATE);
        locationRequest.setFastestInterval(UPDATE_RATE);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);



        zoomLevel = (SeekBar)findViewById(R.id.game_zoom_level);
        currentZoomLevel = zoomLevel.getProgress()+12;
        zoomLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentZoomLevel = progress + 12;
                zoomGameMap();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pointWindow = (TextView) findViewById(R.id.game_current_points);

        View mapView = findViewById(R.id.game_map);
        mapView.setClickable(false);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.game_map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onLocationChanged(Location location) {

        MyOutput.displayShortMessage("Location Updated!", getApplicationContext());

        if(generateFlags){
            generateFlagsAroundPlayer();
            generateFlags = false;
            recreating = false;
        } else if(recreating){
            setPreferences();
            recreating = false;
        }
        currentLocation = location;

        updateUI();
        pickFlagIfClose();
    }

    /**
     * Iterates through all flags, deletes them and increments points if player is close enough
     */
    public void pickFlagIfClose(){

        //GUARD
        if(flags == null){
            MyOutput.displayShortMessage("FLAGS IS NULL", getApplicationContext());
            return;
        }
        //END OF GUARD

        Iterator<Marker> it = flags.iterator();
        while(it.hasNext()){
            Marker currentMark = it.next();

            //GUARD
            if(currentMark == null){
                continue;
            }
            LatLng position = currentMark.getPosition();

            if(position == null){
                continue;
            }
            //END OF GUARD

            boolean flagPicked = false;
            double flagLong = position.longitude;
            double flagLat = position.latitude;
            if(Mathematics.getDistanceFromLatLonInKm(currentCoordinates.latitude, currentCoordinates.longitude, flagLat ,flagLong) < PICK_RADIUS){
                currentMark.remove();
                it.remove();
                flagPicked = true;
                currentPointsCollected++;
            }

            if(flagPicked){
                MyOutput.displayShortMessage("Flag Picked!", getApplicationContext());
            }
        }
    }

    public void generateFlagsAroundPlayer(){
        for(int i = 0; i < AMOUNT_OF_FLAGS_WISHED; i++){
            fetchNewFlag();
        }
        flagsGenerated = true;
    }

    void zoomGameMap() {
        theMap.moveCamera(CameraUpdateFactory.zoomTo(currentZoomLevel));
    }

    void applyMapSettings () {
        UiSettings mapSettings = theMap.getUiSettings();
        theMap.setMyLocationEnabled(true);
        mapSettings.setMyLocationButtonEnabled(false);
        mapSettings.setCompassEnabled(false);
        mapSettings.setMapToolbarEnabled(false);
        mapSettings.setRotateGesturesEnabled(false);
        mapSettings.setScrollGesturesEnabled(false);
        mapSettings.setTiltGesturesEnabled(false);
        mapSettings.setZoomGesturesEnabled(false);
        theMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });

    }

    void getLastKnownLocation() {

        Log.d("READY", "GetLastKnownLocation");

        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);


        if (gamePoint == null && currentLocation != null) {
            gamePoint = currentLocation;
            placeGamePointFlag(new LatLng(gamePoint.getLatitude(), gamePoint.getLongitude()));
        }

        if (currentLocation != null) {
            updateUI();
        }
    }

    void updateUI() {

        //GUARD
        if(currentLocation == null){
            return;
        }
        //END OF GUARD

        currentCoordinates = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        theMap.moveCamera(CameraUpdateFactory.newLatLng(currentCoordinates));
        pointWindow.setText("" + currentPointsCollected);
    }

    void placeFlag(LatLng pos) {

        MarkerOptions newFlag = new MarkerOptions();
        newFlag.position(pos);
        newFlag.title("FlagMarker" + flags.size());
        newFlag.icon(BitmapDescriptorFactory.fromResource(R.drawable.flag));
        flags.add(theMap.addMarker(newFlag));

    }


    void fetchNewFlag() {

        if (gamePoint != null) {
            Intent intent = new Intent(this, RandomLocationAroundPoint.class);
            intent.putExtra(Constants.RECEIVER, resultReceiver);
            intent.putExtra(Constants.GAME_POINT, gamePoint);
            startService(intent);
        }
    }

    class NewFlagLocationReceiver extends ResultReceiver {
        public NewFlagLocationReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == Constants.SUCCESS_RESULT) {
                double[] latLng = resultData.getDoubleArray(Constants.RESULT_DATA_KEY);
                LatLng newFlagLocation = new LatLng(latLng[0], latLng[1]);
                placeFlag(newFlagLocation);
            }
        }
    }


    void placeGamePointFlag(LatLng pos) {
        MarkerOptions newFlag = new MarkerOptions();
        newFlag.position(pos);
        newFlag.title("GamePoint");
        newFlag.icon(BitmapDescriptorFactory.fromResource(R.drawable.game_point));
        theMap.addMarker(newFlag);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("READY", "Map ready!");
        theMap = map;

        zoomGameMap();
        applyMapSettings();
    }

    void startLocationUpdates() {
        Log.d("START", "StartLocationUpdates");
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        requestingLocationUpdates = true;
    }

    void stopLocationUpdates() {
        Log.d("STOP", "StopLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        requestingLocationUpdates = false;
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
        Log.d("SUSPENDED", "Connection suspended!");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("FAILED", "Connection failed!");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("HEJ", "STARTING");
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("PAUSING","PAUSING");
        if(flagsGenerated)
            savePreferences();
        //

        stopLocationUpdates();
    }

    public void savePreferences(){
        //Save data
        Log.i("SAVE", "SAVING!");
        ArrayList<DualValue> dvList = Converter.convertMarkerListToDV(flags);
        SharedPreferences prefs = getSharedPreferences("MARK", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        try {
            editor.putString(Constants.MARKER_LIST, ObjectSerializer.serialize(dvList));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPrefs = getSharedPreferences("MARK",Context.MODE_PRIVATE);
        String pref = sharedPrefs.getString(Constants.MARKER_LIST, null);
        if(pref != null && gameCode == Constants.CONTINUE_GAME_CODE){
            Log.i("D", "CREATING");
            recreating = true;
            flagsGenerated = true;
            generateFlags = false;
        } else {
            Log.i("D", "FirstTime");
            generateFlags = true;
            flags = new ArrayList<>();
        }

        if(googleApiClient.isConnected() && !requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    public void setPreferences(){
        ArrayList<DualValue> dvList;

        SharedPreferences prefs = getSharedPreferences("MARK",Context.MODE_PRIVATE);
        try {
            dvList = (ArrayList<DualValue>) ObjectSerializer.deserialize(prefs.getString(Constants.MARKER_LIST, ObjectSerializer.serialize(new ArrayList<DualValue>())));
            convertDVListToMarkers(dvList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }



    public void convertDVListToMarkers(ArrayList<DualValue> dvList){
        Log.i("HEJ","CONVERTING");
        flags = new ArrayList<>();

        for(DualValue dv : dvList){
            LatLng pos = new LatLng(dv.lat,dv.longi);
            placeFlag(pos);
        }
    }

    //@Override
   // public void onSaveInstanceState(Bundle savedInstanceState) {
     //   super.onSaveInstanceState(savedInstanceState);
       // savePreferences();

//    }

  //  @Override
    //public void onRestoreInstanceState(Bundle savedInstanceState) {
      //  super.onRestoreInstanceState(savedInstanceState);
        //Log.i("HEJ", "RESTORINGSTATE");
        //recreating = true;

    //}

}
