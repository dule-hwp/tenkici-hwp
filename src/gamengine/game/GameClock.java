package gamengine.game;

import java.util.Observable;

/*Game clock ticks on every frame and notifies observers to update*/
public class GameClock extends Observable {
	protected int startTime;
	protected int frame;
	
	public GameClock(){
		startTime = (int) System.currentTimeMillis();
		frame = 0;
	}
		
	public void tick(){
		frame++;
		setChanged();
		this.notifyObservers();
	}
	
	public int getFrame(){
		return this.frame;
	}
	
	public int getTime(){
		return (int)System.currentTimeMillis()-startTime;
	}
}
