package com.kommunity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class TitleTilePopUp extends ActionBarActivity {


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient locationClient;
    private Location currentLocation;
    private static final double OFFSET_CALCULATION_INIT_DIFF = 1.0;
    private static final float METERS_PER_FEET = 0.3048f;
    private static final float OFFSET_CALCULATION_ACCURACY = 0.01f;
    private float radius;
    private static final String KEY_SEARCH_DISTANCE = "searchDistance";
    private static final float DEFAULT_SEARCH_DISTANCE = 250.0f;
    double latitude = storage.latitude;
    double longitude = storage.longitude;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_tile_pop_up);
        View v = findViewById(android.R.id.content);
        TextView header = (TextView) v.findViewById(R.id.thisIsHeader);
        TextView content = (TextView) v.findViewById(R.id.thisIsContent);
        header.append(storage.header);
        content.append(storage.content);


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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_title_tile_pop_up, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_back:
                startActivity(new Intent(getApplicationContext(), dashboard.class));
                // Add the functionality to switch
                // to the google maps page
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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

        /*LatLng latLng1 = new LatLng(latitude, longitude);
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng1)
                .radius(1000)
                .fillColor(Color.GREEN)
                .strokeColor(Color.BLACK)
                .strokeWidth(5);
        mMap.addCircle(circleOptions);*/

        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are Here!").snippet("Consider Yourself Located"));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                SupportMapFragment mapContainer = (SupportMapFragment) (getSupportFragmentManager().findFragmentById(R.id.map));
                mapContainer.getView().setLayoutParams(new LinearLayout.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT, 5f ));
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
    public void returnToDash(View v){
        startActivity(new Intent(getApplicationContext(), dashboard.class));
    }
}
