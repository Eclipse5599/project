package com.sammy.edward.flagcap;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
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

import java.util.HashMap;

public class GameActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, OnMapReadyCallback {

    private GoogleApiClient googleApiClient;
    private GoogleMap theMap;
    
    Location gamePoint;
    private NewFlagLocationReceiver resultReceiver;
    int currentPointsCollected = 0;
    private Location currentLocation;
    private LatLng currentCoordinates;
    private LocationRequest locationRequest;
    private Boolean requestingLocationUpdates = false;

    private HashMap<String, Marker> flags;

    TextView pointWindow;
    private SeekBar zoomLevel;
    private int currentZoomLevel;

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
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        flags = new HashMap();

        zoomLevel = (SeekBar)findViewById(R.id.game_zoom_level);
        currentZoomLevel = zoomLevel.getProgress()+14;
        zoomLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentZoomLevel = progress+14;
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

    @Override
    public void onLocationChanged(Location location) {
        Log.d("CHANGED", "Location changed!");
        currentLocation = location;
        updateUI();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("READY", "Map ready!");
        theMap = map;

        LatLng latlng = new LatLng(59.4633094, 17.9470539);
        placeFlag(latlng);

        zoomGameMap();
        applyMapSettings();
    }

    void zoomGameMap() {
        theMap.moveCamera(CameraUpdateFactory.zoomTo(currentZoomLevel));
    }

    void applyMapSettings () {
        UiSettings mapSettings = theMap.getUiSettings();

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
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (gamePoint == null) {
            gamePoint = currentLocation;
        }

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
        fetchNewFlag();
        pointWindow.setText("" + currentPointsCollected);
    }

    void placeFlag(LatLng pos) {
        MarkerOptions newFlag = new MarkerOptions();
        newFlag.position(pos);
        newFlag.title("FlagMarker" + flags.size());
        newFlag.icon(BitmapDescriptorFactory.fromResource(R.drawable.flag));

        flags.put(newFlag.getTitle(), theMap.addMarker(newFlag));
        currentPointsCollected = flags.size();
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
                //Log.i("GameActivity", "Requested location is: (" + latLng[0] + ", " + latLng[1] + ")");
                placeFlag(newFlagLocation);
            }
        }
    }
}
