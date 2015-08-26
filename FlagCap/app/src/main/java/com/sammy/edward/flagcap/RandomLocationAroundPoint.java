package com.sammy.edward.flagcap;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.google.android.gms.maps.model.LatLng;

import java.util.Random;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class RandomLocationAroundPoint extends IntentService {

    //public static final String TAG = "RandomLocationAroundPoint";
    protected ResultReceiver receiver;
    public double rangeInMeter = 3000;

    public RandomLocationAroundPoint () {
        super("RandomLocationAroundPoint");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Random random = new Random();

        Location location = intent.getParcelableExtra(Constants.GAME_POINT);

        //http://gis.stackexchange.com/questions/25877/how-to-generate-random-locations-nearby-my-location
        double x0 = location.getLongitude();
        double y0 = location.getLatitude();

        double u = random.nextInt(1);
        double v = random.nextInt(1);
        double w = convertMetersToDegrees(rangeInMeter) * sqrt(u);
        double t = 2 * PI * v;
        double xTemp = w * cos(t);
        double y = w * sin(t);

        //Adjust x(longitude) for the shrinking of the east-west distances
        double x = xTemp / cos(y0);

        LatLng newLocation = new LatLng((y+y0), (x+x0));
        deliverResultToReceiver(Constants.SUCCESS_RESULT, newLocation);
    }

    private double convertMetersToDegrees (double meters) {
        return meters/111300;
    }

    private void deliverResultToReceiver(int resultCode, LatLng location) {
        Bundle bundle = new Bundle();
        //if (resultCode == Constants.SUCCESS_RESULT) {
        double[] latLng = {location.latitude, location.longitude};
        bundle.putDoubleArray(Constants.RESULT_DATA_KEY, latLng);
        //}
        receiver.send(resultCode, bundle);
    }
}
