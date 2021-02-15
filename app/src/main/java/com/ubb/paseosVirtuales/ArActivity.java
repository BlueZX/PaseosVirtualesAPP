package com.ubb.paseosVirtuales;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.gson.Gson;
import com.ubb.paseosVirtuales.helper.InfoCardHelper;
import com.ubb.paseosVirtuales.helper.LocationPermissionHelper;
import com.ubb.paseosVirtuales.helper.getDataHelper;
import com.ubb.paseosVirtuales.model.DataModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ArActivity extends AppCompatActivity implements LocationListener {
    private static final String TAG = ArActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private List<DataModel> modelsList;
    private Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        //TODO: llenar lista de modelos
        modelsList = new ArrayList<>();

        String jsonFileString = getDataHelper.getJsonFromAssets(getApplicationContext(), "sampledata/dataObj.json");
        assert jsonFileString != null;
        Log.i("dataJSON", jsonFileString);

        Gson gson = new Gson();
        DataModel dataTest = gson.fromJson(jsonFileString, DataModel.class);
        dataTest.location.setLocation(dataTest.location.lat, dataTest.location.lon);
        Log.i("dataJSON", "latitud: "+ dataTest.location.lat + ", longitud: " + dataTest.location.lon);
        modelsList.add(dataTest);


        setContentView(R.layout.activity_ar);

        // se genera la session de ar
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdate);

        //interfaz
        ImageButton settingsButton = findViewById(R.id.settings_button);
        ImageButton backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainMenuActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onUpdate(FrameTime frameTime) {

        //renderiza todos los modelos 3D
        for (DataModel dm : modelsList) {
            if(dm.location.posicionado){
                return;
            }

            Frame frame = arFragment.getArSceneView().getArFrame();

            Collection<Plane> planes = frame.getUpdatedTrackables(Plane.class);

            for(Plane plane : planes){
                if(plane.getTrackingState() == TrackingState.TRACKING && dm.location.visible) {
                    Anchor anchor = plane.createAnchor(plane.getCenterPose());

                    //renderizado del obj 3D cuando se este dentro del rango de 10 metros.
                    renderObj(dm, anchor, frameTime);

                    //Se renderiza la informacion del Obj 3D
                    //renderInfo(dm, anchor);

                    break;
                }
            }
        }
    }

    private void renderInfo(DataModel dm, Anchor anchor) {

        ViewRenderable.builder()
                .setView(arFragment.getContext(), R.layout.controls)
                .build()
                .thenAccept( renderable -> {
                    renderable.setShadowCaster(false);
                    renderable.setShadowReceiver(false);
                    ImageButton imageButton = (ImageButton) renderable.getView().findViewById(R.id.info_button);
                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("boton ar","se toco el boton info");
                        }
                    });

                    dm.model.setViewRenderable(renderable);
                    //addControlsToScene(anchor, renderable);
                })
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "No se puede leer/renderizar el elemento de UI", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                });
    }

    private void addControlsToScene(Anchor anchor, ViewRenderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);

        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }

    private void renderObj(DataModel dm, Anchor anchor, FrameTime frameTime) {

        dm.location.setPosicionado(true);

        ModelRenderable.builder()
                .setSource(this, R.raw.andy)
                .build()
                .thenAccept(renderable -> {

                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
                    node.setParent(anchorNode);

                    InfoCardHelper dmModel = new InfoCardHelper(dm,renderable,this, arFragment, node);
                    dmModel.setParent(node);

                    // Create the transformable node and add it to the anchor.
                    //TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
                    //node.setParent(anchorNode);
                    //node.setRenderable(renderable);
                    //node.select();

                    //Vector3 cameraPosition = arFragment.getArSceneView().getScene().getCamera().getWorldPosition();

                    //InfoCardHelper info = new InfoCardHelper(dm.model.getViewRenderable( node.getCollisionShape(), cameraPosition ), dataModel, dmRenderable, infoCard, context, arFragment);

                    //node.addChild(info);


                    //anchorNode.setRenderable(renderable);
                    //arFragment.getArSceneView().getScene().addChild(anchorNode);

                })
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Si aun no se tienen los permisos de localizacion, se solicitan
        // en caso contrario se procede a manejar la ubicacion del dispositivo
        if (!LocationPermissionHelper.hasLocationPermission(this)) {
            LocationPermissionHelper.requestLocationPermission(this);
        }
        else {
            handleLocationServices();
        }

        //Se reanuda la session del arFragment
        arFragment.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

        //Se pausa la session de arFragment
        arFragment.onPause();
    }

    private void handleLocationServices() {
        //si no se poseen los permisos de Localizacion finaliza el thread
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.location = location;

        //se calcula la distancia que exista entre la ubicacion de los objeto y la del dispositivo
        for(DataModel dm: modelsList){
            dm.location.setDistance(location.distanceTo(dm.location.location));

            Log.d("distancia",dm.location.distance +"");

            if(dm.location.distance <= 10){
                dm.location.setVisible(true);
            }
            else{
                dm.location.setVisible(false);
            }
        }
    }

}