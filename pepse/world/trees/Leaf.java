
package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;
import danogl.components.ScheduledTask;
import java.util.Random;

import static pepse.world.Terrain.TOP_GROUND_TAG;

public class Leaf extends GameObject {

    public static final String LEAF = "leaf";
    private static final float FADEOUT_TIME = 8f;
    private static final int MIN_NUM = 3;
    private static final int MAX_LIFE_TIME = 50;
    private static final int MAX_DEATH_TIME = 12;
    private static final int MAX_WAIT_TIME = 10;
    public static final String FIRST_GROUND = "ground 0";
    public static final String SECOND_GROUND = "ground 1";
    private final int waitTime;
    private final Vector2 center;
    private int layer;
    private Random random;

    private float lifeTime;
    private Transition<Float> horizontalTransition;
    private Transition<Vector2> widthTransition;
    private Transition<Float> angleTransition;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Leaf(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, int layer, Random random) {
        super(topLeftCorner, dimensions, renderable);
        this.layer = layer;
        this.random = random;
        this.waitTime = random.nextInt(MAX_WAIT_TIME) ;
        this.center = this.getCenter();
        createLeafOnTreeTransition();
        birthLeaf();
    }

    /**
     * handles to start the fade out process on the right time
     */
    private void birthLeaf() {
        new ScheduledTask(this,  random.nextInt(MAX_LIFE_TIME) + MIN_NUM,
                false, this::createFadeout);
    }

    /**
     * handles the behavior when the leaf on the tree
     */
    private void createLeafOnTreeTransition() {
        new ScheduledTask(this, waitTime, true, this::createAngleTransition);
        new ScheduledTask(this, waitTime, true, this::createWidthTransition);
    }

    /**
     * moves the leaf right to left on the tree
     */
    private void createAngleTransition(){
        this.angleTransition = new Transition<Float>(
                this, // the game object being changed
                angle -> this.renderer().setRenderableAngle(angle), // the method to call
                -8f, // initial transition value
                8f, // final transition value
                Transition.CUBIC_INTERPOLATOR_FLOAT, // use a cubic interpolator
                1, // transtion fully over half a day
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, // Choose appropriate ENUM value
                null) ;// nothing further to execute upon reaching final value
    }

    /**
     * make the leaf bigger and smaller on the tree
     */
    private void createWidthTransition() {
        this.widthTransition = new Transition<Vector2>(
                this, // the game object being changed
                this::setDimensions, // the method to call
                new Vector2(Block.SIZE , Block.SIZE ), // initial transition value
                new Vector2(Block.SIZE -5, Block.SIZE -5),// final transition value
                Transition.LINEAR_INTERPOLATOR_VECTOR, // use a cubic interpolator
                3, // transition fully over
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, // Choose appropriate ENUM value
                null);// nothing further to execute upon reaching final value

    }

    /**
     * creates movement left and right when the leaf falls
     */
    private void createXTransition() {
        this.horizontalTransition = new Transition<Float>(
                this, // the game object being changed
                speed -> this.transform().setVelocityX(speed), // the method to call
                -30f, // initial transition value
                30f,// final transition value
                Transition.CUBIC_INTERPOLATOR_FLOAT, // use a cubic interpolator
                1, // transition fully over
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, // Choose appropriate ENUM value
                null);// nothing further to execute upon reaching final value
    }

    /**
     * handles the falling from tree process of the leaf
     */
    private void createFadeout() {
        //begin falling down
        this.transform().setVelocityY(80);

        // begin falling from side to side
        createXTransition();

        //begin opaqueness + after fade out handling
        this.renderer().fadeOut(FADEOUT_TIME, this::afterFadeOut);
    }

    /**
     * randomizes the time the leaf should be not visible a
     */
    private void afterFadeOut()
    {
        // waiting death time and start again
        new ScheduledTask(this, random.nextInt(MAX_DEATH_TIME) + MIN_NUM,
                false, this::reBorn);

    }

    /**
     * handles how the leaf get back to the tree and start over
     */
    private void reBorn() {
        this.renderer().setOpaqueness(1);
        this.setCenter(this.center);
        this.birthLeaf();
    }

    /**
     * handles when the leaf collides with object in game
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other.getTag().equals(TOP_GROUND_TAG)){
            new ScheduledTask(this,0.001f,
                    false,()->transform().setVelocity(Vector2.ZERO));
            this.removeComponent(angleTransition);
            this.removeComponent(widthTransition);
            this.removeComponent(horizontalTransition);
        }
    }
};





