/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 *
 * @author dusan_cvetkovic
 */
public class TankGame {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final TankWorld game = TankWorld.getInstance();
	    JFrame f = new JFrame("Tank Game");
	    f.addWindowListener(new WindowAdapter() {
		    public void windowGainedFocus(WindowEvent e) {
		        game.requestFocusInWindow();
		    }
	    });
	    f.getContentPane().add("Center", game);
            f.getContentPane().setPreferredSize(new Dimension(640, 480));       //size of the window
	    f.pack();
//	    f.setPreferredSize(new Dimension(640, 480));
//	    game.setDimensions(640, 480);
	    game.init();
	    f.setVisible(true);
	    f.setResizable(false);
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    game.start();
    }
    
}
