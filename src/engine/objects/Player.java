package engine.objects;

import engine.io.Input;
import engine.io.Window;
import main.World.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.glfw.GLFW;

public class Player {
    public Camera camera;
    public Vector3f position, rotation;
    public Matrix4f playerPos;
    private float moveSpeed = 0f;
    private final float defaultMoveSpeed = 10f;
    private double oldMouseX = 0;
    private double oldMouseY = 0;
    private final float rotationSpeed = 4f;
    private long time;
    private int frames = 0;

    private float yVelocity = 0.0f;
    private boolean jumpRequested = false;
    private final float gravity = -1f;
    Vector3i lookingAtBlock = null;
    Vector3i placeOnBlock = null;
    Vector3i groundBlock = null;
    private double blockPlaceDelay = 0.1f;
    private double lastTimeBlockPlaced = 0;
    private double lastTimeBlockBroken = 0;
    private double blockBreakDelay = 0.5f;
    private boolean isBlockedFront = false;
    private boolean isBlockedLeft = false;
    private boolean isBlockedRight = false;
    private boolean isBlockedUp = false;
    private boolean isBlockedBehind = false;
    private boolean isGrounded = false;

    public Player(Vector3f position) {
        camera = new Camera(new Vector3f(position).add(0.0f, 1.7f, 0.0f), new Vector3f(0.0f, 0f, 0.0f));
        rotation = new Vector3f(new Vector3f(180.0f, 0f, 0.0f));
        this.position = position;
    }

    public void update() {
        moveSpeed = defaultMoveSpeed;

        if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) moveSpeed *= 2;

        double newMouseX = Input.getMouseX();
        double newMouseY = Input.getMouseY();

        playerPos = new Matrix4f().translate(position).invert();
        camera.cameraPos = new Matrix4f().translate(camera.position).invert();


        playerPos = playerPos.rotateLocalX(Math.toRadians(rotation.y));
        camera.cameraPos.rotateLocalY(Math.toRadians(camera.rotation.x));
        camera.cameraPos.rotateLocalX(Math.toRadians(camera.rotation.y));

        float x = (float) (Math.cos(Math.toRadians(camera.rotation.x)) * moveSpeed * Window.deltaTime);
        float z = (float) (Math.sin(Math.toRadians(camera.rotation.x)) * moveSpeed * Window.deltaTime);
        float y = (float) (moveSpeed * Window.deltaTime);


        if (Input.isKeyDown(GLFW.GLFW_KEY_A) && !isBlockedLeft) position.add(new Vector3f(-x, 0, -z));
        if (Input.isKeyDown(GLFW.GLFW_KEY_D) && !isBlockedRight) position.add(new Vector3f(x, 0, z));
        if (Input.isKeyDown(GLFW.GLFW_KEY_W) && !isBlockedFront) position.add(new Vector3f(z, 0, -x));
        if (Input.isKeyDown(GLFW.GLFW_KEY_S) && !isBlockedBehind) position.add(new Vector3f(-z, 0, x));
        position.add(new Vector3f(0.0f, yVelocity, 0.0f));

//        if (Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) position = new Vector3f(0, y, 0).add(position);

//        if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
//            if (!isGrounded) position = new Vector3f(0, -y, 0).add(position);
//        }

        jumpRequested = Input.isKeyDown(GLFW.GLFW_KEY_SPACE);


        if (groundBlock != null && !jumpRequested) {
            position.y = groundBlock.y + 1.0f;
        } else {
            applyGravity();
        }


        if (isGrounded) {
            if (jumpRequested) {
                yVelocity = 0.2f;
                jumpRequested = false;
                isGrounded = false;
            }
        }
        camera.position = new Vector3f(position).add(0.0f, 1.7f, 0.0f);


        float dx = (float) (newMouseX - oldMouseX);
        float dy = (float) (newMouseY - oldMouseY);

        rotation.add((float) (dx * rotationSpeed * Window.deltaTime), 0, 0);
        camera.rotation.add((float) (dx * rotationSpeed * Window.deltaTime), (float) (dy * rotationSpeed * Window.deltaTime), 0.0f);


        if (camera.rotation.y >= 90) {
            camera.rotation.y = 90;
        } else if (camera.rotation.y < -90) {
            camera.rotation.y = -90;
        }

        oldMouseX = newMouseX;
        oldMouseY = newMouseY;

        updateLookingAtBlock();
        updateBlockages();

