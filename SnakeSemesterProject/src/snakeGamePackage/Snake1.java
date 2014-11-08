package snakeGamePackage;

import java.util.ArrayList;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;

public class Snake1 extends Actor {
	

	public Snake1() {
		super("sprites/squarehead.png");		// call the sprite image
	}
	
	public void act() {
		{
		move();									// moves 
	    if (isNearBorder())						// keeps it in between the gamegrid 
	      turn(180);							// angles the actor and turns it. This would be changed to the key movement
		}
	}


}