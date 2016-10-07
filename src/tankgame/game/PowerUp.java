package tankgame.game;

import gamengine.game.GameObject;
import gamengine.modifiers.AbstractGameModifier;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Observable;

import gamengine.modifiers.weapons.AbstractWeapon;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.nio.file.Path;

/* PowerUp extends ship so that it can hold a weapon to give to player*/
public class PowerUp extends Ship {

    public PowerUp(Point loc, int health, AbstractWeapon weapon, Image powerupImage) {
        super(loc, new Point(0, 1), health, powerupImage);
        this.weapon = weapon;
    }

    public PowerUp(Point loc, int health, Image powerupImage) {
        super(loc, new Point(0, 0), health, powerupImage);
    }

    @Override
    public void update(Observable o, Object arg) {
        AbstractGameModifier modifier = (AbstractGameModifier) o;
        modifier.read(this);
    }

    @Override
    public void die() {
        this.show = false;
    }

    public boolean checkForCollision(ArrayList<?> objects) {
        boolean collisionDetected = false;
        for (Object o : objects) {
            GameObject go = (GameObject) o;
            if (collisionDetected = this.collision(go)) {
                break;
            }
        }
        return collisionDetected;
    }

    @Override
    public Area getCollisionArea() {
//        RoundRectangle2D rr = new RoundRectangle2D.Float
//        (location.x, location.y, width+20, height, width/2, height/2);
        Ellipse2D.Float ellipse = new Ellipse2D.Float(location.x, location.y, location.width, location.height);
        return new Area(ellipse);
        
        
    }

    
}
