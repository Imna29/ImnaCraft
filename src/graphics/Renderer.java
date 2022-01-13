package graphics;

import engine.io.Window;
import engine.objects.Camera;
import engine.objects.GameObject;
import main.World.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

public class Renderer {
    private Shader shader;
    private Window window;
    private int textureID;

    public Renderer(Window window,Shader shader) {
        this.shader = shader;
        this.window = window;
    }

    public void renderChunk(GameObject object, Camera camera) {
        if(object.getMesh() == null || object.getMesh().getVao() == 0)
            return;

        if (World.material.buffer == null){
            World.material.create();
            System.out.println(textureID);
        }

        GL46.glBindVertexArray(object.getMesh().getVao());
        GL46.glEnableVertexAttribArray(0);
        GL46.glEnableVertexAttribArray(1);
        GL46.glEnableVertexAttribArray(2);
        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, object.getMesh().getIbo());


        shader.bind();

        Matrix4f translation = new Matrix4f().translation(new Vector3f(object.position.x*16, object.position.y, object.position.z*16));
        shader.setUniform("model", translation);


        shader.setUniform("view", camera.getPosition());

        shader.setUniform("projection", window.getProjection());
        GL46.glDrawElements(GL46.GL_TRIANGLES, object.getMesh().getIndices().size(), GL46.GL_UNSIGNED_INT, 0);
        shader.unbind();
        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL46.glDisableVertexAttribArray(0);
        GL46.glDisableVertexAttribArray(1);
        GL46.glDisableVertexAttribArray(2);
        GL46.glBindVertexArray(0);

    }
}
