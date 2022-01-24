package main.World;

import engine.objects.GameObject;
import graphics.Mesh;
import org.joml.Vector3i;

public class Chunk extends GameObject {
    byte[][][] blockMap = new byte[16][64][16];

    public Chunk(Vector3i position) {
        super.position = position;

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 64; y++) {
                for (int z = 0; z < 16; z++) {
                    if(y == 63){
                        blockMap[x][y][z] = 2;
                    }else{
                        blockMap[x][y][z] = 1;
                    }
                }
            }
        }


    }

    public void generateMesh(){
        mesh = new Mesh();
        mesh.addToMesh(blockMap, position);

    }


    public Mesh getMesh() {
        return mesh;
    }
}
