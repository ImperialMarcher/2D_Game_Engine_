package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.lang.Math;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;

public class Shader
{
    private int shaderProgramID;
    private boolean beingUsed;

    private String vertexSource, fragmentSource;
    private final String filePath;

    public Shader(String filePath)
    {
        this.filePath = filePath;

        try
        {
            String source = new String(Files.readAllBytes(Paths.get(filePath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // Find the first pattern after #type 'pattern'
            int index = source.indexOf("#type") + 6;
            int endOfLine = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, endOfLine).trim();

            // Find the second pattern after #type 'pattern'
            index = source.indexOf("#type", endOfLine) + 6;
            endOfLine = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, endOfLine).trim();

            if (firstPattern.equals("vertex"))
            {
                vertexSource = splitString[1];
            }
            else if(firstPattern.equals("fragment"))
            {
                fragmentSource = splitString[1];
            }
            else
            {
                throw new IOException("Unexpected token '" + firstPattern + "' in '" + filePath + "'");
            }

            if (secondPattern.equals("vertex"))
            {
                vertexSource = splitString[2];
            }
            else if(secondPattern.equals("fragment"))
            {
                fragmentSource = splitString[2];
            }
            else
            {
                throw new IOException("Unexpected token '" + secondPattern + "' in '" + filePath + "'");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            assert false : "ERROR [Shader]: Could not open file for shader: '" + filePath + "'";
        }
    }

    public void compile()
    {
        // ========================
        // Compile and link shaders
        // ========================

        int vertexID, fragmentID;

        // First load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        // Check for errors in compilation
        if (glGetShaderi(vertexID, GL_COMPILE_STATUS) == GL_FALSE)
        {
            int length = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR [Shader]: '\" + filePath + \"'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, length));
            assert false : "";
        }

        // Load and compile the fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        // Check for errors in compilation
        if (glGetShaderi(fragmentID, GL_COMPILE_STATUS) == GL_FALSE)
        {
            int length = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR [Shader]: '\" + filePath + \"'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, length));
            assert false : "";
        }

        // Link shaders and check for errors
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        // Check for linking error
        if (glGetProgrami(shaderProgramID, GL_LINK_STATUS) == GL_FALSE)
        {
            int length = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR [Shader]: '" + filePath + "'\n\tLinking shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, length));
            assert false : "";
        }
    }

    public void use()
    {
        if (!beingUsed)
        {
            // Bind shader program
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }

    public void detach()
    {
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String varName, Matrix4f val)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer((int)Math.pow(4, 2));
        val.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f val)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer((int)Math.pow(3, 2));
        val.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadMat2f(String varName, Matrix2f val)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer((int)Math.pow(2, 2));
        val.get(matBuffer);
        glUniformMatrix2fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f val)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, val.x, val.y, val.z, val.w);
    }

    public void uploadVec3f(String varName, Vector3f val)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, val.x, val.y, val.z);
    }

    public void uploadVec2f(String varName, Vector2f val)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, val.x, val.y);
    }

    public void uploadFloat(String varName, float val)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }

    public void uploadTexture(String varName, int val)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }

    public void uploadIntArray(String varName, int[] val)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1iv(varLocation, val);
    }
}
