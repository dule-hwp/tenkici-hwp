package tankgame.game;

import gamengine.game.AnimatedBackgroundObject;
import gamengine.utils.ImageUtils;
import java.awt.Point;
import java.awt.image.BufferedImage;
import tankgame.TankWorld;


/* Small explosions happen whenever an enemy dies */
public class SmallExplosion extends AnimatedBackgroundObject {

    static BufferedImage sprite = ImageUtils.toBufferedImage(TankWorld.sprites.get("Explosion_small_strip6"));
    
    public SmallExplosion(Point location) {
        super(location, sprite, 6);
    }
}
