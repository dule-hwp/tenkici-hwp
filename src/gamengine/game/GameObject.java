/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamengine.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Observable;
import java.util.Observer;

import gamengine.modifiers.AbstractGameModifier;
import gamengine.utils.ImageUtils;

public class GameObject implements Observer {

    protected Point speed;
    protected Rectangle location;
    protected BufferedImage img;
    protected int height, width;
    protected double heading = 0;
    protected ImageObserver observer;
    public boolean show;

    public GameObject() {
    }

    public GameObject(Point location, Point speed, Image img) {
        this.speed = speed;
        this.img = ImageUtils.toBufferedImage(img);
        this.show = true;
        height = img.getHeight(observer);
        width = img.getWidth(observer);
        this.location = new Rectangle(location.x, location.y, width, height);
    }

    public GameObject(Point location, int w, int h, Point speed, Image img) {
        this.speed = speed;
        this.img = ImageUtils.toBufferedImage(img);
        this.show = true;
        this.location = new Rectangle(location.x, location.y, w, h);
        width = w;
        height = h;
    }

    public GameObject(Point location, Image img) {
        this(location, new Point(0, 0), img);
    }

    public void draw(Graphics g, ImageObserver obs) {
        if (show) {
            g.drawImage(img, location.x, location.y, obs);
        }
    }

    public void setImage(Image img) {
        this.img = ImageUtils.toBufferedImage(img);
        this.height = img.getWidth(observer);
        this.width = img.getHeight(observer);
        this.location.setSize(width, height);
    }

    public void update(int w, int h) {
        location.x += speed.x;
        location.y += speed.y;

        if (location.y < -100 || location.y == h + 100) {
            this.show = false;
        }

    }

    @Override
    public void update(Observable o, Object arg) {
        AbstractGameModifier modifier = (AbstractGameModifier) o;
        modifier.read(this);
    }

    public int getX() {
        return location.x;
    }

    public int getY() {
        return location.y;
    }

    public int getSizeX() {
        return width;
    }

    public int getSizeY() {
        return height;
    }

    public Point getSpeed() {
        return speed;
    }

    public void setLocation(Point newLocation) {
        location.setLocation(newLocation);
    }

    public Rectangle getLocation() {
        return new Rectangle(this.location);
    }

    public Point getLocationPoint() {
        return new Point(location.x, location.y);
    }

    public void move(int dx, int dy) {
        location.translate(dx, dy);
    }

    public void move() {
        location.translate(speed.x, speed.y);
    }

    public void moveByHeading(double angle) {

        int dy = (int) Math.round(speed.x * Math.sin(angle));
        int dx = (int) Math.round(speed.x * Math.cos(angle));
        location.translate(dx, dy);
    }

    public boolean collision(GameObject otherObject) {
        Area a = getCollisionArea();
        a.intersect(otherObject.getCollisionArea());
        return !a.isEmpty();
    }
    
    public void collide(GameObject otherObject) {
//        Area a = getCollisionArea();
//        a.intersect(otherObject.getCollisionArea());
//        a.isEmpty();
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public Area getCollisionArea(){
        return new Area(location);
    }
    
    protected void drawBoundingOfCollisionArea(Graphics g) {
        Color old = g.getColor();
        g.setColor(Color.red);
        Area a = getCollisionArea();
        Rectangle bb = a.getBounds();
//        Rectangle bb = new Rectangle(location);
        g.drawRect(bb.x, bb.y, bb.width, bb.height);
        g.setColor(old);
    }
    
    protected Point getCenterFromUperLeft(Point uperLeft, int w, int h)
    {
        Point p = new Point(uperLeft);
        p.translate(-w/2, -h/2);
        return p;
    }
}
