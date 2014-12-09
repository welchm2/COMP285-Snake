package snakeGame;

import ch.aplu.jgamegrid.Actor;

public class SnakeBody extends Actor {

	/**
	 * Defines the image of the SnakeBody class.
	 * @param colorMax Max. amount of images
	 */
	public SnakeBody(final int colorMax) {
		super("sprites/squarebody.png", colorMax); // 4 Images, squarebody_0, _1, _2 and _3
	}

}
