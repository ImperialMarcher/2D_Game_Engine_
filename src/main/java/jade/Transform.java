package jade;

import org.joml.Vector2f;

public class Transform
{
    public Vector2f position, scale;

    public Transform()
    {
        this(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position)
    {
        this(position, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale)
    {
        this.position = position;
        this.scale = scale;
    }

    public Transform copy()
    {
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
    }

    public void copy(Transform transform)
    {
        transform.position.set(this.position);
        transform.scale.set(this.scale);
    }

    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof Transform))
        {
            return false;
        }

        Transform transform = (Transform)object;

        return transform.position.equals(position) && transform.scale.equals(scale);
    }
}
