package tankgame.game;

import gamengine.game.AnimatedBackgroundObject;
import gamengine.utils.ImageUtils;
import java.awt.Point;
import java.awt.image.BufferedImage;
import tankgame.TankWorld;


/* BigExplosion plays when player dies*/
public class BigExplosion extends AnimatedBackgroundObject {

    static BufferedImage sprite = ImageUtils.toBufferedImage(TankWorld.sprites.get("Explosion_large_strip7"));
    
    public BigExplosion(Point location) {
        super(location, sprite, 7);
    }

}
