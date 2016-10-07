/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lazarusgame.game;

import gamengine.game.Wall;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;

/**
 *
 * @author dusan_cvetkovic
 */
public class EndButton extends Wall{

    public EndButton(int w, int h, Point startingPoint, Image img) {
        super(w, h, startingPoint, img, true);
    }

    @Override
    public Area getCollisionArea() {
        Rectangle clone = (Rectangle) location.clone();
        int w = width/5;
        clone.setLocation(clone.x+width/2-w/2,clone.y+height/2-w/2);
        clone.setSize(w, w);
        return new Area(clone);
    }
    
    
    
}
