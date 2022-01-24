package engine.io;

import org.lwjgl.glfw.*;

public class Input {
    private static final boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
    private static final boolean[] mouseButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static final boolean[] mouseButtonsOnce = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    private static double frameCalled = 0;
    private static double mouseX, mouseY;
    private static double scrollX, scrollY;


    private GLFWKeyCallback keyboardCallback;
    private GLFWCursorPosCallback mouseMoveCallback;
    private GLFWMouseButtonCallback mouseButtonsCallback;
    private GLFWScrollCallback scrollCallback;

    public Input() {
        keyboardCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                keys[key] = (action != GLFW.GLFW_RELEASE);
            }
        };

        mouseMoveCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                mouseX = xpos;
                mouseY = ypos;
            }
        };

        mouseButtonsCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                mouseButtons[button] = (action != GLFW.GLFW_RELEASE);

                if (frameCalled != Window.currentFrame) {
                    mouseButtonsOnce[button] = (action == GLFW.GLFW_PRESS);
                    frameCalled = Window.currentFrame;
                }
            }
        };

        scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                scrollX += xoffset;
                scrollY += yoffset;
            }
        };
    }

    public static boolean isKeyDown(int key) {
        return keys[key];
    }

    public static boolean isButtonDown(int button) {
        return mouseButtons[button];
    }

    public static boolean isButtonDownOnce(int button) {
        return mouseButtonsOnce[button];
    }


    public void destroy() {
        keyboardCallback.free();
        mouseButtonsCallback.free();
        mouseMoveCallback.free();
        scrollCallback.free();
    }

    public static double getScrollX() {
        return scrollX;
    }

    public static double getScrollY() {
        return scrollY;
    }

    public GLFWScrollCallback getScrollCallback() {
        return scrollCallback;
    }

    public static double getMouseX() {
        return mouseX;
    }

    public static double getMouseY() {
        return mouseY;
    }

    public GLFWKeyCallback getKeyboardCallback() {
        return keyboardCallback;
    }

    public GLFWCursorPosCallback getMouseMoveCallback() {
        return mouseMoveCallback;
    }

    public GLFWMouseButtonCallback getMouseButtonsCallback() {
        return mouseButtonsCallback;
    }
}
