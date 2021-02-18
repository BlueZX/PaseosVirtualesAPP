package com.ubb.paseosVirtuales.MainMenuFragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.ubb.paseosVirtuales.ArActivity;
import com.ubb.paseosVirtuales.DataBase.Modelo;
import com.ubb.paseosVirtuales.R;
import com.ubb.paseosVirtuales.helper.getDataHelper;
import com.ubb.paseosVirtuales.model.DataModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    private static final String TAG = MapFragment.class.getSimpleName();

    private MapView mapView;
    private GoogleMap map;
    private List<DataModel> modelsList = new ArrayList<>();
    private Location location;
    private JSONArray jsonArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        try {
            Modelo obj = new Modelo();
            String json = obj.getParametro(getContext(),"MODELS");
            jsonArray = new JSONArray(json);

            Gson gson = new Gson();

            for (int i = 0; i < jsonArray.length(); i++) {
                DataModel m = gson.fromJson(jsonArray.getJSONObject(i).toString(),DataModel.class);
                modelsList.add(m);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            //TODO: mensaje de error
        }

        mapView = (MapView) root.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        return root;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        map.setMyLocationEnabled(true);
        //map.setMinZoomPreference(5.0f);
        map.setMaxZoomPreference(18.0f);
        handleNewLocation(location);

        for(DataModel dm: modelsList){
            LatLng loc = new LatLng(dm.location.lat, dm.location.lng);
            map.addMarker(new MarkerOptions().position(loc).title(dm.data.getName().length() > 0 ? dm.data.getName() : "Sin nombre"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        handleLocationServices();
    }

    private void handleLocationServices() {
        //si no se poseen los permisos de Localizacion finaliza el thread
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            //Se verifica que se tenga activado las funciones de GPS y el estado de su conexion a internet
            boolean flagGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean flagNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            //se obtiene la ultima localizaciopn obtenida por medio del internet
            if (flagNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                this.location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            //se obtienen la ultima localizacion obtenida por medio del sensor GPS
            if (flagGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                this.location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

        }
        catch(Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    private void handleNewLocation(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        float zoomLevel = 16.0f;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }
}