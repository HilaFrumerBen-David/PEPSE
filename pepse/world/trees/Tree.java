
package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class Tree {

    private static final int MAX_RANGE = 101;
    private static final String LEAF_TAG = "leaf";
    private static final int ADD_FACTOR_LEAVES = 10;
    private final int seed;
    private final GameObjectCollection gameObjects;
    private final Function<Float, Float> groundHeightFunc;

    private static final Color COLOR_LEAF = new Color(50, 200, 30);

    /**
     * constructor of tree
     * @param seed seed to randomize
     * @param gameObjects objects in game
     * @param groundHeightFunc function that calculates the height of ground in certain col
     */
    public Tree(int seed, GameObjectCollection gameObjects, Function<Float, Float> groundHeightFunc)
    {
        this.seed= seed;
        this.gameObjects = gameObjects;
        this.groundHeightFunc = groundHeightFunc;
    }

    /**
     * creates trees in certain range
     * @param minX minimum col of the range
     * @param maxX maximum col of the range
     */
    public void createInRange(int minX, int maxX) {

        int xMinFix = (int) (minX - (minX % Block.SIZE));
        int xMaxFix =(int) (maxX + (maxX % Block.SIZE));
        for (int i = 0; i < ((xMaxFix - xMinFix) / Block.SIZE) + 1; i++) {
            int xCurCol = (int)(xMinFix + i * Block.SIZE);
            Random rand = new Random(Objects.hash(xCurCol, seed));
            int num = rand.nextInt(MAX_RANGE);
            if (num < 10)  // plant tree
            {
                // create Trunk
                float groundHeightAt = groundHeightFunc.apply((float) xCurCol);
                groundHeightAt = (int) (((int) (groundHeightAt / Block.SIZE)) * Block.SIZE);
                int numBlocksInTrunk = Trunk.create(gameObjects, Layer.STATIC_OBJECTS + 15, groundHeightAt,
                        xCurCol, rand);

                // create leaves
                createLeaves(numBlocksInTrunk, xCurCol, (int) groundHeightAt, rand);
                i++;
            }
        }
    }

    /**
     * creates leaves on a tree
     * @param numBlocksInTrunk number of blocks create the trunk of tree
     * @param xCurCol current col to create leaves
     * @param groundHeightAt height of the ground in relevant col
     * @param rand randomize
     */
    private void createLeaves(int numBlocksInTrunk, int xCurCol, int groundHeightAt, Random rand) {
        int sizeLeaves = chooseSizeLeaves(numBlocksInTrunk);
        int xStartCol = (int)(xCurCol - (sizeLeaves / 2) * Block.SIZE);
        int yStartRow = (int)(groundHeightAt - (numBlocksInTrunk - 2) * Block.SIZE ) ;
        for (int row = 0; row < sizeLeaves; row++) {
            for (int col = 0; col < sizeLeaves; col++) {
                Vector2 locOfBlock = new Vector2(xStartCol + row * Block.SIZE,
                        yStartRow - (Block.SIZE * (col + 1)));
                Renderable greenRectangle =
                        new RectangleRenderable(ColorSupplier.approximateColor(COLOR_LEAF));
                Leaf leaf = new Leaf(locOfBlock, new Vector2(Block.SIZE, Block.SIZE), greenRectangle,
                        Layer.BACKGROUND + ADD_FACTOR_LEAVES,
                        rand);
                gameObjects.addGameObject(leaf, Layer.STATIC_OBJECTS + 20);
                leaf.setTag(LEAF_TAG);
            }
        }
    }

    /**
     * size of the leaves rectangle on a tree
     * @param numBlocksInTrunk number of blocks that creates the trunk
     * @return size of the leaves
     */
    private int chooseSizeLeaves(int numBlocksInTrunk) {
        int num = (numBlocksInTrunk + 4) / 2;
        if (num % 2 == 0)  { num ++; }
        return num;
    }
};
