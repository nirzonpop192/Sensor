package com.primetech.android.sensor;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.primetech.android.sensor.annotation.LocationModel;
import com.primetech.android.sensor.network.ConnectionDetector;

/**
 * Created by TD-Android on 1/11/2018.
 */

public class GPSTrackerGoogleApiClient extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int REQUEST_READWRITE_STORAGE = 1001;
    private Context mContext;
    private LocationModel mRoad;

    private Location mLastLocation;
    private float accuracy = 10000;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec


    private Location location; // location
    private double latitude; // latitude
    private double longitude; // longitude

   // private GpsNotificationUser gpsNotificationUser;

    private LocationManager locationManager;

    boolean hasGps;
    private Location myLocation;
    public boolean isRoadSurveyStared;


    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

    public GPSTrackerGoogleApiClient() {
    }

    public GPSTrackerGoogleApiClient(Context mcContext, Activity activity, LocationModel road) {
        this.mContext = mcContext;
        this.mRoad = road;
        Log.e("Service Start", "Service Start");
        boolean checkPlayServices = checkPlayServices();
        Log.e("checkPlayServices", "" + checkPlayServices);

        //gpsNotificationUser = (GpsNotificationUser) activity;

        boolean checkRuntimePersion = checkRuntimePersion();
        if (checkRuntimePersion == true) {
            // star gps
            if (checkPlayServices == true) {
                mRequestingLocationUpdates = true;
                buildGoogleApiClient();
                createLocationRequest();
                startLocationUpdates();
            } else {
                // now user notifications no google play serve found

           /*     if (gpsNotificationUser != null) {
                    gpsNotificationUser.permissioinGooglePlayServerNotFoud();
                }*/
                // implement old framework api
                setUp();

            }

        } else {

     /*       gpsNotificationUser.permissioinNotFound();*/

        }


    }

    private boolean checkRuntimePersion() {

        int permissionCheck1 = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck2 = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_READWRITE_STORAGE);

          /*  if (gpsNotificationUser != null) {
                gpsNotificationUser.permissioinNotFound();
                return false;
            }*/

        }

        return true;
    }

    // check google play sevices...
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        Log.e("Request code", "" + resultCode);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {


                // show the user notification
                return false;

            } else {


                // this device not support...
            }

            return false;
        }

        return true;
    }


    protected void startLocationUpdates() {

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    public void stopLocationUpdates() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                return;
            }
            locationManager.removeUpdates(this);
        }


    }


    private void togglePeriodicLocationUpdates() {

        if (mRequestingLocationUpdates == true) {
            startLocationUpdates();
        } else {

            stopLocationUpdates();

        }
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(GPSConstantVariable.UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(GPSConstantVariable.FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(GPSConstantVariable.DISPLACEMENT);


    }

    @Override
    public void onLocationChanged(Location location) {

        Toast.makeText(mContext, "A " + location.getAccuracy(), Toast.LENGTH_SHORT).show();

        saveRowData(location);

        float accuracyTemp = location.getAccuracy();
        Log.e("--onLocationChanged--", "" + location.getAccuracy());
        Log.e("-onLocationChanged---", "" + location.getProvider());


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String location_accuracy = prefs.getString("location_accuracy", "10");
        Float defaltAccuracy = Float.parseFloat(location_accuracy);

        if (accuracyTemp <= defaltAccuracy) {

            accuracy = accuracyTemp;
            updateUI(location);
        }


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


    @Override
    public void onConnected(Bundle arg0) {

        displayLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        Log.e("onConnected", "onConnected");

    }

    @Override
    public void onConnectionSuspended(int arg0) {

        mGoogleApiClient.connect();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {


    }

//   * Method to display the location on UI
//   * */

    private void displayLocation() {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {

            updateUI(location);
            //lblLocation.setText(latitude + ", " + longitude);

        } else {

            // lblLocation.setText("(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void updateUI(Location location) {


        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Toast.makeText(mContext, "accuracy" + accuracy, Toast.LENGTH_LONG).show();
            Toast.makeText(mContext, "latitude" + latitude, Toast.LENGTH_LONG).show();
            Toast.makeText(mContext, "longitude" + longitude, Toast.LENGTH_LONG).show();

            Log.e("update l ", "" + location.getLatitude());
            Log.e("Update L", "" + location.getLongitude());
            Log.e("Update A", "" + location.getAccuracy());


            savaLocationAccuarcyData(location);




        }


    }


    private String getDeviceUUID(Context context) {
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
            return tManager.getDeviceId();
        else
            return "";
    }

    private void savaLocationAccuarcyData(Location location) {

  /*     // LocationDataWithAccuarcy locationDataWithAccuarcy = new LocationDataWithAccuarcy();

        locationDataWithAccuarcy.setRoadId(this.mRoad.getRoad_id());
        locationDataWithAccuarcy.setAccuracy(location.getAccuracy());
        locationDataWithAccuarcy.setLatitude(location.getLatitude());
        locationDataWithAccuarcy.setLongitude(location.getLongitude());
        locationDataWithAccuarcy.setAltitude(location.getAltitude());
        locationDataWithAccuarcy.setDataAnd*/
       // Time(DateUtility.getCurrentTime());
//        locationDataWithAccuarcy.save();

        Toast.makeText(mContext, "accuracy " + location.getAccuracy(), Toast.LENGTH_LONG).show();
//        Toast.makeText(mContext, "latitude "+location.getLatitude(), Toast.LENGTH_LONG).show();
//        Toast.makeText(mContext, "longitude "+location.getLongitude(), Toast.LENGTH_LONG).show();

    }

    private void saveRowData(Location location) {

      /*  LocationDataWithoutAccuarcy locationDataWithoutAccuarcy = new LocationDataWithoutAccuarcy();

        locationDataWithoutAccuarcy.setRoadId(this.mRoad.getRoad_id());
        locationDataWithoutAccuarcy.setAccuracy(location.getAccuracy());
        locationDataWithoutAccuarcy.setLatitude(location.getLatitude());
        locationDataWithoutAccuarcy.setLongitude(location.getLongitude());
        locationDataWithoutAccuarcy.setAltitude(location.getAltitude());
        locationDataWithoutAccuarcy.setDataAndTime(DateUtility.getCurrentTime());
        locationDataWithoutAccuarcy.setRowData(true);

        locationDataWithoutAccuarcy.save();*/


    }

    public void setUp() {

        PackageManager pm = mContext.getPackageManager();
        hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        if (hasGps == true && ConnectionDetector.isNetworkConnected(mContext) == false) {


            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!gpsEnabled) {

            }

        }
        // check internet conenction

        Location gpsLocation = null;

        // / locationManager.removeUpdates(listener);
        Location networkLocation = null;

        gpsLocation = requestUpdateFromProvider(LocationManager.GPS_PROVIDER);
        networkLocation = requestUpdateFromProvider(LocationManager.NETWORK_PROVIDER);

        if (gpsLocation != null && networkLocation != null) {
            myLocation = getBetterLocation(gpsLocation, networkLocation);
            updateUI(myLocation);

        } else if (gpsLocation != null) {
            updateUI(gpsLocation);

        } else if (networkLocation != null) {
            updateUI(networkLocation);

        } else {
            // no data

            // Toast.makeText(getActivity(), "No data Found",
            // Toast.LENGTH_LONG).show();
            // showMessageComfirmeAlert();

        }

    }

    public Location requestUpdateFromProvider(String provider) {
        Location location = null;

        if (locationManager.isProviderEnabled(provider)) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return null;
            }
            locationManager.requestLocationUpdates(provider, GPSConstantVariable.UPDATE_INTERVAL_long, GPSConstantVariable.DISPLACEMENT_flat, (android.location.LocationListener) this);
            location = locationManager.getLastKnownLocation(provider);

        } else {

            // showSettingsAlert();

        }
        return location;
    }


    com.google.android.gms.location.LocationListener locationListener = new com.google.android.gms.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            updateUI(location);


        }
    };

    private Location getBetterLocation(Location newLocation,
                                       Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return newLocation;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > 20000;
        boolean isSignificantlyOlder = timeDelta < 20000;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use
        // the new location
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return newLocation;
            // If the new location is more than two minutes older, it must be
            // worse
        } else if (isSignificantlyOlder) {
            return currentBestLocation;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation
                .getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;

        // Determine location quality using a combination of timeliness and
        // accuracy
        if (isMoreAccurate) {
            return newLocation;
        } else if (isNewer && !isLessAccurate) {
            return newLocation;
        }
        return currentBestLocation;
    }

   /* public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }*/


}
