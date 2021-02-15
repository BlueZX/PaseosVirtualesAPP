package com.ubb.paseosVirtuales;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ubb.paseosVirtuales.MainMenuFragment.HomeFragment;
import com.ubb.paseosVirtuales.MainMenuFragment.MapFragment;
import com.ubb.paseosVirtuales.helper.CameraPermissionHelper;
import com.ubb.paseosVirtuales.helper.LocationPermissionHelper;

public class MainMenuActivity extends AppCompatActivity {

    private boolean viewIsAtHome;
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

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

        // Solicitud de permisos de localizacion
        if (!LocationPermissionHelper.hasLocationPermission(this)) {
            LocationPermissionHelper.requestLocationPermission(this);
        }

        // Solicitud de permisos de camara.
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this);
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

