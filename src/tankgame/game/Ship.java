package tankgame.game;

import gamengine.game.MoveableObject;
import gamengine.game.GameObject;
import java.awt.Image;
import java.awt.Point;
import tankgame.TankWorld;

import gamengine.modifiers.motions.MotionController;
import gamengine.modifiers.weapons.AbstractWeapon;
import gamengine.modifiers.motions.NullMotion;

/* Ships are things that have weapons and health */
public class Ship extends MoveableObject {

    protected AbstractWeapon weapon;
    protected int health;
    protected Point gunLocation;

    public Ship(Point location, Point speed, int strength, Image img) {
        super(location, speed, img);
        this.strength = strength;
        this.health = strength;
//    	this.gunLocation = new Point(32,40);
    }

    public Ship(int x, Point speed, int strength, Image img) {
        this(new Point(x, -90), speed, strength, img);
    }

    public void setWeapon(AbstractWeapon weapon) {
        if (this.weapon != null) {
            this.weapon.remove();
        }
        this.weapon = weapon;
    }

    public AbstractWeapon getWeapon() {
        return this.weapon;
    }

    public void damage(int damageDone) {
        this.health -= damageDone;
        if (health <= 0) {
            this.die();
        }
        return;
    }

    public void die() {
        this.show = false;
        SmallExplosion explosion = new SmallExplosion(new Point(location.x, location.y));
        TankWorld.getInstance().addBackground(explosion);
        weapon.deleteObserver(this);
        motion.deleteObserver(this);
        TankWorld.getInstance().removeClockObserver(motion);

    }

    public void collide(GameObject otherObject) {
    }

    public void fire() {
        weapon.fireWeapon(this);
    }

    /* some setters and getters!*/
    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public MotionController getMotion() {
        return this.motion;
    }

    public void setMotion(MotionController motion) {
        this.motion = motion;
    }

    public Point getGunLocation() {
        return this.gunLocation;
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
