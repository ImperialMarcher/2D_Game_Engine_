package components;

import jade.GameObject;
import jade.MouseListener;
import jade.Window;
import util.Settings;

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
            holdingObject.transform.position.x = MouseListener.getOrthoX();
            holdingObject.transform.position.y = MouseListener.getOrthoY();
            holdingObject.transform.position.x = (int)(holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
            holdingObject.transform.position.y = (int)(holdingObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
            {
                placeObject();
            }
        }
    }
}
