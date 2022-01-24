package main.World;

import engine.objects.Player;
import graphics.Material;
import graphics.Renderer;
import org.joml.Math;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class World {
    public Player player;
    public static final BlockType[] blockTypes = new BlockType[3];
    public static final ChunkStorage chunkStorage = new ChunkStorage();
    private final List<Vector3i> activeChunks = new CopyOnWriteArrayList<>();
    ChunkLoader loader;
    public static Material material;

    private static int viewDistance = 5;

    public World() {
        Vector3f spawnPosition = new Vector3f((float) 16 * 1200, 70.0f, (float) 16 * 1200);
        player = new Player(spawnPosition);
        material = new Material("/textures/grass_block.png");
        loader = ChunkLoader.getInstance();
    }

    public void render(Renderer renderer) {
        updateActiveChunks();

        for (Vector3i chunkPosition : activeChunks) {
            Chunk chunk = chunkStorage.getChunk(chunkPosition, false);

            if (chunk == null) {
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

        renderer.renderChunk(player);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean checkIfBlockIsSolid(Vector3i position) {
        Vector2i chunkPos = getChunkPosition(position);
        Vector3i localPos = getLocalPosition(position);


        Chunk chunk = chunkStorage.getChunk(chunkPos.x, chunkPos.y, false);

        if (chunk != null) {
            try {
                if (blockTypes[chunk.blockMap[localPos.x][localPos.y][localPos.z]] != null) {
                    return blockTypes[chunk.blockMap[localPos.x][localPos.y][localPos.z]].isSolid();
                }
            } catch (IndexOutOfBoundsException e) {
                return false;
            }
        }
        return false;
    }

    public static Vector2i getChunkPosition(Vector3i position) {
        int chunkX = position.x / 16;
        int chunkZ = position.z / 16;

        return new Vector2i(chunkX, chunkZ);
    }

    public static Vector3i getLocalPosition(Vector3i position) {
        int localX = Math.abs(position.x) % 16;
        int localY = Math.abs(position.y);
        int localZ = Math.abs(position.z) % 16;

        return new Vector3i(localX, localY, localZ);
    }

    public static void placeBlock(Vector3i position, byte blockType) {
        Vector2i chunkPos = getChunkPosition(position);
        Vector3i localPos = getLocalPosition(position);

        Chunk chunk = chunkStorage.getChunk(chunkPos.x, chunkPos.y, false);

        if(chunk !=null){
            chunk.blockMap[localPos.x][localPos.y][localPos.z] = blockType;
            chunk.mesh = null;
        }
    }

    public static void removeBlock(Vector3i position) {
        Vector2i chunkPos = getChunkPosition(position);
        Vector3i localPos = getLocalPosition(position);


        Chunk chunk = chunkStorage.getChunk(chunkPos.x, chunkPos.y, false);

        if (chunk != null) {
            chunk.blockMap[localPos.x][localPos.y][localPos.z] = 0;
            chunk.mesh = null;
        } else {
            System.out.println("null");
        }
    }

    public void generate() {
        material = new Material("/textures/grass_block.png");


        for (int x = -viewDistance; x < viewDistance; x++) {
            for (int z = -viewDistance; z < viewDistance; z++) {
                activeChunks.add(new Vector3i(x, 0, z));
            }
        }
    }

    public void updateActiveChunks() {
        Vector3i chunkPosition = getChunkPositionFromPlayerPosition(player.position);

        activeChunks.clear();

        for (int x = chunkPosition.x - viewDistance; x < chunkPosition.x + viewDistance; x++) {
            for (int z = chunkPosition.z - viewDistance; z < chunkPosition.z + viewDistance; z++) {
                activeChunks.add(new Vector3i(x, 0, z));
            }
        }
    }

    public static Vector3i getChunkPositionFromPlayerPosition(Vector3f playerPosition) {
        return new Vector3i((int) (Math.floor(playerPosition.x) / 16), 0, (int) (Math.floor(playerPosition.z) / 16));
    }
}
