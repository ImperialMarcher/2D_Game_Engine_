package renderer;

import jade.Window;
import components.SpriteRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class RenderBatch implements Comparable<RenderBatch>
{
    // Vertex
    // =======================================================================
    // Pos              Color                           Tex co-ords     Tex ID
    // float, float,    float, float, float, float,     float, float,   float
    // =======================================================================
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEXTURE_COORDINATES_SIZE = 2;
    private final int TEXTURE_ID_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEXTURE_COORDINATES_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEXTURE_ID_OFFSET = TEXTURE_COORDINATES_OFFSET + TEXTURE_COORDINATES_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 9;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private final SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private final float[] vertices;
    private final int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private final List<Texture> textures;
    private int vaoID, vboID;
    private final int maxBatchSize;
    private final Shader shader;
    private final int zIndex;

    public RenderBatch(int maxBatchSize, int zIndex)
    {
        sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;
        shader = AssetPool.getShader("assets/shaders/default.glsl");
        this.zIndex = zIndex;

        // 4 vertices quad
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        numSprites = 0;
        hasRoom = true;
        textures = new ArrayList<>();
    }

    public void start()
    {
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long)vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable the buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEXTURE_COORDINATES_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_COORDINATES_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEXTURE_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_ID_OFFSET);
        glEnableVertexAttribArray(3);
    }

    public void addSprite(SpriteRenderer sprite)
    {
        // Get index and add renderObject
        int index = numSprites;
        sprites[index] = sprite;
        numSprites++;

        if (sprite.getTexture() != null)
        {
            if (!hasTexture(sprite.getTexture()))
            {
                textures.add(sprite.getTexture());
            }
        }

        // Add properties to local vertices array
        loadVertexProperties(index);

        if (numSprites >= maxBatchSize)
        {
            hasRoom = false;
        }
    }

    public void render()
    {
        boolean reBufferData = false;

        for (int i = 0; i < numSprites; i++)
        {
            SpriteRenderer spriteRenderer = sprites[i];

            if (spriteRenderer.isDirty())
            {
                loadVertexProperties(i);
                spriteRenderer.setClean();
                reBufferData = true;
            }
        }

        if (reBufferData)
        {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        // Use shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getCurrentScene().getCamera().getViewMatrix());

        for (int i = 0; i < textures.size(); i++)
        {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }

        shader.uploadIntArray("uTextures", texSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (Texture texture : textures)
        {
            texture.unbind();
        }

        shader.detach();
    }

    private void loadVertexProperties(int index)
    {
        SpriteRenderer sprite = sprites[index];

        // Find offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;
        Vector4f color = sprite.getColor();
        Vector2f[] textureCoordinates = sprite.getTextureCoordinates();
        int texID = 0;

        if (sprite.getTexture() != null)
        {
            for (int i = 0; i < textures.size(); i++)
            {
                if (textures.get(i).equals(sprite.getTexture()))
                {
                    texID = i + 1;
                    break;
                }
            }
        }

        // Add vertices with the appropriate properties
        float xAdd = 1.0f;
        float yAdd = 1.0f;

        for (int i = 0; i < 4; i++)
        {
            if (i == 1)
            {
                yAdd = 0.0f;
            }
            else if (i == 2)
            {
                xAdd = 0.0f;
            }
            else if (i == 3)
            {
                yAdd = 1.0f;
            }

            // Load position
            vertices[offset] = sprite.gameObject.transform.position.x + xAdd * sprite.gameObject.transform.scale.x;
            vertices[offset + 1] = sprite.gameObject.transform.position.y + yAdd * sprite.gameObject.transform.scale.y;

            // Load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            // Load texture coordinates
            vertices[offset + 6] = textureCoordinates[i].x;
            vertices[offset + 7] = textureCoordinates[i].y;

            // Load texture ID
            vertices[offset + 8] = texID;

            offset += VERTEX_SIZE;
        }
    }

    private int[] generateIndices()
    {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * maxBatchSize];

        for (int i = 0; i < maxBatchSize; i++)
        {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    private void loadElementIndices(int[] elements, int index)
    {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // 3, 2, 0, 0, 2, 1,        7, 6, 4, 4, 6, 5
        // Triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset;
        // Triangle 2
        elements[offsetArrayIndex + 3] = offset;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom()
    {
        return hasRoom;
    }

    public boolean hasTextureRoom()
    {
        return textures.size() < 8;
    }

    public boolean hasTexture(Texture texture)
    {
        return textures.contains(texture);
    }

    public int getZIndex()
    {
        return zIndex;
    }

    @Override
    public int compareTo(@NotNull RenderBatch o)
    {
        return Integer.compare(zIndex, o.zIndex);
    }
}
