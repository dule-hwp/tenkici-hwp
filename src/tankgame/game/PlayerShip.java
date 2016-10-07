package tankgame.game;

import gamengine.game.GameObject;
import gamengine.modifiers.AbstractGameModifier;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Observable;
import java.util.Observer;
import tankgame.TankWorld;

import tankgame.modifiers.motions.InputController;
import tankgame.weapons.SimpleWeapon;

public class PlayerShip extends Ship implements Observer {

    int lives;
    int score;
    Point resetPoint;
    int lastFired = 0;
    boolean isFiring = false;
    public int left = 0, right = 0, up = 0, down = 0;
    double rotationSpeed = Math.PI / 90;
//    Point rotationPoint;
//    String name;
    Color mColor;

    public PlayerShip(Point location, Point speed, Image img, int[] controls, Color color) {
        super(location, speed, 100, img);
        resetPoint = new Point(location);
//        rotationPoint = new Point((int) this.location.getCenterX(), (int)this.location.getCenterY());
        mColor = color;
//        this.name = name;
        weapon = new SimpleWeapon();
//        updateGunLocation();
        motion = new InputController(this, controls);
//        lives = 2;
        health = 100;
        strength = 100;
        score = 0;
//        respawnCounter = 0;
    }

    public void draw(Graphics g, ImageObserver observer) {
//    	if(respawnCounter<=0)
//    		g.drawImage(img, location.x, location.y, observer);
        double locationX = img.getWidth() / 2;
        double locationY = img.getHeight() / 2;
        AffineTransform tx = AffineTransform.getRotateInstance(heading, locationX, locationY);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

        BufferedImage rotatedImage = op.filter(img, null);
        g.drawImage(rotatedImage, location.x, location.y, observer);

        drawBoundingOfCollisionArea(g);
    }

    @Override
    public void damage(int damageDone) {
//        if (respawnCounter <= 0) {
        super.damage(damageDone);
//        }
    }

    @Override
    public void update(int w, int h) {
        if (isFiring) {
            int frame = TankWorld.getInstance().getFrameNumber();
            if (frame >= lastFired + weapon.reload) {
                fire();
                lastFired = frame;
            }
        }

        //rotation change
        if (right == 1 || left == 1) {
            rotateTank(right == 1);
        }

        if ((down == 1 || up == 1)) {
            Rectangle potPosition = getNextPotPosition();
            if (!goingOutOfBounds(w, h, potPosition)) {
                location = potPosition;
            }
        }
    }

    public Rectangle getNextPotPosition() {
        Rectangle temp = new Rectangle(location);
        double headingLoc = heading;
        headingLoc += down == 1 ? Math.PI : 0;
        temp.y += Math.round(speed.x * Math.sin(headingLoc));
        temp.x += Math.round(speed.x * Math.cos(headingLoc));
        return temp;
    }

    @Override
    public boolean collision(GameObject otherObject) {
        if (down == 1 || up == 1) {
            return super.collision(otherObject);
        } else {
            return false;
        }
    }

    public void startFiring() {
        isFiring = true;
    }

    public void stopFiring() {
        isFiring = false;
    }

    public void rotateTank(boolean rotateRight) {
        heading += rotateRight ? rotationSpeed : -rotationSpeed;
//        updateGunLocation();
    }

    @Override
    public void fire() {
//        if (respawnCounter <= 0) {
        weapon.fireWeapon(this);
//    		GameWorld.getInstance().sound.play("Resources/snd_explosion1.wav");
//        }
    }

    public void die() {
        this.show = false;
        BigExplosion explosion = new BigExplosion(new Point(location.x, location.y));
        TankWorld.getInstance().addBackground(explosion);
//        GameWorld.getInstance().incrementScore(this);
//        lives -= 1;
        if (lives >= 0) {
            TankWorld.getInstance().removeClockObserver(this.motion);
            reset();
        } else {
            this.motion.delete(this);
        }
    }

    public void reset() {
        this.setLocation(resetPoint);
        health = strength;
        heading = 0;
//        respawnCounter = 160;
//    	this.weapon = new SimpleWeapon();
    }

    public int getLives() {
        return this.lives;
    }

    public int getScore() {
        return this.score;
    }

    public Color getColor() {
        return mColor;
    }

//    public String getName() {
//        return this.name;
//    }
    public void incrementScore(int increment) {
        score += increment;
    }

    public boolean isDead() {
        return health <= 0;
    }

    @Override
    public void update(Observable o, Object arg) {
        AbstractGameModifier modifier = (AbstractGameModifier) o;
        modifier.read(this);
    }

    private boolean goingOutOfBounds(int w, int h, Rectangle potLocation) {
        if ((potLocation.y < h - height && potLocation.y > 0) && (potLocation.x > 0 && potLocation.x < w - width)) {
            return false;
        }
        return true;
    }

    public void bounce(int w, int h) {

        if (down == 1) {
            down = 0;
            up = 1;
            update(w, h);

            up = 0;
            down = 1;
        } else {
            up = 0;
            down = 1;
            update(w, h);
            down = 0;
            up = 1;
        }
    }

    @Override
    public Area getCollisionArea() {
        Rectangle rec;
        if (down == 1)    
        {
            rec = new Rectangle(location.x + 7, location.y + 9, 27, 45);
        } else if (up == 1) 
        {
            rec = new Rectangle(location.x + 7 + 27, location.y + 9, 27, 45);
        } else {
            rec = new Rectangle(location.x + 7, location.y + 9, 53, 45);
        }

        AffineTransform af = new AffineTransform();
        Area a = new Area(rec);
        af.rotate(heading, this.location.getCenterX(), this.location.getCenterY());
        Area ra = a.createTransformedArea(af);
        return ra;
    }

    @Override
    public String toString() {
        return mColor.toString() + " player";
    }

}
