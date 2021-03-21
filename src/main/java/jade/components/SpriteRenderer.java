package jade.components;

import imgui.ImGui;
import jade.Component;
import jade.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Texture;

public class SpriteRenderer extends Component
{
    private final Vector4f color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private Sprite sprite = new Sprite();

    private transient Transform lastTransform;
    private transient boolean isDirty;

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

    @Override
    public void imgui()
    {
        float[] imColor = {color.x, color.y, color.z, color.w};

        if (ImGui.colorPicker4("Color Picker: ", imColor))
        {
            color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
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
