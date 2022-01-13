package graphics;

import engine.io.Window;
import engine.objects.Camera;
import engine.objects.GameObject;
import main.World.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

import java.util.HashMap;

public class Renderer {
    private Shader shader;
    private Window window;
    private final HashMap<Mesh, GameObject> cache;
    private final HashMap<Mesh, GameObject> toRender;

    public Renderer(Window window,Shader shader) {
        this.shader = shader;
        this.window = window;
        this.toRender = new HashMap<>();
        this.cache = new HashMap<>();
    }

    public void addToRender(GameObject object){
        toRender.put(object.getMesh(), object);
    }


    public void renderChunk(Camera camera) {

        if (World.material.buffer == null){
            World.material.create();
        }
        for (Mesh mesh:toRender.keySet()) {
            if(mesh == null || mesh.getVao() == 0)
                return;

            GameObject object = toRender.get(mesh);

            GL46.glBindVertexArray(mesh.getVao());
            GL46.glEnableVertexAttribArray(0);
            GL46.glEnableVertexAttribArray(1);
            GL46.glEnableVertexAttribArray(2);
            GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, mesh.getIbo());


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

        toRender.clear();
    }
}
