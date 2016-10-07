package lazarusgame.game;

import gamengine.game.AnimatedBackgroundObject;
import gamengine.game.BackgroundObject;
import gamengine.utils.ImageUtils;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import lazarusgame.LazarusWorld;


/* BigExplosion plays when player dies*/
public class LazarusSquished extends AnimatedBackgroundObject {

    static BufferedImage sprite = ImageUtils.toBufferedImage(LazarusWorld.sprites.get("Lazarus_squished11"));

    public LazarusSquished(Point location) {
        super(location, sprite, 11);
    }

    @Override
    public void update(int w, int h) {
        super.update(w, h);
        if(!show)
            LazarusWorld.getInstance().finishGame();
    }

}
