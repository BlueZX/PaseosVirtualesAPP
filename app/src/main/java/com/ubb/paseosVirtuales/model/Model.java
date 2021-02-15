package com.ubb.paseosVirtuales.model;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.collision.Box;
import com.google.ar.sceneform.collision.CollisionShape;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;

public class Model {
    String obj;
    String texture;
    String shadow;
    ViewRenderable info;

    public void setViewRenderable(ViewRenderable vr){
        info = vr;
    }

    public Node getViewRenderable(CollisionShape collisionShape, Vector3 cameraPosition){
        Box box = (Box) collisionShape;
        Vector3 size = box.getSize();

        Node node = new Node();
        node.setRenderable(this.info);
        node.setLocalPosition(new Vector3(size.x * 0.5f, size.y * 1.25f, size.z * 0f));

        Vector3 cardPosition = node.getWorldPosition();
        Vector3 direction = Vector3.subtract(cameraPosition, cardPosition);
        Quaternion lookRotation = Quaternion.lookRotation(direction, Vector3.up());
        node.setWorldRotation(lookRotation);

        //node.setLocalRotation(Quaternion.axisAngle(new Vector3(-1.0f, -1.0f, 0.5f), 90));
        //node.setWorldRotation(Quaternion.axisAngle(new Vector3(-1.0f, -1.0f, 0.5f), 90));
        //node.setLocalScale(new Vector3(0.0f, 0.0f, 0.0f));
        return node;
    }
}
