package com.example.imagingnavigator.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.imagingnavigator.R;
import com.example.imagingnavigator.function.Router;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
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

    private MarkerOptions markerOptions;
    private LatLng latLng;

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

        double dLat = 43.0054446;
        double dLong = -87.9678884;

        if(location!=null){
            //get the latitude
            dLat = location.getLatitude();
            //get the longitude
            dLong = location.getLongitude();
        }
        drawRoute(new LatLng(dLat, dLong), new LatLng(42.9257104d, -88.0508355d), "driving");

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

        System.out.println("-------------");
        Log.d(TAG, "----");

        //initialize search button
        searchMap();

//        double dLat = 43.0054446;
//        double dLong = -87.9678884;
//
//        if(location!=null){
//            //get the latitude
//            dLat = location.getLatitude();
//            //get the longitude
//            dLong = location.getLongitude();
//        }
//        drawRoute(new LatLng(dLat, dLong), new LatLng(dLat + 20d, dLong - 20d), "driving");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search_list_activity,menu);
        return true;
    }

    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {
        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;
            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            if (addresses == null || addresses.size() == 0) {
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }
            // Clears all the existing markers on the map
            mMap.clear();
            // Adding Markers on Google Map for each matching address
            for (int i = 0; i < addresses.size(); i++) {
                Address address = (Address) addresses.get(i);
                // Creating an instance of GeoPoint, to display in Google Map
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getCountryName());
                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);
                mMap.addMarker(markerOptions);
                // Locate the first location
                if (i == 0)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
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
     * Initialize the search function
     */
    private void searchMap(){
        // Getting reference to btn_find of the layout activity_main
        Button btn_find = (Button) findViewById(R.id.btn_find);
        // Defining button click event listener for the find button
        View.OnClickListener findClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting reference to EditText to get the user input location
                EditText etLocation = (EditText) findViewById(R.id.et_location);
                // Getting user input location
                String location = etLocation.getText().toString();
                if(location!=null && !location.equals("")){
                    new GeocoderTask().execute(location);
                }
            }
        };
        // Setting button click event listener for the find button
        btn_find.setOnClickListener(findClickListener);
    }



    /**
     * update to the current location
     */
    private void updateToCurLocation(Location location){
        mMap.clear();
        markerOpt = new MarkerOptions();
        // Add a marker in Sydney
        double dLat = 43.0054446;
        double dLong = -87.9678884;

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

    private void drawRoute(LatLng origin, LatLng dest, String mode) {
        Router router = new Router(mMap);
        router.drawRoute(origin, dest, mode);
    }

    /**
     * Callback function for on-click router button
     * Will open map based router
     * */
    public void onClickRouter(View view){
        startMapBasedRouter();
    }

}
