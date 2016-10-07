package gamengine.modifiers.motions;

import gamengine.game.IGameWorld;
import gamengine.game.MoveableObject;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Observable;

public abstract class AbsInputController extends MotionController implements KeyListener {

    protected Field field;
    protected Method action;
    protected int moveState;
    protected int[] keys;
    protected MoveableObject mPlayer;
    protected final Class<?> mPlayerClass;

//    public AbsInputController(MoveableObject player, IGameWorld world, Class<?> playerClass) {
//        this(player, world, playerClass, new int[]{KeyEvent.VK_LEFT, KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN});
//        
//    }

    public AbsInputController(MoveableObject player, IGameWorld world, Class<?> playerClass, int[] keys) {
        super(world);
        mPlayerClass = playerClass;
        mPlayer = player;
        this.addObserver(player);
        this.action = null;
        this.field = null;
        this.keys = keys;
        moveState = 0;
        worldInstance.setKeyListener(this);
    }

    public void signalKeyPress(KeyEvent e) {

    }

    @Override
    public void read(Object theObject) {
        MoveableObject playerObject = (MoveableObject) theObject;

        try {
            if (field!=null && playerObject.canChangeFields())
                field.setInt(playerObject, moveState);
            else if(action!=null)
                action.invoke(mPlayer);
        } catch (Exception e) {
            e.printStackTrace();
//            try {
//                action.invoke(mPlayer);
//            } catch (Exception e2) {
//            }
        }
    }

    @Override
    public void clearChanged() {
        super.clearChanged();
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public abstract void keyReleased(KeyEvent e);
    @Override
    public abstract void keyPressed(KeyEvent e);
    @Override
    public void keyTyped(KeyEvent e) {

    }
    
    protected void setMove(String direction) {
        try {
            field = mPlayerClass.getDeclaredField(direction);
            moveState = 1;
            this.setChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyObservers();
    }
    
    protected void unsetMove(String direction) {
        try {
            field = mPlayerClass.getDeclaredField(direction);
            moveState = 0;
            this.setChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyObservers();
    }
}
