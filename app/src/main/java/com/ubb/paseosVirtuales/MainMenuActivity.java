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
import com.android.volley.VolleyError;
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

                        Gson gson = new Gson();
                        JSONArray jsonArray = new JSONArray(response.get("modelos").toString());

                        for (int i = 0; i < jsonArray.length(); i++) {
                            DataModel dm = gson.fromJson(jsonArray.getJSONObject(i).toString(),DataModel.class);

                            File myDir = new File(Environment.getExternalStorageDirectory().toString() + "/Download");
                            myDir.mkdirs();

                            File file = new File(myDir, dm.model.obj);

                            if(!file.exists()){
                                tvLoading.setText("Descargando modelos 3D");
                                if(!loading.isShowing()){
                                    loading.show();
                                }

                                DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                Uri uri = Uri.parse(GlobalHelper.DOWNLOAD + "/uploads/modelo/"+ dm.model.obj);

                                DownloadManager.Request request = new DownloadManager.Request(uri);

                                request.setTitle("Modelo_"+ dm.data.getName() );
                                request.setDescription("Modelo para paseos virtuales");
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setVisibleInDownloadsUi(false);

                                request.setDestinationUri(Uri.parse("file://" + Environment.getExternalStorageDirectory() +"/Download/"+ dm.model.obj));

                                downloadmanager.enqueue(request);
                            }
                            else {
                                loading.dismiss();
                            }

                        }

                        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                        BroadcastReceiver receiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                loading.dismiss();
                            }
                        };
                        registerReceiver(receiver, filter);

                        int resInsert = obj.insertParametros( MainMenuActivity.this, param);

                        if(resInsert == 1){
                            Log.i("Almacenado", param.toString());
                        }
                        else {
                            messageSnackbarHelper.showMessageWithDismiss((Activity) MainMenuActivity.this, "A ocurrido un error inesperado al inicializar la aplicacion", Color.RED);
                        }
                    }
                } catch (JSONException e) {
                    messageSnackbarHelper.showMessageWithDismiss((Activity) MainMenuActivity.this, "A ocurrido un error inesperado al leer los modelos 3D", Color.RED);

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                messageSnackbarHelper.showMessageWithDismiss((Activity) MainMenuActivity.this, "No es posible conectar con el servidor", Color.RED);

            }
        });

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
                        Intent intent = new Intent(getApplicationContext(), ArActivity.class);
                        startActivity(intent);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

