package tankgame.weapons;

import gamengine.modifiers.weapons.AbstractWeapon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import tankgame.TankWorld;

import tankgame.game.Bullet;
import gamengine.utils.ImageUtils;
import java.awt.Point;
import tankgame.game.Ship;
import tankgame.modifiers.motions.AngledMotion;

public class WeaponBounceMissile extends AbstractWeapon {

    int strength;

    public WeaponBounceMissile() {
        this(5, 10);
    }

    public WeaponBounceMissile(int reload) {
        this(5, reload);
    }

    public WeaponBounceMissile(int strength, int reload) {
        super();
        this.reload = reload;
        this.strength = strength;
    }
    
    @Override
    public Image getImage() {
        BufferedImage biWeapons = ImageUtils.toBufferedImage(TankWorld.sprites.get("weapons"));
        return biWeapons.getSubimage(16, 0, 16, 16);
    }

    //this should be changed to bounce missle
    @Override
    public Bullet getBullet(Ship theShip) {
        Point speed = new Point(6, 0);
        Point location = theShip.getLocationPoint();
        Bullet b = new Bullet(location, speed, strength, new AngledMotion(), theShip, getImage());
        b.setHeading(theShip.getHeading());
        return b;
    }

}
