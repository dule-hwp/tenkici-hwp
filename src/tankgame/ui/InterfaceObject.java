package tankgame.ui;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.util.Observable;
import java.util.Observer;

public abstract class InterfaceObject implements Observer{
	Point location;
	ImageObserver observer;
	
	public abstract void draw(Graphics g);

	public void update(Observable o, Object arg) {
	}
}
