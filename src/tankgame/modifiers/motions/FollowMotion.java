package tankgame.modifiers.motions;

import gamengine.game.GameObject;
import gamengine.modifiers.motions.MotionController;
import gamengine.game.MoveableObject;
import tankgame.TankWorld;

public class FollowMotion extends MotionController {

    GameObject go;

    public FollowMotion() {
        super(TankWorld.getInstance());
    }

    public FollowMotion(GameObject followObject) {
        this();
        this.go = followObject;
    }

    public void read(Object theObject) {
        MoveableObject object = (MoveableObject) theObject;
        object.setLocation(go.getLocationPoint());
    }
}
