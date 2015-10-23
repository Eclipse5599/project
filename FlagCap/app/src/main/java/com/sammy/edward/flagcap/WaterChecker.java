package com.sammy.edward.flagcap;


import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WaterChecker extends IntentService {

    protected ResultReceiver receiver;

    public WaterChecker() {
        super("WaterChecker");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LatLng location = intent.getParcelableExtra(Constants.WATER_POINT);
        receiver = intent.getParcelableExtra(Constants.WATER_RECEIVER);
        Log.d("WaterChecker", "Starting a water check on location: {" + location.latitude + ", " + location.longitude + "}");

        boolean result = isPointWater(location);
        deliverResultToReceiver(Constants.SUCCESS_RESULT, result);
    }

    public boolean isPointWater(LatLng point) {
        double latitude = point.latitude;
        double longitude = point.longitude;
        String latData = "lat=" + latitude;
        String longData = "long=" + longitude;
        String GETQuery = "?" + latData + "&" + longData;

        try {
            //Create a connection to the url.
            URL url = new URL("http://people.dsv.su.se/~edha0441/waterchecker/waterchecker.php" + GETQuery);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            //Read the result, will be a string with either the value true or false, then disconnect.
            String result = reader.readLine();
            urlConnection.disconnect();

            if (result.equals("true")) {
                //The point is water.
                Log.d("WaterChecker", "The point: " + point + " is water.");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //The point is not water.
        Log.d("WaterChecker", "The point: " + point + " is not water.");
        return false;
    }

    private void deliverResultToReceiver(int resultCode, boolean isWater) {
        Log.d("WaterChecker", "Finishing a water check, with the result: " + isWater);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.WATER_RESULT_DATA_KEY, isWater);
        receiver.send(resultCode, bundle);
    }
}
