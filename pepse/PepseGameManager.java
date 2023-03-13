package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Counter;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.NumericEnergyCounter;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Leaf;
import pepse.world.trees.Tree;
import pepse.world.trees.Trunk;


import java.awt.*;
import java.util.Random;

/**
 * The main class of the simulator.
 */
public class PepseGameManager extends GameManager {

    private static final int SUN_HALO_LAYER = Layer.BACKGROUND + 10;
    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int SUN_LAYER = Layer.BACKGROUND + 5;
    private static final int NIGHT_LAYER = Layer.FOREGROUND;
    private static final int CYCLE_LENGTH = 30;
    private static final int LEAF_LAYER = Layer.STATIC_OBJECTS + 20;
    private static final int TOP_GROUND_LAYER = Layer.STATIC_OBJECTS;
    private static final int AVATAR_LAYER = Layer.DEFAULT;
    private static final int TRUNK_LAYER = Layer.STATIC_OBJECTS + 15;
    private static final int MAX_SEED = 250;
    public static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 30);
    private static final int GROUND_LAYER = Layer.STATIC_OBJECTS + 10;
    private Avatar avatar;
    private int max;
    private int min;
    private Vector2 windowsDimensions;
    private Terrain terrain;
    private Tree tree;
    private Vector2 initialAvatarLocation;
    private Counter energyCounter;
    private WindowController windowController;
    private GameObject sun;

    /**
     * initializes the game and the objects
     * @param imageReader Contains a single method: readImage, which reads an image from disk.
     *                 See its documentation for help.
     * @param soundReader Contains a single method: readSound, which reads a wav file from
     *                    disk. See its documentation for help.
     * @param inputListener Contains a single method: isKeyPressed, which returns whether
     *                      a given key is currently pressed by the user or not. See its
     *                      documentation.
     * @param windowController Contains an array of helpful, self explanatory methods
     *                         concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.windowController = windowController;
        this.windowsDimensions = windowController.getWindowDimensions();
        this.initialAvatarLocation = new Vector2(windowController.getWindowDimensions().mult(0.5f));

        //creating sky
        Sky.create(gameObjects(), windowController.getWindowDimensions(), SKY_LAYER);

        //create terrain
        int seed = new Random().nextInt(MAX_SEED);
        this.terrain = new Terrain(gameObjects(), TOP_GROUND_LAYER, windowController.getWindowDimensions(),
                seed);
        this.min = (int) (initialAvatarLocation.x() - windowController.getWindowDimensions().x());
        this.max = (int) (initialAvatarLocation.x() + windowController.getWindowDimensions().x());
        this.terrain.createInRange(min, max);

        //creating night
        Night.create(gameObjects(), NIGHT_LAYER, windowController.getWindowDimensions(), CYCLE_LENGTH);

        //creating sun
        this.sun = Sun.create(gameObjects(), SUN_LAYER, windowController.getWindowDimensions(), CYCLE_LENGTH);

        //creating sun Halo
        SunHalo.create(gameObjects(), SUN_HALO_LAYER, sun, SUN_HALO_COLOR);

        //create trees
        this.tree = new Tree(seed, gameObjects(), terrain::groundHeightAt);
        tree.createInRange(min, max);

        //creating SpongeBob and camera to follow after SpongeBob
        this.avatar = Avatar.create(gameObjects(), AVATAR_LAYER, initialAvatarLocation, inputListener,
                imageReader);
        setCamera(new Camera(avatar,
                windowController.getWindowDimensions().mult(0.5f).subtract(initialAvatarLocation),
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));

        //creating collision :
                // ground and leafs
        gameObjects().layers().shouldLayersCollide(LEAF_LAYER, TOP_GROUND_LAYER, true);
                //ground and SpongeBob
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TOP_GROUND_LAYER, true);
                // trunk Top and SpongeBob
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TRUNK_LAYER, true);

        //creating Numeric Energy Counter
        initializeNumericEnergyCounter();

    }

    /**
     *
     */
    private void initializeNumericEnergyCounter() {
        NumericEnergyCounter numericEnergyCounter = new NumericEnergyCounter(
                Vector2.ZERO, this.gameObjects(), this.avatar::getEnergy);
        numericEnergyCounter.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        this.gameObjects().addGameObject(numericEnergyCounter, Layer.FOREGROUND);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        //Check if we need to build new left world
        if (windowsDimensions.x() * 0.75 > Math.abs(this.avatar.getCenter().x() - min)){
            nextWorldLeft();
        }

        //Check if we need to build new right world
        if (windowsDimensions.x() * 0.75 > Math.abs(max - this.avatar.getCenter().x())) {
            nextWorldRight();
        }
    }

    private void nextWorldRight() {

        //calculating new values
        int newMaxX = (int) (max + windowsDimensions.x() * 0.5);
        int newMinx = (int) (min + windowsDimensions.x() * 0.5);

        //creating terrain and tree on new right world
        this.terrain.createInRange(max, newMaxX);
        this.tree.createInRange(max, newMaxX);

        //removing the world in old left side
        removeWorldInRange(min, newMinx);

        //updating min and max to new val
        this.max = newMaxX;
        this.min = newMinx;
    }

    private void nextWorldLeft() {

        //calculating new values
        int newMinx = (int) (min - windowsDimensions.x() * 0.5);
        int newMaxX = (int) (max - (windowsDimensions.x() * 0.5));

        //creating terrain and tree on new left world
        this.terrain.createInRange(newMinx, min);
        this.tree.createInRange(newMinx, min);

        //removing the world in old right side
        removeWorldInRange(newMaxX, max);

        //updating min and max to new val
        this.max = newMaxX;
        this.min = newMinx;
    }

    private void removeWorldInRange(int minX, int maxX) {
        int[] layers = new int[]{TOP_GROUND_LAYER, GROUND_LAYER, TRUNK_LAYER, LEAF_LAYER};
        for (int layer : layers) {
            for (GameObject gameObj : gameObjects().objectsInLayer(layer)) {
                if ((minX < gameObj.getCenter().x()) && (gameObj.getCenter().x() < maxX)) {
                    gameObjects().removeGameObject(gameObj, layer);
                }
            }
        }
    }

    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
