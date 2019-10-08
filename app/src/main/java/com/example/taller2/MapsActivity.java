package com.example.taller2;

import androidx.fragment.app.FragmentActivity;

import android.app.DownloadManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;

    EditText editTextAddress;

    public static final double lowerLeftLatitude = 1.396967;
    public static final double lowerLeftLongitude= -78.903968;
    public static final double upperRightLatitude= 11.983639;
    public static final double upperRigthLongitude= -71.869905;
    Geocoder mGeocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                mGeocoder = new Geocoder(getBaseContext());

                Calendar calendario = Calendar.getInstance();
                int hora = calendario.get(Calendar.HOUR_OF_DAY);
                //int minutos = calendario.get(Calendar.MINUTE);
                //int segundos = calendario.get(Calendar.SECOND);

                final double latitudeSource = location.getLatitude();
                final double longitudeSource = location.getLongitude();

                if (location != null) {
                    LatLng bogota = new LatLng( location.getLatitude(), location.getLongitude() );
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));

                    mMap.addMarker(new MarkerOptions().position(bogota)
                            .title("BOGOTÁ")
                            .snippet("Población: 8.081.000")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
                    );


                    if (hora >= 6 && hora <18){
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style_day));
                    }
                    else {
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style_night));
                    }

                    editTextAddress = (EditText) findViewById(R.id.editTextAddress);
                    editTextAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE){
                            String addressString = editTextAddress.getText().toString();
                            findAddress(addressString, mGeocoder, latitudeSource, longitudeSource);
                        }
                        return false;
                        }
                    });
                }
                else {
                    Toast.makeText(MapsActivity.this, "Pedir que active el GPS", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void findAddress (String addressString, Geocoder mGeocoder, double latitudeSource, double longitudeSource ){

        if (!addressString.isEmpty()) {
            try {
                List<Address> addresses = mGeocoder.getFromLocationName(addressString, 2);
                if (addresses != null && !addresses.isEmpty()) {
                    Address addressResult = addresses.get(0);
                    double latitudeDes = addressResult.getLatitude();
                    double longitudeDes = addressResult.getLongitude();
                    LatLng position = new LatLng(latitudeDes, longitudeDes);

                    if (mMap != null) {
                        MarkerOptions myMarkerOptions = new MarkerOptions();
                        myMarkerOptions.position(position);
                        myMarkerOptions.title("Dirección Encontrada");
                        myMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        mMap.addMarker(myMarkerOptions);

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(latitudeDes, longitudeDes ))
                            .zoom(15)
                            .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        //      CALCULAR RUTA       //

                    }
                } else {Toast.makeText(MapsActivity.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();}
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {Toast.makeText(MapsActivity.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();}

    }
}
