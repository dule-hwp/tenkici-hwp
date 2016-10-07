package tankgame.game;

import gamengine.game.GameObject;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

import tankgame.TankWorld;
import gamengine.modifiers.motions.MotionController;
import gamengine.utils.ImageUtils;

/*Bullets fired by player and enemy weapons*/
public class Rocket extends Bullet {

    static Image rocketImage = ImageUtils.toBufferedImage(TankWorld.sprites.get("rocket")).getSubimage(0, 0, 24, 24);

    public Rocket(Point location, Point speed, int strength, MotionController motion, GameObject owner) {
        super(location, speed, strength, motion, owner, rocketImage);
    }

    @Override
    public Area getCollisionArea() {
        Rectangle collisonRec = new Rectangle(location.x, location.y + 5, (int) location.getWidth(), (int) (location.getHeight() - 10));
        Area a = new Area(collisonRec);
        AffineTransform af = new AffineTransform();
        af.rotate(heading, collisonRec.getCenterX(), collisonRec.getCenterY());//rotate 45 degrees around ax, ay
        Area ra = a.createTransformedArea(af);
        return ra;
    }

}
