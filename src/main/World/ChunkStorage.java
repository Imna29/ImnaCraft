package main.World;

import org.joml.Vector3i;

public class ChunkStorage {
    public Chunk[][] chunks;

    public ChunkStorage() {
        chunks = new Chunk[12800][12800];
    }

    public void storeChunk(Chunk chunk) {
        int newX = chunk.position.x;
        int newZ = chunk.position.z;

        chunks[newX + chunks.length / 2][newZ + chunks.length / 2] =chunk;

    }

    public Chunk getChunk(Vector3i position, boolean shouldGenerate) {
        if (shouldGenerate && chunks[position.x + chunks.length /2][position.z + chunks.length /2] == null) {
            storeChunk(new Chunk(new Vector3i(position.x, 0, position.z)));
        }

        return chunks[position.x + chunks.length /2][position.z + chunks.length /2];
    }

    public Chunk getChunk(int x, int z, boolean shouldGenerate) {
        return getChunk(new Vector3i(x, 0, z), shouldGenerate);
    }
}
