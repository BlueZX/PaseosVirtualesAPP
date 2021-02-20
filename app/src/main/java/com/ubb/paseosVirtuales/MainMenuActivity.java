package com.ubb.paseosVirtuales;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.google.gson.Gson;
import com.ubb.paseosVirtuales.DataBase.Modelo;
import com.ubb.paseosVirtuales.DataBase.ParametrosDTO;
import com.ubb.paseosVirtuales.MainMenuFragment.HomeFragment;
import com.ubb.paseosVirtuales.MainMenuFragment.MapFragment;
import com.ubb.paseosVirtuales.helper.CameraPermissionHelper;
import com.ubb.paseosVirtuales.helper.GlobalHelper;
import com.ubb.paseosVirtuales.helper.LocationPermissionHelper;
import com.ubb.paseosVirtuales.helper.SnackbarHelper;
import com.ubb.paseosVirtuales.helper.VolleyHelper;
import com.ubb.paseosVirtuales.helper.WritePermissionHelper;
import com.ubb.paseosVirtuales.model.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class MainMenuActivity extends AppCompatActivity {

    private boolean viewIsAtHome;
    private BottomNavigationView navigation;
    private VolleyHelper volleyHelper;
    private Dialog loading;
    private TextView tvLoading;

    private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        volleyHelper = new VolleyHelper(this, GlobalHelper.ENDPOINT);
        loading = new Dialog(this);
        loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loading.setContentView(R.layout.loading);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);

        tvLoading = (TextView) loading.findViewById(R.id.tv_loading);
        tvLoading.setText("Cargando información");

        navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                switch (itemId){
                    case R.id.home:
                        addFragment(new HomeFragment());
                        viewIsAtHome = true;
                        break;
                    case R.id.initRA:
                        viewIsAtHome = false;
                        getModels();
                        Intent intent = new Intent(getApplicationContext(), ArActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.map:
                        addFragment(new MapFragment());
                        viewIsAtHome = false;
                        break;
                }
                return true;
            }
        });

        navigation.setSelectedItemId(R.id.home);

    }

    private void addFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (!viewIsAtHome) {
            BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
            bottomNavigationView.setSelectedItemId(R.id.home);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Solicitud de permisos de Almacenamiento
        if (!WritePermissionHelper.hasLocationPermission(this)) {
            WritePermissionHelper.requestLocationPermission(this);
        }

        // Solicitud de permisos de localizacion
        if (!LocationPermissionHelper.hasLocationPermission(this)) {
            LocationPermissionHelper.requestLocationPermission(this);
        }

        // Solicitud de permisos de camara.
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this);
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getModels();
    }

    private void getModels(){
        loading.show();

        volleyHelper.get("api/modelos", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Models", response.toString());
                try {
                    if(response.get("ok").toString().equals("true")){
                        Modelo obj = new Modelo();
                        ParametrosDTO param = new ParametrosDTO();

                        param.setId("3");
                        param.setNombre("MODELS");
                        param.setValue(response.get("modelos").toString());

                        if(obj.getParametro(MainMenuActivity.this,"MODELS").equals("")){
                            int resInsert = obj.insertParametros( MainMenuActivity.this, param);

                            if(resInsert == 1){
                                loading.dismiss();
                                Log.i("Almacenado", param.toString());
                            }
                            else {
                                loading.dismiss();
                                messageSnackbarHelper.showMessageWithDismiss((Activity) MainMenuActivity.this, "A ocurrido un error inesperado al inicializar la aplicacion", Color.RED);
                            }
                        }
                        else{
                            if(obj.updateParametro(MainMenuActivity.this, "MODELS", param)){
                                loading.dismiss();
                            }
                            else{
                                loading.dismiss();
                                messageSnackbarHelper.showMessageWithDismiss((Activity) MainMenuActivity.this, "A ocurrido un error inesperado al leer los modelos 3D", Color.RED);
                            }
                        }

                    }
                } catch (JSONException e) {
                    loading.dismiss();
                    messageSnackbarHelper.showMessageWithDismiss((Activity) MainMenuActivity.this, "A ocurrido un error inesperado al leer los modelos 3D", Color.RED);

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                messageSnackbarHelper.showMessageWithDismiss((Activity) MainMenuActivity.this, "No es posible conectar con el servidor", Color.RED);

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

