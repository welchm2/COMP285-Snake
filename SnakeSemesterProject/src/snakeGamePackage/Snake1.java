package snakeGamePackage;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;

public class Snake1 extends Actor {
	private ArrayList<Bit> tails = new ArrayList<Bit>();
	

	public Snake1() {
		super("sprites/squarehead.png");		// call the sprite image
	}
	
	public void act() {
		int i = 0;
		while(i < 3){
		   	Bit tail = new Bit();
		   	gameGrid.addActor(tail, new Location(getX(), getY()));
		   	i++;
		}
		
		move();									// this method moves the bits 
		if(gameGrid.isKeyPressed(KeyEvent.VK_UP) && getDirection() != 90 && getDirection() != 270){
			setDirection(270);
		}
		if(gameGrid.isKeyPressed(KeyEvent.VK_DOWN) && getDirection() != 270 && getDirection() != 90){
			setDirection(90);
		}
		if(gameGrid.isKeyPressed(KeyEvent.VK_RIGHT) && getDirection() != 180 && getDirection() != 0){
			setDirection(0);
		}
		if(gameGrid.isKeyPressed(KeyEvent.VK_LEFT) && getDirection() != 0 && getDirection() != 180){
			setDirection(180);
		}
		if(!isInGrid()){
			System.exit(-1);
		}
	}
}