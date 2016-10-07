package tankgame.weapons;

import gamengine.modifiers.weapons.AbstractWeapon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import tankgame.TankWorld;

import tankgame.game.Bullet;
import gamengine.utils.ImageUtils;
import java.awt.Point;
import tankgame.game.Ship;
import tankgame.modifiers.motions.FollowMotion;

public class WeaponShield extends AbstractWeapon {

    int strength;

    public WeaponShield() {
        this(5, 10);
    }

    public WeaponShield(int reload) {
        this(5, reload);
    }

    public WeaponShield(int strength, int reload) {
        super();
        this.reload = reload;
        this.strength = strength;
    }

    @Override
    public Image getImage() {
        BufferedImage biWeapons = ImageUtils.toBufferedImage(TankWorld.sprites.get("weapons"));
        return biWeapons.getSubimage(32, 0, 16, 16);
    }

    @Override
    public Bullet getBullet(Ship theShip) {
        Point speed = new Point(0, 0);
        Point location = theShip.getLocationPoint();
        Bullet b = new Bullet(location, speed, strength, new FollowMotion(theShip), theShip, TankWorld.sprites.get("Shield1"));
        b.setHeading(theShip.getHeading());
        return b;
    }

}
