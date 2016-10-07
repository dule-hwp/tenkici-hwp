package gamengine.game;

import gamengine.utils.ImageUtils;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.image.ImageObserver;

/* This is where the scrolling background is drawn*/
public class Background extends BackgroundObject {
	int move, w, h;

	public Background(int w, int h, Point speed, Image img) {
		super(new Point(0,0), speed, img);
		this.img = ImageUtils.toBufferedImage(img);
		this.w = w;
		this.h = h;
		move = 0;
                
	}
	
    public void update(int w, int h) {
    }
	
    public void draw(Graphics g, ImageObserver obs) {
        int TileWidth = img.getWidth(obs);
        int TileHeight = img.getHeight(obs);

        int NumberX = (int) (w / TileWidth);
        int NumberY = (int) (h / TileHeight);

        //Image Buffer = GameWorld.getInstance().createImage(NumberX * TileWidth, NumberY * TileHeight);
        //Graphics BufferG = Buffer.getGraphics();

        for (int i = -1; i <= NumberY; i++) {
            for (int j = 0; j <= NumberX; j++) {
                g.drawImage(img, j * TileWidth, i * TileHeight + (move % TileHeight), TileWidth, TileHeight, obs);
            }
        }
        move += speed.y;
        
//        drawBoundingOfCollisionArea(g);
    }

    @Override
    public Area getCollisionArea() {
        return new Area(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
