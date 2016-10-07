package gamengine.game;

import java.awt.Image;
import java.awt.Point;

/*BackgroundObjects move at speed of 1 and are not collidable*/
public class BackgroundObject extends GameObject {
	public BackgroundObject(Point location, Image img){
		super(location, new Point(0,1), img);
	}
	
	public BackgroundObject(Point location, Point speed, Image img){
		super(location, speed, img);
	}
        
        public BackgroundObject(Point location,int w, int h, Point speed, Image img){
		super(location,w,h, speed, img);
	}

        
}
