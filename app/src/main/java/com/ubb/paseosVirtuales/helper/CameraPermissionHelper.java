package com.ubb.paseosVirtuales.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

// Helper para preguntar los permisos de la camara.
public final class CameraPermissionHelper {
    private static final int CAMERA_PERMISSION_CODE = 0;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;

    // Verifica que se tenga los permisos concedidos para ocupar la camara en la aplicación.
    public static boolean hasCameraPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED;
    }

    // Verifica que se tenga los permisos necesarios ocupar la aplicación, en caso de no poseerlos los solicita
    public static void requestCameraPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[] {CAMERA_PERMISSION}, CAMERA_PERMISSION_CODE);
    }

    // Verifica si se necesita mostrar por que se necesita el permiso de la camara
    public static boolean shouldShowRequestPermissionRationale(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION);
    }

    // Inicia la configuracion de la aplicacion para dar el permiso.
    public static void launchPermissionSettings(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }
}
