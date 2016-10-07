package tankgame.ui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import tankgame.TankWorld;
import tankgame.game.PlayerShip;

public class ScoreInfo extends InterfaceObject {

    PlayerShip[] mPlayers;
    int playerNumber;
    private final Point[] mScoreLocations;

    public ScoreInfo(PlayerShip[] newObjects) {
        mPlayers = newObjects;
        mScoreLocations = new Point[mPlayers.length];
        int x = TankWorld.getInstance().getWindowSize().width / 4;
        for (int i=0;i<mPlayers.length;i++)
        {
            mScoreLocations[i]=new Point(x+i*2*x, 50);
        }
    }

    @Override
    public void draw(Graphics g2) {
        g2.setFont(new Font("default", Font.BOLD, 30));
        for (int i=0;i<mPlayers.length;i++)
        {
            PlayerShip p = mPlayers[i];
            g2.setColor(p.getColor());
            g2.drawString(Integer.toString(p.getScore()), mScoreLocations[i].x, mScoreLocations[i].y);
            
        }
        
    }

}
