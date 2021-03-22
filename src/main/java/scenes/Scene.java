package scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import jade.Camera;
import jade.GameObject;
import components.Component;
import components.ComponentDeserializer;
import components.GameObjectDeserializer;
import renderer.Renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class Scene
{
    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected GameObject activeGameObject = null;
    protected boolean levelLoaded = false;

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

    public void sceneImgui()
    {
        if (activeGameObject != null)
        {
            ImGui.begin("Inspector");
            activeGameObject.imgui();
            ImGui.end();
        }

        imgui();
    }

    public void imgui()
    {
    }

    public void saveExit()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Component.class, new ComponentDeserializer()).registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).create();

        try
        {
            FileWriter writer = new FileWriter("level.txt");
            writer.write(gson.toJson(gameObjects));
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void load()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Component.class, new ComponentDeserializer()).registerTypeAdapter(GameObject.class, new GameObjectDeserializer()).create();
        String inFile = "";

        try
        {
            inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (!inFile.equals(""))
        {
            int maxGOid = -1;
            int maxCid = -1;

            GameObject[] objects = gson.fromJson(inFile, GameObject[].class);

            for (GameObject object : objects)
            {
                addGameObjectToScene(object);

                for (Component c : object.getAllComponents())
                {
                    if (c.getUid() > maxCid)
                    {
                        maxCid = c.getUid();
                    }
                }

                if (object.getUid() > maxGOid)
                {
                    maxGOid = object.getUid();
                }
            }

            maxGOid++;
            maxCid++;
            System.out.println(maxGOid);
            System.out.println(maxCid);
            GameObject.init(maxGOid);
            Component.init(maxCid);

            levelLoaded = true;
        }
    }
}
