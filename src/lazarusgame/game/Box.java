package lazarusgame.game;

import gamengine.game.GameObject;
import gamengine.game.MoveableObject;
import gamengine.modifiers.AbstractGameModifier;
import gamengine.modifiers.motions.SimpleMotion;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.ImageObserver;
import java.util.ListIterator;
import java.util.Observable;
import lazarusgame.LazarusWorld;

/* PowerUp extends ship so that it can hold a weapon to give to player*/
public class Box extends MoveableObject {

    protected boolean isStatic;
//    private int strength=-1;
    public Box(Point location, Image img, int s) {
        this(location, new Point(0,2), img, s);
    }
    
    public Box(Point location, Point speed, Image img, int s) {
        super(location, speed, img);
//        setMotion();
        isStatic = false;
        strength = s;
    }

    @Override
    public void update(Observable o, Object arg) {
        AbstractGameModifier modifier = (AbstractGameModifier) o;
        modifier.read(this);
    }

    public void die() {
        this.show = false;
//    	weapon.deleteObserver(this);
//        motion.deleteObserver(this);
//        TankWorld.getInstance().removeClockObserver(motion);
    }
    
    public boolean checkForCollision(ListIterator<?> iterator) {
        boolean collisionDetected = false;
        while (iterator.hasNext()) {
            GameObject go = (GameObject) iterator.next();
            if (collisionDetected = this.collision(go)) {
                this.setLocation(new Point(location.x,go.getY()-height));
                if (go instanceof LazarusAffraid)
                {
                    LazarusWorld.getInstance().getLazarus().die();
                    go.show=false;
                }
                break;
            }
        }
        
        return collisionDetected;
    }

    public Box getCollidedBox(ListIterator<Box> iterator) {
        while (iterator.hasNext()) {
            Box b = iterator.next();
            if (this.collision(b) && this!=b ) {
                this.setLocation(new Point(location.x,b.getY()-height));
                return b;
            }
        }
        return null;
    }

    @Override
    public void start() {
        this.motion.addObserver(this);
    }

    @Override
    public void stop() {
        super.stop();
        isStatic = true;
    }

    public boolean isStatic() {
        return isStatic;
    }

    @Override
    protected void setMotion() {
        motion = new SimpleMotion(LazarusWorld.getInstance());
    }

    @Override
    public boolean canChangeFields() {
        return true;
    }

    @Override
    public void draw(Graphics g, ImageObserver obs) {
        super.draw(g, obs); 
//        if (!isStatic)
//            drawBoundingOfCollisionArea(g);
    }
}
