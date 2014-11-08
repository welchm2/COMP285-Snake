package snakeGamePackage;

import ch.aplu.jgamegrid.Actor;

public class Bit extends Actor {  // 
	public Bit() {
		super("sprites/squarebody.png");
	}
		public void act(){        // intializes a moving class 
			move();				 // this method moves the bits 
		    if (isNearBorder())  // keeps track on where the bit is 
		      turn(180);		 // moves it in the follwing direction this would be a factor used to create the key movement
			
		}
	}

