package com.example.locationtest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.appcompat.app.AppCompatActivity;

public class GeolocationActivity extends AppCompatActivity {

    private TextView textLatitude;
    private TextView textLongitude;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 42;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geolocation);
        setTitle(R.string.title_geolocation);

        textLatitude = findViewById(R.id.text_latitude);
        textLongitude = findViewById(R.id.text_longitude);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the current location and pass it to the BarcodeScanActivity
        getLastLocationAndLaunchBarcodeScanActivity();
    }

    private void getLastLocationAndLaunchBarcodeScanActivity() {
        if (checkLocationPermissions()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                textLatitude.setText(Double.toString(location.getLatitude()));
                                textLongitude.setText(Double.toString(location.getLongitude()));

                                // Launch BarcodeScanActivity and pass current location
                                launchBarcodeScanActivity(location.getLatitude(), location.getLongitude());
                            } else {
                                textLatitude.setText("Location not available");
                                textLongitude.setText("Location not available");
                            }
                        }
                    });
        } else {
            requestLocationPermissions();
        }
    }

    private void launchBarcodeScanActivity(double latitude, double longitude) {
        Intent intent = new Intent(this, BarcodeScanActivity.class);
        intent.putExtra("currentLatitude", latitude);
        intent.putExtra("currentLongitude", longitude);
        startActivity(intent);
    }

    private boolean checkLocationPermissions() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void requestLocationPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
    }
}