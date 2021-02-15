package com.ubb.paseosVirtuales.MainMenuFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.ubb.paseosVirtuales.R;
import com.ubb.paseosVirtuales.helper.getDataHelper;
import com.ubb.paseosVirtuales.model.DataModel;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap map;
    private List<DataModel> modelsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_map, container, false);

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

        modelsList = new ArrayList<>();

        String jsonFileString = getDataHelper.getJsonFromAssets(getActivity().getApplicationContext(), "sampledata/dataObj.json");
        assert jsonFileString != null;
        Log.i("dataJSON", jsonFileString);

        Gson gson = new Gson();
        DataModel dataTest = gson.fromJson(jsonFileString, DataModel.class);
        dataTest.location.setLocation(dataTest.location.lat, dataTest.location.lon);
        Log.i("dataJSON", "latitud: "+ dataTest.location.lat + ", longitud: " + dataTest.location.lon);
        modelsList.add(dataTest);

        for(DataModel dm: modelsList){
            LatLng Maharashtra = new LatLng(dm.location.lat, dm.location.lon);
            map.addMarker(new MarkerOptions().position(Maharashtra).title("que wea csm toy en Maharashtra"));
            map.moveCamera(CameraUpdateFactory.newLatLng(Maharashtra));
        }
    }
}