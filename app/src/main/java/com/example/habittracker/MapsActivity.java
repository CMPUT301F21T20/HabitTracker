package com.example.habittracker;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.habittracker.databinding.ActivityMapsBinding;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private String filterAddress = "";
    private Location currentLocation;
    private Button locationUpdateBtn;
    private TextView updatedLocation_textView;

    // TODO: dynamically set the Latitude and Longitude
    private double userLat;
    private double userLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        updatedLocation_textView = findViewById(R.id.updatedLocation_textView);
        locationUpdateBtn = findViewById(R.id.locationUpdateBtn);
        locationUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                setLocationTextView();
            }
        });
    }

    public void formatLocation() {
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.CANADA);
        try {
            List<Address> addresses =
                    geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);

            if (addresses.size() > 0) {
                for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++)
                    filterAddress += addresses.get(0).getAddressLine(i) + " ";
            }
            Toast.makeText(this, filterAddress, Toast.LENGTH_LONG).show();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
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

        getLocation();
        setLocationTextView();

        // Add a marker in Sydney and move the camera
        LatLng latLng = new LatLng(userLat,userLong);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
    }

    public void getLocation(){
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);
        criteria.setBearingRequired(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        // get the best available provider
        String provider = locationManager.getBestProvider(criteria,true);
        // get the first one from all the available ones
        if (provider == null){
            List<String> providers = locationManager.getProviders(true);
            if (providers != null && providers.size() > 0) {
                provider = providers.get(0);
            }
        }
        // if no one is supported, return
        if (TextUtils.isEmpty(provider)) {
            return;
        }

        // check permission
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        currentLocation = locationManager.getLastKnownLocation(provider);
        userLat = currentLocation.getLatitude();
        userLong = currentLocation.getLongitude();
        Toast.makeText(this, "Latitude\t\t: " + (String.format("%+10.2f", userLat)) + "\n" +
                "Longitude\t: " + (String.format("%+10.2f", userLong)),Toast.LENGTH_SHORT).show();
    }

    public void setLocationTextView(){
        formatLocation();
        updatedLocation_textView.setText(filterAddress);
    }
}