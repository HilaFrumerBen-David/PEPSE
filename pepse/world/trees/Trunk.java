
package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.Random;

public class Trunk {


    private static final Color TRUNK_COLOR = new Color(100, 50, 20);
    private static final int MIN_HEIGHT = 5;
    private static final int MAX_HEIGHT = 10;
    public static final String TRUNK_TAG = "trunk";
    public static final String TOP_TRUNK_TAG = "topTrunk";

    /**
     * creates the trunk of a tree
     * @param gameObjects objects in game
     * @param layer layer of the trunk
     * @param groundHeightAt height of ground in relevant col
     * @param xLocation relevant col
     * @param random randomize
     * @return number of blocks to create the trunk
     */
    public static int create(GameObjectCollection gameObjects, int layer, float groundHeightAt,
                             float xLocation, Random random) {

        int numBlocksToCreate = random.nextInt(MAX_HEIGHT);
        while (numBlocksToCreate < MIN_HEIGHT)
        {
            numBlocksToCreate = random.nextInt(MAX_HEIGHT);
        }

        for (int i = 0; i < numBlocksToCreate; i++) {
            Vector2 locOfBlock = new Vector2(xLocation, groundHeightAt - (Block.SIZE * (i + 1)));
            Renderable brownRectangle =
                    new RectangleRenderable(ColorSupplier.approximateColor(TRUNK_COLOR));
            Block block = new Block(locOfBlock, brownRectangle);
            gameObjects.addGameObject(block, layer);
            if (i == numBlocksToCreate-1)
            {
                block.setTag(TOP_TRUNK_TAG);
            }
            else {block.setTag(TRUNK_TAG);}
        }
        return numBlocksToCreate;
    }
};


