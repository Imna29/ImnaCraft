package engine.objects;

import graphics.Mesh;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class GameObject {
    public Vector3i position;
    public Vector3f rotation;
    public Vector3f scale;
    public Mesh mesh;


    public GameObject(){

    }



    public Mesh getMesh() {
        return mesh;
    }
}
