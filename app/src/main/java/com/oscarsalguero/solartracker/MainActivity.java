/***
 * Copyright (c) 2015 Oscar Salguero www.oscarsalguero.com
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oscarsalguero.solartracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oscarsalguero.solartracker.bean.SunResults;
import com.oscarsalguero.solartracker.utils.DateDeserializer;
import com.oscarsalguero.solartracker.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

/**
 * Shows how to track the position of the sun to change colors and color temperature.
 * Created by RacZo on 9/4/15.
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    private static final String LOG_TAG = MainActivity.class.getName();
    private Activity activity;
    private SunResults sunResults;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting_location_updates";
    private static final String LOCATION_KEY = "location";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "last_updated_time";

    private static final int GPS_PING_INTERVAL = 1; // Minutes

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private boolean mRequestingLocationUpdates;
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;
    protected LocationSettingsRequest mLocationSettingsRequest;
    protected ProgressDialog progressDialog;

    private TextView textViewLatLon;
    private TextView textViewSunrise;
    private TextView textViewSunset;
    private TextView textViewDayLength;
    private TextView textViewCivilTwilightBegin;
    private TextView textViewCivilTwilightEnd;
    private TextView textViewNauticalTwilightBegin;
    private TextView textViewNauticalTwilightEnd;
    private TextView textViewAstroTwilightBegin;
    private TextView textViewAstroTwilightEnd;
    private TextView textViewColorTemperature;
    private TextView textViewColor;

    private SolarTrackerApplication solarTrackerApplication;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.activity = this;
        solarTrackerApplication = (SolarTrackerApplication) getApplication();

        textViewLatLon = (TextView) this.findViewById(R.id.text_view_lat_lon);
        textViewSunrise = (TextView) this.findViewById(R.id.text_view_sunrise);
        textViewSunset = (TextView) this.findViewById(R.id.text_view_sunset);
        textViewDayLength = (TextView) this.findViewById(R.id.text_view_day_length);
        textViewCivilTwilightBegin = (TextView) this.findViewById(R.id.text_view_civil_twilight_begin);
        textViewCivilTwilightEnd = (TextView) this.findViewById(R.id.text_view_civil_twilight_end);
        textViewNauticalTwilightBegin = (TextView) this.findViewById(R.id.text_view_nautical_twilight_begin);
        textViewNauticalTwilightEnd = (TextView) this.findViewById(R.id.text_view_nautical_twilight_end);
        textViewAstroTwilightBegin = (TextView) this.findViewById(R.id.text_view_astronomical_twilight_begin);
        textViewAstroTwilightEnd = (TextView) this.findViewById(R.id.text_view_astronomical_twilight_end);
        textViewColorTemperature = (TextView) this.findViewById(R.id.text_view_color_temp);
        textViewColor = (TextView) this.findViewById(R.id.text_view_color);

        updateValuesFromBundle(savedInstanceState);

        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();

        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resp != ConnectionResult.SUCCESS) {
            Toast.makeText(this, "Please install Google Play Services.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mLastLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                if(mGoogleApiClient.isConnected()) {
                    startLocationUpdates();
                }else{
                    mGoogleApiClient.connect();
                }
                break;
            case R.id.action_about:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.git_hub_repo_url)));
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(LOG_TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        if (mLastLocation != null) {
            Log.d(LOG_TAG, "Location changed to (lat,lon): " + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
            getSunInformation(mLastLocation);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(LOG_TAG, "Connected to GoogleApiClient");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        /*
        if (mLastLocation != null) {
            Log.d(LOG_TAG, "Location on connected (lat,lon): " + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
            getSunInformation(mLastLocation);
        }
        */
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "Connection failed. " + connectionResult.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "Connection to Google API Services suspended.");
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(LOG_TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(LOG_TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                    status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(LOG_TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(LOG_TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    protected void showProgressDialog(String message) {
        if (progressDialog == null) {
            if (this != null) {
                if (!this.isFinishing()) {
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage(message);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            }
        }
    }

    protected void hideProgressDialog() {
        if (this != null) {
            if (!this.isFinishing() && progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    private void updateUI(Location location, SunResults sunResults) {

        try {
            textViewSunrise.setText(solarTrackerApplication.getDateFormat().format(sunResults.getSunrise()));
            textViewSunset.setText(solarTrackerApplication.getDateFormat().format(sunResults.getSunset()));
            textViewCivilTwilightBegin.setText(solarTrackerApplication.getDateFormat().format(sunResults.getCivilTwilightBegin()));
            textViewCivilTwilightEnd.setText(solarTrackerApplication.getDateFormat().format(sunResults.getCivilTwilightEnd()));
            textViewNauticalTwilightBegin.setText(solarTrackerApplication.getDateFormat().format(sunResults.getNauticalTwilighBegin()));
            textViewNauticalTwilightEnd.setText(solarTrackerApplication.getDateFormat().format(sunResults.getNauticalTwilightEnd()));
            textViewAstroTwilightBegin.setText(solarTrackerApplication.getDateFormat().format(sunResults.getAstronomicalTwilightBegin()));
            textViewAstroTwilightEnd.setText(solarTrackerApplication.getDateFormat().format(sunResults.getAstronomicalTwilightEnd()));
        } catch (Exception e) {
            Log.e(LOG_TAG, "An exception has occurred.", e);
        }

        textViewDayLength.setText(sunResults.getDayLength() + " " + getString(R.string.seconds));
        textViewLatLon.setText(location.getLatitude() + ", " + location.getLongitude());
        textViewColorTemperature.setText(R.string.daylight_overcast_color_temp);
        textViewColor.setText(getString(R.string.daylight_overcast_hex_string));
    }

    private void getSunInformation(final Location location) {
        hideProgressDialog();
        showProgressDialog(getString(R.string.refreshing));
        String url = Constants.API_BASE_URL + "lat=" + location.getLatitude() + "&lng=" + location.getLongitude();
        JsonObjectRequest request = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOG_TAG, "Response: " + response.toString());
                hideProgressDialog();
                try {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create();
                    if (response.has(Constants.RESPONSE_STATUS)) {
                        Log.d(LOG_TAG, "Response status: " + response.getString(Constants.RESPONSE_STATUS));
                        if (response.getString(Constants.RESPONSE_STATUS).equalsIgnoreCase(Constants.API_STATUS_OK)) {
                            if (response.has(Constants.RESPONSE_RESULTS)) {
                                JSONObject jsonResults = response.getJSONObject(Constants.RESPONSE_RESULTS);
                                sunResults = gson.fromJson(jsonResults.toString(), SunResults.class);
                                updateUI(location, sunResults);
                            }
                        } else {
                            Utils.showAlertDialog(activity, getString(R.string.dialog_error_title), getString(R.string.dialog_error_message));
                        }
                    }

                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage());
                    Utils.showAlertDialog(activity, getString(R.string.dialog_error_title), e.getMessage());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                Utils.showAlertDialog(activity, getString(R.string.dialog_error_title), error.getMessage());
            }

        });

        RetryPolicy policy = new DefaultRetryPolicy(Constants.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        request.setShouldCache(false);
        solarTrackerApplication.addToRequestQueue(request);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;
            }
        });
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
            }
        });
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mLastLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
        }
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    public void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        long locationUpdateInterval = GPS_PING_INTERVAL * 60 * 1000;
        Log.d(LOG_TAG, "Will update location every " + GPS_PING_INTERVAL + " minutes (" + locationUpdateInterval + " milliseconds)" );
        mLocationRequest.setInterval(locationUpdateInterval);
        mLocationRequest.setFastestInterval(locationUpdateInterval);
        mLocationRequest.setSmallestDisplacement(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

}
