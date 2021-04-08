package renderer;

import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer
{
    private int fboID = 0;
    private Texture texture = null;

    public FrameBuffer(int width, int height)
    {
        // Generate framebuffer
        fboID = glGenFramebuffers();
        bind();

        // Create the texture to render the data to, and attach it to our framebuffer
        texture = new Texture(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getTextureID(), 0);

        // Create render buffer to store the depth info
        int rboID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID);

        assert glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE : "ERROR [FrameBuffer]: Framebuffer is not complete";

        unbind();
    }

    public void bind()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
    }

    public void unbind()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getFboID()
    {
        return fboID;
    }

    public int getTextureID()
    {
        return texture.getTextureID();
    }
}
