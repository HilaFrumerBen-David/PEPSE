package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.function.Supplier;


/**
 * Display a graphic object on the game window showing a numeric count of energy left.
 */
public class NumericEnergyCounter extends GameObject {
    private static Vector2 ENERGY_COUNTER = new Vector2(20,20);
    private static final String ENERGY_MSG = "Energy of SpongeBob: %d";
    private final String energyMsg;
    private final TextRenderable textRenderable;
    private final Supplier<Float> getEnergyFunc;
    private final GameObjectCollection gameObjects;

    /**
     * numeric counter of the energy of the avatar
     * @param topLeftCorner location of counter
     * @param gameObjects objects in game
     * @param getEnergyFunc function of the energy
     */
    public NumericEnergyCounter(Vector2 topLeftCorner,
                                GameObjectCollection gameObjects,
                                Supplier<Float> getEnergyFunc) {

         super(topLeftCorner, ENERGY_COUNTER, null);
         this.gameObjects = gameObjects;
         this.getEnergyFunc = getEnergyFunc;

         //create text of energy
         this.energyMsg = ENERGY_MSG;
         String str = String.format(energyMsg, (int)((float)this.getEnergyFunc.get()));
         this.textRenderable = new TextRenderable(str);
         this.textRenderable.setColor(Color.RED);

         //create object of energy
         GameObject energyObg = new GameObject(topLeftCorner, ENERGY_COUNTER, textRenderable);
         gameObjects.addGameObject(energyObg, Layer.FOREGROUND);
         energyObg.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
         }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        this.textRenderable.setString(String.format(this.energyMsg, (int)((float)this.getEnergyFunc.get())));
    }
}
