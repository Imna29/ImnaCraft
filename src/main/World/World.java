package main.World;

import engine.objects.Camera;
import graphics.Material;
import graphics.Renderer;
import org.joml.Math;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class World {
    public Camera camera;
    public static final BlockType[] blockTypes = new BlockType[3];
    public static final ChunkStorage chunkStorage = new ChunkStorage();
    private final List<Vector3i> activeChunks = new CopyOnWriteArrayList<>();
    ChunkLoader loader;
    public static Material material;

    private int viewDistance = 5;

    public World() {
        Vector3f spawnPosition = new Vector3f((float) 0, 70.0f, (float) 0);
        camera = new Camera(spawnPosition, new Vector3f(180.0f, 0f, 0.0f));
        material = new Material("/textures/grass_block.png");
        loader = ChunkLoader.getInstance();
    }


    public void render(Renderer renderer) {
        updateActiveChunks();

        for (Vector3i chunkPosition : activeChunks) {
            Chunk chunk = chunkStorage.getChunk(chunkPosition, false);

            if(chunk == null){
                chunk = new Chunk(chunkPosition);
                chunkStorage.storeChunk(chunk);
            }

            if (chunk.getMesh() == null || chunk.getMesh().generating) {
                continue;
            }

            if (chunk.getMesh().getVao() == 0) {
                chunk.getMesh().create();
            }
            renderer.addToRender(chunk);
        }

        loader.loadChunks(activeChunks);

        renderer.renderChunk(camera);



    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean CheckIfBlockIsSolid(Vector3i position) {
        int chunkX = position.x / 16;
        int chunkZ = position.z / 16;

        int localX = Math.abs(position.x) % 16;
        int localY = Math.abs(position.y);
        int localZ = Math.abs(position.z) % 16;


        Chunk chunk = chunkStorage.getChunk(chunkX, chunkZ, false);

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


        for (int x = -viewDistance; x < viewDistance; x++) {
            for (int z = -viewDistance; z < viewDistance; z++) {
                activeChunks.add(new Vector3i(x, 0, z));
            }
        }
    }

    public void updateActiveChunks(){
        Vector3i chunkPosition = getChunkPositionFromPlayerPosition(camera.getPlayerPosition());

        activeChunks.clear();

        for (int x = chunkPosition.x - viewDistance; x < chunkPosition.x + viewDistance; x++) {
            for(int z = chunkPosition.z - viewDistance; z < chunkPosition.z + viewDistance; z++){
                activeChunks.add(new Vector3i(x, 0, z));
            }
        }
    }

    public Vector3i getChunkPositionFromPlayerPosition(Vector3f playerPosition){
        return new Vector3i((int)(Math.floor(playerPosition.x) / 16), 0, (int) (Math.floor(playerPosition.z) / 16));
    }
}
