package snakeGamePackage;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;

public class Snake1 extends Actor {
	private boolean start = true;
	private ArrayList<Bit> tails = new ArrayList<Bit>();
	

	public Snake1() {
		super("sprites/squarehead.png");		// call the sprite image
	}
	
	public void act() {
		if (start) {
			start = false;
			newSnake();
		}
		
		refresh();
		
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
		
		Actor body = gameGrid.getOneActorAt(getLocation(), Bit.class);
		if(body != null){
			gameGrid.doPause();
			
		}
		
		Actor apple = gameGrid.getOneActorAt(getLocation(), Apple.class);
		if(apple != null){
			Location next = gameGrid.getRandomEmptyLocation();
			if(next != null){
				apple.setLocation(next);
			}
			else{
				
			}
			
		}
		
		if(!isInGrid()){
			gameGrid.doPause();
		}
	}

	void newSnake() {
		for(int i = 0; i < 3; i++){
			Bit tail = new Bit();
			gameGrid.addActor(tail, new Location(getX() - i - 1, getY()));
			tails.add(tail);
		}
	}
	
	private void refresh() {
		int last = tails.size() - 1;
		for (int i = last; i > 0; i--) {
			tails.get(i).setLocation(tails.get(i - 1).getLocation()); // push the square one further index 5-> 4, 4->3
		}
		tails.get(0).setLocation(getLocation()); // move first bodySquare at the head location
	}
}