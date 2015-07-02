package com.sammy.edward.flagcap;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddressLookup extends IntentService {

    public static final String TAG = "AddressLookup";
    protected ResultReceiver receiver;

    public AddressLookup(){
        super("AddressLookup");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        receiver = intent.getParcelableExtra(Constants.RECIEVER);
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); //Get a single address from coordinates
        } catch(IOException ioException) { //Network or other I/O problems
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch(IllegalArgumentException illegalArgumentException) { //Invalid latitude or longitude value
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " + "Latitude = " + location.getLatitude() + ", Longitude = " + location.getLongitude(), illegalArgumentException);
        }



        if (addresses == null || addresses.size() == 0) { //No addresses were found
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();


            //Fetch address lines, join them, send to thread
            for (int i = 0 ; i < address.getMaxAddressLineIndex() ; i++) {
                addressFragments.add(address.getAddressLine(i));
            }

            Log.i(TAG, getString(R.string.address_found));
            deliverResultToReceiver(Constants.SUCCESS_RESULT, TextUtils.join(System.getProperty("line.separator"), addressFragments));
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        receiver.send(resultCode, bundle);
    }
}
