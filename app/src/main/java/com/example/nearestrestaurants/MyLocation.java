package com.example.nearestrestaurants;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class MyLocation extends Service implements LocationListener {

    private final Context mContext;


    boolean checkGPS = false;


    boolean ok = false;
    boolean checkNetwork = false;



    Location loc;
    double latitude;
    double longitude;

   //min distance for update
    private static final long minDist = 10;

// min time between updates
    private static final long minTime = 2000 * 60 * 1;
    protected LocationManager locationManager;

    public MyLocation(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

    private Location getLocation() {

        try {
            //create a location manager
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // get GPS status
            checkGPS = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // get network provider status
            checkNetwork = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!checkGPS && !checkNetwork) {
                Toast.makeText(mContext, "No Service Provider", Toast.LENGTH_LONG).show();
            } else {
                this.ok = true;

                // if GPS Enabled get lat/long using GPS Services
                if (checkGPS) {

                // check needed permissions
                    if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        //  request the missing permissions

                    }

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDist, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return loc;
    }

    public double getLongitude() {

        if (loc != null) {
            longitude = loc.getLongitude();
        }
        return longitude;
    }

    public double getLatitude() {
        if (loc != null) {
            latitude = loc.getLatitude();
        }
        return latitude;
    }

    public boolean canGetLocation() {

        return this.ok;
    }

    public void showActivationAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);


        alertDialog.setTitle("GPS is not Enabled!");

        alertDialog.setMessage("Do you want to turn on GPS?");


        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });


        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alertDialog.show();
    }


    public void stopListener() {
        if (locationManager != null) {


            locationManager.removeUpdates(MyLocation.this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
