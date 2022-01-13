package engine.objects;

import engine.io.Input;
import engine.io.Window;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.glfw.GLFW;

public class Camera {
    private Vector3f position, rotation;
    private float moveSpeed = 0f;
    private float defaultMoveSpeed = 10f;
    private double oldMouseX = 0;
    private double oldMouseY = 0;
    private final float rotationSpeed = 4f;
    private Matrix4f cameraPos;
    private long time;
    private int frames = 0;

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public void update() {
        if(Input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)){
            moveSpeed = defaultMoveSpeed * 2;
        }else{
            moveSpeed = defaultMoveSpeed;
        }

        double newMouseX = Input.getMouseX();
        double newMouseY = Input.getMouseY();

        cameraPos = new Matrix4f().translate(position).invert();
        cameraPos = cameraPos.rotateLocalY(Math.toRadians(rotation.x));
        cameraPos = cameraPos.rotateLocalX(Math.toRadians(rotation.y));

        float x = (float) (Math.cos(Math.toRadians(rotation.x)) * moveSpeed * Window.deltaTime);
        float z = (float) (Math.sin(Math.toRadians(rotation.x)) * moveSpeed * Window.deltaTime);
        float y = (float) (moveSpeed * Window.deltaTime);


        if (Input.isKeyDown(GLFW.GLFW_KEY_A)) position = new Vector3f(-x, 0, -z).add(position);
        if (Input.isKeyDown(GLFW.GLFW_KEY_D)) position = new Vector3f(x, 0, z).add(position);
        if (Input.isKeyDown(GLFW.GLFW_KEY_W)) position = new Vector3f(z, 0, -x).add(position);
        if (Input.isKeyDown(GLFW.GLFW_KEY_S)) position = new Vector3f(-z, 0, x).add(position);
        if (Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) position = new Vector3f(0, y, 0).add(position);
        if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) position = new Vector3f(0, -y, 0).add(position);

        float dx = (float) (newMouseX - oldMouseX);
        float dy = (float) (newMouseY - oldMouseY);

        rotation.add((float) (dx * rotationSpeed  * Window.deltaTime), (float) (dy * rotationSpeed *  Window.deltaTime), 0);
//        rotation.mul((float));

        if(rotation.y >= 90){
            rotation.y = 90;
        }else if(rotation.y < -90){
            rotation.y = -90;
        }

        oldMouseX = newMouseX;
        oldMouseY = newMouseY;

//        System.out.println(position);
        frames++;
        if (System.currentTimeMillis() > time + 100) {
            GLFW.glfwSetWindowTitle(Window.window, String.format("%s %s %s - %s", position.x, position.y, position.z, frames*10));
            time = System.currentTimeMillis();
            frames = 0;
        }
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
