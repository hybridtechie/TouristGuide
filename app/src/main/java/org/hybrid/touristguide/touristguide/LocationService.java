package org.hybrid.touristguide.touristguide;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

/**
 * Created by nithi on 22-05-2017.
 */

public class LocationService implements LocationListener {

    //The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 100; // 10 meters

    //The minimum time beetwen updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    private final static boolean forceNetwork = false;

    private static LocationService instance = null;

    private LocationManager locationManager;
    public Location location;
    public double longitude;
    public double latitude;
    public Boolean isGPSEnabled;
    public Boolean isNetworkEnabled;
    public Boolean locationServiceAvailable;
    public LocationChangedListener locationChangedListener;
    /**
     * Singleton implementation
     * @return
     */
    public static LocationService getLocationManager(Context context)     {
        if (instance == null) {
            instance = new LocationService(context);
        }
        return instance;
    }

    /**
     * Local constructor
     */
    public LocationService( Context context,LocationChangedListener locationChangedListener )     {
        this.locationChangedListener=locationChangedListener;
        initLocationService(context);

    //   LogService.log("LocationService created");
    }
    public LocationService( Context context )     {
        this.locationChangedListener=null;
        initLocationService(context);

        //   LogService.log("LocationService created");
    }

    private LocationService(  )     {


    }
    /**
     * Sets up location service after permissions is granted
     */
    @TargetApi(23)
    private void initLocationService(Context context) {


        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        try   {
            this.longitude = 0.0;
            this.latitude = 0.0;
            this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // Get GPS and network status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (forceNetwork) isGPSEnabled = false;

            if (!isNetworkEnabled && !isGPSEnabled)    {
                // cannot get location
                this.locationServiceAvailable = false;
            }
            //else
            {
                this.locationServiceAvailable = true;

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null)   {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        updateCoordinates();
                    }
                }//end if

                if (isGPSEnabled)  {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null)  {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        updateCoordinates();
                    }
                }
            }
        } catch (Exception ex)  {
           // LogService.log( "Error creating location service: " + ex.getMessage() );

        }
    }

    private void updateCoordinates() {
        Constants.Latitude=latitude;
        Constants.Longitude=longitude;
        locationChangedListener.onLocationchange(null);
    }


    @Override
    public void onLocationChanged(Location location)     {
        Constants.Latitude=location.getLatitude();
        Constants.Longitude=location.getLongitude();
        locationChangedListener.onLocationchange(null);
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