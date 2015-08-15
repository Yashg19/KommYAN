package com.kommunity;

import java.util.*;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.RadioGroup.*;
import android.content.SharedPreferences;
import android.graphics.*;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.common.api.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.location.*;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class MapsActivity extends FragmentActivity {

    public static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient locationClient;
    private Location currentLocation;
    private static final double OFFSET_CALCULATION_INIT_DIFF = 1.0;
    private static final float METERS_PER_FEET = 0.3048f;
    private static final float OFFSET_CALCULATION_ACCURACY = 0.01f;
    private float radius;
    private static final String KEY_SEARCH_DISTANCE = "searchDistance";
    private static final float DEFAULT_SEARCH_DISTANCE = 250.0f;
    double latitude = 43.071436;
    double longitude = -89.407043;
    private LocationRequest locationRequest;
    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // A fast interval ceiling
    private static final int FAST_CEILING_IN_SECONDS = 1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;

    // A fast ceiling of update intervals, used when the app is visible
    private static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * FAST_CEILING_IN_SECONDS;
    private static SharedPreferences preferences;
    private ParseGeoPoint point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("userdata", Context.MODE_PRIVATE);
        radius = getSearchDistance();
        setContentView(R.layout.activity_maps);

        //YG
        locationRequest = LocationRequest.create();

        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);


        setUpMapIfNeeded();
    }

    private Location getLocation() {
        // If Google Play Services is available
            return LocationServices.FusedLocationApi.getLastLocation(locationClient);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                mMap.getUiSettings().setZoomControlsEnabled(true);
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        final Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").snippet("Snippet"));

        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").snippet("Snippet"));
        //Enable My Location Layer of Google Map
        mMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, true);

        Location myLocation = locationManager.getLastKnownLocation(provider);

        if(myLocation != null){
            latitude = myLocation.getLatitude();
            longitude = myLocation.getLongitude();
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng latLng = new LatLng(latitude-5, longitude-5);

        LatLng zoom = new LatLng(latitude-5, longitude-5);

        final LatLng zoom2 = new LatLng(latitude+5, longitude+5);

        final LatLngBounds bounds = new LatLngBounds(zoom, zoom2);
        //mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,2));

        //mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(5));

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                //LinearLayout mapContainer = (LinearLayout) findViewById(R.id.map);
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5), new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        try{
                            mMap.wait(5000);
                        }
                        catch(Exception e) {

                        }
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), new GoogleMap.CancelableCallback() {
                            public void onFinish(){
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
                            }

                            public void onCancel(){

                            }
                        });
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                mMap.setOnCameraChangeListener(null);
            }
        });

        //mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        LatLng latLng1 = new LatLng(latitude, longitude);
        CircleOptions circleOptions = new CircleOptions()
            .center(latLng1)
            .radius(1000)
            .fillColor(Color.GREEN)
            .strokeColor(Color.BLACK)
            .strokeWidth(5);
        mMap.addCircle(circleOptions);

        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are Here!").snippet("Consider Yourself Located"));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                SupportMapFragment mapContainer = (SupportMapFragment) (getSupportFragmentManager().findFragmentById(R.id.map));
                mapContainer.getView().setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 5f ));
                return false;
            }
        });

    }

    LatLngBounds calculateBoundsWithCenter(LatLng myLatLng) {
        // Create a bounds
        LatLngBounds.Builder builder = LatLngBounds.builder();

        // Calculate east/west points that should to be included
        // in the bounds
        double lngDifference = calculateLatLngOffset(myLatLng, false);
        LatLng east = new LatLng(myLatLng.latitude, myLatLng.longitude + lngDifference);
        builder.include(east);
        LatLng west = new LatLng(myLatLng.latitude, myLatLng.longitude - lngDifference);
        builder.include(west);

        // Calculate north/south points that should to be included
        // in the bounds
        double latDifference = calculateLatLngOffset(myLatLng, true);
        LatLng north = new LatLng(myLatLng.latitude + latDifference, myLatLng.longitude);
        builder.include(north);
        LatLng south = new LatLng(myLatLng.latitude - latDifference, myLatLng.longitude);
        builder.include(south);

        return builder.build();
    }

    private double calculateLatLngOffset(LatLng myLatLng, boolean bLatOffset) {
        // The return offset, initialized to the default difference
        double latLngOffset = OFFSET_CALCULATION_INIT_DIFF;
        // Set up the desired offset distance in meters
        float desiredOffsetInMeters = radius * METERS_PER_FEET;
        // Variables for the distance calculation
        float[] distance = new float[1];
        boolean foundMax = false;
        double foundMinDiff = 0;
        // Loop through and get the offset
        do {
            // Calculate the distance between the point of interest
            // and the current offset in the latitude or longitude direction
            if (bLatOffset) {
                Location.distanceBetween(myLatLng.latitude, myLatLng.longitude, myLatLng.latitude
                        + latLngOffset, myLatLng.longitude, distance);
            } else {
                Location.distanceBetween(myLatLng.latitude, myLatLng.longitude, myLatLng.latitude,
                        myLatLng.longitude + latLngOffset, distance);
            }
            // Compare the current difference with the desired one
            float distanceDiff = distance[0] - desiredOffsetInMeters;
            if (distanceDiff < 0) {
                // Need to catch up to the desired distance
                if (!foundMax) {
                    foundMinDiff = latLngOffset;
                    // Increase the calculated offset
                    latLngOffset *= 2;
                } else {
                    double tmp = latLngOffset;
                    // Increase the calculated offset, at a slower pace
                    latLngOffset += (latLngOffset - foundMinDiff) / 2;
                    foundMinDiff = tmp;
                }
            } else {
                // Overshot the desired distance
                // Decrease the calculated offset
                latLngOffset -= (latLngOffset - foundMinDiff) / 2;
                foundMax = true;
            }
        } while (Math.abs(distance[0] - desiredOffsetInMeters) > OFFSET_CALCULATION_ACCURACY);
        return latLngOffset;
    }

    public static float getSearchDistance() {
        return preferences.getFloat(KEY_SEARCH_DISTANCE, DEFAULT_SEARCH_DISTANCE);
    }

    public void savedata(View v){
        EditText et = (EditText) findViewById(R.id.editText);
        EditText etx = (EditText) findViewById(R.id.editText2);

        ParseObject testObject = new ParseObject("sosObject");
        point = new ParseGeoPoint(latitude, longitude);
        testObject.put("location", point);
        testObject.put("sub", et.getText().toString());
        testObject.put("desc", etx.getText().toString());
        testObject.saveInBackground();
        try {
            wait(50);
        }
        catch (Exception e){
            //
        }
        startActivity(new Intent(getApplicationContext(), dashboard.class));

    }

}
