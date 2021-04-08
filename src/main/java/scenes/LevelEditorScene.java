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
import org.lwjgl.system.CallbackI;
import renderer.DebugDraw;
import util.AssetPool;

public class LevelEditorScene extends Scene
{
    private GameObject obj1;
    private SpriteSheet sprites;
    SpriteRenderer obj1SpriteRenderer;
    GameObject levelEditorStuff = new GameObject("LevelEditor", new Transform(new Vector2f()), 0);

    @Override
    public void init()
    {
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());

        loadResources();

        camera = new Camera(new Vector2f());
        sprites = AssetPool.getSpriteSheet("assets/images/spriteSheets/decorationsAndBlocks.png");

        if (levelLoaded)
        {
            if(gameObjects.size() > 0)
            {
                activeGameObject = gameObjects.get(0);
            }

            return;
        }


    }

    private void loadResources()
    {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/images/spriteSheets/decorationsAndBlocks.png", new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/decorationsAndBlocks.png"), 16, 16, 81, 0));
        AssetPool.getTexture("assets/images/testImage2.png");

        for (GameObject go : gameObjects)
        {
            if (go.getComponent(SpriteRenderer.class) != null)
            {
                SpriteRenderer sr = go.getComponent(SpriteRenderer.class);

                if (sr.getTexture() != null)
                {
                    sr.setTexture(AssetPool.getTexture(sr.getTexture().getFilePath()));
                }
            }
        }
    }

    float x = 0.0f;
    float y = 0.0f;

    @Override
    public void update(float deltaTime)
    {
        levelEditorStuff.update(deltaTime);

        DebugDraw.addCircle(new Vector2f(x, y), 64.0f, new Vector3f(0.0f, 0.0f, 1.0f), 1);
        x += 50.0f * deltaTime;
        y += 50.0f * deltaTime;

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

            if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoordinates[2].x, textureCoordinates[0].y, textureCoordinates[0].x, textureCoordinates[2].y))
            {
                GameObject object = Prefabs.generateSpriteObject(sprite, 32, 32);
                levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
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
