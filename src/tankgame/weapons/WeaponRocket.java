package tankgame.weapons;

import gamengine.modifiers.weapons.AbstractWeapon;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ListIterator;
import tankgame.TankWorld;

import tankgame.game.Bullet;
import gamengine.utils.ImageUtils;
import tankgame.game.PlayerShip;
import tankgame.game.Rocket;
import tankgame.game.Ship;
import tankgame.modifiers.motions.AngledMotion;

public class WeaponRocket extends AbstractWeapon {

    int strength;

    public WeaponRocket() {
        this(5, 10);
    }

    public WeaponRocket(int reload) {
        this(5, reload);
    }

    public WeaponRocket(int strength, int reload) {
        super();
        this.reload = reload;
        this.strength = strength;
    }

    @Override
    public Image getImage() {
        BufferedImage biWeapons = ImageUtils.toBufferedImage(TankWorld.sprites.get("weapons"));
        return biWeapons.getSubimage(0, 0, 16, 16);
    }

    @Override
    public Bullet getBullet(Ship theShip) {
        Point speed = new Point(6, 0);
        Point location = theShip.getLocationPoint();
        Rocket bullet = new Rocket(location, speed, strength, new AngledMotion(), theShip);
        ListIterator<PlayerShip> players = TankWorld.getInstance().getPlayers();
        while (players.hasNext())
        {
            PlayerShip player = players.next();
            if (player != theShip)
            {
                double angle = Math.atan2(player.getY() - theShip.getY(), player.getX() - theShip.getX());
                bullet.setHeading(angle);
            }
        }
        return bullet;
    }

}
