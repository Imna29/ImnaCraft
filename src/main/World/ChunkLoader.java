package main.World;

import org.joml.Vector3i;

import java.util.List;

public class ChunkLoader implements Runnable {
    private static ChunkLoader instance = null;
    private static Thread thread;
    List<Vector3i> chunksToLoad;

    private ChunkLoader() {
    }

    public static ChunkLoader getInstance() {
        if (instance == null)
            instance = new ChunkLoader();
        thread = new Thread(instance);

        return instance;
    }

    public void loadChunks(List<Vector3i> chunk) {
        this.chunksToLoad = chunk;

        if (!thread.isAlive()) {
            if (thread.getState() == Thread.State.TERMINATED)
                thread = new Thread(this);

            thread.start();
        }
    }

    @Override
    public void run() {
        if (chunksToLoad == null)
            return;


        for (Vector3i chunkPosition : chunksToLoad) {
            Chunk chunk = World.chunkStorage.getChunk(chunkPosition, false);
            if (chunk == null)
                continue;


            if (chunk.getMesh() == null) {
                chunk.generateMesh();
            }
        }
    }
}
