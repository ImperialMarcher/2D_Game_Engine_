package jade;

import jade.components.Sprite;
import jade.components.SpriteRenderer;
import jade.components.SpriteSheet;
import org.joml.Vector2f;
import util.AssetPool;

public class LevelEditorScene extends Scene
{
    private GameObject obj1;
    private SpriteSheet sprites;

    @Override
    public void init()
    {
        loadResources();

        camera = new Camera(new Vector2f());

        sprites = AssetPool.getSpriteSheet("assets/images/spriteSheet.png");

        obj1 = new GameObject("Object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), -1);
        obj1.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/images/blendImage1.png"))));
        addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 2);
        obj2.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/images/blendImage2.png"))));
        addGameObjectToScene(obj2);
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
}
