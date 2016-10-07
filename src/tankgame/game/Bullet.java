package tankgame.game;

import gamengine.game.MoveableObject;
import gamengine.game.GameObject;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.AffineTransformOp;
import java.awt.image.ImageObserver;

import gamengine.modifiers.motions.MotionController;
import tankgame.TankWorld;
import gamengine.modifiers.motions.NullMotion;

/*Bullets fired by player and enemy weapons*/
public class Bullet extends MoveableObject {

    PlayerShip owner;
    boolean friendly;

    public Bullet(Point location, Point speed, int strength, MotionController motion, GameObject owner, Image img) {
        super(location, speed, img);
        this.strength = strength;
        this.setImage(img);
        this.owner = (PlayerShip) owner;
        if (motion!=null)
            this.motion = motion;
        this.motion.addObserver(this);
    }

    public PlayerShip getOwner() {
        return owner;
    }

    public boolean isFriendly() {
        if (friendly) {
            return true;
        }
        return false;
    }

    @Override
    public void draw(Graphics g, ImageObserver obs) {
        double locationX = img.getWidth() / 2;
        double locationY = img.getHeight() / 2;
        AffineTransform tx = AffineTransform.getRotateInstance(heading, locationX, locationY);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        g.drawImage(op.filter(img, null), location.x, location.y, observer);

//        drawBoundingOfCollisionArea(g);
    }

    @Override
    public Area getCollisionArea() {
        Area a = new Area(location);
        AffineTransform af = new AffineTransform();
        af.rotate(heading, location.getCenterX(), location.getCenterY());//rotate 45 degrees around ax, ay
        Area ra = a.createTransformedArea(af);
        return ra;
    }

    @Override
    protected void setMotion() {
        this.motion = new NullMotion(TankWorld.getInstance());
    }

    @Override
    public boolean canChangeFields() {
        return true;
    }

}
