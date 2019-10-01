package com.example.taller2;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;

    TextView textViewLatitud;
    TextView textViewLongitud;
    TextView textViewAltitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                textViewLatitud = (TextView) findViewById(R.id.textViewLatitud);
                textViewLongitud = (TextView) findViewById(R.id.textViewLongitud );
                textViewAltitud = (TextView) findViewById(R.id.textViewAltitud );

                if (location != null) {
                    textViewAltitud.setText(""+location.getAltitude());
                    textViewLatitud.setText(""+location.getLatitude());
                    textViewLongitud.setText(""+location.getLongitude());
                }
            }
        });
    }
}
