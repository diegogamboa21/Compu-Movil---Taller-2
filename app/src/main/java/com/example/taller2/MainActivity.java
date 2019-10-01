package com.example.taller2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //public static final String TAG = "MainActivity";
    private static final int REQUEST_CONTACTS = 0;
    private static final int REQUEST_LOCATION = 1;

    private static String PERMISSIONS_CONTACT = Manifest.permission.READ_CONTACTS;
    private static String PERMISSIONS_CAMERA = Manifest.permission.CAMERA;

    ImageButton imageButtonContacts;
    ImageButton imageButtonCamera;
    ImageButton imageButtonMap;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButtonContacts = (ImageButton) findViewById(R.id.imageButtonContact);
        imageButtonCamera = (ImageButton) findViewById(R.id.imageButtonCamera);
        imageButtonMap = (ImageButton) findViewById(R.id.imageButtonMap);

        imageButtonContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermissionContacts();
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(getApplicationContext(), ListContacts.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "no se tiene acceso a los contactos!!!", Toast.LENGTH_LONG).show();
                }
            }
        });

        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                startActivity(intent);
            }
        });

        imageButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermissionMap();
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //-------------------------------------------------
            } else {
                Toast.makeText(this, "Funcionalidad Limitada!!!", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //-------------------------------------------------
            } else {
                Toast.makeText(this, "Funcionalidad Limitada!!!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void askPermissionContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this, "Se necesita el permiso para poder mostrar los contactos!", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS);
        }
    }

    private void askPermissionMap() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Se necesita el permiso para poder mostrar la localización!", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }

    //  -----------FUNCION GENERAL--------------    //
    public void requestPermission(String permiso, String justificacion, int code) {
        if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permiso) ){
                Toast.makeText(this, "Se necesita acceder a "+justificacion+"!", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this, "Ya tiene permisos a "+justificacion+"!", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{permiso}, code);
            }
        }
    }



}

////implementation 'com.android.support:design:28.0.0'