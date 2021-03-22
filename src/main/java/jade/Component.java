package jade;

import imgui.ImGui;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component
{
    public transient GameObject gameObject = null;

    public void start()
    {
    }

    public void update(float deltaTime)
    {
    }

    public void imgui()
    {
        try
        {
            Field[] fields = this.getClass().getDeclaredFields();

            for (Field field : fields)
            {
                if(Modifier.isTransient(field.getModifiers()))
                {
                    continue;
                }

                boolean isPrivate = Modifier.isPrivate(field.getModifiers());

                if (isPrivate)
                {
                    field.setAccessible(true);
                }

                Class<?> type = field.getType();
                Object value = field.get(this);
                String name = field.getName();

                if (type == int.class)
                {
                    int val = (int)value;
                    int[] aVal = {val};

                    if (ImGui.dragInt(name + ": ", aVal))
                    {
                        field.set(this, aVal[0]);
                    }
                }
                else if (type == float.class)
                {
                    float val = (float)value;
                    float[] aVal = {val};

                    if (ImGui.dragFloat(name + ": ", aVal))
                    {
                        field.set(this, aVal[0]);
                    }
                }
                else if (type == boolean.class)
                {
                    boolean val = (boolean)value;

                    if (ImGui.checkbox(name + ": ", val))
                    {
                        field.set(this, !val);
                    }
                }
                else if (type == Vector3f.class)
                {
                    Vector3f val = (Vector3f)value;
                    float[] aVal = {val.x, val.y, val.z};

                    if (ImGui.dragFloat3(name + ": ", aVal))
                    {
                        val.set(aVal[0], aVal[1], aVal[2]);
                    }
                }
                else if (type == Vector4f.class)
                {
                    Vector4f val = (Vector4f)value;
                    float[] aVal = {val.x, val.y, val.z, val.w};

                    if (ImGui.dragFloat4(name + ": ", aVal))
                    {
                        val.set(aVal[0], aVal[1], aVal[2], aVal[3]);
                    }
                }

                if (isPrivate)
                {
                    field.setAccessible(false);
                }
            }
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
}
