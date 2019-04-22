package com.shareefoo.pubgcompanion.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Looper;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.shareefoo.pubgcompanion.BuildConfig;
import com.shareefoo.pubgcompanion.R;
import com.shareefoo.pubgcompanion.data.SpManager;
import com.shareefoo.pubgcompanion.model.map.PlayerLocation;

import java.text.DateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import timber.log.Timber;

public class NearbyPlayersActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    private String mLastUpdateTime;

//    private Boolean mRequestingLocationUpdates;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final int PERMISSION_REQUEST_ACCESS_LOCATION = 10;

    private SpManager spManager;
    private DatabaseReference mDatabase;
    private DatabaseReference mPlayersReference;
    private ValueEventListener mPlayersListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_players);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        spManager = SpManager.getInstance(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mPlayersReference = FirebaseDatabase.getInstance().getReference().child("players");

        mPlayersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PlayerLocation playerLocation = dataSnapshot.getValue(PlayerLocation.class);
                if (playerLocation != null) {
                    LatLng myLocation = new LatLng(playerLocation.playerLatitude, playerLocation.playerLongitude);
                    mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Timber.w(databaseError.toException(), "onCancelled");
                Toast.makeText(NearbyPlayersActivity.this, "Failed to load players.", Toast.LENGTH_LONG).show();
            }
        });

        init();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new PermissionListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Timber.d("onCreate: Permission granted");

                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(NearbyPlayersActivity.this, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        // Got last known location. In some rare situations this can be null.
                                        if (location != null) {
                                            Timber.d("onSuccess: Latitude: %s", location.getLatitude());
                                            Timber.d("onSuccess: Longitude: %s", location.getLongitude());

                                            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                            mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

                                            int beginIndex = spManager.getString(SpManager.KEY_PLAYER_ID, "").indexOf(".") + 1;
                                            int endIndex = spManager.getString(SpManager.KEY_PLAYER_ID, "").length();

                                            String playerId = spManager.getString(SpManager.KEY_PLAYER_ID, "").substring(beginIndex, endIndex);
                                            String playerName = spManager.getString(SpManager.KEY_PLAYER_NAME, "");

                                            PlayerLocation playerLocation = new PlayerLocation(playerName, location.getLatitude(), location.getLongitude());

                                            mDatabase.child("players").child(playerId).setValue(playerLocation)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Timber.d("onSuccess: Data has been written successfully");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Timber.d("onFailure: Faild to write data");
                                                        }
                                                    });
                                        }

                                    }
                                });

                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Timber.d("onCreate: Permission not granted");

                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                if (mCurrentLocation != null) {
                    Timber.d("onLocationResult: Latitude: %s", mCurrentLocation.getLatitude());
                    Timber.d("onLocationResult: Longitude: %s", mCurrentLocation.getLongitude());
                } else {
                    Timber.d("onLocationResult: mCurrentLocation is null");
                }
                Timber.d("onLocationResult: Last Update Time: %s", mLastUpdateTime);
            }
        };

//        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mCurrentLocation != null) {
            LatLng myLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQUEST_ACCESS_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted
//                    Log.d(TAG, "onCreate: Permission granted");
//
//                    mFusedLocationClient.getLastLocation()
//                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                                @Override
//                                public void onSuccess(Location location) {
//                                    // Got last known location. In some rare situations this can be null.
//                                    if (location != null) {
//                                        Log.d(TAG, "onSuccess: Latitude: " + location.getLatitude());
//                                        Log.d(TAG, "onSuccess: Longitude: " + location.getLongitude());
//                                    }
//
//                                }
//                            });
//
//                } else {
//                    // permission denied
//                    Log.d(TAG, "onCreate: Permission not granted");
//
//                }
//                return;
//            }
//        }
//    }


    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Timber.i("All location settings are satisfied.");

                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

                        if (mCurrentLocation != null) {
                            Timber.d("onLocationResult: Latitude: " + mCurrentLocation.getLatitude());
                            Timber.d("onLocationResult: Longitude: " + mCurrentLocation.getLongitude());
                        } else {
                            Timber.d("onLocationResult: mCurrentLocation is null");
                        }
                        Timber.d("onLocationResult: Last Update Time: " + mLastUpdateTime);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Timber.i("Location settings are not satisfied. Attempting to upgrade location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(NearbyPlayersActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Timber.i("PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Timber.e(errorMessage);

                                Toast.makeText(NearbyPlayersActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        if (mCurrentLocation != null) {
                            Timber.d("onLocationResult: Latitude: %s", mCurrentLocation.getLatitude());
                            Timber.d("onLocationResult: Longitude: %s", mCurrentLocation.getLongitude());
                        } else {
                            Timber.d("onLocationResult: mCurrentLocation is null");
                        }
                        Timber.d("onLocationResult: Last Update Time: %s", mLastUpdateTime);
                    }
                });
    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Timber.e("User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Timber.e("User chose not to make required location settings changes.");
//                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void showLastKnownLocation() {
        if (mCurrentLocation != null) {
            Toast.makeText(getApplicationContext(), "Lat: " + mCurrentLocation.getLatitude()
                    + ", Lng: " + mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Last known location is not available!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resuming location updates depending on button state and
        // allowed permissions
        if (checkPermissions()) {
            startLocationUpdates();
        }

        if (mCurrentLocation != null) {
            Timber.d("onLocationResult: Latitude: " + mCurrentLocation.getLatitude());
            Timber.d("onLocationResult: Longitude: " + mCurrentLocation.getLongitude());
        } else {
            Timber.d("onLocationResult: mCurrentLocation is null");
        }
        Timber.d("onLocationResult: Last Update Time: " + mLastUpdateTime);

    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    protected void onPause() {
        super.onPause();


        // pausing location updates
        stopLocationUpdates();

    }

}
