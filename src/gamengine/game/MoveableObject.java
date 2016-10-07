package gamengine.game;

import java.awt.Image;
import java.awt.Point;

import gamengine.modifiers.motions.MotionController;
//import tankgame.modifiers.motions.NullMotion;

/*MoveableObjects have movement behaviors*/
public abstract class MoveableObject extends GameObject {

    protected int strength;
    protected MotionController motion;

    public MoveableObject(Point location, Point speed, Image img) {
        super(location, speed, img);
        this.strength = 0;
        setMotion();
//        this.motion = new NullMotion();
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void update(int w, int h) {
        motion.read(this);
    }

    public void start() {
        motion.addObserver(this);
    }
    
    public void stop() {
        motion.delete(this);
    }

    protected abstract void setMotion();

    public abstract boolean canChangeFields();
}
