package jade;

import renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene
{
    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();

    public void init()
    {
    }

    public void start()
    {
        for (GameObject go : gameObjects)
        {
            go.start();
            renderer.add(go);
        }

        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go)
    {
        if (!isRunning)
        {
            gameObjects.add(go);
        }
        else
        {
            gameObjects.add(go);
            go.start();
            renderer.add(go);
        }
    }

    public abstract void update(float deltaTime);

    public Camera getCamera()
    {
        return camera;
    }
}
