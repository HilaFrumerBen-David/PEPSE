package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;


import java.awt.*;


public class Night {

    private static final Color NIGHT_COLOR = Color.BLACK;
    private static final Float MIDNIGHT_OPACITY = 0.5f;
    private static final String NIGHT_TAG = "night";

    private static final float INITIAL_TRANSITION_VALUE = 0f;



    /**
     * creates the night cycle
     * @param gameObjects game objects of the game
     * @param layer layer of the night
     * @param windowDimensions dimensions of the game window
     * @param cycleLength cycle of day and night
     * @return night
     */
    public static GameObject create(GameObjectCollection gameObjects,
                                    int layer,
                                    Vector2 windowDimensions,
                                    float cycleLength) {
        Renderable blackRectangle =
                new RectangleRenderable(ColorSupplier.approximateColor(NIGHT_COLOR));
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions,blackRectangle);
        gameObjects.addGameObject(night, layer);
        night.setTag(NIGHT_TAG);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

        new Transition<Float>(
                night, // the game object being changed
                night.renderer()::setOpaqueness, // the method to call
                INITIAL_TRANSITION_VALUE, // initial transition value
                MIDNIGHT_OPACITY, // final transition value
                Transition.CUBIC_INTERPOLATOR_FLOAT, // use a cubic interpolator
                cycleLength/2, // transtion fully over half a day
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, // Choose appropriate ENUM value
                null); // nothing further to execute upon reaching final value

        return night;

    }

}
