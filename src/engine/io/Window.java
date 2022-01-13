package engine.io;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;

public class Window {
    private int width;
    private int height;
    private final String title;
    public static long window;
    private int frames;
    public Input input;
    private final Vector3f background = new Vector3f(1.0f, 0.0f, 0.0f);
    private GLFWWindowSizeCallback sizeCallback;
    private boolean isResized;
    private boolean isFullScreen = false;
    private final int[] windowPosX = new int[1];
    private final int[] windowPosY = new int[1];
    private final Matrix4f projection;

    //Time stuff
    public static double deltaTime;
    private double currentFrame;
    private double lastFrame;

    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
        projection = new Matrix4f().perspective(Math.toRadians(70), (float) width / (float) height, 0.1f, 1000.0f);
    }

    public void createWindow() {
        if (!GLFW.glfwInit()) {
            System.err.println("ERROR: GLFW WASN'T INITIALIZED");
            return;
        }

        window = GLFW.glfwCreateWindow(width, height, title, 0, 0);

        if (window == 0) {
            System.err.println("ERROR: WINDOW WASN'T CREATED");
            return;
        }
        input = new Input();

        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if (vidMode != null) {
            windowPosX[0] = (vidMode.width() - width) / 2;
            windowPosY[0] = (vidMode.height() - height) / 2;
        }
        GLFW.glfwSetWindowPos(window, windowPosX[0], windowPosY[0]);
        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
        GL46.glEnable(GL46.GL_DEPTH_TEST);

        createCallbacks();

        GLFW.glfwShowWindow(window);

        //Enable culling
        GL46.glEnable(GL46.GL_CULL_FACE);

        GLFW.glfwSwapInterval(1);

        deltaTime = GLFW.glfwGetTime();
        currentFrame = deltaTime;
        lastFrame = deltaTime;

    }

    private void createCallbacks() {
        sizeCallback = new GLFWWindowSizeCallback() {

            @Override
            public void invoke(long window, int w, int h) {
                width = w;
                height = h;
                isResized = true;
            }
        };

        GLFW.glfwSetKeyCallback(window, input.getKeyboardCallback());
        GLFW.glfwSetCursorPosCallback(window, input.getMouseMoveCallback());
        GLFW.glfwSetMouseButtonCallback(window, input.getMouseButtonsCallback());
        GLFW.glfwSetScrollCallback(window, input.getScrollCallback());
        GLFW.glfwSetWindowSizeCallback(window, sizeCallback);
    }

    public void toggleFullScreen() {
        isResized = true;
        isFullScreen = !isFullScreen;

        if (isFullScreen) {
            GLFW.glfwGetWindowPos(window, windowPosX, windowPosY);
            GLFW.glfwSetWindowMonitor(window, GLFW.glfwGetPrimaryMonitor(), 0, 0, width, height, 0);
        } else {
            GLFW.glfwSetWindowMonitor(window, 0, windowPosX[0], windowPosY[0], width, height, 0);
        }
    }

    public void update() {
        currentFrame = GLFW.glfwGetTime();
        deltaTime = currentFrame - lastFrame;
        lastFrame = currentFrame;


        if (isResized) {
            if (isFullScreen) {
                GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
                if (vidMode != null) {
                    GL46.glViewport(0, 0, vidMode.width(), vidMode.height());
                }
            } else {
                GL46.glViewport(0, 0, width, height);
            }
            isResized = false;
            System.out.println("resize");
        }
        GL46.glClearColor(background.x, background.y, background.z, 1.0f);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        GLFW.glfwPollEvents();


        frames++;
//        if (System.currentTimeMillis() > time + 1000) {
//            GLFW.glfwSetWindowTitle(window, String.format("ImnaCraft | FPS %s", frames));
//            time = System.currentTimeMillis();
//            frames = 0;
//        }
    }

    public void setBackgroundColor(float r, float g, float b) {
        background.x = r;
        background.y = g;
        background.z = b;
    }

    public void destroy() {
        System.out.println("destroying");
        input.destroy();

        sizeCallback.free();
        GLFW.glfwWindowShouldClose(window);
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public void setMouseLock(boolean lock){
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, lock ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
    }

    public void swapBuffers() {
        GLFW.glfwSwapBuffers(window);
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }


    public Matrix4f getProjection() {
        return projection;
    }
}
