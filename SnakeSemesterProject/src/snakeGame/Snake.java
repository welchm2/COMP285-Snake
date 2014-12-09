package snakeGame;

import java.util.ArrayList;
import java.util.LinkedList;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;
import ch.aplu.jgamegrid.Location.CompassDirection;

public class Snake extends Actor {

	private static final int SNAKE_START_LENGTH = 3;
	private static final int SNAKE_EXPAND_PER_APPLE = 2;
	private static final int TIME_INTERVAL = 5; // seconds
	private static final int SPEED_INCREASE_PER_TIME = 2; // 5ms
	private static final int SPEED_START = 180; // 180 ms delay
	private static final int SPEED_MAX = 80; // 80ms delay
	private static final int COLOR_MAX = 4; // 4 different colors
	private static final int SCORE_PER_APPLE = 10;
	private static final float SCORE_FACTOR_TIME = 3.0f; //100% speed -> 1 apple = 30 points

	public boolean gameFinish;
	private boolean start = true;
	public LinkedList<CompassDirection> directions;
	private ArrayList<SnakeBody> tailList = new ArrayList<SnakeBody>();
	private CompassDirection currentDirection;

	public boolean autoChangeColor = true;
	private int currentColor = 0;
	private int appleEaten = 0;
	private int expandSnakeLength = 0;
	private long nextInterval;
	private int countInterval;

	private int speedPercent = 0; 	// used for title
	private int countScore = 0;		// used for title
	
	/**
	 * Defines the image of the Snake class.
	 */
	public Snake() {
		super("sprites/squarehead.png", COLOR_MAX); // 4 Images: squarehead_0.png, _1, _2 and _3
	}

	/**
	 * Updates the SnakeHead and Body, handles keyEvents and detects collisions (border, body, apple).
	 * Checks Timer and updates SnakeSpeed.
	 * 
	 * GameGrid calls the act() method during the simulation is running.
	 * @see <a href="http://www.aplu.ch/home/apluhomex.jsp?site=45">Simulation Period</a>
	 */
	public void act() {
		if (start) { 		// do it only once
			start = false;
			initSnake();	// initializes the snake
		}

		// update timer
		checkSpeedTimer();

		// update snake body
		updateSnakeBody();

		// do we need to turn (a new Direction)?
		checkNewDirection();

		// check for valid move
		if (isMoveValid()) {
			move(); // move the snake

			if (checkForSnakeBody()) {
				// Collision with SnakeBody :(
				gameOver();
			}

			if (checkForApple()) {
				// we found an apple :)
				appleEaten++;
				expandSnakeLength += SNAKE_EXPAND_PER_APPLE;
				
				countScore += SCORE_PER_APPLE * (SCORE_FACTOR_TIME * ((speedPercent/10.0)+1));
				
				if (autoChangeColor) {
					changeColor();
				}
				
				updateTitle();
			}
		}
		else { // outside of the gamegrid
			gameOver();
		}
	}
	
	/**
	 * Initialize the snake body and sets the start direction.
	 */
	public void initSnake() {
		
		gameFinish = false;
		gameGrid.setSimulationPeriod(SPEED_START); // set start speed

		// init timer for speed increase
		nextInterval = System.currentTimeMillis() + 1000; // current System time + one second
		countInterval = 0;

		// init direction --> EAST (to the right)
		currentDirection = Location.EAST;
		setDirection(currentDirection);

		// add SnakeBody, Start Direction is EAST --> we add the body on the left side (x-axis) of the head (-i - 1)
		// filling the linked array list with the amount of body squares defined by SNAKE_START_LENGTH
		for (int i = 0; i < SNAKE_START_LENGTH; i++) {
			SnakeBody tail = new SnakeBody(COLOR_MAX); // create a new body square
			gameGrid.addActor(tail, new Location(getX() - i - 1, getY())); // getX() = xPos of SnakeHead
			tailList.add(tail);	// add a SnakeBody square to the list
		}
	}
	/**
	 * Updates the timer and calculates the speed percentage in the title.
	 */
	private void checkSpeedTimer() {
		if (System.currentTimeMillis() > nextInterval) {
			nextInterval = System.currentTimeMillis() + 1000; // current System time + one second
			countInterval++;

			updateTitle();
		}

		if (countInterval >= TIME_INTERVAL) {
			countInterval = 0; // reset counter
		
			if (gameGrid.getSimulationPeriod() > SPEED_MAX) { // < 80 is too fast... do not go faster then 80ms
				gameGrid.setSimulationPeriod(gameGrid.getSimulationPeriod() - SPEED_INCREASE_PER_TIME); // increase speed ;-)
				speedPercent = Math.abs(100 / (SPEED_START - SPEED_MAX) * (gameGrid.getSimulationPeriod() - SPEED_START)); // math.abs() ->>+
			}
		}
	}
	
