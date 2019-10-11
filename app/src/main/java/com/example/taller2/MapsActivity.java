package com.example.taller2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final double RADIUS_OF_EARTH_KM = 6371;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    JSONObject jsonObject;

    EditText editTextAddress;

    public static final double lowerLeftLatitude = 4.515144;
    public static final double lowerLeftLongitude= -74.226309;
    public static final double upperRightLatitude= 4.758701;
    public static final double upperRigthLongitude= -74.023228;

    double latitudeSource;
    double longitudeSource;

    public static final int REQUEST_CHECK_SETTINGS = 10;
    Geocoder mGeocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationRequest = createLocationRequest();

        //updateLocation();

    }

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
                if (location != null) {
                    locationFound(location);
                }
                else {
                    locationNotFound();
                }
            }
        });
    }

    //--------------------------
/*
    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS: {
                if (resultCode == RESULT_OK) {
                    startLocationUpdates(); //Se encendió la localización!!!
                } else {
                    Toast.makeText(this,
                            "Sin acceso a localización, hardware deshabilitado!",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    /*private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }*/

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000); //tasa de refresco en milisegundos
        mLocationRequest.setFastestInterval(5000); //máxima tasa de refresco
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    public double distance(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        return Math.round(result*100.0)/100.0;
    }

    private void locationFound (final Location location){

        Calendar calendario = Calendar.getInstance();
        int hora = calendario.get(Calendar.HOUR_OF_DAY);

        //***** Cambiar diseño  *****//
        if (hora >= 6 && hora <18){
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style_day));
        }
        else {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style_night));
        }


        //***** marcador posición actual    *****//
        LatLng bogota = new LatLng( location.getLatitude(), location.getLongitude() );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));

        mMap.addMarker(new MarkerOptions().position(bogota)
                .title("BOGOTÁ")
                .snippet("Ubicación actual")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
        );

        //***** Eveto presionar enter   *****//
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        editTextAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    String addressString = editTextAddress.getText().toString();
                    findAddress(addressString, mGeocoder, location.getLatitude(), location.getLongitude());
                }
                return false;
            }
        });
    }

    public void findAddress (String addressString, Geocoder mGeocoder, double latitudeSource, double longitudeSource ){

        if (!addressString.isEmpty()) {
            try {
                List<Address> addresses = mGeocoder.getFromLocationName( addressString, 2,
                        lowerLeftLatitude,
                        lowerLeftLongitude,
                        upperRightLatitude,
                        upperRigthLongitude
                );

                if (addresses != null && !addresses.isEmpty()) {
                    Address addressResult = addresses.get(0);
                    double latitudeDes = addressResult.getLatitude();
                    double longitudeDes = addressResult.getLongitude();
                    LatLng position = new LatLng(latitudeDes, longitudeDes);

                    String url ="https://maps.googleapis.com/maps/api/directions/json?origin="+latitudeSource+","+longitudeSource+"&destination="+latitudeDes+","+longitudeDes;

                    if (mMap != null) {
                        MarkerOptions myMarkerOptions = new MarkerOptions();
                        myMarkerOptions.position(position);
                        myMarkerOptions.title("Dirección Encontrada");
                        myMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        myMarkerOptions.snippet("Distancia: "+ distance(latitudeSource,longitudeSource, latitudeDes,longitudeDes)+" KM");
                        mMap.addMarker(myMarkerOptions);

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(latitudeDes, longitudeDes ))
                                .zoom(15)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        //      CALCULAR RUTA       //
                        openJsonRoute(url);

                        //**************************//
                    }
                } else {Toast.makeText(MapsActivity.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();}
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {Toast.makeText(MapsActivity.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();}

    }

    private void locationNotFound(){
        Toast.makeText(MapsActivity.this, "Pedir que active el GPS", Toast.LENGTH_SHORT).show();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(getApplicationContext());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
/*
                    task.addOnSuccessListener(getParent(), new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            startLocationUpdates();
                        }
                    });

                    task.addOnFailureListener(getParent(), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int statusCode = ((ApiException)e).getStatusCode();
                            switch (statusCode){
                                case CommonStatusCodes.RESOLUTION_REQUIRED:
                                    try{
                                        ResolvableApiException resolvable = (ResolvableApiException)e;
                                        resolvable.startResolutionForResult(getParent(),REQUEST_CHECK_SETTINGS);
                                    }
                                    catch (IntentSender.SendIntentException sendEx){
                                        //ignore
                                    }break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    //
                                    break;
                            }
                        }
                    });
*/
    }

    private void updateLocation() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    latitudeSource = location.getLatitude();
                    longitudeSource = location.getLongitude();
                }
            }
        };
    }

    private void openJsonRoute (String url){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonObject = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            traceRoute(jsonObject);
                            Log.i("", ""+response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue.add(stringRequest);
    }

    private void traceRoute(JSONObject jsonObject) {
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;

        try {
            jRoutes = jsonObject.getJSONArray("routes");
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) (jRoutes.get(i))).getJSONArray("legs");
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "" + ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        Log.i("end", "" + polyline);
                        List<LatLng> list = PolyUtil.decode(polyline);
                        mMap.addPolyline(new PolylineOptions().addAll(list).color(Color.GRAY).width(5));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
