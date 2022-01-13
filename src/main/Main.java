package main;

import engine.io.Input;
import engine.io.Window;
import graphics.*;
import main.World.BlockType;
import main.World.World;
import org.lwjgl.glfw.GLFW;


public class Main implements Runnable {
    public Thread game;
    public Window window;
    public Renderer renderer;
    public Shader shader;
    public final int WIDTH = 1280, HEIGHT = 760;



    public static World world;

    public void start() {
        game = new Thread(this, "game");
        game.start();
    }

    public void init() {
        System.out.println("Initializing Game!");

        window = new Window(WIDTH, HEIGHT, "ImnaCraaft");
        shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
        renderer = new Renderer(window, shader);


        window.setBackgroundColor(1.0f, 0.5f, 0.0f);
        window.createWindow();
        shader.create();
        window.setMouseLock(true);

        initializeBlocks();
        generateWorld();
    }

    public void initializeBlocks() {
        World.blockTypes[1] = new BlockType(
                "Dirt",
                true,
                0, 0, 0, 0, 0, 0
        );
        World.blockTypes[2] = new BlockType(
                "Grass",
                true,
                1, 1, 1, 1, 2, 0
        );
    }

    public void generateWorld(){
        world = new World();
        world.generate();
    }

    public void run() {
        init();
        while (!window.shouldClose()) {
            update();
            render();

            if (Input.isKeyDown(GLFW.GLFW_KEY_F11)) window.toggleFullScreen();
            if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) window.setMouseLock(false);

        }

        close();
    }

    private void update() {
        window.update();
        world.camera.update();
    }

    private void render() {
        world.render(renderer);
        window.swapBuffers();
    }


    private void close() {
        window.destroy();
        shader.destroy();
    }

    public static void main(String[] args) {
        new Main().start();
    }
}
