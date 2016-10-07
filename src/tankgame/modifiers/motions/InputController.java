package tankgame.modifiers.motions;

import gamengine.modifiers.motions.AbsInputController;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import tankgame.TankWorld;
import tankgame.game.PlayerShip;

public class InputController extends AbsInputController implements KeyListener {

    public InputController(PlayerShip player) {
        this(player, new int[]{KeyEvent.VK_LEFT, KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_SPACE});
    }

    public InputController(PlayerShip player, int[] keys) {
        super(player, TankWorld.getInstance(), PlayerShip.class, keys);
    }
    private void setFire() {
        field = null;
        try {
            action = PlayerShip.class.getMethod("startFiring");
            this.setChanged();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        notifyObservers();
    }

    private void unsetFire() {
        field = null;
        try {
            action = PlayerShip.class.getMethod("stopFiring");
            this.setChanged();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        notifyObservers();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        // left
        if (code == keys[0]) {
            this.setMove("left");
        } // up
        else if (code == keys[1]) {
            this.setMove("up");
        } // right
        else if (code == keys[2]) {
            this.setMove("right");
        } // down
        else if (code == keys[3]) {
            this.setMove("down");
        } // fire
        else if (code == keys[4]) {
            this.setFire();
        }
        setChanged();
        this.notifyObservers();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == keys[0]) {		//
            this.unsetMove("left");
        } else if (code == keys[1]) {
            this.unsetMove("up");
        } else if (code == keys[2]) {
            this.unsetMove("right");
        } else if (code == keys[3]) {
            this.unsetMove("down");
        } else if (code == keys[4]) {
            this.unsetFire();
        }
        setChanged();
        this.notifyObservers();
    }
}
