package main.World;

public record BlockType(String blockName, boolean isSolid, int backFaceTexture, int frontFaceTexture,
                        int leftFaceTexture, int rightFaceTexture, int topFaceTexture, int bottomFaceTexture) {



    public int GetTextureID (int faceIndex) {
        return switch (faceIndex) {
            case 1 -> backFaceTexture;
            case 0 -> frontFaceTexture;
            case 2 -> topFaceTexture;
            case 3 -> bottomFaceTexture;
            case 4 -> leftFaceTexture;
            case 5 -> rightFaceTexture;
            default -> 0;
        };
    }
}