	/**
	 * Moves the SnakeBody to the new position.  
	 */
	private void updateSnakeBody() {
		int lastIndex = tailList.size() - 1; // size -1 because it starts at 0
		Location lastLocation = tailList.get(lastIndex).getLocation(); // push the square one further index (3->2, 2->1, 1->0)

		for (int i = lastIndex; i > 0; i--) {
			tailList.get(i).setLocation(tailList.get(i - 1).getLocation());
		}
		tailList.get(0).setLocation(getLocation()); // move first bodySquare at the head's location
		
		// expands the snake lengths if apple(s) got eaten
		if (expandSnakeLength > 0) {
			// expand our snake
			SnakeBody tail = new SnakeBody(COLOR_MAX);
			gameGrid.addActor(tail, lastLocation);
			tail.show(currentColor);
			tailList.add(tail);
			expandSnakeLength--; // only one at a time
		}
	}
	
	/**
	 * Changes the direction if the new direction is valid.
	 * Not valid: Same as the previous or opposite direction.
	 * Possible directions are: NORTH, EAST, SOUTH and WEST
	 */
	private void checkNewDirection() {
		// check for new directions in our DirectionsList of the GameWindow class
		if (!directions.isEmpty()) { // key got pressed
			
			CompassDirection newDirection = currentDirection;
			// get first new direction from the list
			CompassDirection checkDirection = directions.removeFirst();
			// System.out.println("Check new Direction: " + checkDirection + " (Current: " + currentDirection + ")");

			switch (checkDirection) {
			case NORTH:
				if (currentDirection != Location.SOUTH) {
					newDirection = Location.NORTH;
				}
				break;
				
			case EAST:
				if (currentDirection != Location.WEST) {
					newDirection = Location.EAST;
				}
				break;
				
			case SOUTH:
				if (currentDirection != Location.NORTH) {
					newDirection = Location.SOUTH;
				}
				break;
				
			case WEST:
				if (currentDirection != Location.EAST) {
					newDirection = Location.WEST;
				}
				break;
				
			default: // ignore all other directions, for example southwest
				break;
			}

			// set new direction
			if (currentDirection != newDirection) {
				currentDirection = newDirection;
				setDirection(currentDirection);
			}
		}
	}
	
	/**
	 * Checks if the snakeHead run against its body.
	 * @return true, if the SnakeBody hit itself
	 */
	private boolean checkForSnakeBody() {
		boolean result = false;
		Actor snakeBody = gameGrid.getOneActorAt(getLocation(), SnakeBody.class);
		if (snakeBody != null) {
			result = true;
		}
		return result;
	}
	
	/**
	 * Checks if the snakeHead hits an apple.
	 * @return true, if the snakeHead hits an apple
	 */
	private boolean checkForApple() {
		Actor apple = gameGrid.getOneActorAt(getLocation(), Apple.class);
		if (apple != null) {
			// place the apple to a new location
			Location newLocation = gameGrid.getRandomEmptyLocation();
			if (newLocation != null) {
				apple.setLocation(newLocation);
			}
			else {
				// no more free spaces --> Game Won :D
				gameWon();
			}
			return true;
		}
		return false;
	}
	/**
	 * Changes the color of the snake.
	 */
	public void changeColor() {
		// "calc" new Color
		currentColor = (currentColor + 1) % COLOR_MAX;
		
		this.show(currentColor); // change current SnakeHead Image to the next Color;
		if (!tailList.isEmpty()) {
			// update SnakeBody Square Colors
			for (int i = 0; i < tailList.size(); i++) {
				tailList.get(i).show(currentColor);
			}
		}
	}
	/**
	 * Updates the information in the title.
	 */
	private void updateTitle() {
		gameGrid.setTitle("Score: " + countScore + " | Apples: " + appleEaten + 
							" | Time: " + (TIME_INTERVAL - countInterval) + " | Speed: " + speedPercent + "%");
	}

	/**
	 * Ends the program and updates the title.
	 * Possible reasons: Snake hit the boarder or itself.
	 */
	private void gameOver() {
		gameGrid.setTitle("GAME  OVER (Score: " + countScore + " | Apples: " + appleEaten + " | Speed: " + speedPercent + "%)");
		gameGrid.doPause();
		gameFinish = true;
	}

	/**
	 * Ends the program -  the game has been won.
	 */
	private void gameWon() {
		gameGrid.setTitle("GRADULATIONS (Score: " + countScore + " | Apples: " + appleEaten + " | Speed: " + speedPercent + "%)");
		gameGrid.doPause();
		gameFinish = true;
	}

	/**
	 * The gameGrid resets to the initial situation, when the user hits the reset button.
	 */
	public void reset() {
		
		super.reset();
		removeSnake();
		appleEaten = 0;
		countScore = 0;
		speedPercent = 0;
		
		// set start speed
		gameGrid.setSimulationPeriod(SPEED_START);

		// init timer for speed increase
		nextInterval = System.currentTimeMillis() + (TIME_INTERVAL * 1000);
		countInterval = 0;

		// init direction --> EAST (to the right)
		directions.clear();
		currentDirection = Location.EAST;
		setDirection(currentDirection);

		updateTitle();
		gameFinish = false;
		start = true;
	}
	
	/**
	 *  Remove all snake body squares from the game grid.
	 */
	public void removeSnake() {
		if (!tailList.isEmpty()) {
			// removes SnakeBodySquares form the GameGrid
			for (int i = 0; i < tailList.size(); i++) {
				gameGrid.removeActor(tailList.get(i));
			}
			tailList.clear(); // delete all objects in the array list
		}
	}

}