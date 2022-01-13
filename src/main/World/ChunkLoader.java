package main.World;

import java.util.List;

public class ChunkLoader implements Runnable {
    List<Chunk> chunksToLoad;

    public void loadChunks(List<Chunk> chunk) {
        this.chunksToLoad = chunk;
    }

    @Override
    public void run() {
        if (chunksToLoad == null)
            return;

        for (Chunk chunkToLoad : chunksToLoad) {
            if (chunkToLoad.getMesh() == null) {
                chunkToLoad.generateMesh();
            }

        }
    }
}
