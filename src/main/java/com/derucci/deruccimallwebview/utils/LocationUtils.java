package com.derucci.deruccimallwebview.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Author: 林雄军
 * Description: Description
 * Date: 2023/6/6
 */
public class LocationUtils {

    public static int REQUEST_LOCATION_CODE = 111;

    @SuppressLint("MissingPermission")
    public static LatLng getLatLng(Context context) {
        if (!EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            EasyPermissions.requestPermissions((Activity) context, "获取定位", REQUEST_LOCATION_CODE, perms);
        }
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = null;
        if (provider != null) {
            location = locationManager.getLastKnownLocation(provider);
        }
        if (location != null) {
            return new LatLng(location.getLatitude(), location.getLongitude());
        }
        return null;
    }
}