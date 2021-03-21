package jade.components;

import org.joml.Vector2f;
import renderer.Texture;

public class Sprite
{
    private Texture texture = null;
    private Vector2f[] textureCoordinates = {
            new Vector2f(1, 1),
            new Vector2f(1, 0),
            new Vector2f(0, 0),
            new Vector2f(0, 1)
    };

    public Texture getTexture()
    {
        return texture;
    }

    public Vector2f[] getTextureCoordinates()
    {
        return textureCoordinates;
    }

    public void setTexture(Texture texture)
    {
        this.texture = texture;
    }

    public void setTextureCoordinates(Vector2f[] textureCoordinates)
    {
        this.textureCoordinates = textureCoordinates;
    }
}
