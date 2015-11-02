package com.example.imagingnavigator.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.example.imagingnavigator.imagingnavigator.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapBasedViewActivity extends FragmentActivity {

    private static final String TAG = MapBasedViewActivity.class.getSimpleName();

    /**
     * Start activity type for start the CameraBasedViewActivity.
     */
    private static final int CAMERA_BASED_VIEW = 2;

    /**
     * The Google Map object.
     */
    private GoogleMap mMap;

    /**
     *  The LocationManager object.
     */
    private LocationManager locationManager;

    /**
     * The marker in Google Map
     */
    private MarkerOptions markerOpt;
    private CameraPosition cameraPosition;

    private Location location;
    private String bestProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_based_view);

        //create SupportMapFragment object, and get Provider
        initProvider();
        //Obtain the Map Fragment
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        updateToCurLocation(location);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        // set the listener, update the location per 3 seconds(3*1000) automatically or moving more than 8 meters
        locationManager.requestLocationUpdates(bestProvider, 3 * 1000, 8, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateToCurLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

                if (ContextCompat.checkSelfPermission(MapBasedViewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MapBasedViewActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                location = locationManager.getLastKnownLocation(provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                updateToCurLocation(null);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }

    /**
     * initialize the provider
     */
    private void initProvider() {
        //create LocationManager object
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //check if GPS will work
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please open GPS...", Toast.LENGTH_SHORT).show();
            //jump to GPS configuration page
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }


        //list all providers
        List<String> providers = locationManager.getAllProviders();

        Criteria criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, false);

        //get the latest location

        location = locationManager.getLastKnownLocation(bestProvider);

        /**
         * TODO: Handle the exception when we close GPS service or close network
         */


       // System.out.println("latitude:" + location.getLatitude() + ", longitude:" + location.getLongitude());
    }

    /**
     * update to the current location
     */
    private void updateToCurLocation(Location location){
        mMap.clear();
        markerOpt = new MarkerOptions();
        // Add a marker in Sydney
        double dLat = -34.00;
        double dLong = 150.00;

        if(location!=null){
            //get the latitude
            dLat = location.getLatitude();
            //get the longitude
            dLong = location.getLongitude();
        }
        //set the marker
        markerOpt.position(new LatLng(dLat, dLong));
        markerOpt.draggable(false);
        markerOpt.visible(true);
        markerOpt.anchor(0.5f, 0.5f);
        markerOpt.title("current location");
        mMap.addMarker(markerOpt);

        //move the camera to the current location
        cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(dLat,dLong))      //set the center of the map to current location
                .zoom(14)      //map zoom
                .bearing(0)    //Sets the orientation of the camera to east
                .tilt(90)      // Sets the tilt of the camera to 30 degrees
                .build();      // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    /**
     * Start the map based router
     */
    private void startMapBasedRouter(){
        Intent intent = new Intent();
        intent.setClass(MapBasedViewActivity.this, MapBasedRouterActivity.class);
        startActivity(intent);
    }


    /**
     * Callback function for on-click router button
     * Will open map based router
     * */
    public void onClickRouter(View view){
        startMapBasedRouter();
    }

}
