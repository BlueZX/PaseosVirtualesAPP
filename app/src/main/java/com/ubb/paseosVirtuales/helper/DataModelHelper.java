package com.ubb.paseosVirtuales.helper;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.collision.Box;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.ubb.paseosVirtuales.DetailActivity;
import com.ubb.paseosVirtuales.MainMenuActivity;
import com.ubb.paseosVirtuales.R;
import com.ubb.paseosVirtuales.model.DataModel;

public class DataModelHelper extends Node implements Node.OnTapListener {
    private final DataModel dataModel;
    private final ModelRenderable dmRenderable;
    private final Context context;
    private final ArFragment arFragment;

    private final TransformableNode parent;
    private Node dmVisual;
    private Node infoCard;

    public DataModelHelper(DataModel dataModel, ModelRenderable dmRenderable, Context context, ArFragment arFragment, TransformableNode parent){
        this.dataModel = dataModel;
        this.dmRenderable = dmRenderable;
        this.context = context;
        this.arFragment = arFragment;
        this.parent = parent;

        setOnTapListener(this);
    }

    public void onActivate() {
        if (getScene() == null) {
            throw new IllegalStateException("Scene is null!");
        }

        if (infoCard == null) {
            infoCard = new Node();
            infoCard.setEnabled(false);

            Box box = (Box) dmRenderable.getCollisionShape();
            Vector3 size = box.getSize();

            infoCard.setLocalPosition(new Vector3(size.x * 0.5f, size.y * 1.25f, size.z * 0f));

            ViewRenderable.builder()
                    .setView(context, R.layout.controls)
                    .build()
                    .thenAccept(
                            (renderable) -> {
                                renderable.setShadowCaster(false);
                                renderable.setShadowReceiver(false);

                                infoCard.setRenderable(renderable);
                                TextView title = (TextView) renderable.getView().findViewById(R.id.tv_msg_model);
                                title.setText(dataModel.data.getName().length() > 0 ? dataModel.data.getName() : "Sin nombre");

                                LinearLayout imageButton = (LinearLayout) renderable.getView().findViewById(R.id.btn_card_model);
                                imageButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.d("boton ar","se toco el boton info");
                                        Intent intent = new Intent(context, DetailActivity.class);

                                        intent.putExtra("name", dataModel.data.getName().length() > 0 ? dataModel.data.getName() : "Sin nombre");
                                        intent.putExtra("detail", dataModel.data.getDescription().length() > 0 ? dataModel.data.getDescription() : "Sin descripción");
                                        intent.putExtra("extraInfo", dataModel.data.getExtraInfo().length() > 0 ? dataModel.data.getExtraInfo() : "Sin información extra");
                                        intent.putExtra("date", dataModel.data.getDateMonument().length() > 0 ? dataModel.data.getDateMonument() : "");

                                        context.startActivity(intent);
                                    }
                                });

                            })
                    .exceptionally(
                            (throwable) -> {
                                throw new AssertionError("Could not load info card view.", throwable);
                            });
        }

        if (dmVisual == null) {
            dmVisual = new Node();
            dmVisual.setParent(this);
            dmVisual.addChild(infoCard);
            dmVisual.setRenderable(dmRenderable);
        }
    }

    @Override
    public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
        if (infoCard == null) {
            return;
        }

        infoCard.setEnabled(!infoCard.isEnabled());
    }

    @Override
    public void onUpdate(FrameTime frameTime) {

        if (infoCard == null) {
            return;
        }

        if (!infoCard.isEnabled()) {
            return;
        }

        if (getScene() == null) {
            return;
        }

        if(infoCard.isEnabled() && parent.isTransforming()){
            infoCard.setEnabled(false);
        }

        Vector3 cameraPosition = getScene().getCamera().getWorldPosition();
        Vector3 cardPosition = infoCard.getWorldPosition();
        Vector3 direction = Vector3.subtract(cameraPosition, cardPosition);
        Quaternion lookRotation = Quaternion.lookRotation(direction, Vector3.up());
        infoCard.setWorldRotation(lookRotation);
    }

}
