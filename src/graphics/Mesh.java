package graphics;

import main.World.BlockType;
import main.World.World;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Mesh {
    private final List<Integer> indices;
    private int vao, pbo, ibo, tbo;
    private final List<Vector3f> positions;
    private List<Float> positionData;
    private List<Float> textureData;
    private int indicesCount = 0;
    public boolean generating = false;

    public Mesh() {
        positions = new ArrayList<>();
        indices = new ArrayList<>();
        positionData = new ArrayList<>();
        textureData = new ArrayList<>();
    }

    private final Vector3f[] verts = new Vector3f[]{
            new Vector3f(0.0f, 0.0f, 0.0f),
            new Vector3f(1.0f, 0.0f, 0.0f),
            new Vector3f(1.0f, 1.0f, 0.0f),
            new Vector3f(0.0f, 1.0f, 0.0f),
            new Vector3f(0.0f, 0.0f, 1.0f),
            new Vector3f(1.0f, 0.0f, 1.0f),
            new Vector3f(1.0f, 1.0f, 1.0f),
            new Vector3f(0.0f, 1.0f, 1.0f),
    };

    private final int[][] tris = new int[][]{
            {5, 6, 4, 7}, // Front Face
            {0, 3, 1, 2}, // Back Face
            {3, 7, 2, 6}, // Top Face
            {1, 5, 0, 4}, // Bottom Face
            {4, 7, 0, 3}, // Left Face
            {1, 2, 5, 6} // Right Face
    };

    private final Vector3i[] faceCheck = new Vector3i[]{
            new Vector3i(0, 0, 1),
            new Vector3i(0, 0, -1),
            new Vector3i(0, 1, 0),
            new Vector3i(0, -1, 0),
            new Vector3i(-1, 0, 0),
            new Vector3i(1, 0, 0),
    };

    public void addToMesh(byte[][][] blockMap, Vector3i chunkPosition) {
        generating = true;
        for (int x = 0; x < blockMap.length; x++) {
            for (int y = 0; y < blockMap[0].length; y++) {
                for (int z = 0; z < blockMap[0][0].length; z++) {
                    for (int side = 0; side < 6; side++) {
                        boolean shouldRender = true;

                        try {
                            if (World.blockTypes[blockMap[x + faceCheck[side].x][y + faceCheck[side].y][z + faceCheck[side].z]].isSolid()) {
                                shouldRender = false;
                            }
                        } catch (IndexOutOfBoundsException e) {
                            Vector3i toCheck = new Vector3i(x + (chunkPosition.x * 16), y, z + (chunkPosition.z * 16)).add(faceCheck[side]);
                            shouldRender = !World.CheckIfBlockIsSolid(toCheck);
                        }

                        if (!shouldRender) continue;

                        positionData.add(x + verts[tris[side][0]].x);
                        positionData.add(y + verts[tris[side][0]].y);
                        positionData.add(z + verts[tris[side][0]].z);

                        positionData.add(x + verts[tris[side][1]].x);
                        positionData.add(y + verts[tris[side][1]].y);
                        positionData.add(z + verts[tris[side][1]].z);

                        positionData.add(x + verts[tris[side][2]].x);
                        positionData.add(y + verts[tris[side][2]].y);
                        positionData.add(z + verts[tris[side][2]].z);

                        positionData.add(x + verts[tris[side][3]].x);
                        positionData.add(y + verts[tris[side][3]].y);
                        positionData.add(z + verts[tris[side][3]].z);


                        indices.add(indicesCount);
                        indices.add(indicesCount + 1);
                        indices.add(indicesCount + 2);
                        indices.add(indicesCount + 2);
                        indices.add(indicesCount + 1);
                        indices.add(indicesCount + 3);

                        indicesCount += 4;

                        int textureId = World.blockTypes[blockMap[x][y][z]].GetTextureID(side);
                        int textureX = textureId / 4;
                        int textureY = textureId % 4;

                        textureData.add(0.25f * textureY + 0.25f);
                        textureData.add(0.25f * textureX + 0.25f);
                        textureData.add(0.25f * textureY + 0.25f);
                        textureData.add(0.25f * textureX);
                        textureData.add(0.25f * textureY);
                        textureData.add(0.25f * textureX + 0.25f);
                        textureData.add(0.25f * textureY);
                        textureData.add(0.25f * textureX);


                        /*textureData.add(0.25f * textureY + 0.25f);
                        textureData.add(0.25f * textureX + 0.25f);
                        textureData.add(0.25f * textureY);
                        textureData.add(0.25f * textureX + 0.25f);
                        textureData.add(0.25f * textureY + 0.25f);
                        textureData.add(0.25f * textureX);
                        textureData.add(0.25f * textureX);
                        textureData.add(0.25f * textureY);*/
                    }
                }
            }
        }
        generating = false;
    }

    public void create() {
        vao = GL46.glGenVertexArrays();
        GL46.glBindVertexArray(vao);


        FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(positionData.size());
        float[] positionDataArray = new float[positionData.size()];

        for (int index = 0; index < positionData.size(); index++) {
            positionDataArray[index] = positionData.get(index);
        }


        FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(textureData.size());
        float[] textureDataArray = new float[textureData.size()];

        for (int index = 0; index < textureData.size(); index++) {
            textureDataArray[index] = textureData.get(index);
        }

        positionBuffer.put(positionDataArray).flip();
        textureBuffer.put(textureDataArray).flip();
        pbo = storeData(positionBuffer, 0, 3);
        tbo = storeData(textureBuffer, 2, 2);

        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.size());
        int[] indicesArray = new int[indices.size()];

        for (int index = 0; index < indices.size(); index++) {
            indicesArray[index] = indices.get(index);
        }

        indicesBuffer.put(indicesArray).flip();

        ibo = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, ibo);
        GL46.glBufferData(GL46.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL46.GL_STATIC_DRAW);
        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, 0);

        MemoryUtil.memFree(positionBuffer);
        MemoryUtil.memFree(textureBuffer);
        MemoryUtil.memFree(indicesBuffer);

    }

    private int storeData(FloatBuffer buffer, int index, int size) {
        int bufferID = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, bufferID);
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, buffer, GL46.GL_STATIC_DRAW);
        GL46.glVertexAttribPointer(index, size, GL46.GL_FLOAT, false, 0, 0);
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
        return bufferID;
    }

    public void destroy() {
        GL46.glDeleteBuffers(pbo);
        GL46.glDeleteBuffers(ibo);
        GL46.glDeleteBuffers(tbo);


        GL46.glDeleteVertexArrays(vao);
        World.material.destroy();
    }

    public List<Integer> getIndices() {
        return indices;
    }


    public int getVao() {
        return vao;
    }

    public int getPbo() {
        return pbo;
    }

    public int getIbo() {
        return ibo;
    }

    public int getTbo() {
        return tbo;
    }

    public Material getMaterial() {
        return World.material;
    }
}
