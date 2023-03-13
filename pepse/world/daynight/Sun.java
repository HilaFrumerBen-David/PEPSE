
package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.function.Consumer;

public class Sun {

    private static final Color SUN_COLOR = Color.YELLOW;
    private static final int SUN_RADIUS = 90;
    private static final float CHANGE_TO_ELIPTIC = 2f;
    private static final String SUN_TAG = "sun";
    private static float sunFirstLocY;
    private static final Float FIRST_ANGLE = 0f;
    private static final Float END_ANGLE = 360f;
    private static final int DIVIDE_Y = 10;

    /**
     *
     * @param gameObjects objects of game
     * @param layer layer of the sun in the game
     * @param windowDimensions dimension of game window
     * @param cycleLength length of cycle of sun
     * @return sun
     */
    public static GameObject create(GameObjectCollection gameObjects,
                                    int layer,
                                    Vector2 windowDimensions,
                                    float cycleLength){

        Renderable yellowOvalRenderable =
                new OvalRenderable(ColorSupplier.approximateColor(SUN_COLOR));
        sunFirstLocY = windowDimensions.y() / DIVIDE_Y;
        GameObject sun = new GameObject(Vector2.ZERO, new Vector2(SUN_RADIUS, SUN_RADIUS),
                yellowOvalRenderable);
        sun.setCenter(new Vector2(windowDimensions.x() / 2, sunFirstLocY));
        gameObjects.addGameObject(sun, layer);
        sun.setTag(SUN_TAG);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);


        new Transition<Float>(
                sun, // the game object being changed
                angle -> sun.setCenter(calcSunPosition(windowDimensions,angle)), // the method to call
                FIRST_ANGLE, // initial transition value
                END_ANGLE, // final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT, // use a cubic interpolator
                cycleLength, // transtion fully over half a day
                Transition.TransitionType.TRANSITION_LOOP, // Choose appropriate ENUM value
                null); // nothing further to execute upon reaching final value
        return sun;
    }

    /**
     * calculates the sun position
     * @param windowDimensions dimensions of game window
     * @param angleInSky angle relevant to Vector2.UP
     * @return position
     */
    private static Vector2 calcSunPosition(Vector2 windowDimensions, float angleInSky) {
        float centerY = windowDimensions.y() / 2;
        float centerX = windowDimensions.x() / 2;
        float radius = centerY - sunFirstLocY;
        angleInSky = (float) Math.toRadians(angleInSky);
        float curY = (float) (centerY - Math.cos(angleInSky) * radius);
        float curX = (float) (centerX + Math.sin(angleInSky) * radius * CHANGE_TO_ELIPTIC);
        return new Vector2(curX, curY);
    }

};

