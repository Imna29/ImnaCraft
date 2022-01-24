package engine.objects;

import engine.io.Input;
import engine.io.Window;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.glfw.GLFW;

public class Camera {
    public Vector3f position, rotation;
    public Matrix4f cameraPos;

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }


    public Matrix4f getPosition() {
        return this.cameraPos;
    }

    public Vector3f getPlayerPosition(){
        return this.position;
    }

    public Vector3f getRotation() {
        return this.rotation;
    }
}