        if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            if (lookingAtBlock != null) {
                if(Window.currentFrame - lastTimeBlockBroken >  blockBreakDelay){
                    World.removeBlock(lookingAtBlock);
                    lastTimeBlockBroken = Window.currentFrame;
                }
            }
        }

        if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
            if (placeOnBlock != null) {
                if(Window.currentFrame - lastTimeBlockPlaced >  blockPlaceDelay){
                    World.placeBlock(placeOnBlock, (byte) 1);
                    lastTimeBlockPlaced = Window.currentFrame;
                }
            }
        }

        frames++;
        if (System.currentTimeMillis() > time + 100) {
            GLFW.glfwSetWindowTitle(Window.window, String.format("%s %s %s - %s", position.x, position.y, position.z, frames * 10));
            time = System.currentTimeMillis();
            frames = 0;
        }

    }

    private void applyGravity() {
        if (!isGrounded) if (yVelocity >= gravity) yVelocity += gravity * Window.deltaTime;
    }

    private @Nullable
    Vector3i raycast(Vector3f startingPosition, Vector3f direction, float step, float distance) {
        int checkIndex = 0;

        float x = startingPosition.x;
        float y = startingPosition.y;
        float z = startingPosition.z;

        float dx = direction.x;
        float dy = direction.y;
        float dz = direction.z;

        placeOnBlock = null;


        while (checkIndex * step <= distance) {
            Vector3f block = new Vector3f(x + dx * checkIndex * step, y + dy * checkIndex * step, z + dz * checkIndex * step);
            block.floor();
            Vector3i blockToCheck = new Vector3i((int) block.x, (int) block.y, (int) block.z);
            if (World.checkIfBlockIsSolid(blockToCheck)) {
                return blockToCheck;
            }
            placeOnBlock = blockToCheck;

            checkIndex++;
        }

        return null;
    }

    private @NotNull Vector3f lookingAt() {
        float m = Math.sin(Math.toRadians(camera.rotation.y - 90));

        float x = (Math.cos(Math.toRadians(camera.rotation.x - 90))) * m;
        float y = Math.sin(Math.toRadians(camera.rotation.y));
        float z = (Math.sin(Math.toRadians(camera.rotation.x - 90))) * m;

        return new Vector3f(-x, -y, -z);
    }

    private void updateLookingAtBlock() {
        lookingAtBlock = raycast(new Vector3f(camera.position), lookingAt(), 0.1f, 5f);
    }

    private void updateBlockages() {
        isGrounded = false;
        isBlockedLeft = false;
        isBlockedRight = false;
        isBlockedFront = false;
        isBlockedBehind = false;
        isBlockedUp = false;

        float x = Math.cos(Math.toRadians(camera.rotation.x));
        float z = Math.sin(Math.toRadians(camera.rotation.x));


        Vector3i blockToCheck = raycast(this.position, new Vector3f(0.0f, -1.0f, 0.0f), 0.1f, 0.1f);
        groundBlock = null;
        if (blockToCheck != null && World.checkIfBlockIsSolid(blockToCheck)) {
            groundBlock = blockToCheck;
            isGrounded = true;
        }

        blockToCheck = raycast(position, new Vector3f(z, 0.0f, -x), 0.05f, 0.3f);
        if (blockToCheck != null && World.checkIfBlockIsSolid(blockToCheck)) isBlockedFront = true;

        blockToCheck = raycast(camera.position, new Vector3f(z, 1f, -x), 0.05f, 0.1f);
        if (blockToCheck != null && World.checkIfBlockIsSolid(blockToCheck)) isBlockedFront = true;

        blockToCheck = raycast(position, new Vector3f(x, 0, z), 0.05f, 0.3f);
        if (blockToCheck != null && World.checkIfBlockIsSolid(blockToCheck)) isBlockedRight = true;

        blockToCheck = raycast(position, new Vector3f(x, 1f, z), 0.05f, 0.3f);
        if (blockToCheck != null && World.checkIfBlockIsSolid(blockToCheck)) isBlockedRight = true;

        blockToCheck = raycast(position, new Vector3f(new Vector3f(-x, 0, -z)), 0.05f, 0.3f);
        if (blockToCheck != null && World.checkIfBlockIsSolid(blockToCheck)) isBlockedLeft = true;

        blockToCheck = raycast(position, new Vector3f(new Vector3f(-x, 1f, -z)), 0.05f, 0.3f);
        if (blockToCheck != null && World.checkIfBlockIsSolid(blockToCheck)) isBlockedLeft = true;


        blockToCheck = raycast(position,new Vector3f(-z, 0.0f, x), 0.05f, 0.3f);
        if (blockToCheck != null && World.checkIfBlockIsSolid(blockToCheck)) isBlockedBehind = true;

        blockToCheck = raycast(position,new Vector3f(-z, 1.0f, x), 0.05f, 0.3f);
        if (blockToCheck != null && World.checkIfBlockIsSolid(blockToCheck)) isBlockedBehind = true;


    }
}
