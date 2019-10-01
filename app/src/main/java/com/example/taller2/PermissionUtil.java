package com.example.taller2;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtil {

    public static final int MY_PERMISSIONS_REQUEST = 5;


    public void onRequestPermissionsResult(Activity context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Acceso a contactos!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Funcionalidad Limitada!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void requestPermission(Activity context, String permiso, String justificacion, int idCode) {
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permiso) ){
                Toast.makeText(context, "Se necesita el permiso"+justificacion+"!", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permiso}, MY_PERMISSIONS_REQUEST);
        }
    }

}