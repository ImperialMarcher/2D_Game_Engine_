package jade;

import components.Component;

import java.util.ArrayList;
import java.util.List;

public class GameObject
{
    private static int ID_COUNTER = 0;
    private final int uid;

    private final String name;
    private final List<Component> components;
    public Transform transform;
    private final int zIndex;

    public GameObject(String name, Transform transform, int zIndex)
    {
        uid = ID_COUNTER++;
        this.name = name;
        components = new ArrayList<>();
        this.transform = transform;
        this.zIndex = zIndex;
    }

    public <T extends Component> T getComponent(Class<T> componentClass)
    {
        for (Component c : components)
        {
            if (componentClass.isAssignableFrom(c.getClass()))
            {
                try
                {
                    return componentClass.cast(c);
                }
                catch (ClassCastException e)
                {
                    e.printStackTrace();
                    assert false : "ERROR [GameObject]: Casting component '" + c + "'";
                }
            }
        }

        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass)
    {
        for (Component c : components)
        {
            if (componentClass.isAssignableFrom(c.getClass()))
            {
                components.remove(c);
                return;
            }
        }
    }

    public void addComponent(Component c)
    {
        c.generateID();
        components.add(c);
        c.gameObject = this;
    }

    public void update(float deltaTime)
    {
        for (Component c : components)
        {
            c.update(deltaTime);
        }
    }

    public void start()
    {
        for (Component c : components)
        {
            c.start();
        }
    }

    public void imgui()
    {
        for (Component c : components)
        {
            c.imgui();
        }
    }

    public int getZIndex()
    {
        return zIndex;
    }

    public static void init(int maxID)
    {
        ID_COUNTER = maxID;
    }

    public int getUid()
    {
        return uid;
    }

    public List<Component> getAllComponents()
    {
        return components;
    }
}
