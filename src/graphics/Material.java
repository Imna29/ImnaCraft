package graphics;

import main.World.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;
import org.lwjgl.stb.STBImage;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

public class Material {
    private Texture texture;
    private int textureID;
    private final String path;
    public ByteBuffer buffer;

    public Material(String path) {
        this.path = path;
    }

    public void create() {
        int[] width = new int[1], height = new int[1], nrChannels = new int[1];
        buffer = STBImage.stbi_load("/home/imna/Documents/Projects/ImnaCraft/resources/textures/textures.png", width, height, nrChannels, 3);
        buffer.flip();

        System.out.println(width[0]);
        System.out.println(height[0]);

        textureID = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, textureID);
        GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_NEAREST);
        GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, GL46.GL_RGB, width[0], height[0], 0, GL46.GL_RGB, GL46.GL_UNSIGNED_BYTE, World.material.buffer);
        GL46.glGenerateMipmap(GL46.GL_TEXTURE_2D);

    }

    public int getTextureID() {
        return textureID;
    }

    public void destroy() {
        GL46.glDeleteTextures(textureID);
    }
}
