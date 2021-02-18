package com.ubb.paseosVirtuales.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public final class WritePermissionHelper {
    public static final int LOCATION_PERMISSIONS_CODE = 786;
    private static final String WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    // Verifica que se tenga los permisos concedidos para ocupar la localización en la aplicación.
    public static boolean hasLocationPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, WRITE_PERMISSION) == PackageManager.PERMISSION_GRANTED;
    }

    // Verifica que se tenga los permisos necesarios ocupar la aplicación, en caso de no poseerlos los solicita
    public static void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[] {WRITE_PERMISSION}, LOCATION_PERMISSIONS_CODE);
    }

    // Verifica si se necesita mostrar por que se necesita el permiso de la localizacion
    public static boolean shouldShowRequestPermissionRationale(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, WRITE_PERMISSION);
    }

    // Inicia la configuracion de la aplicacion para dar el permiso.
    public static void launchPermissionSettings(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }


}
