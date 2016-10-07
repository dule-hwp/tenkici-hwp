package gamengine.modifiers.weapons;

import java.awt.Image;
import tankgame.game.*;

public class NullWeapon extends AbstractWeapon {

    @Override
    public void fireWeapon(Ship theShip) {
        return;
    }

    @Override
    public void read(Object theObject) {
    }

    @Override
    public Image getImage() {
        return null;
    }

    @Override
    public Bullet getBullet(Ship theShip) {
        return null;
    }

}
