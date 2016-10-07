package tankgame.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import tankgame.game.PlayerShip;

public class InfoBar extends InterfaceObject {

    PlayerShip player;
    int playerNumber;
    private final int healthBarWidth;
    private final int healthBarHeight = 5;
//    private final Point scoreLocation=new Point(0,50);
//    private Color scoreColor;

    public InfoBar(PlayerShip player, int playerNum) {
        this.player = player;
        this.playerNumber = playerNum;
        this.healthBarWidth = player.getCollisionArea().getBounds().width;

    }

    @Override
    public void draw(Graphics g2) {
        g2.setFont(new Font("Calibri", Font.PLAIN, 24));
        if (player.getHealth() > 40) {
            g2.setColor(Color.GREEN);
        } else if (player.getHealth() > 20) {
            g2.setColor(Color.YELLOW);
        } else {
            g2.setColor(Color.RED);
        }
        Rectangle tankLoc = player.getLocation();
        float healthPercentage = (float) player.getHealth() / (float) 100;
        g2.fillRect((int) (tankLoc.getCenterX() - healthBarWidth / 2),
                tankLoc.y - healthBarHeight,
                (int) Math.round(healthPercentage * healthBarWidth),
                healthBarHeight);

        Image img = player.getWeapon().getImage();
        g2.drawImage(img, (int) (tankLoc.getCenterX() - healthBarWidth / 2), 
                (int) (tankLoc.y + tankLoc.getHeight()), 
                observer);
    }

}
