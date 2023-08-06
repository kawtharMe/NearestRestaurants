package com.example.nearestrestaurants;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static org.osmdroid.tileprovider.util.StorageUtils.getStorage;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class LocateRestaurantActivity extends AppCompatActivity  {


    List<Restaurant> restaurants;
    Response response;
    String json;
    JSONObject jsonObject;
    String url;
    double latitude;
    double longitude;

    private MapView myOpenMapView;
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    MyLocation locationTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //handle permissions

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissionsToRequest = ungrantedPermissions(permissions);
        if (permissionsToRequest.size() > 0)
            requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);

        IConfigurationProvider provider = Configuration.getInstance();
        provider.setUserAgentValue(BuildConfig.APPLICATION_ID);
        provider.setOsmdroidBasePath(getStorage());
        provider.setOsmdroidTileCache(getStorage());

        setContentView(R.layout.activity_locate_restaurants);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        myOpenMapView = findViewById(R.id.mapview);
        myOpenMapView.setBuiltInZoomControls(true);
        myOpenMapView.setClickable(true);
        myOpenMapView.getController().setZoom(11);


       // get my position
        locationTrack = new MyLocation(LocateRestaurantActivity.this);

        if (locationTrack.canGetLocation()) {


           longitude = locationTrack.getLongitude();
            latitude = locationTrack.getLatitude();

          //  Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {

            locationTrack.showActivationAlert();
        }

        //center the map on my position
        myOpenMapView.getController().setCenter(new GeoPoint(latitude,longitude));
        //add a marker to my position
       Marker mrk = new Marker(myOpenMapView);
        mrk.setPosition(new GeoPoint(latitude, longitude));
        mrk.setSnippet("My position");

      mrk.setIcon(getDrawable(org.osmdroid.library.R.drawable.ic_menu_mylocation));
        mrk.setAlpha(0.75f);
        mrk.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
        myOpenMapView.getOverlays().add(mrk);



       // get nearest restaurants
        constructQuery();
        //add restaurants to the map
        for(Restaurant r: restaurants) {
            Marker mark = new Marker(myOpenMapView);
            mark.setPosition(new GeoPoint(r.getLatitude(), r.getLongitude()));
            mark.setSnippet(r.getName());
            mark.setAlpha(0.75f);
Log.d("kk","okk");
            mark.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            myOpenMapView.getOverlays().add(mark);
       }
     //   myOpenMapView.invalidate();

    }

    // construct an overpass query to get the nearest restaurants
    private void constructQuery() {
        Log.d("lat",latitude+"");
        // Construct the query
        String query = "[out:json]; " +
                "(" +
                "  node[\"amenity\"=\"restaurant\"](around:100000," + latitude + "," + longitude + ");" +

               ");" +
                " out;";

        // Send the query to the Overpass API
        OkHttpClient client = new OkHttpClient();
        try {
            url = "http://overpass-api.de/api/interpreter?data=" + URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        Request request = new Request.Builder().url(url).build();

        try {
            response = client.newCall(request).execute();

            json = response.body().string();

           jsonObject = new JSONObject(json);
            JSONArray elements = jsonObject.getJSONArray("elements");
            Log.d("tet",elements.toString());
            restaurants = new ArrayList<>();

            for (int i = 0; i < elements.length(); i++) {

                JSONObject element = elements.getJSONObject(i);

                if (!element.has("tags")) {
                    continue;
                }
                JSONObject tags = element.getJSONObject("tags");
                if (!tags.has("amenity")) {
                    continue;
                }
                String amenity = tags.getString("amenity");
                if (!"restaurant".equals(amenity) ) {
                    continue;
                }
                String name = tags.optString("name", "Unnamed");
                String address = tags.optString("addr:full", "");
                double lat = element.getDouble("lat");
                double lon = element.getDouble("lon");
                Restaurant restaurant = new Restaurant(name, address, lat, lon);
                Log.d("tesst",restaurant.getName());
                restaurants.add(restaurant);
            }
        } catch (JSONException|IOException e) {
            throw new RuntimeException(e);
        }
    }





private ArrayList ungrantedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
        if (!hasPermission((String) perm)) {
        result.add(perm);
        }
        }

        return result;
        }

private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }
        }
        return true;
        }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {



                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);

                                        }
                                    });
                            return;
                        }


                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LocateRestaurantActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

        }
