package components;

import jade.GameObject;
import jade.MouseListener;
import jade.Window;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component
{
    GameObject holdingObject = null;

    public void pickupObject(GameObject go)
    {
        holdingObject = go;
        Window.getCurrentScene().addGameObjectToScene(go);
    }

    public void placeObject()
    {
        holdingObject = null;
    }

    @Override
    public void update(float deltaTime)
    {
        if (holdingObject != null)
        {
            holdingObject.transform.position.x = MouseListener.getOrthoX() - 16;
            holdingObject.transform.position.y = MouseListener.getOrthoY() - 16;

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
            {
                placeObject();
            }
        }
    }
}
