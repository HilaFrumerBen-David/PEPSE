package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.PerlinNoise;
import java.awt.*;

/**
 * Responsible for the creation and management of terrain.
 */
public class Terrain {

    private PerlinNoise perlinNoise;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    //    private static final int TERRAIN_DEPTH = 25;
    public static final String TOP_GROUND_TAG = "topGround";
    public static final String GROUND_TAG = "ground";
    private GameObjectCollection gameObjects;
    private static final float MULT_FACTOR = 2 / 3f;
    private final int seed;
    private float groundHeightAtX0;

    private Vector2 windowDimensions;
    private int groundLayer;


    /**
     * constructor
     *
     * @param gameObjects      objects in game
     * @param groundLayer      layer of the ground in game
     * @param windowDimensions dimensions of window
     * @param seed             seed to randomize
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {
        this.groundLayer = groundLayer;
        this.gameObjects = gameObjects;
        this.windowDimensions = windowDimensions;
        this.groundHeightAtX0 = windowDimensions.y() * MULT_FACTOR;
        this.seed = seed;
        this.perlinNoise = new PerlinNoise(seed);

        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
    }

    public float groundHeightAt(float x) {
        float groundHeight = (float) (Block.SIZE * perlinNoise.noise(x / Block.SIZE) * 15);
        if (groundHeight < 0) {
            return groundHeightAtX0;
        } else if (groundHeight + groundHeightAtX0 > windowDimensions.y()) {
            return windowDimensions.y() - 90;
        }
        return groundHeight + groundHeightAtX0;
    }

    /**
     * creates blocks in range
     *
     * @param minX minimum col to create
     * @param maxX maximum col to create
     */
    public void createInRange(int minX, int maxX) {


        int fixMaxX = maxX;
        if (maxX % Block.SIZE != 0) {
            int res = (int) (maxX % Block.SIZE);
            fixMaxX = (int) (maxX + Block.SIZE - res);
        }
        int fixMinx = (int) (minX - (minX % Block.SIZE));
        createAllBlocks(fixMinx, fixMaxX);


    }

    /**
     * handles the creation blocks in range
     *
     * @param minX minimum col to create
     * @param maxX maximum col to create
     */
    private void createAllBlocks(int minX, int maxX) {
        for (int x = 0; x < (maxX - minX) / Block.SIZE; x++) {
            int xCoord = (int) (minX + x * Block.SIZE);
            double highestBlockYCoord = Math.floor(groundHeightAt(xCoord) / Block.SIZE) * Block.SIZE;
            int numBlocksInXCoord =
                    (int) (((int) (2 * windowDimensions.y() - (int) highestBlockYCoord) / Block.SIZE));
            createBlocksInCol(numBlocksInXCoord, xCoord, highestBlockYCoord);
        }
    }

    /**
     * creates blocks in certain col
     *
     * @param numBlocksInXCoord  number of blocks to create
     * @param xCoord             relevant col
     * @param highestBlockYCoord the highest block loction to create
     */
    private void createBlocksInCol(int numBlocksInXCoord, int xCoord, double highestBlockYCoord)
    {
        for (int y = 0; y < numBlocksInXCoord; y++) {
            RectangleRenderable rectangleRenderable =
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
            Block block = new Block(new Vector2(xCoord, (int) highestBlockYCoord + (y * Block.SIZE)),
                    rectangleRenderable);
            if ((y == 0) || (y == 1)) {
                gameObjects.addGameObject(block, groundLayer);
                block.setTag(TOP_GROUND_TAG);
            } else {
                gameObjects.addGameObject(block, groundLayer + 10);
                block.setTag(GROUND_TAG);
            }
        }
    }
}