package gamengine.modifiers.motions;

import gamengine.game.IGameWorld;
import gamengine.modifiers.motions.MotionController;
import java.util.Observable;

/*Motion controller that does nothing*/
public class NullMotion extends MotionController {

	public NullMotion(IGameWorld world) {
		super(world);
	}

	@Override
	public void update(Observable o, Object arg) {}

}
