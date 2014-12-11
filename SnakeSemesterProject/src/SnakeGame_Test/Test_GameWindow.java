package SnakeGame_Test;

import SnakeGame.Apple;
import SnakeGame.GameWindow;
import SnakeGame.Snake;
import ch.aplu.jgamegrid.*;
import ch.aplu.jgamegrid.Location.CompassDirection;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;

public class Test_GameWindow extends GameGrid implements GGKeyListener{

	private static final long serialVersionUID = 1L; // needs to be defined for a serializable class
	private static final int GRID_WIDTH = 20; // 25 Cells
	private static final int GRID_HEIGHT = 20; // 25 Cells
	private static final int GRID_SIZE = 18; // 18 Pixel
	private static final int APPLE_COUNT = 1; // 10 Apples
	private static final int MAX_QUEUED_DIRECTIONS = 5; // amount of stored key-inputs
	private LinkedList<CompassDirection> directionList = new LinkedList<Location.CompassDirection>(); // stores directions of key-inputs
	private ArrayList<Apple> appleList = new ArrayList<Apple>();
	private Snake mySnake;
	/**
	 * Initializes the GameGrid and adds our snake and the apples.
	 */
	public Test_GameWindow() {
		
		// Initializing the GameGrid
		super(GRID_WIDTH, GRID_HEIGHT, GRID_SIZE, Color.gray, false);
			// Create and add our Snake to the GameGrid
		mySnake = new Snake();
		mySnake.directions = directionList;
		addActor(mySnake, new Location(GRID_WIDTH / 2, GRID_HEIGHT / 2));
			// Register ourself for KeyEvents (to the GameGrid)
		addKeyListener(this);
			// Add Apple's
		addApples();
			// Show GameGrid
		show();
			// do one Step --> Generate SnakeBody
		doStep(); // (not required, if we start with doRun() )
	}
	
	/**
	 * Adds as many apples to the GameGrid as defined in APPLE_COUNT.
	 */
	public void addApples(){
		Apple a = new Apple();
		appleList.add(a);
		addActor(a, new Location(14, 10));
	}
		/**
	 * Removes all apples from the gameGrid.
		 */
	private void removeAllApples() {
		for (Apple aApple : appleList) {
			removeActor(aApple);
		}
	}
	/**
	 * This method reacts on key events, checks which key got pressed and saves the direction value into the linked list.
	 * @param evt KeyEvent
	 * @return keyEvent has been handled
	 */
	public boolean keyPressed(KeyEvent evt) {
			switch (evt.getKeyCode()) {
		case KeyEvent.VK_UP:
			if (directionList.size() < MAX_QUEUED_DIRECTIONS) {
				directionList.add(Location.NORTH);
			}
			break;
		case KeyEvent.VK_RIGHT:
			if (directionList.size() < MAX_QUEUED_DIRECTIONS) {
				directionList.add(Location.EAST);
			}
			break;
		case KeyEvent.VK_DOWN:
			if (directionList.size() < MAX_QUEUED_DIRECTIONS) {
				directionList.add(Location.SOUTH);
			}
			break;
		case KeyEvent.VK_LEFT:
			if (directionList.size() < MAX_QUEUED_DIRECTIONS) {
				directionList.add(Location.WEST);
			}
			break;
			
		case KeyEvent.VK_ENTER: // start / restart Game / toggle Pause
		case KeyEvent.VK_P:
		case KeyEvent.VK_SPACE:
			if (mySnake.gameFinish) {
				doReset();
				mySnake.reset();
			}
			if (isPaused()) {
				doRun();
				setTitle("");
			}
			else {
				doPause();
				setTitle("P A U S E D");
			}
			break;
			
		case KeyEvent.VK_R: // reset Game
			doReset();
			mySnake.reset();
			setTitle("Press ENTER to start...");
			break;
		case KeyEvent.VK_C: // change Color
			mySnake.changeColor();
			break;
		case KeyEvent.VK_A: // toggle autoChangeColor (change color with every eaten apple)
			mySnake.autoChangeColor = !mySnake.autoChangeColor;
			break;
		}
		return true; // true = key event is handled
	}
		
	/**
	 * Resets the game to the start conditions and initializes the snake and the apples.
	 */
	public void reset() {
		super.reset();
		setTitle("New Game...");
		removeAllApples();
		addApples();
		mySnake.reset();
		mySnake.initSnake();
	}
		
	@Override
	public boolean keyReleased(KeyEvent evt) {
		// not used
		return true;
	}
}
