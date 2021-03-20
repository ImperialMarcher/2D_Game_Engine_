package jade.components;

import jade.Component;
import jade.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Texture;

public class SpriteRenderer extends Component
{
    private final Vector4f color;
    private Sprite sprite;

    private Transform lastTransform;
    private boolean isDirty;

    public SpriteRenderer(Vector4f color)
    {
        this(color, new Sprite(null));
    }

    public SpriteRenderer(Sprite sprite)
    {
        this(new Vector4f(1, 1, 1, 1), sprite);
    }

    public SpriteRenderer(Vector4f color, Sprite sprite)
    {
        this.color = color;
        this.sprite = sprite;
        isDirty = true;
    }

    @Override
    public void start()
    {
        lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(float deltaTime)
    {
        if (!lastTransform.equals(gameObject.transform))
        {
            gameObject.transform.copy(lastTransform);
            isDirty = true;
        }
    }

    public Vector4f getColor()
    {
        return color;
    }

    public Texture getTexture()
    {
        return sprite.getTexture();
    }

    public Vector2f[] getTextureCoordinates()
    {
        return sprite.getTextureCoordinates();
    }

    public void setColor(Vector4f color)
    {
        if (!this.color.equals(color))
        {
            this.color.set(color);
            isDirty = true;
        }
    }

    public void setSprite(Sprite sprite)
    {
        this.sprite = sprite;
        isDirty = true;
    }

    public boolean isDirty()
    {
        return isDirty;
    }

    public void setClean()
    {
        isDirty = false;
    }
}
