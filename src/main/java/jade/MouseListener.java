package jade;

import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY, xPos, yPos, lastX, lastY;
    private final boolean[] mouseButtonPressed = new boolean[9];
    private boolean isDragging;

    private MouseListener() {
        scrollX = 0.0;
        scrollY = 0.0;
        xPos = 0.0;
        yPos = 0.0;
        lastX = 0.0;
        lastY = 0.0;
    }

    public static MouseListener get() {
        if (instance == null) {
            instance = new MouseListener();
        }

        return instance;
    }

    public static void mousePosCallback(long window, double xPos, double yPos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xPos;
        get().yPos = yPos;
        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2];
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            get().mouseButtonPressed[button] = false;
            get().isDragging = false;
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void endFrame() {
        get().scrollX = 0.0;
        get().scrollY = 0.0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static float getX() {
        return (float)get().xPos;
    }

    public static float getY() {
        return (float)get().yPos;
    }

    public static float getOrthoX()
    {
        float currentX = (getX() / (float)Window.getWidth()) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(currentX, 0.0f, 0.0f, 1.0f);
        tmp.mul(Window.getCurrentScene().getCamera().getInverseProjection()).mul(Window.getCurrentScene().getCamera().getInverseView());

        return tmp.x;
    }

    public static float getOrthoY()
    {
        float currentY = ((Window.getHeight() - getY()) / (float)Window.getHeight()) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(0.0f, currentY, 0.0f, 1.0f);
        tmp.mul(Window.getCurrentScene().getCamera().getInverseProjection()).mul(Window.getCurrentScene().getCamera().getInverseView());

        return tmp.y;
    }

    public static float getDX() {
        return (float)(get().lastX - get().xPos);
    }

    public static float getDY() {
        return (float)(get().lastY - get().yPos);
    }

    public static float getScrollX() {
        return (float)get().scrollX;
    }

    public static float getScrollY() {
        return (float)get().scrollY;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button) {
        if (button < get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[button];
        } else {
            return false;
        }
    }
}
