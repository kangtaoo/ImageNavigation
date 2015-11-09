package com.example.imagingnavigator.function;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.barcode.Barcode.GeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by zhangxi on 11/1/15.
 * <p>
 * Router of the navigator.
 */
public class Router {

    private static final String TAG = Router.class.getSimpleName();

    private static final int GEOPOTINT_VERSION = 1;

    private final GoogleMap mMap;

    public Router(GoogleMap map) {
        mMap = map;
    }

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
        String output = "json";
        // String output = "xml";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + parameters;
        System.out.println("getDerectionsURL--->: " + url);
        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        //URL url = new URL(strUrl);
        URL url = new URL(strUrl);

        String data = "";

        // Creating an http connection to communicate with url
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        // Connecting to url
        urlConnection.connect();

        // Reading data from url
        InputStream iStream = urlConnection.getInputStream();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception download url", e.toString());
            iStream.close();
            urlConnection.disconnect();
        }
        iStream.close();
        urlConnection.disconnect();
        System.out.println("url:" + strUrl + "---->   downloadurl:" + data);
        return data;
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
                System.out.println("do in background:" + routes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            //MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                //points = new ArrayList<LatLng>();
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(3);

                // Changing the color polyline according to the mode
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    public class DirectionsJSONParser {
        /**
         * Receives a JSONObject and returns a list of lists containing latitude and
         * longitude
         */
        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

            List<List<HashMap<String, String>>> routes = new ArrayList<>();
//            JSONArray jRoutes = null;
//            JSONArray jLegs = null;
//            JSONArray jSteps = null;
            JSONArray jRoutes;
            JSONArray jLegs;
            JSONArray jSteps;

            try {

                jRoutes = jObject.getJSONArray("routes");

                /** Traversing all routes */
                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
//                    List path = new ArrayList<HashMap<String, String>>();
                    List<HashMap<String, String>> path = new ArrayList<>();

                    /** Traversing all legs */
                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        /** Traversing all steps */
                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline;
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps
                                    .get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            /** Traversing all points */
                            for (int l = 0; l < list.size(); l++) {
//                                HashMap<String, String> hm = new HashMap<String, String>();
//                                hm.put("lat",
//                                        Double.toString(((LatLng) list.get(l)).latitude));
//                                hm.put("lng",
//                                        Double.toString(((LatLng) list.get(l)).longitude));
//                                path.add(hm);
                                HashMap<String, String> hm = new HashMap<>();
                                hm.put("lat",
                                        Double.toString((list.get(l)).latitude));
                                hm.put("lng",
                                        Double.toString((list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return routes;
        }

        /**
         * Method to decode polyline points Courtesy :
         * jeffreysambells.com/2010/05/27
         * /decoding-polylines-from-google-maps-direction-api-with-java
         * */
        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }
    }

    public void drawRoute(LatLng origin, LatLng dest, String mode) {
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(getDirectionsUrl(origin, dest, mode));
    }

}
