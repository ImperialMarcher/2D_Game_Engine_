package jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera
{
    private final Matrix4f projectionMatrix, viewMatrix, inverseProjection, inverseView;
    public Vector2f positionVector;

    public Camera(Vector2f positionVector)
    {
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.positionVector = positionVector;
        inverseProjection = new Matrix4f();
        inverseView = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection()
    {
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, 32.0f * 40.0f, 0.0f, 32.0f * 21.0f, 0.0f, 100.0f);
        projectionMatrix.invert(inverseProjection);
    }

    public Matrix4f getProjectionMatrix()
    {
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix()
    {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        viewMatrix.identity();
        viewMatrix.lookAt(new Vector3f(positionVector.x, positionVector.y, 20.0f), cameraFront.add(positionVector.x, positionVector.y, 0.0f), cameraUp);
        viewMatrix.invert(inverseView);

        return viewMatrix;
    }

    public Matrix4f getInverseProjection()
    {
        return inverseProjection;
    }

    public Matrix4f getInverseView()
    {
        return inverseView;
    }
}
