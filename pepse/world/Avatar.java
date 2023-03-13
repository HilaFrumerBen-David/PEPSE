
package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

import static pepse.world.Terrain.TOP_GROUND_TAG;
import static pepse.world.trees.Trunk.TOP_TRUNK_TAG;

public class Avatar extends GameObject {

    private static final float INITIAL_ENERGY = 100f;
    private static final float GRAVITY = 500;
    private static final float VELOCITY_X = 300;
    private static final float VELOCITY_Y = -200;
    private static final String FLY_PATH = "pepse/assets/fly.jpg";
    private static final String JUMP_PATH = "pepse/assets/jump.jpg";
    private static final String STAND_PATH = "pepse/assets/stand.jpg";
    private static final String WALK_LEFT_PATH = "pepse/assets/walkleft.jpg";
    private static final String WALK_RIGHT_PATH = "pepse/assets/walkright.jpg";
    private static final Vector2 AVATAR_DIMENSIONS = new Vector2(60, 60);
    private static final String AVATAR_TAG = "avatar";
    private boolean isMove;
    private Renderable walkLeftBob;
    private Renderable walkRightBob;
    private Renderable standBob;
    private Renderable flyBob;
    private Renderable jumpBob;
    private boolean isFly;
    private final Vector2 topLeftCorner;
    private float energy;
    private ImageReader imageReader;
    private UserInputListener inputListener;
    private boolean isJump;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     *
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, ImageReader imageReader
            ,UserInputListener inputListener) {
        super(topLeftCorner, dimensions, renderable);
        this.isJump = false;
        this.isFly = false;
        this.isMove = false;
        this.topLeftCorner = topLeftCorner;
        this.energy = INITIAL_ENERGY;
        this.imageReader = imageReader;
        this.inputListener = inputListener;
        this.walkLeftBob = imageReader.readImage(WALK_LEFT_PATH, true);
        this.walkRightBob = imageReader.readImage(WALK_RIGHT_PATH, true);
        this.standBob = imageReader.readImage(STAND_PATH, true);
        this.jumpBob = imageReader.readImage(JUMP_PATH, true);
        this.flyBob = imageReader.readImage(FLY_PATH, true);


    }

    /**
     * creates avatar
     * @param gameObjects objects in game
     * @param layer layer of avatar
     * @param topLeftCorner top left corner of initial location
     * @param inputListener input from user listener
     * @param imageReader to read image
     * @return avatar object
     */
    public static Avatar create(GameObjectCollection gameObjects,
                                int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader){

        Renderable standBob = imageReader.readImage(STAND_PATH, true);
        Avatar avatar = new Avatar(topLeftCorner, AVATAR_DIMENSIONS, standBob, imageReader, inputListener);
        gameObjects.addGameObject(avatar, layer);
        avatar.setTag(AVATAR_TAG);
        avatar.physics().preventIntersectionsFromDirection(Vector2.ZERO); // on collision
        avatar.transform().setAccelerationY(GRAVITY); // fall down
        return avatar;
    }

    /**
     * updates each frame, handles avatar behavior
     * @param deltaTime time for updates
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;
        isMove = false;
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            xVel -= VELOCITY_X;
            this.renderer().setRenderable(walkLeftBob);
            isMove = true;
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            xVel += VELOCITY_X;
            this.renderer().setRenderable(walkRightBob);
            isMove = true;
        }
        transform().setVelocityX(xVel);
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && inputListener.isKeyPressed(KeyEvent.VK_SHIFT)
                && energy > 0) {
            isFly = true;
            isMove = true;
            transform().setVelocityY(VELOCITY_Y);
            renderer().setRenderable(flyBob);
        }

        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) &&
                !(inputListener.isKeyPressed(KeyEvent.VK_SHIFT)) && getVelocity().y() == 0){
            isMove = true;
            isJump = true;
            transform().setVelocityY(VELOCITY_Y);
            renderer().setRenderable(jumpBob);
        }
        handleEnergy();
    }

    /**
     * handles the energy changes of the avatar in game
     */
    private void handleEnergy() {

        if (isFly && energy > 0){
            this.energy -= 0.5f;
        }
        if (isFly && (energy == 0)){
            transform().setVelocityY(-VELOCITY_Y);
        }
        if (!isMove && !isJump){
            if (energy < 95.5) {
                this.energy += 0.5f;
            }
            else { energy = 100; }
        }
    }

    /**
     * handles what happened when collided
     * @param other othe object that collided with this
     * @param collision the collision object
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other.getTag().equals(TOP_GROUND_TAG) || (other.getTag().equals(TOP_TRUNK_TAG))) {
            if (other.getTag().equals(TOP_GROUND_TAG) || isFly) {
                this.transform().setVelocityY(0);
            }

            if (!isJump && !isMove) {
                renderer().setRenderable(standBob);
                this.energy += 0.5f;
            }
            isFly = false;
            isMove = false;
            isJump = false;
        }
    }

    /**
     * returns the energy of the avatar
     * @return float represents the energy
     */
    public float getEnergy() { return energy;}
}

