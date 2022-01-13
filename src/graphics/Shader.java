package graphics;

import engine.math.Matrix4f;
import engine.utils.FileUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class Shader {
    private String vertexFile, fragmentFile;
    private int vertexID, fragmentID, programID;

    public Shader(String vertexPath, String fragmentPath) {
        vertexFile = FileUtils.loadAsString(vertexPath);
        fragmentFile = FileUtils.loadAsString(fragmentPath);
    }

    public void create() {
        programID = GL46.glCreateProgram();
        vertexID = GL46.glCreateShader(GL46.GL_VERTEX_SHADER);

        GL46.glShaderSource(vertexID, vertexFile);
        GL46.glCompileShader(vertexID);

        if (GL46.glGetShaderi(vertexID, GL46.GL_COMPILE_STATUS) == GL46.GL_FALSE) {
            System.err.println("Vertex Shader: " + GL46.glGetShaderInfoLog(vertexID));
            return;
        }

        fragmentID = GL46.glCreateShader(GL46.GL_FRAGMENT_SHADER);

        GL46.glShaderSource(fragmentID, fragmentFile);
        GL46.glCompileShader(fragmentID);

        if (GL46.glGetShaderi(fragmentID, GL46.GL_COMPILE_STATUS) == GL46.GL_FALSE) {
            System.err.println("Fragment Shader: " + GL46.glGetShaderInfoLog(fragmentID));
            return;
        }

        GL46.glAttachShader(programID, vertexID);
        GL46.glAttachShader(programID, fragmentID);

        GL46.glLinkProgram(programID);
        if (GL46.glGetProgrami(programID, GL46.GL_LINK_STATUS) == GL46.GL_FALSE) {
            System.err.println("Program Linking: " + GL46.glGetProgramInfoLog(programID));
            return;
        }

        GL46.glValidateProgram(programID);
        if (GL46.glGetProgrami(programID, GL46.GL_VALIDATE_STATUS) == GL46.GL_FALSE) {
            System.err.println("Program Validation: " + GL46.glGetProgramInfoLog(programID));
            return;
        }
    }

    public int getUniformLocation(String name) {
        return GL46.glGetUniformLocation(programID, name);
    }

    public void setUniform(String name, float value) {
        GL46.glUniform1f(getUniformLocation(name), value);
    }

    public void setUniform(String name, int value) {
        GL46.glUniform1i(getUniformLocation(name), value);
    }

    public void setUniform(String name, boolean value) {
        GL46.glUniform1i(getUniformLocation(name), value ? 1 : 0);
    }

    public void setUniform(String name, Vector2f value) {
        GL46.glUniform2f(getUniformLocation(name), value.x, value.y);
    }

    public void setUniform(String name, Vector3f value) {
        GL46.glUniform3f(getUniformLocation(name), value.x, value.y, value.z);
    }

    public void setUniform(String name, Matrix4f value) {
        FloatBuffer matrix = MemoryUtil.memAllocFloat(Matrix4f.SIZE * Matrix4f.SIZE);
        matrix.put(value.getAll()).flip();
        GL46.glUniformMatrix4fv(getUniformLocation(name), true, matrix);
    }

    public void setUniform(String name, org.joml.Matrix4f value) {
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        value.get(fb);
        GL46.glUniformMatrix4fv(getUniformLocation(name), false, fb);
    }


    public void bind() {
        GL46.glUseProgram(programID);
    }

    public void unbind() {
        GL46.glUseProgram(0);
    }

    public void destroy() {
        GL46.glDetachShader(programID, vertexID);

        GL46.glDeleteProgram(programID);
    }
}
