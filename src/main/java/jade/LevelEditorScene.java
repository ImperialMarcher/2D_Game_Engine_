package jade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import jade.components.Sprite;
import jade.components.SpriteRenderer;
import jade.components.SpriteSheet;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

public class LevelEditorScene extends Scene
{
    private GameObject obj1;
    private SpriteSheet sprites;
    SpriteRenderer obj1SpriteRenderer;

    @Override
    public void init()
    {
        loadResources();

        camera = new Camera(new Vector2f());

        sprites = AssetPool.getSpriteSheet("assets/images/spriteSheet.png");

        obj1 = new GameObject("Object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), 2);
        obj1SpriteRenderer = new SpriteRenderer();
        obj1SpriteRenderer.setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        obj1.addComponent(obj1SpriteRenderer);
        addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 3);
        SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
        Sprite obj2Sprite = new Sprite();
        obj2Sprite.setTexture(AssetPool.getTexture("assets/images/testImage2.png"));
        obj2SpriteRenderer.setSprite(obj2Sprite);
        obj2.addComponent(obj2SpriteRenderer);
        addGameObjectToScene(obj2);

        GameObject obj3 = new GameObject("Object 3", new Transform(new Vector2f(600, 100), new Vector2f(256, 256)), 4);
        SpriteRenderer obj3SpriteRenderer = new SpriteRenderer();
        obj3SpriteRenderer.setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        obj3.addComponent(obj3SpriteRenderer);
        addGameObjectToScene(obj3);
        activeGameObject = obj3;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(obj1SpriteRenderer));
    }

    private void loadResources()
    {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/images/spriteSheet.png", new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheet.png"), 16, 16, 26, 0));
    }

    @Override
    public void update(float deltaTime)
    {
        for (GameObject go : gameObjects)
        {
            go.update(deltaTime);
        }

        renderer.render();
    }

    @Override
    public void imgui()
    {
        ImGui.begin("Test window");
        ImGui.text("Some random text");
        ImGui.end();
    }
}
