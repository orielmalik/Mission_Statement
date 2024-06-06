package com.example.missionstatement.Menu;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.missionstatement.Firebase.Realtime;
import com.example.missionstatement.Objects.User;
import com.example.missionstatement.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import com.example.missionstatement.Tools.MissionClassifierHanLP;
public class DeatilsTest extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 100;  // Unique request code
    private LocationManager locationManager;
    private static  int counter=0;
    private MaterialButton submit;
    private Realtime server;
    private enum STATE {
        NA,
        NO_REGULAR_PERMISSION,
        NO_BACKGROUND_PERMISSION,
        LOCATION_DISABLE,
        LOCATION_SETTINGS_PROCCESS,
        LOCATION_SETTINGS_OK,
        LOCATION_ENABLE
    }

    private FloatingActionButton fblocation;
    private Spinner month,day,year;
    private STATE state = STATE.NA;
    private Bundle bundle;
    private HashMap<String,String>deatils;
    private MaterialTextView locationTXT;
    static User user;
    private EditText description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deatils_test);
        server=new Realtime(this);



            user = new User();
            user.setPhoneNumber(getIntent().getStringExtra("ph"));
            findViews();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }
    private void findViews()
    {
        fblocation=findViewById(R.id.locationFab);
        day=findViewById(R.id.DTEST_daySpinner);
        month=findViewById(R.id.DTEST_monthSpinner);
        year=findViewById(R.id.DTEST_yearSpinner);
        locationTXT=findViewById(R.id.locationDescriptionEditText);
        submit=findViewById(R.id.BTN_DEAT_SUBMIT);
        description=findViewById(R.id.descriptionEditText);
        setSpinners();
    }

    private void setSpinners()
    {
// Create adapters for each spinner
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);

        for (int year = 1900; year <= Calendar.getInstance().get(Calendar.YEAR); year++) {
            yearAdapter.add(String.valueOf(year));
        }

        for (int i = 0; i < 31; i++) {
            if(i<12)
            {
                monthAdapter.add(String.valueOf(i+1));
            }
            dayAdapter.add(String.valueOf(i+1));
        }


        year.setAdapter(yearAdapter);
        month.setAdapter(monthAdapter);
        day.setAdapter(dayAdapter);

    }

    private LocalDate getSelectedDate() {
        int years = Integer.parseInt(year.getSelectedItem().toString());
        int months = month.getSelectedItemPosition(); // 0-based month index
        int days = Integer.parseInt(day.getSelectedItem().toString());

        // Adjust month for LocalDate (0-based month)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDate.of(years, months + 1, days);
        }
        return null;
    }
    private  void  locationSetup()
    {
        fblocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  askForPermissions(checkForMissingPermission(this));

            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d("pttt", "hasFocus= " + hasFocus);
        if (hasFocus) {
            start();
        }
    }

    private void start() {
        String missingPermission = checkForMissingPermission(this);
        if (!isLocationEnabled(this)) {
            state =STATE.LOCATION_DISABLE;
        } else if (missingPermission != null) {
            if (missingPermission.equals(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                state =STATE.NO_BACKGROUND_PERMISSION;
            } else {
                state =STATE.NO_REGULAR_PERMISSION;
            }
        } else {
            state = STATE.LOCATION_SETTINGS_PROCCESS;
            // All permissions granted
            validateLocationSensorsEnabled();
        }
        updateUI();
    }


    @Override
    protected void onResume() {
        super.onResume();

        fblocation.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            }
        });
        submit.setOnClickListener(v -> {
            save();
        });
    }
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d("pttt", "isGranted");
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    start();
                } else {
                    Log.d("pttt", "NOT Granted");
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.

                    if (shouldShowRequestPermissionRationale(checkForMissingPermission(DeatilsTest.this))) {
                        Snackbar.make(findViewById(android.R.id.content),
                                        R.string.permission_rationale,
                                        Snackbar.LENGTH_INDEFINITE)
                                .setDuration(Snackbar.LENGTH_LONG)
                                .setAction(R.string.settings, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        requestPermissionLauncher.launch(checkForMissingPermission(DeatilsTest.this));
                                    }
                                })
                                .show();
                    } else {
                        buildAlertMessageManuallyBackgroundPermission(checkForMissingPermission(DeatilsTest.this));
                    }
                }
            });

    private void buildAlertMessageManuallyBackgroundPermission(String permission) {
        if (permission == null) {
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String sofix = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? "Allow all the time" : "Allow";

        builder.setMessage("You need to enable background location permission manually." +
                        "\nOn the page that opens - click on PERMISSIONS, then on LOCATION and then check '" + sofix + "'")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        openAppSettings();                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    ActivityResultLauncher<IntentSenderRequest> locationClientSettingsResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("pttt", "onActivityResult2" + result.toString());
                    if (result.getResultCode() == RESULT_CANCELED) {
                        finish();
                    } else {
                        start();
                    }
                }
            });

    ActivityResultLauncher<Intent> appSettingsResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("pttt", "onActivityResult" + result.toString());
                    start();
                }
            });

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        appSettingsResultLauncher.launch(intent);
    }

    private void askForPermissions(String permission) {
        Log.d("pttt", "permission = " + permission);
        if (shouldShowRequestPermissionRationale(permission)) {
            Log.d("pttt", "shouldShowRequestPermissionRationale");
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.

            if (permission.equals(Manifest.permission.ACCESS_BACKGROUND_LOCATION)  &&  Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // This is a new method provided in API 30
                // Manually background location permission
                buildAlertMessageManuallyBackgroundPermission(permission);
            } else {
                requestPermissionLauncher.launch(permission);
            }

        } else {
            // 1. First Time
            // 2. Don't Ask Me Again state


            Log.d("pttt", "NOT shouldShowRequestPermissionRationale");
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(permission);
        }
    }

    private static String checkForMissingPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return Manifest.permission.ACCESS_FINE_LOCATION;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return Manifest.permission.ACCESS_COARSE_LOCATION;
        }
        if (Build.VERSION.SDK_INT >= 29  &&  ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return Manifest.permission.ACCESS_BACKGROUND_LOCATION;
        }

        return null;
    }

    private void enableLocationServicesProgrammatically() {
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    @SuppressWarnings("deprecation")
    public static Boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is a new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
            // This was deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }

    private void validateLocationSensorsEnabled() {
        // Check whether l
        // ocation settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

        builder.addLocationRequest(LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));
        builder.setAlwaysShow(true);
        LocationSettingsRequest mLocationSettingsRequest = builder.build();

        SettingsClient mSettingsClient = LocationServices.getSettingsClient(this);

        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(locationSettingsResponse -> {
                    int x = 0;
                    int y = x + 0;
                    state = STATE.LOCATION_SETTINGS_OK;
                    updateUI();
                })
                .addOnFailureListener(e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {

                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(resolvable.getResolution()).build();
                                locationClientSettingsResultLauncher.launch(intentSenderRequest);


                                // // Cast to a resolvable exception.
                                // ResolvableApiException resolvable = (ResolvableApiException) e;
                                // // Show the dialog by calling startResolutionForResult(),
                                // // and check the result in onActivityResult().
                                // resolvable.startResolutionForResult(Activity_LocationValidationPro.this, 123);
                            } catch (Exception sie) {
                                Log.e("GPS", "Unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // 	Location settings can't be changed to meet the requirements, no dialog pops up

                            /*
                                Instead the default Yes, Not now and Never buttons if you call setAlwaysShow(true);
                                you will have only Yes and No, so the user won't choose Never and you will never
                                receive SETTINGS_CHANGE_UNAVAILABLE

                                ! Note !
                                if you have airplane mode on, and location off while requesting this,
                                you will actually receive a SETTINGS_CHANGE_UNAVAILABLE, even if you have setAlwaysShow
                             */

                            // Ask to disable Airplane Mode
                            // or
                            // Manually enable GPS
//                             buildAlertMessageNoGps();
                            Log.e("GPS", "Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
                            break;
                    }
                })
                .addOnCanceledListener(() -> Log.e("GPS", "checkLocationSettings -> onCanceled"));
    }
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            // Handle location updates here
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            locationTXT.setText(getCityName(DeatilsTest.this,latitude,longitude));
            //Toast.makeText(DeatilsTest.this, getCityName(DeatilsTest.this,latitude,longitude), Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };
    private void updateUI() {
        if (state ==STATE.NA) {

        } else if (state == STATE.LOCATION_DISABLE/*&&counter==1*/) {

            fblocation.setOnClickListener(v -> {
                enableLocationServicesProgrammatically();
            });
            locationTXT.setText("Turn On");
            fblocation.setVisibility(View.VISIBLE);
        } else if (state ==STATE.NO_REGULAR_PERMISSION) {

            fblocation.setOnClickListener(v -> {
                askForPermissions(checkForMissingPermission(this));
            });

        } else if (state == STATE.NO_BACKGROUND_PERMISSION) {

            fblocation.setOnClickListener(v -> {
                askForPermissions(checkForMissingPermission(this));
            });

        } else if (state == STATE.LOCATION_SETTINGS_PROCCESS) {

            locationTXT.setText("");

            //Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(, false));
        } else if (state == STATE.LOCATION_SETTINGS_OK) {
            locationTXT.setText("Click again on Location button");
            fblocation.setOnClickListener(v -> {
                //finish();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                        0, locationListener);
            });
            submit.setVisibility(View.VISIBLE);

        }
    }
    public static String getCityName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String cityName = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                cityName=(address.getThoroughfare()+","+address.getLocality()+", "+address.getCountryName());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return cityName;
    }




    private  void save()
    {

        user.setBirthdate(getSelectedDate());
        Log.d(null, "saveBierthdate:"+MissionClassifierHanLP.classifyMission(description.getText().toString()));
        user.setDescription(MissionClassifierHanLP.classifyMission(description.getText().toString()));
        user.setDescriptionText(description.getText().toString());
        user.setLocation(locationTXT.getText().toString());
        countChangePassword(user);
        Toast.makeText(this, ""+MissionClassifierHanLP.classifyMission(description.getText().toString()), Toast.LENGTH_SHORT).show();
        server.getmDatabase().child("USER").child(user.getPhoneNumber()).setValue(user);
        Intent i=new Intent(this,Personality_Test.class);
        i.putExtra("user",user);
        startActivity(i);

    }



    //method to check past value of user at Realtime
    private void countChangePassword(User user)
    {
        server.checkDataSnapshot("USER").thenAccept(new Consumer<HashMap<String, HashMap<String, String>>>() {
            @Override
            public void accept(HashMap<String, HashMap<String, String>> hashMap) {
                if(hashMap.containsKey(user.getPhoneNumber()))
                {
                    if(user.getBirthdate().equals(hashMap.get(user.getPhoneNumber()).get("birthdate")))
                    {
                        user.setCountChangeDate(Integer.parseInt(hashMap.get(user.getPhoneNumber()).get("countChangeDate")+1));
                    }
                    if(!hashMap.get(user.getPhoneNumber()).get("descriptionText").isEmpty())
                    {
                        user.setDescriptionText(hashMap.get(user.getPhoneNumber()).get("descriptionText")+": "+user.getDescriptionText());
                    }
                }
            }
        });

    }
}
