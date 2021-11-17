package com.example.habittracker;

import static android.content.Context.LOCATION_SERVICE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends Fragment {
    private Context thiscontext;
    private GoogleMap mMap;
    private String filterAddress = "";
    private Location currentLocation;
    private Button locationConfirmBtn;
    private Button locationUpdateBtn;
    private TextView updatedLocation_textView;

    private double userLat;
    private double userLong;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            getLocation();
            setLocationTextView();

            // Add a marker in Sydney and move the camera
            LatLng latLng = new LatLng(userLat, userLong);
            mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        thiscontext = getContext();

        updatedLocation_textView = view.findViewById(R.id.updatedLocation_textView);
        locationUpdateBtn = view.findViewById(R.id.locationUpdateBtn);
        locationConfirmBtn = view.findViewById(R.id.locationConfirmBtn);

        locationUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                getLocation();
                setLocationTextView();
            }
        });

        locationConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!updatedLocation_textView.getText().toString().equals("Location not updated yet...")) {
                    confirmLocation();
                } else {

                }
            }
        });
    }

    public void formatLocation() {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses =
                    geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);

            if (addresses.size() > 0) {
                filterAddress = addresses.get(0).getAddressLine(0);

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getLocation() {
        LocationManager locationManager = (LocationManager) requireActivity().getApplicationContext().getSystemService(LOCATION_SERVICE);

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
        String provider = locationManager.getBestProvider(criteria, true);
        // get the first one from all the available ones
        if (provider == null) {
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
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        currentLocation = locationManager.getLastKnownLocation(provider);
        if (currentLocation == null) {
            Log.i("TEST", "Current Location is NULL!");
            locationManager.requestLocationUpdates(provider, 1000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    currentLocation = location;
                    userLat = currentLocation.getLatitude();
                    userLong = currentLocation.getLongitude();
                    Toast.makeText(thiscontext, "Latitude\t\t: " + (String.format("%+10.2f", userLat)) + "\n" +
                            "Longitude\t: " + (String.format("%+10.2f", userLong)),Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            userLat = currentLocation.getLatitude();
            userLong = currentLocation.getLongitude();
            Toast.makeText(getContext(), "Latitude\t\t: " + (String.format("%+10.2f", userLat)) + "\n" +
                    "Longitude\t: " + (String.format("%+10.2f", userLong)),Toast.LENGTH_SHORT).show();
        }

    }

    public void setLocationTextView(){
        formatLocation();
        updatedLocation_textView.setText(filterAddress);
    }

    public void confirmLocation(){
        EditText addLocation_editText = requireActivity().findViewById(R.id.addLocation_editText);
        addLocation_editText.setText(filterAddress);
        requireActivity().onBackPressed();
    }
}