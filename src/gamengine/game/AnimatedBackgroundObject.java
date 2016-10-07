package gamengine.game;

import gamengine.utils.ImageUtils;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

/* BigExplosion plays when player dies*/
public class AnimatedBackgroundObject extends BackgroundObject {

    int timer;
    int frame;
    Image animation[];
//    static BufferedImage sprite = ImageUtils.toBufferedImage(LazarusWorld.sprites.get("Lazarus_afraid10"));
    private final int mNumOfImagesInSprite;
    private boolean mAnimationLoop;

    public AnimatedBackgroundObject(Point location, BufferedImage sprite, int numOfSprites) {
        super(location, new Point(0, 0), sprite.getSubimage(0, 0, sprite.getHeight(), sprite.getHeight()));
        timer = 0;
        frame = 0;
        mAnimationLoop = false;
        mNumOfImagesInSprite = numOfSprites;
        animation = new Image[numOfSprites];
        int spriteSize = sprite.getHeight();
        for (int i = 1; i < numOfSprites+1; i++) {
            animation[i - 1] = sprite.getSubimage((i-1)*spriteSize, 0, spriteSize, spriteSize);
        }
    }

    public void update(int w, int h) {
        super.update(w, h);
        timer++;
        if (timer % 5 == 0) {
            
            if (frame < mNumOfImagesInSprite) {
                this.img = ImageUtils.toBufferedImage(animation[frame]);
            } else {
                if (!mAnimationLoop)
                    this.show = false;
                else
                    frame = -1;
            }
            frame++;
        }

    }
    
    public void setLoop(boolean shouldLoop)
    {
        mAnimationLoop = shouldLoop;
    }
    
//    public void draw(Graphics g, ImageObserver obs) {
//        if (show) {
//            g.drawImage(img, location.x-sprite.getWidth(observer)/2, location.y-sprite.getHeight(observer)/2, obs);
//        }
//    }

}
