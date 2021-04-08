package jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import renderer.DebugDraw;
import renderer.FrameBuffer;
import scenes.LevelEditorScene;
import scenes.LevelScene;
import scenes.Scene;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window
{
    private int width, height;
    private final String title;
    private long glfwWindow;
    private ImGuiLayer imGuiLayer;
    private FrameBuffer frameBuffer;

    public float r, g, b, a;


    private static Window instance = null;
    private static Scene currentScene;

    private Window()
    {
        width = 1920;
        height = 1080;
        title = "Mario";
        r = 1.0f;
        g = 1.0f;
        b = 1.0f;
        a = 1.0f;
    }

    public static void changeScene(int newScene)
    {
        switch (newScene)
        {
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "ERROR [Window]: Unknown scene '" + newScene + "'";
                break;
        }

        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public static Window get() {
        if (instance == null) {
            instance = new Window();
        }

        return instance;
    }

    public static Scene getCurrentScene()
    {
        return currentScene;
    }

    public void run() {
        System.out.println("LWJGL version " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if ( glfwWindow == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a mouse callback. It will be called every time a mouse button is pressed, dragged or released.
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(glfwWindow, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            assert vidMode != null;
            glfwSetWindowPos(
                    glfwWindow,
                    (vidMode.width() - pWidth.get(0)) / 2,
                    (vidMode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        imGuiLayer = new ImGuiLayer(glfwWindow);
        imGuiLayer.initImGui();

        frameBuffer = new FrameBuffer(width, height);
        glViewport(0, 0, width, height);

        Window.changeScene(0);
    }

    public void loop() {
        float beginTime = (float)glfwGetTime();
        float endTime;
        float deltaTime = -1.0f;

        // Run the rendering loop until the user has attempted to close
        // the window.
        while (!glfwWindowShouldClose(glfwWindow))
        {
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

            DebugDraw.beginFrame();

            frameBuffer.bind();

            // Set the clear color
            glClearColor(r, g, b, a);
            // Clear the framebuffer
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            if (deltaTime >= 0)
            {
                DebugDraw.draw();
                currentScene.update(deltaTime);
            }

            frameBuffer.unbind();
            imGuiLayer.update(deltaTime, currentScene);
            glfwSwapBuffers(glfwWindow); // swap the color buffers

            endTime = (float)glfwGetTime();
            deltaTime = endTime - beginTime;
            beginTime = endTime;
        }

        currentScene.saveExit();
    }

    public static int getWidth()
    {
        return get().width;
    }

    public static int getHeight()
    {
        return get().height;
    }

    public static void setWidth(int width)
    {
        get().width = width;
    }

    public static void setHeight(int height)
    {
        get().height = height;
    }

    public static FrameBuffer getFrameBuffer()
    {
        return get().frameBuffer;
    }

    public static float getTargetAspectRatio()
    {
        return 16.0f / 9.0f;
    }
}
