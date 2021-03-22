package renderer;

import jade.GameObject;
import components.SpriteRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer
{
    private final int MAX_BATCH_SIZE = 1000;
    private final List<RenderBatch> batches;

    public Renderer()
    {
        batches = new ArrayList<>();
    }

    public void add(GameObject go)
    {
        SpriteRenderer sprite = go.getComponent(SpriteRenderer.class);

        if (sprite != null)
        {
            add(sprite);
        }
    }

    private void add(SpriteRenderer sprite)
    {
        boolean added = false;

        for (RenderBatch batch : batches)
        {
            if (batch.hasRoom() && batch.getZIndex() == sprite.gameObject.getZIndex())
            {
                Texture texture = sprite.getTexture();

                if (texture == null || batch.hasTextureRoom() || batch.hasTexture(texture))
                {
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }

        if (!added)
        {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.getZIndex());
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }

    public void render()
    {
        for (RenderBatch batch : batches)
        {
            batch.render();
        }
    }
}
