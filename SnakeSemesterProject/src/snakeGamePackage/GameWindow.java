package snakeGamePackage;

import ch.aplu.jgamegrid.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;

public class GameWindow extends GameGrid{

	private static final long serialVersionUID = 1L;

	private static final int GRID_WIDTH = 25; // 25 Cells
	private static final int GRID_HEIGHT = 25; // 25 Cells
	private static final int GRID_SIZE = 18; // 18 Pixel
	private static final int APPLE_COUNT = 1; // 1 Apple

	private ArrayList<Apple> appleList = new ArrayList<Apple>();
	private Snake1 mySnake;

	/**
	 * Initializes the GameGrid and adds our snake and the apples.
	 */
	public GameWindow() {

		// Initializing the GameGrid
		super(GRID_WIDTH, GRID_HEIGHT, GRID_SIZE, Color.gray, true); // true = toolbar visible

		// Create and add our Snake to the GameGrid
		mySnake = new Snake1();
		addActor(mySnake, new Location(GRID_WIDTH / 2, GRID_HEIGHT / 2));

		// Add Apple's
		addApples();

		// Show GameGrid 
		show();

		// do one Step --> Generate SnakeBody
		doStep();
	}

	/**
	 * Adds as many apples to the GameGrid as defined in APPLE_COUNT.
	 */
	private void addApples() {
		for (int i = 0; i < APPLE_COUNT; i++) {
			Apple aApple = new Apple();
			appleList.add(aApple); // required for reset
			addActor(aApple, getRandomEmptyLocation()); 
		}
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
	 * Resets the game to the start conditions and initializes the snake and the apples.
	 */
	public void reset() {
		super.reset();

		setTitle("New Game...");

		removeAllApples();
		addApples();

		mySnake.reset();
		mySnake.newSnake();
	}
}