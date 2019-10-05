package com.example.testappfornatlex;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

public class MyLocation implements LocationListener {

    static Location imHere;

    public static int SetUpLocationListener(Context context) // это нужно запустить в самом начале работы программы
    {
        int PERMISSION_LOCATION = 0;
        LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocation();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_LOCATION);
            if (PERMISSION_LOCATION == 1) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        5000,
                        1000,
                        locationListener);
            }
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5000,
                    1000,
                    locationListener);
        }

        imHere = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        return PERMISSION_LOCATION;
    }

    @Override
    public void onLocationChanged(Location location) {
        imHere = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
