package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;

public class SunHalo {

    private static final int SUN_HALO_RADIUS = 150;
    private static final String SUN_HALO_TAG = "sunHalo";

    /**
     * creates the sun halo
     * @param gameObjects objects in game
     * @param layer layer of the sun halo in game
     * @param sun sun in the game
     * @param color color of the sun halo
     * @return sun halo
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            GameObject sun,
            Color color){

        Renderable ovalRenderable =
                new OvalRenderable(color);
        GameObject sunHalo = new GameObject(Vector2.ZERO, new Vector2(SUN_HALO_RADIUS, SUN_HALO_RADIUS),
                ovalRenderable);
        gameObjects.addGameObject(sunHalo, layer);
        sunHalo.setTag(SUN_HALO_TAG);
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));
        return sunHalo;
    }
};
