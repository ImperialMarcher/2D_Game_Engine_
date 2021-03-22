package scenes;

import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import jade.Camera;
import jade.GameObject;
import jade.Prefabs;
import jade.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import renderer.DebugDraw;
import util.AssetPool;

public class LevelEditorScene extends Scene
{
    private GameObject obj1;
    private SpriteSheet sprites;
    SpriteRenderer obj1SpriteRenderer;
    MouseControls mouseControls = new MouseControls();

    @Override
    public void init()
    {
        loadResources();

        camera = new Camera(new Vector2f());
        sprites = AssetPool.getSpriteSheet("assets/images/spriteSheets/decorationsAndBlocks.png");

        if (levelLoaded)
        {
            activeGameObject = gameObjects.get(0);
            return;
        }

        obj1 = new GameObject("Object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), 2);
        obj1SpriteRenderer = new SpriteRenderer();
        obj1SpriteRenderer.setColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        obj1.addComponent(obj1SpriteRenderer);
        obj1.addComponent(new RigidBody());
        addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 5);
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
    }

    private void loadResources()
    {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/images/spriteSheets/decorationsAndBlocks.png", new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/decorationsAndBlocks.png"), 16, 16, 81, 0));
        AssetPool.getTexture("assets/images/testImage2.png");
    }

    float t = 0.0f;

    @Override
    public void update(float deltaTime)
    {
        mouseControls.update(deltaTime);

        float x = ((float)Math.sin(t) * 200.0f) + 600.0f;
        float y = ((float)Math.cos(t) * 200.0f) + 400.0f;
        t += 0.05f;
        DebugDraw.addLine2D(new Vector2f(600.0f, 400.0f), new Vector2f(x, y), new Vector3f(0.0f, 0.0f, 1.0f));

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

        ImVec2 windowPosition = new ImVec2();
        ImGui.getWindowPos(windowPosition);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);
        float windowX2 = windowPosition.x + windowSize.x;

        for (int i = 0; i < sprites.size(); i++)
        {
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 4;
            float spriteHeight = sprite.getHeight() * 4;
            int id = sprite.getTextureID();
            Vector2f[] textureCoordinates = sprite.getTextureCoordinates();

            ImGui.pushID(i);

            if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoordinates[0].x, textureCoordinates[0].y, textureCoordinates[2].x, textureCoordinates[2].y))
            {
                GameObject object = Prefabs.generateSpriteObject(sprite, spriteWidth, spriteHeight);
                mouseControls.pickupObject(object);
            }

            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;

            if (i + 1 < sprites.size() && nextButtonX2 < windowX2)
            {
                ImGui.sameLine();
            }
        }

        ImGui.end();
    }
}
