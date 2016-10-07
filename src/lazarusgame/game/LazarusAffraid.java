package lazarusgame.game;

import gamengine.game.AnimatedBackgroundObject;
import gamengine.utils.ImageUtils;
import java.awt.Point;
import java.awt.image.BufferedImage;
import lazarusgame.LazarusWorld;


/* BigExplosion plays when player dies*/
public class LazarusAffraid extends AnimatedBackgroundObject {

    static BufferedImage sprite = ImageUtils.toBufferedImage(LazarusWorld.sprites.get("Lazarus_afraid10"));
    
    public LazarusAffraid(Point location) {
        super(location, sprite, 10);
    }
    
}
