package com.example.imagingnavigator.function;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode.GeoPoint;


/**
 * Created by zhangxi on 11/1/15.
 * <p>
 * Router of the navigator.
 */
public class Router {

    private static final String TAG = Router.class.getSimpleName();

    private static final int GEOPOTINT_VERSION = 1;

    public GeoPoint locationToGeoPoint(Location location) {
//        BigDecimal million = new BigDecimal(1E6);
//        double latitudeE6 = new BigDecimal(location.getLatitude()).multiply(million).doubleValue();
//        double longtitudeE6 = new BigDecimal(location.getLongitude()).multiply(million).doubleValue();
        double latitude = location.getLatitude();
        double longtitude = location.getLongitude();
        return new GeoPoint(GEOPOTINT_VERSION, latitude, longtitude);
    }


    /**
     * Get the directions Url for url request from google
     *
     * @param origin the start point
     * @param dest   the end point
     * @param mode   driving
     * @return url
     */
    private String getDirectionsUrl(LatLng origin, LatLng dest, String mode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + ","
                + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Travelling Mode
        String tmpMode = "mode=" + mode;
        //String mode = "mode=driving";

        // String waypointLatLng = "waypoints="+"40.036675"+","+"116.32885";

        // 如果使用途径点，需要添加此字段
        // String waypoints = "waypoints=";

        //String parameters = "";
        String parameters;
        // Building the parameters to the web service

        parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + tmpMode;
        // parameters = str_origin + "&" + str_dest + "&" + sensor + "&"
        // + mode+"&"+waypoints;

        // Output format
        // String output = "json";
        String output = "xml";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + parameters;
        System.out.println("getDerectionsURL--->: " + url);
        return url;
    }

    // The best way to get direction and routes you can use the Web Service of Google Maps.
    // It will provide you everything. I have used this in my application.

    // final Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?" + "saddr="+ latitude + "," + longitude + "&daddr=" + latitude + "," + longitude));
//    intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
//    startActivity(intent);

}
