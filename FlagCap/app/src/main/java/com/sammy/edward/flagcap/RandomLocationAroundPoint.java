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
    public final double RANGE_IN_METER = 2000;

    public RandomLocationAroundPoint () {
        super("RandomLocationAroundPoint");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Random random = new Random();

        Location location = intent.getParcelableExtra(Constants.GAME_POINT);
        receiver = intent.getParcelableExtra(Constants.GAME_RECEIVER);

        //http://gis.stackexchange.com/questions/25877/how-to-generate-random-locations-nearby-my-location
        double x0 = location.getLongitude();
        double y0 = location.getLatitude();

        double u = random.nextDouble();
        double v = random.nextDouble();
        //To solve the bug where flags were placed in an ellipse instead of a circle
        double wx = convertMetersToDegrees(RANGE_IN_METER) * sqrt(u) * 2;
        double wy = convertMetersToDegrees(RANGE_IN_METER) * sqrt(u);
        double t = 2 * PI * v;
        double xTemp = wx * cos(t);
        double y = wy * sin(t);

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
        bundle.putDoubleArray(Constants.GAME_RESULT_DATA_KEY, latLng);
        //}
        receiver.send(resultCode, bundle);
    }
}
