package gamengine.game;

import gamengine.utils.Direction;
import gamengine.utils.ImageUtils;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.ImageObserver;

public class Wall extends BackgroundObject {

    private final boolean mIsBreakabe;
    Direction wallPosition;
//    public int destroyedInFrame = 0;

    public Wall(int w, int h, Point startingPoint, Image img, boolean isBreakable) {
        super(startingPoint, w, h, new Point(0, 0), img);
        this.img = ImageUtils.toBufferedImage(img);
        mIsBreakabe = isBreakable;
    }

    public void setWallPosition(Direction wallPosition) {
        this.wallPosition = wallPosition;
    }

    public Direction getWallPosition() {
        return wallPosition;
    }
    
    
    
//    public Wall(int w, int h, Point startingPoint, Image img, boolean isBreakable, boolean isShown, int destroyedInFrame) {
//        super(startingPoint, w, h, new Point(0, 0), img);
//        this.img = GameObject.toBufferedImage(img);
//        mIsBreakabe = isBreakable;
//        show = isShown;
//        this.destroyedInFrame = destroyedInFrame;
//    }

    @Override
    public void update(int w, int h) {
//        if (mIsBreakabe && location.y == 384) {
//            if (!show) {
//                int frame = GameWorld.getInstance().getFrameNumber();
//                if (destroyedInFrame ==0)
//                    destroyedInFrame = frame;
//                if (frame >= destroyedInFrame + 5) {
//                    show = true;
//                    destroyedInFrame=0;
//                }
//            }
//        }
    }
    

    public void draw(Graphics g, ImageObserver obs) {
        if (mIsBreakabe) {
            super.draw(g, obs);
            return;
        }
        int TileWidth = img.getWidth(obs);
        int TileHeight = img.getHeight(obs);

        int NumberX = (int) (width / TileWidth);
        int NumberY = (int) (height / TileHeight);

        for (int i = 0; i < NumberY; i++) {
            for (int j = 0; j < NumberX; j++) {
                g.drawImage(img, location.x + j * TileWidth, location.y + i * TileHeight, TileWidth, TileHeight, obs);
            }
        }
        
//        drawBoundingOfCollisionArea(g);
    }

    @Override
    public String toString() {
        return "Wall: w:" + this.width + " h:" + this.height;
    }

    public boolean isBreakable() {
        return mIsBreakabe;
    }

}
