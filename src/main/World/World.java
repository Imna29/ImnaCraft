package main.World;

import engine.objects.Camera;
import graphics.Material;
import graphics.Renderer;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjglx.Sys;

import java.util.ArrayList;
import java.util.List;


public class World {
    public Camera camera;
    public Vector3f spawn;
    public static final BlockType[] blockTypes = new BlockType[3];
    public static final Chunk[][] chunks = new Chunk[128][128];
    private final List<Chunk> activeChunks = new ArrayList<>();
    ChunkLoader loader;
    Thread thread;
    public static Material material;

    public World() {
        camera = new Camera(new Vector3f(8 * 8, 70.0f, 8 * 8f), new Vector3f(180.0f, 0f, 0.0f));
        material = new Material("/textures/grass_block.png");
        loader = new ChunkLoader();
        thread = new Thread(loader);
    }


    public void render(Renderer renderer) {

        loader.loadChunks(activeChunks);
        if(!thread.isAlive()){
            if(thread.getState() != Thread.State.TERMINATED)
                thread.start();
        }

        for (Chunk chunk : activeChunks) {
            if(chunk.getMesh() == null || chunk.getMesh().generating)
                continue;

            if(chunk.getMesh().getVao() == 0){
                chunk.getMesh().create();
            }
            renderer.renderChunk(chunk, camera);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean CheckIfBlockIsSolid(Vector3i position) {
        int chunkX = position.x / 16;
        int chunkZ = position.z / 16;

        int localX = position.x - (chunkX * 16);
        int localY = position.y;
        int localZ = position.z - (chunkZ * 16);

        Chunk chunk = chunks[chunkX][chunkZ];

        if (chunk != null) {
            try {
                return blockTypes[chunk.blockMap[localX][localY][localZ]].isSolid();
            } catch (IndexOutOfBoundsException e) {
                return false;
            }
        }
        return false;
    }

    public void generate() {
        material = new Material("/textures/grass_block.png");


        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                Chunk chunk = new Chunk(new Vector3i(x, 0, z));
                chunks[x][z] = chunk;
                activeChunks.add(chunk);
            }
        }
    }

}
