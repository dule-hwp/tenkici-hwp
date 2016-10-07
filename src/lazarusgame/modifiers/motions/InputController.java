package lazarusgame.modifiers.motions;

import gamengine.game.MoveableObject;
import gamengine.modifiers.motions.AbsInputController;
import java.awt.event.KeyEvent;
import lazarusgame.LazarusWorld;
import lazarusgame.game.Lazarus;

public class InputController extends AbsInputController {

    public InputController(Lazarus player) {
        this(player, new int[]{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_I});
    }

    public InputController(Lazarus player, int[] keys) {
        super(player,LazarusWorld.getInstance(), Lazarus.class, keys);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == keys[0]) {		//
//            this.unsetMove("left");
            
        } else if (code == keys[1]) {
//            this.unsetMove("right");
        } 
        else if (code == keys[2]) {
            this.unsetMove("info");
        } 
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == keys[0]) {		//
            this.setMove("left");
        } else if (code == keys[1]) {
            this.setMove("right");
        } 
        else if (code == keys[2]) {
            this.setMove("info");
        } 
    }

//    @Override
//    public void read(Object theObject) {
//        Lazarus object = (Lazarus) theObject;
////        object.move();
//    }
    
    
    
}
