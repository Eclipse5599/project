package extra;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.sammy.edward.flagcap.Constants;
import com.sammy.edward.flagcap.R;
import com.sammy.edward.flagcap.RandomLocationAroundPoint;

import java.util.ArrayList;
import java.util.Iterator;

public class GameActivityFog extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, OnMapReadyCallback {

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
    private ArrayList<Marker> fogs;
    private TextView pointWindow;
    private SeekBar zoomLevel;
    private int currentZoomLevel;
    private boolean generateFlags = true;
    private boolean generateFogs = true;
    private final int AMOUNT_OF_FLAGS_WISHED = 500;
    private final int AMOUNT_OF_FOGS_WISHED = 1000;
    private final double PICK_RADIUS = 0.008; //is 10 meters
    private final int UPDATE_RATE = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
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

        flags = new ArrayList<>();

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

        Context context = getApplicationContext();
        CharSequence text = "LOCATION UPDATE!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        Log.d("CHANGED", "Location changed!");
        currentLocation = location;
        updateUI();
        pickFlagIfClose();
        if(generateFlags){
            generateFlagsAroundPlayer();
            generateFlags = false;
            //processThread.start();
        }
    }

    /**
     * Iterates through all flags, deletes them and increments points if player is close enough
     */
    public void pickFlagIfClose(){
        Log.d("TAG", "PICK!");

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

            double flagLong = position.longitude;
            double flagLat = position.latitude;
            if(Mathematics.getDistanceFromLatLonInKm(currentCoordinates.latitude, currentCoordinates.longitude, flagLat ,flagLong) < PICK_RADIUS){
                currentMark.remove();
                it.remove();
                currentPointsCollected++;
            }
        }
    }

    public void generateFlagsAroundPlayer(){
        for(int i = 0; i < AMOUNT_OF_FLAGS_WISHED; i++){
            fetchNewFlag();
        }
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


        if (gamePoint == null) {
            gamePoint = currentLocation;
            placeGamePointFlag(new LatLng(gamePoint.getLatitude(), gamePoint.getLongitude()));
        }

        if (currentLocation != null) {
            updateUI();
        }
    }

    void updateUI() {
        Log.d("UPDATEUI", "UpdateUI");
        if(currentLocation == null){
            Context context = getApplicationContext();
            CharSequence text = "SUCCESS PICK!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        currentCoordinates = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        theMap.moveCamera(CameraUpdateFactory.newLatLng(currentCoordinates));
        pointWindow.setText("" + currentPointsCollected);
    }

    void placeFlag(LatLng pos) {
        Log.d("START", "PLACEFLAG");
        MarkerOptions newFlag = new MarkerOptions();
        newFlag.position(pos);
        newFlag.title("FlagMarker" + flags.size());
        newFlag.icon(BitmapDescriptorFactory.fromResource(R.drawable.flag));
        flags.add(theMap.addMarker(newFlag));

    }

    public void placeFog(LatLng pos){
        Log.d("START", "PLACEFOG");
        MarkerOptions newFog = new MarkerOptions();
        newFog.position(pos);
        newFog.title("FlagMarker" + flags.size());
        newFog.icon(BitmapDescriptorFactory.fromResource(R.drawable.fog));
        flags.add(theMap.addMarker(newFog));
    }

    void fetchNewFlag() {
        Log.d("START", "Newflag");
        if (gamePoint != null) {
            Intent intent = new Intent(this, RandomLocationAroundPoint.class);
            intent.putExtra(Constants.GAME_RECEIVER, resultReceiver);
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
                double[] latLng = resultData.getDoubleArray(Constants.GAME_RESULT_DATA_KEY);
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

}
