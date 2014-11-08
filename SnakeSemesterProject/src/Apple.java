import ch.aplu.jgamegrid.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;


public class Apple extends Actor {

	public Apple() {
	
		super("sprites/apple_small.png");
	}
	
	public void act() {
		
		/*
		 * move()
		 * For a small grid (total number of cells <= 2500 = 50 * 50) moves to one 
		 * of 8 neighbour cells in the current direction (compass directions 45 degrees wide).
		 */
		move();
	    
		if (!isMoveValid())  // isMoveValid Returns true, if the next call of move() will put the actor in a cell inside the game grid.
	      turn(180);
	}
	
}
