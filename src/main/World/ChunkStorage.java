package main.World;

import org.joml.Vector3i;

import java.util.HashMap;

public class ChunkStorage {
    private final HashMap<Vector3i, Chunk> chunkHashMap;

    public ChunkStorage() {
        chunkHashMap = new HashMap<>();
    }

    public void storeChunk(Chunk chunk) {
        chunkHashMap.put(chunk.position, chunk);
    }

    public Chunk getChunk(Vector3i position, boolean shouldGenerate) {
        if(shouldGenerate && chunkHashMap.get(position) == null){
            chunkHashMap.put(position, new Chunk(position));
        }

        return chunkHashMap.get(position);
    }

    public Chunk getChunk(int x, int z, boolean shouldGenerate) {
        return getChunk(new Vector3i(x, 0, z), shouldGenerate);
    }
}
